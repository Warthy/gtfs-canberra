package fr.isep.tp6;


public class WeightedGraph extends Graph {

	public WeightedGraph() {
		super();
	}

	public WeightedGraph(String GTFSDirectory) {
		super(GTFSDirectory);
	}

	public void addEdge(Node from, Node to) {
		from.addEdge(new WeightedEdge(from, to));
		to.addEdge(new WeightedEdge(to, from));
	}

	@Override
	public void removeEdge(String from, String to) {
		vertices.get(from).getEdges().remove(to);
		vertices.get(to).getEdges().remove(from);
	}
}
