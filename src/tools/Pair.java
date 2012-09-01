package tools;
/**
 *
 * A simple useful class for programming
 * @author Salim Malakouti
 *
 * @param <S>
 * @param <T>
 */
public class Pair<S, T> {
	private S s;
	private T t;

	public Pair(S s, T t) {
		this.s = s;
		this.t = t;
	}

	public S getFirst() {
		return s;
	}

	public T getSecond() {
		return t;
	}

	public void setFirst(S s) {
		this.s = s;
	}

	public void setSecond(T t) {
		this.t = t;
	}

}
