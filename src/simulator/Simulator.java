package simulator;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import agent.AbstractAgent;

import simulator.conf.SConfigParser;
import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.data.Instances;
import simulator.data.database.DBManager;
import simulator.data.loaders.AbstractDataLoader;
import simulator.data.loaders.FileDataLoader;
import simulator.month.AbstractTransactionManager;
import simulator.month.StaticTransactionManager;
import simulator.policy.SimPolicy;
import simulator.policy.SimpleSimPolicy;

public class Simulator extends Thread {
	public static final String RT_TIMEOUT_PREVENTIVE = "PREVENTIVE";
	public static final String RT_TIMEOUT_PENALIZATIVE = "PENALIZATIVE";
	public static final String RT_NONE = "NONE";
	public static final int DEFAULT_RT_TIMEOUT_TIME = 500;
	// ------------------------------
	// ------------------------------
	private SOptions opts;
	private int timeOfMonth;
	private int timeOfRest;
	private int rounds;
	private boolean hasInitiator;
	private AbstractTransactionManager tManager;
	private AbstractAgent agent;
	private AbstractDataLoader dataLoader;
	private SimPolicy simPolicy;
	private DBManager db;
	private String rtoType;
	private int rtTimeoutTime;

	public Simulator(String confAddress) throws Exception {
		SConfigParser confParser = new SConfigParser(confAddress);
		opts = confParser.parse();
		timeOfMonth = opts.getInt("TIME_OF_MONTH", 1000);

		rounds = opts.getInt("ROUNDS", 10);
		hasInitiator = opts.getBoolean("HAS_INITIATOR", true);

		String testDataLoaderName = opts.getString("TEST_DATA_LOADER.NAME", "");
		if (testDataLoaderName.equals("")) {
			throw new Exception(
					"Test data loader is not set, Set TEST_DATA_LOADER.NAME!!!");
		}
		SOptions tdlOpts = opts.getOptions(testDataLoaderName);
		String tdlPath = opts.getString(testDataLoaderName + ".CLASSPATH",
				FileDataLoader.class.getCanonicalName());
		dataLoader = getDataLoader(tdlPath, tdlOpts);

		String spName = opts.getString("SCORING_POLICY.NAME", "");
		if (spName.equals("")) {
			throw new Exception(
					"Scoring Policyis not set, Set SCORING_POLICY.NAME!!!");
		}
		SOptions spOpts = opts.getOptions(spName);
		String spPath = opts.getString(spName + ".CLASSPATH",
				SimpleSimPolicy.class.getCanonicalName());
		simPolicy = getScoringPolicy(spPath, spOpts);

		db = new DBManager();

		rtoType = opts.getString("REST_TIME_TIMEOUT_MANAGEMENT",
				RT_TIMEOUT_PREVENTIVE);
		if (!rtoType.equals(RT_NONE)) {
			timeOfRest = opts.getInt("TIME_OF_REST", 500);
		}

	}

	@Override
	public void run() {
		super.run();
		try {
			if (hasInitiator)
				runInitiator();
			for (int i = 0; i < rounds; i++) {
				int totalTransactions = runMonth(i + 1);
				if (i != rounds - 1) {// is not last round
					runRest(i + 1, totalTransactions);
				}
			}
			simPolicy.scoreSimulation(rounds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.insertSim(simPolicy.getScoreBoard(), opts);

	}

	private void runRest(final int month, int total) {
		if (rtoType.equals(RT_NONE)) {
			agent.improve(month);
		} else if (rtoType.equals(RT_TIMEOUT_PENALIZATIVE)) {
			long t1 = System.currentTimeMillis();
			agent.improve(month);
			long time = System.currentTimeMillis() - t1;
			simPolicy.scoreRest(month, time, total, timeOfRest);
		} else if (rtoType.equals(RT_TIMEOUT_PREVENTIVE)) {
			Callable<Boolean> run = new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					agent.improve(month);
					return true;
				}
			};

			RunnableFuture<Boolean> rf = new FutureTask<Boolean>(run);
			ExecutorService service = Executors.newSingleThreadExecutor();
			service.execute(rf);
			Boolean done = false;
			try {
				done = (Boolean) rf.get(rtTimeoutTime, TimeUnit.MILLISECONDS);
			} catch (TimeoutException ex) {
				// timed out. Try to stop the code if possible.
				rf.cancel(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			simPolicy.scoreRest(month, done, total, rtTimeoutTime);
		}
	}

	private int runMonth(int month) throws Exception {
		AbstractTransactionManager atm = null;
		String tmName = opts.getString("TRANSACTION_MANAGER.NAME", "");
		if (tmName.equals("")) {
			throw new Exception(
					"Transaction Manager is not defined... Set TRANSACTION_MANAGER.NAME!");
		}
		String tmPath = opts.getString(tmName + ".CLASSPATH",
				StaticTransactionManager.class.getCanonicalName());
		SOptions tmOpts = opts.getOptions(tmName);

		try {
			atm = (AbstractTransactionManager) Class.forName(tmPath)
					.getConstructor(new Class[] { SOptions.class })
					.newInstance(new Object[] { tmOpts });

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		int rounds = atm.nextTransactionListSize();

		long passed = 0;
		long t1 = 0;
		for (int i = 0; i < rounds; i++) {
			AbstractInstance ins = dataLoader.readNext();
			AbstractInstance result = ins.getUnLabeledClone();
			t1 = System.currentTimeMillis();
			agent.classify(result);
			// check time
			long time = System.currentTimeMillis() - t1;
			// correct if needed
			correctIfNeeded(result, ins, i, rounds);
			// save as transaction
			db.insertTransaction(result, month);
			// update score
			simPolicy.scoreResult(ins, result, time);
			// check remaining time
			passed += time;
			if (passed >= timeOfMonth)
				break;
		}
		simPolicy.scoreMonth(rounds);
		return rounds;
	}

	private void correctIfNeeded(AbstractInstance result, AbstractInstance ins,
			int index, int total) {
		if (simPolicy.needsCorrection(ins, result, index, total)) {
			result.setTrueLabel(ins.getLabel());
		}
	}

	private void runInitiator() throws Exception {
		String initiatorName = opts.getString("INITIATOR_NAME", "");
		if (initiatorName.equals("")) {
			throw new Exception(
					"Initiator is not defined.. make sure too set INITIATOR.NAME and proper options");
		}
		SOptions dataLoaderOpts = opts
				.getOptions(initiatorName + ".DATALOADER");
		AbstractDataLoader dataLoader = getDataLoader(opts.getString(
				initiatorName + ".DATALOADER.CLASSPATH",
				FileDataLoader.class.getCanonicalName()), dataLoaderOpts);

		Instances train = dataLoader.readAll();
		agent.train(train);
	}

	public SOptions getOpts() {
		return opts;
	}

	public AbstractDataLoader getDataLoader(String classPath, SOptions Opts) {
		AbstractDataLoader dataLoader = null;

		try {
			dataLoader = (AbstractDataLoader) Class.forName(classPath)
					.getConstructor(new Class[] { SOptions.class })
					.newInstance(new Object[] { Opts });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return dataLoader;
	}

	public SimPolicy getScoringPolicy(String classPath, SOptions Opts) {
		SimPolicy sp = null;

		try {
			sp = (SimPolicy) Class.forName(classPath)
					.getConstructor(new Class[] { SOptions.class })
					.newInstance(new Object[] { Opts });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sp;
	}
}
