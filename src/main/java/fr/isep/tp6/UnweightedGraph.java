package fr.isep.tp6;


public class UnweightedGraph extends Graph {

	public UnweightedGraph(String GTFSDirectory) {
		super(GTFSDirectory);
	}

	public Graph addEdge(Node from, Node to) {
		from.addEdge(new Edge(from, to));
		to.addEdge(new Edge(to, from));
		return this;
	}

	@Override
	public Graph removeEdge(String from, String to) {
		vertices.get(from).getEdges().remove(to);
		vertices.get(to).getEdges().remove(from);
		return this;
	}

	public Graph removeEdge(Edge edge) {
		vertices.get(edge.from).getEdges().remove(edge.to);
		vertices.get(edge.to).getEdges().remove(edge.from);
		return this;
	}
}
