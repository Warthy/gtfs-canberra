package fr.isep.tp6;

public class Edge {
	String from;
	String to;

	public Edge(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public Edge(Node from, Node to) {
		this.from = from.getId();
		this.to = to.getId();
	}

}
