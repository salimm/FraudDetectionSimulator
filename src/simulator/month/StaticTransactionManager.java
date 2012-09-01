package simulator.month;

import simulator.conf.SOptions;
import simulator.data.loaders.AbstractDataLoader;

public class StaticTransactionManager extends AbstractTransactionManager{

	private final AbstractDataLoader dataLoader;

	public StaticTransactionManager(SOptions opts,int rounds,AbstractDataLoader dataLoader) {
		super(opts,rounds);
		this.dataLoader = dataLoader;
	}

	@Override
	protected void applyOpts(SOptions opts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int nextTransactionListSize() {
		return dataLoader.getTotalInstances()/rounds;
	}
	
}
