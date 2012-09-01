package agent;

import simulator.data.AbstractInstance;
import simulator.data.Instances;

public abstract class AbstractAgent {

	public abstract String getAgentName();

	public abstract void classify(AbstractInstance ins);
	
	public abstract void train(Instances train);
}
