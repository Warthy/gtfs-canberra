package fr.isep.tp6;


public class WeightedGraph extends Graph {

	public WeightedGraph(String GTFSDirectory) {
		super(GTFSDirectory);
	}

	public void addEdge(Node from, Node to) {
		from.addEdge(new WeightedEdge(from, to));
		to.addEdge(new WeightedEdge(to, from));
	}
}
