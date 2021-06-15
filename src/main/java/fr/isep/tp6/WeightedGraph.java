package fr.isep.tp6;


public class WeightedGraph extends Graph {

	public WeightedGraph(boolean directed) {
		super(directed);
	}

	public WeightedGraph(String GTFSDirectory) {
		super(GTFSDirectory);
	}

	public WeightedGraph addEdge(String from, String to, Double weight) {
		vertices.get(from).addEdge(new WeightedEdge(from, to, weight));
		if(!directed)
			vertices.get(to).addEdge(new WeightedEdge(to, from, weight));
		return this;
	}

	public WeightedGraph addEdge(Node from, Node to) {
		from.addEdge(new WeightedEdge(from, to));
		if(!directed)
			to.addEdge(new WeightedEdge(to, from));

		return this;
	}

	@Override
	public Graph removeEdge(String from, String to) {
		vertices.get(from).getEdges().remove(to);
		if(!directed)
			vertices.get(to).getEdges().remove(from);
		return this;
	}

	@Override
	public Graph removeEdge(Edge edge) {
		vertices.get(edge.from).getEdges().remove(edge.to);
		if(!directed)
			vertices.get(edge.to).getEdges().remove(edge.from);
		return this;
	}
}
