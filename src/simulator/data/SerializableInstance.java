package simulator.data;

import java.io.Serializable;

public class SerializableInstance extends AbstractInstance implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7168176044720381304L;
	private int labelIndex;
	private Serializable[] attributes;
	private Object trueLabel;

	public SerializableInstance(Serializable[] attributes, int labelIndex) {
		this.attributes = attributes;
		this.labelIndex = labelIndex;
		this.trueLabel = attributes[labelIndex];
	}

	@Override
	public boolean isLabeld() {
		return attributes[labelIndex] != null;
	}

	@Override
	public Object getLabel() {
		return attributes[labelIndex];
	}

	@Override
	public AbstractInstance getUnLabeledClone() {
		Serializable[] atts = new Serializable[attributes.length];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = attributes[i];
			if (i == labelIndex) {
				atts[i] = null;
			}
		}
		return new SerializableInstance(atts, labelIndex);
	}

	@Override
	public Object getTrueLabel() {
		return trueLabel;
	}

	@Override
	public void setTrueLabel(Object label) {
		this.trueLabel = label;
	}

}
