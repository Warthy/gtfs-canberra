package fr.isep.tp6;


public class UnweightedGraph extends Graph {

	public UnweightedGraph(String GTFSDirectory) {
		super(GTFSDirectory);
	}

	public void addEdge(Node from, Node to) {
		from.addEdge(new Edge(from, to));
		to.addEdge(new Edge(to, from));
	}
}
