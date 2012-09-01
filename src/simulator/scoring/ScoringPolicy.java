package simulator.scoring;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;

public abstract class ScoringPolicy {

	protected final ScoreBoard scoreBoard;

	public ScoringPolicy(SOptions opts) {
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

}
