package fr.isep.tp6;

import java.util.HashMap;
import java.util.Map;

public class Node {
	private final String id;
	private final Double lng;
	private final Double lat;

	private Map<String, Edge> edges = new HashMap<>();


	public Node(String id) {
		this.id = id;
		this.lng = null;
		this.lat = null;
	}

	public Node(String id, Double lat, Double lng) {
		this.id = id;
		this.lng = lng;
		this.lat = lat;
	}

	public void addEdge(Edge e){
		edges.putIfAbsent(e.to, e);
	}

	public void removeEdge(Edge e){
		edges.remove(e.to);
	}

	public String getId() {
		return id;
	}

	public Double getLng() {
		return lng;
	}

	public Double getLat() {
		return lat;
	}

	public Map<String, Edge> getEdges() {
		return edges;
	}

	public void setEdges(Map<String, Edge> edges) {
		this.edges = edges;
	}
}
