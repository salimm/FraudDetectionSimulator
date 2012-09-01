package simulator.month;

import simulator.conf.SOptions;

public abstract class AbstractTransactionManager {
	protected int totalInstances;
	protected final int rounds;

	public AbstractTransactionManager(SOptions opts,int rounds) {
		this.rounds = rounds;
		applyOpts(opts);
	}

	protected abstract void applyOpts(SOptions opts);

	public abstract int nextTransactionListSize();

	public void setTotalSize(int total) {
		this.totalInstances = total;
	}

	public int getTotalInstances() {
		return totalInstances;
	}
}
