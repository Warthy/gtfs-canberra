package fr.isep.tp6;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BFSShortestPaths {
	Map<String, Boolean> marked;
	Map<String, String> previous;
	Map<String, Integer> distance;


	public BFSShortestPaths(UnweightedGraph graph, String s) {
		this.marked = new HashMap<>(graph.vertices.size());
		this.previous = new HashMap<>(graph.vertices.size());
		this.distance = new HashMap<>(graph.vertices.size());

		Deque<String> buffer = new LinkedList<>();

		buffer.push(s);
		marked.put(s, true);
		distance.put(s, 0);

		while(!buffer.isEmpty()){
			String current = buffer.pop();
			graph.vertices.get(current).getEdges().forEach((to, edge) -> {
				if (!marked.getOrDefault(edge.to, false)){
					buffer.add(edge.to);

					distance.put(edge.to, distance.get(edge.from) + 1);
					previous.put(edge.to, edge.from);
					marked.put(edge.to, true);
				}
			});
		}

	}


	public boolean hasPathTo(String v){
		return marked.getOrDefault(v, false);
	}

	public int distTo(String v){
		return distance.getOrDefault(v, Integer.MAX_VALUE);
	}

	public void printSP(String v){
		LinkedList<String> path = new LinkedList<>();
		String crawl = v;
		path.add(crawl);

		while (previous.get(crawl) != null){
			path.add(previous.get(crawl));
			crawl = previous.get(crawl);
		}

		System.out.println("Path is :");
		for (int i = path.size() - 1; i >= 0; i--)
			System.out.print(path.get(i) + " ");
	}
}

