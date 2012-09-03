package simulator.policy;

import java.util.Random;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.month.AbstractTransactionManager;

public class SimpleSimPolicy extends SimPolicy {

	private Random random;
	// --------------- Types
	private static final String CT_PROBABILITY = "PROBABILITY";
	private static final String CT_RANDOM_SUBSET = "RANDOM_SUBSET";
	private static final String CT_ALL = "ALL";
	private static final String CT_NONE = "NONE";

	private String ctType;

	// --------- RANDOM SOBSET
	public static final float DEFAULT_RANDOM_SUBSET_PORTION = (float) 0.2;
	private float rsPortion;
	private int subsetSize;

	// --------- PROBABILITY
	private float probThreshold;
	public static final float DEFAULT_PROBILITY_THRESHOLD = (float) 0.5;

	private int roundsCorrects;
	private int roundsTotalAnswered;
	private long roundsTotalTime = 0;

	public SimpleSimPolicy(SOptions opts) {
		super(opts);
	}

	@Override
	protected void applyOpts(SOptions opts) {
		ctType = opts.getString("CORRECTION_TYPE", CT_PROBABILITY);
		if (ctType.equals(CT_PROBABILITY)) {
			probThreshold = opts.getFloat("CORRECTION_PROBABILITY",
					DEFAULT_PROBILITY_THRESHOLD);
		} else if (ctType.equals(CT_RANDOM_SUBSET)) {
			rsPortion = opts.getFloat("CORRECTION_PORTION", DEFAULT_RANDOM_SUBSET_PORTION);
		}
		
	}

	@Override
	public void scoreResult(AbstractInstance source, AbstractInstance result,
			long time) {
		if (source.getLabel().equals(result.getLabel())) {
			roundsCorrects++;
		}
		roundsTotalAnswered++;
		roundsTotalTime += time;
	}

	@Override
	public void scoreMonth(int totalInstances) {
		scoreBoard.addRoundScore(new double[] {
				roundsCorrects / roundsTotalAnswered,
				roundsTotalTime / roundsTotalAnswered,
				roundsTotalAnswered / totalInstances });
	}

	@Override
	public void scoreSimulation(int totalRounds) {

	}

	@Override
	protected ScoreBoard createScoreBoard() {
		return new ScoreBoard(new String[] { "Correct Percent",
				"Time Per Transaction", "Percent Answered" });
	}

	@Override
	public void scoreRest(int month, long time, int total, int timeout) {

	}

	@Override
	public void scoreRest(int month, Boolean done, int total, int timeout) {

	}

	@Override
	protected void initiateMonth(int total) {
	}

	@Override
	public boolean needsCorrection(AbstractInstance ins,
			AbstractInstance result, int index, int total) {
		if (ctType.equals(CT_RANDOM_SUBSET)) {
			if ((random.nextFloat() * Integer.MAX_VALUE % (total - index)) < subsetSize) {
				subsetSize--;
				return true;
			}
			return false;
		} else if (ctType.equals(CT_PROBABILITY)) {
			return random.nextFloat() < probThreshold;
		} else if (ctType.equals(CT_ALL)) {
			return true;

		} else if (ctType.equals(CT_NONE)) {
			return false;
		}
		return false;
	}

}
