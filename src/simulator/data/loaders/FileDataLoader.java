package simulator.data.loaders;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.data.Instances;

public class FileDataLoader extends AbstractDataLoader{
	private String fileAddress;
	private int bufferSize;
	private int total;
	public FileDataLoader(SOptions opts) {
		super(opts);
	}

	@Override
	public int getTotalInstances() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AbstractInstance readNext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instances readAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
