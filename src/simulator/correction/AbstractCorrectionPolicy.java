package simulator.correction;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;

public abstract class AbstractCorrectionPolicy {

	public AbstractCorrectionPolicy(SOptions opts) {
		applyOpts(opts);
	}

	protected abstract void applyOpts(SOptions opts);

	public abstract boolean needsCorrection(AbstractInstance ins,AbstractInstance result);
}
