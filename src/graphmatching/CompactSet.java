package graphmatching;

import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Nil
 * @param <E>
 */
class CompactSet {
	private int n;
	private HashSet<Integer> small;
	private boolean[] big;
	private int bigMapSize;
	private boolean useSmall;
	private final int threshold;
	private static final double thresholdRatio = 0.1;

	@Override
	public String toString() {
		if (useSmall) return small.toString();
		else return Arrays.toString(big);
	}
	
	CompactSet(int n) {
		this.n = n;
		threshold = 1 + (int) (n*thresholdRatio);
		useSmall = true;
		small = new HashSet<>();
	}
	
	int size() {
		if (useSmall) return small.size();
		return bigMapSize;
	}

	boolean isEmpty() {
		if (useSmall) return small.isEmpty();
		return bigMapSize == 0;
	}

	boolean contains(Integer key) {
		if (useSmall) return small.contains(key);
		return big[key];
	}

	void add(Integer key) {
		if (useSmall) {
			if (small.size() == threshold) {
				moveToBig();
				addToBig(key);
			} else {
				small.add(key);
			}
		} else {
			addToBig(key);
		}
	}
	
	void remove(Integer key) {
		if (useSmall) small.remove(key);
		else removeFromBig(key);
	}
	
	void clear() {
		small = new HashSet<>();
		useSmall = true;
		big = null;
	}

	private void addToBig(Integer key) {
		if (!big[key]) {
			big[key] = true;
			bigMapSize++;			
		}
	}

	private void removeFromBig(Integer key) {
		if (big[key]) {
			big[key] = false;
			bigMapSize--;
		}
	}

	private void moveToBig() {
		useSmall = false;
		big = new boolean[n];
		for (Integer i : small) {
			big[i] = true;
		}
		bigMapSize = small.size();
		small = null;
	}

	/*
	public void putAll(Map<Integer, E> m) {
		throw new RuntimeException("not implemented");
	}

	public Set<Integer> keySet() {
		if (useSmall) return smallMap.keySet();
		throw new RuntimeException("not implemented");
	}

	public Collection<E> values() {
		if (useSmall) return smallMap.values();
		throw new RuntimeException("not implemented");
	}

	public Set<Entry<Integer, E>> entrySet() {
		if (useSmall) return smallMap.entrySet();
		throw new RuntimeException("not implemented");
	}
	*/
}
