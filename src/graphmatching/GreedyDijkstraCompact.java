/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmatching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


class GreedyDijkstraCompact extends Matcher {
	private static final boolean DBG = false;
	private Double[] distance;
	private Integer[] owner;
	private HashMap<Integer, Integer> limitMap;
	
	@Override
	String acronym() {return "CG_C";}

	@Override
	Integer[] solve(Graph G, ArrayList<Integer> centers) {

		//size of graph
		Integer size = G.numNodes;
		Integer centerSize = centers.size();
		
		// shortest known distance from "s"
		distance = new Double[size];
		owner = new Integer[size];
		limitMap = getQuotas(G.numNodes, centers);
		
		// fill distance with max
		Arrays.fill(distance, Double.MAX_VALUE);
		Arrays.fill(owner, -1);
				
		//HashMap contains center and the returned value from dijkstra
		HashMap<Integer, ReturnObj> map = new HashMap<>();

		//step 1
		for(int i=0; i<centerSize; i++){
			Integer center = centers.get(i);
			map.put(center, dijkstra(G, center, limitMap.get(center)));
		}
		//step 2
		while(true){
			HashMap<Integer, Integer> more = needMore(owner, centers);
			if(more.isEmpty()) break;
			else{
				for(Integer center: more.keySet()){
					map.put(center, dijkstra2(G, map.get(center), limitMap.get(center), more.get(center)));
				}
			}
		}
		return owner;
	}

	private ReturnObj dijkstra(Graph G, Integer source, Integer limit){
		//size of graph
		int n = G.numNodes; 
		// shortest known distance from "s"
		CompactArray<Double> distanceDijk = new CompactArray<>(n);
		// check whether vertices are visited
		CompactSet visited = new CompactSet(n);
		//number of visited nodes = 1 since it involves itself
		Integer visitedNo = 1;
		// fill arrays with information
		//priority queue for Node(vertex, distance) 
		BinaryHeap<Integer> pq = new BinaryHeap<>();

		//start with source vertex
		distance[source] = 0.0;
		if (DBG) System.err.println("match "+source+" to "+source);
		owner[source] = source;
		distanceDijk.put(source, 0.0);
		pq.add(source, distanceDijk.get(source));

		while(!pq.isEmpty() && visitedNo < limit){
			//System.out.println("\nGeneral Distance Array Status: " + Arrays.toString(distance));
			//System.out.print("Queue status: ");
			/*Iterator<Node> it = queue.iterator() ;
			while(it.hasNext() ) {
				System.out.print(it.next() + " ") ;
			}*/
			//System.out.println() ;

			Integer node = pq.extractMin();
			
			//System.out.println("Distance Value: " + distance[node.getValue()]);
			double dist = distanceDijk.get(node);
			if(distance[node] > dist){
				if (DBG) System.err.println("match "+node+" to "+source);
				distance[node] = dist;
				owner[node] = source;
				visitedNo++;
				if(visitedNo > limit) break;
			}
			//System.out.println("Polled node: " + node);
			if(!visited.contains(node)){
				visited.add(node);
				for(Edge e: G.adjList.get(node)){
					Integer nextVertex;
					if(e.first.id != node) nextVertex = e.first.id;
					else nextVertex = e.second.id;
					if(!visited.contains(nextVertex)){
						double newDist = distanceDijk.get(node) + e.dist();
						if(!distanceDijk.containsKey(nextVertex) ||
								distanceDijk.get(nextVertex) > newDist) {
							distanceDijk.put(nextVertex, newDist);
							if(pq.contains(nextVertex)){
								pq.reduceKey(nextVertex, distanceDijk.get(nextVertex));
							} else {
								pq.add(nextVertex, distanceDijk.get(nextVertex));
							}
						}
					}
				}
			}
		}
		ReturnObj result = new ReturnObj(source, distanceDijk, visited, pq);
		//System.out.println(result);
		return result;
	}

	private ReturnObj dijkstra2(Graph G, ReturnObj info, Integer limit, Integer visitedNumber){
		//	source of the dijksta
		Integer source = info.center;
		// shortest known distance from "s"
		CompactArray<Double> distanceDijk = info.distance;
		// check whether vertices are visited
		CompactSet visited = info.visited;
		BinaryHeap<Integer> pq = info.queue;

		Integer visitedNo = visitedNumber;
		while(!pq.isEmpty() && visitedNo < limit){
			//System.out.println("\nGeneral Distance Array Status: " + Arrays.toString(distance));
			Integer node = pq.extractMin();
			double dist = distanceDijk.get(node);
			if(distance[node] > dist){
				if (DBG) System.err.println("match "+node+" to "+source);
				distance[node] = dist;
				owner[node] = source;
				visitedNo++;
				if(visitedNo > limit) break;
			}
			//System.out.println("Polled node: " + node);
			if(!visited.contains(node)){
				visited.add(node);
				for(Edge e: G.adjList.get(node)){
					Integer nextVertex;
					if(e.first.id != node) nextVertex = e.first.id;
					else nextVertex = e.second.id;
					if(!visited.contains(nextVertex)){
						double newDist = distanceDijk.get(node) + e.dist();
						if(!distanceDijk.containsKey(nextVertex) ||
								distanceDijk.get(nextVertex) > newDist){
							distanceDijk.put(nextVertex, newDist);
							if(pq.contains(nextVertex)){
								pq.reduceKey(nextVertex, distanceDijk.get(nextVertex));
							} else {
								pq.add(nextVertex, distanceDijk.get(nextVertex));
							}
						}
					}
				}
			}
		}
		ReturnObj result = new ReturnObj(source, distanceDijk, visited, pq);
		return result;
	}

	private HashMap<Integer, Integer> needMore(Integer[] owner, ArrayList<Integer> centers){

		int len = owner.length;
		//HashMap(centerNo, occurrence)
		HashMap<Integer, Integer> occurrenceMap = new HashMap<>();
		//System.out.println(limitMap);
		for (int i = 0; i < len; i++){
			int cur = owner[i];
			if(cur != -1){
				if (!occurrenceMap.containsKey(cur)){
					occurrenceMap.put(cur, 1);
				} else {
					int value = occurrenceMap.get(cur);
					occurrenceMap.put(cur, value + 1);	
				}
			}
		}
		HashMap<Integer, Integer> res = new HashMap<>();

		// iterate through HashMap and check whether every center
		//has necessary number of nodes
		for (Map.Entry<Integer, Integer> entry : occurrenceMap.entrySet()) {
			Integer value = entry.getValue();
			if(!value.equals(limitMap.get(entry.getKey()))) {
				Integer key = entry.getKey();
				res.put(key, value);
			}
		}
		return res;
	}

	private class ReturnObj{
		private Integer center;
		private CompactArray<Double> distance;
		private CompactSet visited;
		private BinaryHeap<Integer> queue;

		ReturnObj(Integer center, CompactArray<Double> distance, CompactSet visited, BinaryHeap<Integer> pq) {
			this.center = center;
			this.distance = distance;
			this.visited = visited;
			this.queue = pq;
		}

		@Override
		public String toString() {
			return "ReturnObj [center=" + center + ", distance=" + distance + ", visited="
					+ visited + ", queue=" + queue + "]";
		}
	}
}

