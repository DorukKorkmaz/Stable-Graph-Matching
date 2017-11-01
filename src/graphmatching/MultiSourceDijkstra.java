package graphmatching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;


public class MultiSourceDijkstra extends Matcher{

	@Override
	Integer[] solve(Graph G, ArrayList<Integer> centers) {
		// TODO Auto-generated method stub

		//size of graph
		int size = G.numNodes; 

		//number of nodes required for each center
		Integer limit = size / centers.size();
				
		//get adjacencylist info of graph
		ArrayList<ArrayList<Edge>> list = G.adjList;

		// shortest known distance from "s"
		Double[] distance = new Double[size];

		// array showing the owner of each node
		Integer[] owner = new Integer[size];
		

		// fill arrays with information
		Arrays.fill(distance, Double.MAX_VALUE);
		Arrays.fill(owner, -1);
		
		//priority queue for Node(vertex, distance) 
		PriorityQueue<Node> queue = new PriorityQueue<>();

		//HashMap containing <Integer,Center>
		HashMap<Integer,Center> centerMap = new HashMap<Integer,Center>();
		
		//start with source vertices
		for(Integer source: centers){
			distance[source] = 0.0;
			owner[source] = source;
			Center c = new Center(source, size);
			c.getDistance()[source] = 0.0;
			centerMap.put(source, c);
			queue.add(new Node(source, source, distance[source]));
		}
		while(!queue.isEmpty()){
			Node node = queue.poll();
			/*System.out.println(Arrays.toString(distance));
			System.out.println(Arrays.toString(owner));
			System.out.println("Polled Node: " + node);
			System.out.println("Queue: " + queue.toString());
			System.out.println("Map: " + centerMap);
			System.out.println();*/
			if(distance[node.getVertex()] > centerMap.get(node.getCenter()).getDistance()[node.getVertex()] && centerMap.get(node.getCenter()).getVisitedNo() < limit){
				distance[node.getVertex()] = centerMap.get(node.getCenter()).getDistance()[node.getVertex()];
				owner[node.getVertex()] = node.getCenter();
				centerMap.get(node.getCenter()).incrementVisitedNo();	
			}
			
			if(centerMap.get(node.getCenter()).getVisitedNo() < limit && centerMap.get(node.getCenter()).getVisited()[node.getVertex()] == false){
				centerMap.get(node.getCenter()).getVisited()[node.getVertex()] = true;
				Integer cur = node.getVertex();
				ArrayList<Edge> curEdgeList = list.get(cur);
				for(Edge e: curEdgeList){
					Integer nextVertex;
					if(e.first.id != cur) nextVertex = e.first.id;
					else{ nextVertex = e.second.id;}
					if(centerMap.get(node.getCenter()).getDistance()[nextVertex] >= centerMap.get(node.getCenter()).getDistance()[cur] + e.dist()){
						centerMap.get(node.getCenter()).getDistance()[nextVertex] = centerMap.get(node.getCenter()).getDistance()[cur] + e.dist();
						queue.add(new Node(node.getCenter(), nextVertex, centerMap.get(node.getCenter()).getDistance()[nextVertex]));
					}
				}
			}

		}
		
		
		System.out.println(Arrays.toString(distance));
		System.out.println(Arrays.toString(owner));
		return owner;
	}

	@Override
	String acronym() {
		return "CG2";
	}


	class Node implements Comparable<Node>{
		private Integer center;
		private Integer vertex;
		private Double distance;

		public Node(Integer center, Integer vertex, Double distance) {
			super();
			this.center = center;
			this.vertex = vertex;
			this.distance = distance;
		}

		public Integer getCenter() {
			return center;
		}

		public void setCenter(Integer center) {
			this.center = center;
		}

		public Integer getVertex() {
			return vertex;
		}

		public void setVertex(Integer vertex) {
			this.vertex = vertex;
		}

		public Double getDistance() {
			return distance;
		}

		public void setDistance(Double distance) {
			this.distance = distance;
		}

		
		@Override
		public String toString() {
			return "Node [center=" + center + ", vertex=" + vertex + ", distance=" + distance + "]";
		}

		@Override
		public int compareTo(Node o) {
			// TODO Auto-generated method stub
			if(this.distance <= o.distance) return -1;
			else{
				return 1;
			}
		}

	}

	class Center{
		private Integer center;
		private Double[] distance;
		private Boolean[] visited;
		private Integer visitedNo;
		
		public Center(Integer center, Integer numberOfNodes) {
			super();
			this.center = center;
			this.distance = new Double[numberOfNodes];
			this.visited = new Boolean[numberOfNodes];
			this.visitedNo = 1;
			
			Arrays.fill(distance, Double.MAX_VALUE);
			Arrays.fill(visited, false);
		}

		public Integer getCenter() {
			return center;
		}

		public void setCenter(Integer center) {
			this.center = center;
		}

		public Double[] getDistance() {
			return distance;
		}

		public void setDistance(Double[] distance) {
			this.distance = distance;
		}

		public Boolean[] getVisited() {
			return visited;
		}

		public void setVisited(Boolean[] visited) {
			this.visited = visited;
		}
		
		public Integer getVisitedNo() {
			return visitedNo;
		}

		public void setVisitedNo(Integer visitedNo) {
			this.visitedNo = visitedNo;
		}

		public void incrementVisitedNo(){
			this.visitedNo++;
		}
		@Override
		public String toString() {
			return "Center [center=" + center + ",  visitedNo=" + visitedNo + "]";
		}
		
		
	}
}
