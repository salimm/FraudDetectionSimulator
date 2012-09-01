package simulator;

import java.lang.reflect.InvocationTargetException;

import agent.AbstractAgent;

import simulator.conf.SConfigParser;
import simulator.conf.SOptions;
import simulator.conf.SSingleOpt;
import simulator.correction.AbstractCorrectionPolicy;
import simulator.data.AbstractInstance;
import simulator.data.Instances;
import simulator.data.database.DBManager;
import simulator.data.loaders.AbstractDataLoader;
import simulator.data.loaders.FileDataLoader;
import simulator.month.AbstractTransactionManager;
import simulator.month.StaticTransactionManager;
import simulator.scoring.ScoringPolicy;
import simulator.scoring.SimpleScoringPolicy;

public class Simulator extends Thread {
	private SOptions opts;
	private int timeOfMonth;
	private int timeOfRest;
	private int rounds;
	private boolean hasInitiator;
	private AbstractTransactionManager tManager;
	private AbstractAgent agent;
	private AbstractDataLoader dataLoader;
	private ScoringPolicy scoringPolicy;
	private DBManager db;
	private AbstractCorrectionPolicy cPolicy;

	public Simulator(String confAddress) throws Exception {
		SConfigParser confParser = new SConfigParser(confAddress);
		opts = confParser.parse();
		timeOfMonth = opts.getInt("TIME_OF_MONTH", 1000);
		timeOfRest = opts.getInt("TIME_OF_REST", 1000);
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
				SimpleScoringPolicy.class.getCanonicalName());
		scoringPolicy = getScoringPolicy(spPath, spOpts);
		
		db= new DBManager();

	}

	@Override
	public void run() {
		super.run();
		if (hasInitiator)
			runInitiator();
		for (int i = 0; i < rounds; i++) {
			runMonth(i);
			if (i != rounds - 1) {// is not last round
				runRest();
			}
		}

	}

	private void runMonth(int month) throws Exception {
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
			
			// correct if needed
			correctIfNeeded(result,ins);
			// save as transaction
			db.insertTransaction(result, month); 
			// check time
			long time = System.currentTimeMillis() - t1;
			// update score
			scoringPolicy.scoreResult(ins, result, time);
			//check remaining time
			passed += time;
			if (passed >= timeOfMonth)
				break;
		}
		scoringPolicy.scoreMonth(rounds);

	}

	private void correctIfNeeded(AbstractInstance result, AbstractInstance ins) {
		if(cPolicy.needsCorrection(ins,result)){
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

	public ScoringPolicy getScoringPolicy(String classPath, SOptions Opts) {
		ScoringPolicy sp = null;

		try {
			sp = (ScoringPolicy) Class.forName(classPath)
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
