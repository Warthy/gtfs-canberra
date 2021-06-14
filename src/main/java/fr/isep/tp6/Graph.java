package fr.isep.tp6;

import org.jgrapht.alg.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class Graph {

	Map<String, Node> vertices = new HashMap<>();

	public Graph(){

	}

	public Graph(String GTFSDirectory) {
		BufferedReader reader;

		try {
			// GET ALL NODES/STOPS
			reader = new BufferedReader(new FileReader(GTFSDirectory + "/stops.txt"));
			reader.readLine(); // Skip headers

			String line = reader.readLine();
			while (line != null) {
				String[] items = line.split(",");   // edge is always 8 item long

				vertices.put(items[0], new Node(items[0], Double.parseDouble(items[3]), Double.parseDouble(items[4])));

				line = reader.readLine();                // read next line
			}
			reader.close();


			// GET ALL EDGES
			reader = new BufferedReader(new FileReader(GTFSDirectory + "/stop_times.txt"));
			reader.readLine(); // Skip headers

			String line1 = reader.readLine();
			String line2 = reader.readLine();
			while (line1 != null && line2 != null) {
				String[] items1 = line1.split(",");   // edge is always 9 item long
				String[] items2 = line2.split(",");

				if (items1[0].equals(items2[0])) {
					Node from = vertices.get(items1[3]);
					Node to = vertices.get(items2[3]);

					addEdge(from, to);
				}

				line1 = line2;
				line2 = reader.readLine();                // read next line
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	abstract public void addEdge(Node from, Node to);

	abstract public void removeEdge(String from, String to);

	public List<List<String>> getAllShortestPaths() {
		Set<Pair<String, String>> allPossiblePairs = new HashSet<>();
		List<List<String>> shortestPaths = new ArrayList<>();


		for (String n1 : vertices.keySet()) {
			Dijkstra dijkstra = new Dijkstra((WeightedGraph) this, n1);
			for (String n2 : vertices.keySet()) {
				if (!allPossiblePairs.contains(new Pair<>(n1, n2)) & !allPossiblePairs.contains(new Pair<>(n2, n1))) {
					shortestPaths.add(dijkstra.getShortesPath(n2));
					allPossiblePairs.add(new Pair<>(n1, n2));
				}
			}
		}

		return shortestPaths;
	}


	public Map<Pair<String, String>, Integer> getAllEdgesBetweenness() {
		Map<Pair<String, String>, Integer> edgesBetweenness = new HashMap<>();

		getAllShortestPaths().forEach(sp -> {
			for (int i = 0; i < sp.size()-1; i += 2) {
				Pair<String, String> p1 = new Pair<>(sp.get(i), sp.get(i + 1));
				Pair<String, String> p2 = new Pair<>(sp.get(i + 1), sp.get(i));

				if (edgesBetweenness.containsKey(p1)) {
					edgesBetweenness.put(p1, edgesBetweenness.get(p1) + 1);
				} else if (edgesBetweenness.containsKey(p2)) {
					edgesBetweenness.put(p2, edgesBetweenness.get(p2) + 1);
				} else {
					edgesBetweenness.put(p1, 1);
				}
			}
		});

		return sortByEdgeBetweenness(edgesBetweenness);
	}

	private Map<Pair<String, String>, Integer> sortByEdgeBetweenness(Map<Pair<String, String>, Integer> edgeBetweenness){
		List<Map.Entry<Pair<String, String>, Integer>> edgesBTW = new ArrayList<>(edgeBetweenness.entrySet());
		edgesBTW.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

		Map<Pair<String, String>, Integer> res = new LinkedHashMap<>();
		for(Map.Entry<Pair<String, String>, Integer> set: edgesBTW){
			res.put(set.getKey(), set.getValue());
		}

		return res;
	}

}
