package graphmatching;


import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nil
 * @param <E>
 */
class SmartMap<E> {
	private int n;
	private HashMap<Integer,E> smallMap;
	private E[] bigMap;
	private int bigMapSize;
	private boolean useSmall;
	private final int threshold;
	private static final double thresholdRatio = 0.1;
	
	SmartMap(int n) {
		this.n = n;
		threshold = 1 + (int) (n*thresholdRatio);
		useSmall = true;
		smallMap = new HashMap<>();
	}	
	
	int size() {
		if (useSmall) return smallMap.size();
		return bigMapSize;
	}

	boolean isEmpty() {
		if (useSmall) return smallMap.isEmpty();
		return bigMapSize == 0;
	}

	boolean containsKey(Integer key) {
		if (useSmall) return smallMap.containsKey(key);
		return bigMap[key] != null;
	}

	E get(Integer key) {
		if (useSmall) return smallMap.get(key);
		return bigMap[key];
	}

	void put(Integer key, E value) {
		if (useSmall) {
			if (smallMap.size() == threshold) {
				moveToBig();
				putInBig(key, value);
			} else {
				smallMap.put(key, value);
			}
		} else {
			putInBig(key, value);
		}
	}
	
	void remove(Integer key) {
		if (useSmall) smallMap.remove(key);
		else removeFromBig(key);
	}
	
	public void clear() {
		smallMap = new HashMap<>();
		useSmall = true;
		bigMap = null;
	}
	
	private void putInBig(Integer key, E value) {
		if (bigMap[key] == null) bigMapSize++;
		bigMap[key] = value;
	}

	private void removeFromBig(Integer key) {
		if (bigMap[key] != null) {
			bigMap[key] = null;
			bigMapSize--;
		}
	}

	private void moveToBig() {
		useSmall = false;
		bigMap = (E[]) new Object[n];
		for (Map.Entry<Integer, E> entry : smallMap.entrySet()) {
			bigMap[entry.getKey()] = entry.getValue();
		}
		bigMapSize = smallMap.size();
	}

	/*
	public boolean containsValue(E value) {
		if (useSmall) return smallMap.containsValue(value);
		throw new RuntimeException("not implemented");
	}

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
