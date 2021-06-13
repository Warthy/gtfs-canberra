package fr.isep.tp6;


import org.jgrapht.alg.util.Pair;

import java.util.Map;

public class Main {
	public static void main(String[] args) {
		UnweightedGraph uGraph = new UnweightedGraph("tp_assets/gtfs");
		WeightedGraph  wGraph = new WeightedGraph("tp_assets/gtfs");


		Map<Pair<String, String>, Integer> yes =  wGraph.getAllEdgesBetweenness();


		System.out.println("Yo Louis");
	}
}
