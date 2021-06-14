package fr.isep.tp6;

import org.jgrapht.alg.util.Pair;

import java.util.*;

public class ChenAlgorithm {

	public static void run(WeightedGraph graph, int K, String source, String sink) {
		// STEP 1.A
		List<WeightedEdge> sortedEdges = new LinkedList<>();
		graph.vertices.values().forEach(node -> sortedEdges.addAll((Collection<? extends WeightedEdge>) node.getEdges().values()));

		// STEP 1.B
		List<Pair<List<String>, Double>> P = new ArrayList<>(sortedEdges.size());
		WeightedGraph subgraph = graph;
		for (int i = 0; i < sortedEdges.size(); i++) {
			subgraph = createSubgraph(subgraph, sortedEdges.subList(i, sortedEdges.size() - 1));
			P.set(i, computeODSPLoopless(subgraph, source, sink));
		}

		int k = 0;
		List<List<String>> results = new ArrayList<>();
		while (k < K && P.size() != 0) {

			// STEP 2.A
			int minIndex = 0;
			Pair<List<String>, Double> minP = P.get(minIndex);
			for (int j = 0; j < P.size(); j++) {
				if (P.get(j).getSecond() < minP.getSecond()) {
					minIndex = j;
					minP = P.get(j);
				}
			}
			List<String> p = minP.getFirst();

			// STEP 2.B
			subgraph = createSubgraph(graph, sortedEdges.subList(minIndex, sortedEdges.size() - 1));
			P.set(minIndex, computeNextODSPLoopless(subgraph, source, sink, results));


			// STEP 2.C
			if (k == 0 || results.contains(p)) {
				k += 1;
				results.set(k, p);
			}

		}
	}

	public static WeightedGraph createSubgraph(WeightedGraph g, List<WeightedEdge> edges) {
		WeightedGraph subgraph = new WeightedGraph();
		g.vertices.values().forEach(n -> {
			subgraph.vertices.put(n.getId(), new Node(n.getId(), n.getLat(), n.getLng()));
		});

		edges.forEach(e -> subgraph.addEdge(subgraph.vertices.get(e.from), subgraph.vertices.get(e.to)));

		return subgraph;
	}

	public static Pair<List<String>, Double> computeODSPLoopless(WeightedGraph graph, String source, String sink) {
		Dijkstra dijkstra = new Dijkstra(graph, source);
		return new Pair<>(dijkstra.getShortesPath(sink), dijkstra.distTo(sink));
	}

	/**
	 * Yen's Algorithm
	 */
	public static Pair<List<String>, Double> computeNextODSPLoopless(WeightedGraph graph, String source, String sink, List<List<String>> ksp) {
		List<String> kthPath;
		List<String> previousPath = ksp.get(ksp.size() - 2);
		PriorityQueue<List<String>> candidates = new PriorityQueue<>();

		for (int i = 0; i < previousPath.size(); i++) {
			// Initialize a container to store the modified (removed) edges for this node/iteration
			LinkedList<WeightedEdge> removedEdges = new LinkedList<>();

			// Spur node = currently visited node in the (k-1)st shortest path
			String spurNode = previousPath.get(i);

			// Root path = prefix portion of the (k-1)st path up to the spur node
			List<String> rootPath = previousPath.cloneTo(i);

			/* Iterate over all of the (k-1) shortest paths */
			for (List<String> p : ksp) {
				List<String> stub = p.cloneTo(i);
				// Check to see if this path has the same prefix/root as the (k-1)st shortest path
				if (rootPath.equals(stub)) {
                            /* If so, eliminate the next edge in the path from the graph (later on, this forces the spur
                               node to connect the root path with an un-found suffix path) */
					WeightedEdge re = p.getEdges().get(i);
					graph.removeEdge(re.getFromNode(), re.getToNode());
					removedEdges.add(re);
				}
			}

			/* Temporarily remove all of the nodes in the root path, other than the spur node, from the graph */
			for (Edge rootPathEdge : rootPath.getEdges()) {
				String rn = rootPathEdge.getFromNode();
				if (!rn.equals(spurNode)) {
					removedEdges.addAll(graph.removeNode(rn));
				}
			}

			// Spur path = shortest path from spur node to target node in the reduced graph
			List<String> spurPath = (new Dijkstra(graph, spurNode)).getShortesPath(sink);

			// If a new spur path was identified...
			if (spurPath != null) {
				// Concatenate the root and spur paths to form the new candidate path
				List<String> totalPath = rootPath.clone();
				totalPath.addPath(spurPath);

				// If candidate path has not been generated previously, add it
				if (!candidates.contains(totalPath))
					candidates.add(totalPath);
			}

			// Restore all of the edges that were removed during this iteration
			removedEdges.forEach(e -> graph.addEdge(graph.vertices.get(e.from), graph.vertices.get(e.to)));
		}

		/* Identify the candidate path with the shortest cost */
		boolean isNewPath;
		do {
			kthPath = candidates.poll();
			isNewPath = true;
			if (kthPath != null) {
				for (List<String> p : ksp) {
					// Check to see if this candidate path duplicates a previously found path
					if (p.equals(kthPath)) {
						isNewPath = false;
						break;
					}
				}
			}
		} while (!isNewPath);

		return new Pair<>(kthPath, );
	}
}
