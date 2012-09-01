package simulator.data;

import java.util.ArrayList;

public class Instances<E extends AbstractInstance> {
	private ArrayList<E> instances;

	public Instances() {
		setInstances(new ArrayList<E>());
	}

	public void addInstance(E instance) {
		instances.add(instance);
	}

	public ArrayList<E> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<E> instances) {
		this.instances = instances;
	}

}
