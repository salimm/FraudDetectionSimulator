package simulator.data.loaders;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.data.Instances;

public abstract class AbstractDataLoader {

	private final SOptions opts;

	public AbstractDataLoader(SOptions opts) {
		this.opts = opts;
	}

	public abstract int getTotalInstances();

	public abstract AbstractInstance readNext();

	public abstract Instances readAll();

}
