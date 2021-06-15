package fr.isep.tp6;

import org.jgrapht.alg.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Graph {

	Map<String, Node> vertices = new HashMap<>();
	Boolean directed = false;

	public Graph(boolean directed) {
		this.directed = directed;
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

	abstract public Graph addEdge(Node from, Node to);

	abstract public Graph removeEdge(String from, String to);

	abstract public Graph removeEdge(Edge edge);

	public static Double getPathCost(List<WeightedEdge> path) {
		if (path == null || path.size() == 0)
			return Double.MAX_VALUE;

		Double cost = 0.0;
		for (WeightedEdge e : path)
			cost += e.weight;

		return cost;
	}

	public Map<Pair<String, String>, List<WeightedEdge>> findAllShortestPathsFromStart(String start) {
		Dijkstra dijkstra = new Dijkstra((WeightedGraph) this, start);
		Map<Pair<String, String>, List<WeightedEdge>> shortestPaths = new HashMap<>();

		for (String node : vertices.keySet()) {
			if (!start.equals(node)) {
				shortestPaths.put(
						new Pair<>(start, node),
						dijkstra.getShortesPath(node)
				);
			}
		}

		return shortestPaths;
	}

	public Map<Pair<String, String>, List<WeightedEdge>> getAllShortestPaths() {
		Set<Pair<String, String>> allPossiblePairs = new HashSet<>();
		Map<Pair<String, String>, List<WeightedEdge>> shortestPaths = new HashMap<>();

		for (String n1 : vertices.keySet()) {
			Dijkstra dijkstra = new Dijkstra((WeightedGraph) this, n1);
			for (String n2 : vertices.keySet()) {
				if (!n2.equals(n1) && !allPossiblePairs.contains(new Pair<>(n1, n2)) && !allPossiblePairs.contains(new Pair<>(n2, n1))) {
					List<WeightedEdge> path = dijkstra.getShortesPath(n2);

					shortestPaths.put(new Pair<>(n1, n2), path);
					allPossiblePairs.add(new Pair<>(n1, n2));
				}
			}
		}

		return shortestPaths;
	}


	public Map<Pair<String, String>, Integer> getAllEdgesBetweenness() {
		Map<Pair<String, String>, Integer> edgesBetweenness = new HashMap<>();
		getAllShortestPaths().values().forEach(sp -> {
			for (WeightedEdge edge : sp) {
				Pair<String, String> p1 = new Pair<>(edge.from, edge.to);
				Pair<String, String> p2 = new Pair<>(edge.to, edge.from);

				if (edgesBetweenness.containsKey(p1))
					edgesBetweenness.put(p1, edgesBetweenness.get(p1) + 1);
				else if (edgesBetweenness.containsKey(p2))
					edgesBetweenness.put(p2, edgesBetweenness.get(p2) + 1);
				else
					edgesBetweenness.put(p1, 1);

			}
		});

		return sortByEdgeBetweenness(edgesBetweenness);
	}

	private Map<Pair<String, String>, Integer> sortByEdgeBetweenness(Map<Pair<String, String>, Integer> edgeBetweenness) {
		List<Map.Entry<Pair<String, String>, Integer>> edgesBTW = new ArrayList<>(edgeBetweenness.entrySet());
		edgesBTW.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

		Map<Pair<String, String>, Integer> res = new LinkedHashMap<>();
		for (Map.Entry<Pair<String, String>, Integer> set : edgesBTW) {
			res.put(set.getKey(), set.getValue());
		}

		return res;
	}

	public Set<String> traverseGraph(String start){
		Map<String, Boolean> marked = new HashMap<>(vertices.size());
		Deque<String> buffer = new LinkedList<>();

		buffer.push(start);
		marked.put(start, true);
		while(!buffer.isEmpty()){
			String current = buffer.pop();
			vertices.get(current).getEdges().forEach((to, edge) -> {
				if (!marked.getOrDefault(edge.to, false)){
					buffer.add(edge.to);
					marked.put(edge.to, true);
				}
			});
		}

		return marked.keySet();
	}

	public List<Set<String>> createClusters(Integer maxAmount) {
		List<Set<String>> clusters = new ArrayList<>();
		List<Pair<String, String>> removedEdges = new ArrayList<>();

		while (clusters.size() < maxAmount && !vertices.values().stream().allMatch(v -> v.getEdges().size() == 0)) {
			Map<Pair<String, String>, Integer> edgesBetweenness = getAllEdgesBetweenness();
			clusters = new ArrayList<>();

			Pair<String, String> removedEdge = edgesBetweenness.keySet().iterator().next();
			removedEdges.add(removedEdge);
			removeEdge(removedEdge.getFirst(), removedEdge.getSecond());

			List<String> visitedNodes = new ArrayList<>();
			List<String> leftoversNodes = new ArrayList<>(vertices.keySet());

			while(leftoversNodes.size() != 0){
				Set<String> clusterNodes = traverseGraph(leftoversNodes.get(0));
				clusters.add(new HashSet<>(clusterNodes));

				visitedNodes.addAll(clusterNodes);
				leftoversNodes.removeAll(clusterNodes);
			}
		}

		System.out.println("We deleted " + removedEdges.size() + " edges to create the clusters : ");
		for (Pair<String, String> removeEdge : removedEdges) {
			System.out.print(removeEdge.getFirst() + ":" + removeEdge.getSecond() + " ");
		}
		System.out.println("\n");

		return clusters;
	}

}
