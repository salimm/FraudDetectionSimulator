package simulator.scoring;

import java.io.Serializable;
import java.util.ArrayList;

public class ScoreBoard implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7392671352191331133L;
	protected ArrayList<double[]> roundScores;
	protected String[] scoreLabels;

	public ScoreBoard(String[] scoreLabels) {
		this.scoreLabels = scoreLabels;
		roundScores = new ArrayList<double[]>();
	}

	public void addRoundScore(double[] scores) {
		roundScores.add(scores);
	}

	public double[] getRound(int index) {
		return roundScores.get(index);
	}

	public String[] getScoreLabels() {
		return scoreLabels;
	}

}
