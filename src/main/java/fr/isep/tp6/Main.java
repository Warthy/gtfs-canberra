package fr.isep.tp6;

import java.util.List;
import java.util.Set;

public class Main {
	public static void main(String[] args) {
		WeightedGraph wGraph = new WeightedGraph(false);
		wGraph.vertices.put("1", new Node("1"));
		wGraph.vertices.put("2", new Node("2"));
		wGraph.vertices.put("3", new Node("3"));
		wGraph.vertices.put("4", new Node("4"));
		wGraph.vertices.put("5", new Node("5"));
		wGraph.vertices.put("6", new Node("6"));

		wGraph
				.addEdge("1", "2", 1.0)
				.addEdge("1", "5", 1.0)
				.addEdge("2", "3", 1.0)
				.addEdge("2", "5", 2.0)
				.addEdge("3", "5", 1.0)
				.addEdge("3", "4", 3.0)
				.addEdge("4", "5", 1.0)
				.addEdge("5", "4", 1.0)
				.addEdge("5", "6", 1.0)
				.addEdge("6", "1", 3.0);

		List<Set<String>> a = wGraph.createClusters(2);
		System.out.println(a);
	}
}
