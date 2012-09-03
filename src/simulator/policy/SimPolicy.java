package simulator.policy;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;

public abstract class SimPolicy {

	protected final ScoreBoard scoreBoard;

	public SimPolicy(SOptions opts) {
		applyOpts(opts);
		scoreBoard = createScoreBoard();
	}

	protected abstract ScoreBoard createScoreBoard();

	protected abstract void applyOpts(SOptions opts);

	public abstract void scoreResult(AbstractInstance source,
			AbstractInstance result, long time);

	public abstract void scoreMonth(int totalInstances);

	public abstract void scoreSimulation(int totalRounds);

	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}

	public abstract void scoreRest(int month, long time, int total, int timeout);

	public abstract void scoreRest(int month, Boolean done, int total,
			int timeout);

	protected abstract void initiateMonth(int total);

	public abstract boolean needsCorrection(AbstractInstance ins,
			AbstractInstance result, int index, int total);

}
