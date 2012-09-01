package simulator.scoring;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.month.AbstractTransactionManager;

public class SimpleScoringPolicy extends ScoringPolicy {
	private int roundsCorrects;
	private int roundsTotalAnswered;
	private long roundsTotalTime = 0;

	public SimpleScoringPolicy(SOptions opts) {
		super(opts);
	}

	@Override
	protected void applyOpts(SOptions opts) {

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

}
