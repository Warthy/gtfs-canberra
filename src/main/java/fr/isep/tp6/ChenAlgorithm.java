package fr.isep.tp6;

import java.util.*;

public class ChenAlgorithm {

	public static List<List<WeightedEdge>> run(WeightedGraph graph, int K, String source, String sink) {
		// STEP 1.A
		List<WeightedEdge> sortedEdges = new LinkedList<>();
		graph.vertices.values().forEach(node ->
			node.getEdges().values().forEach(e -> sortedEdges.add((WeightedEdge) e))
		);

		// STEP 1.B
		List<List<WeightedEdge>> P = new ArrayList<>(sortedEdges.size());
		WeightedGraph subgraph = graph;
		for (int i = 0; i < sortedEdges.size(); i++) {
			subgraph = createSubgraph(subgraph, sortedEdges.subList(i, sortedEdges.size() - 1));
			P.add(computeODSPLoopless(subgraph, source, sink));
		}

		int k = 0;
		List<List<WeightedEdge>> results = new LinkedList<>();
		while (k < K && P.size() != 0) {

			// STEP 2.A
			int minIndex = 0;
			List<WeightedEdge> minP = P.get(minIndex);
			for (int j = 0; j < P.size(); j++) {
				if (Graph.getPathCost(P.get(j)) < Graph.getPathCost(minP)) {
					minIndex = j;
					minP = P.get(j);
				}
			}
			List<WeightedEdge> p = minP;

			// STEP 2.B
			subgraph = createSubgraph(graph, sortedEdges.subList(minIndex, sortedEdges.size() - 1));
			P.set(minIndex, computeNextODSPLoopless(subgraph, source, sink, results)); // handle null case ?


			// STEP 2.C
			if (k == 0 || results.contains(p)) {
				k += 1;
				results.add(p);
			}
		}

		return results;
	}

	public static WeightedGraph createSubgraph(WeightedGraph g, List<WeightedEdge> edges) {
		WeightedGraph subgraph = new WeightedGraph(true);
		g.vertices.values().forEach(n -> {
			subgraph.vertices.put(n.getId(), new Node(n.getId(), n.getLat(), n.getLng()));
		});

		edges.forEach(e -> subgraph.addEdge(e.from, e.to, e.weight));

		return subgraph;
	}

	public static List<WeightedEdge> computeODSPLoopless(WeightedGraph graph, String source, String sink) {
		Dijkstra dijkstra = new Dijkstra(graph, source);
		return dijkstra.getShortesPath(sink);
	}

	/**
	 * Yen's Algorithm
	 */
	public static List<WeightedEdge> computeNextODSPLoopless(WeightedGraph graph, String source, String sink, List<List<WeightedEdge>> ksp) {
		List<WeightedEdge> kthPath;
		if(ksp.size() == 0)
			ksp.add((new Dijkstra(graph, source)).getShortesPath(sink));

		List<WeightedEdge> previousPath = ksp.get(ksp.size() - 1);
		PriorityQueue<LinkedList<WeightedEdge>> candidates = new PriorityQueue<>(new PathComparator());

		for (int i = 0; i < previousPath.size(); i++) {
			// Initialize a container to store the modified (removed) edges for this node/iteration
			LinkedList<WeightedEdge> removedEdges = new LinkedList<>();

			// Spur node = currently visited node in the (k-1)st shortest path
			String spurNode = previousPath.get(i).from;

			// Root path = prefix portion of the (k-1)st path up to the spur node
			List<WeightedEdge> rootPath = previousPath.subList(0, i);

			/* Iterate over all of the (k-1) shortest paths */
			for (List<WeightedEdge> p : ksp) {
				List<WeightedEdge> stub = p.subList(0, i);
				// Check to see if this path has the same prefix/root as the (k-1)st shortest path
				if (rootPath.equals(stub)) {
                            /* If so, eliminate the next edge in the path from the graph (later on, this forces the spur
                               node to connect the root path with an un-found suffix path) */
					WeightedEdge re = p.get(i);
					graph.removeEdge(re.from, re.to);
					removedEdges.add(re);
				}
			}

			/* Temporarily remove all of the nodes in the root path, other than the spur node, from the graph */
			for (WeightedEdge rootEdge : rootPath) {
				String node = rootEdge.from;
				if (!node.equals(spurNode)) {

					graph.vertices.get(node).getEdges().values().forEach(e -> removedEdges.add((WeightedEdge) e));
					graph.vertices.remove(node);
				}
			}

			// Spur path = shortest path from spur node to target node in the reduced graph
			List<WeightedEdge> spurPath = (new Dijkstra(graph, spurNode)).getShortesPath(sink);

			// If a new spur path was identified...
			if (spurPath != null) {
				// Concatenate the root and spur paths to form the new candidate path
				LinkedList<WeightedEdge> totalPath = new LinkedList<>(rootPath);
				totalPath.addAll(spurPath);

				// If candidate path has not been generated previously, add it
				if (!candidates.contains(totalPath))
					candidates.add(totalPath);
			}

			// Restore all of the edges that were removed during this iteration
			removedEdges.forEach(e -> graph.addEdge(e.from, e.to, e.weight));
		}

		/* Identify the candidate path with the shortest cost */
		boolean isNewPath;
		do {
			kthPath = candidates.poll();
			isNewPath = true;
			if (kthPath != null) {
				for (List<WeightedEdge> p : ksp) {
					// Check to see if this candidate path duplicates a previously found path
					if (p.equals(kthPath)) {
						isNewPath = false;
						break;
					}
				}
			}
		} while (!isNewPath);


		return kthPath;
	}
}


class PathComparator implements Comparator<LinkedList<WeightedEdge>>{
	public int compare(LinkedList<WeightedEdge> p1, LinkedList<WeightedEdge> p2) {
		Double cost1 = Graph.getPathCost(p1);
		Double cost2 = Graph.getPathCost(p2);

		if (cost1 < cost2)
			return 1;
		else if (cost1 > cost2)
			return -1;
		return 0;
	}
}
