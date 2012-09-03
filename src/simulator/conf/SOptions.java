package simulator.conf;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.text.html.Option;

public class SOptions implements SOptInterface {

	private String optName;
	private HashMap<String, SOptInterface> list;

	public SOptions(String optName) {
		list = new HashMap<String, SOptInterface>();
	}

	public void addOpt(String name, String val) {
		name = name.toUpperCase();
		if (name.indexOf("\\.") != -1) {
			String current = name.substring(0, name.indexOf(("\\.")));
			String rest = name.substring(0,
					name.indexOf(("\\.") + 1, name.length()));
			SOptInterface tmp = list.get(current);
			if (tmp == null)
				tmp = new SOptions(current);
			((SOptions) tmp).addOpt(rest, val);
		}
		list.put(name, new SSingleOpt(name, val));
	}

	public SOptInterface getOpt(String name) {
		name = name.toUpperCase();
		if (name.indexOf("\\.") != -1) {
			String current = name.substring(0, name.indexOf(("\\.")));
			String rest = name.substring(0,
					name.indexOf(("\\.") + 1, name.length()));
			SOptInterface tmp = list.get(current);
			if (tmp == null)
				return null;
			if (!(tmp instanceof SOptions))
				return null;
			return ((SOptions) tmp).getOpt(rest);
		}
		return list.get(name);

	}

	public SOptions getOptions(String name) {
		SOptInterface opt = getOpt(name);
		if (opt == null)
			return null;
		return (SOptions) opt;
	}

	public int getInt(String name, int def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Integer(((SSingleOpt) val).getValue().trim());
	}

	public double getDouble(String name, double def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Double(((SSingleOpt) val).getValue().trim());
	}

	public float getFloat(String name, float def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Float(((SSingleOpt) val).getValue().trim());
	}

	public Long getLong(String name, long def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Long(((SSingleOpt) val).getValue().trim());
	}

	public String getString(String name, String def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return ((SSingleOpt) val).getValue();
	}

	public short getShort(String name, short def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Short(((SSingleOpt) val).getValue());
	}

	public byte getByte(String name, byte def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Byte(((SSingleOpt) val).getValue());
	}

	public boolean getBoolean(String name, boolean def) {
		SOptInterface val = getOpt(name);
		if (val == null)
			return def;
		else
			return new Boolean(((SSingleOpt) val).getValue());
	}

	public Collection<SOptInterface> getAllOpts() {
		return list.values();
	}

	public String getOptName() {
		return optName;
	}

}
