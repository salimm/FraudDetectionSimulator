package simulator.data;

import java.io.Serializable;

public abstract class AbstractInstance implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2290455669189575537L;

	/**
	 * If true the instance's label is set;
	 * 
	 * @return
	 */
	public boolean isLabeld(){
		return getLabel()!=null;
	}

	public abstract Object getLabel();

	public abstract AbstractInstance getUnLabeledClone();

	public abstract Object getTrueLabel();

	public boolean hasTrueLabel() {
		return getTrueLabel() != null;
	}
	public abstract void setTrueLabel(Object label);
}
