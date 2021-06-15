package fr.isep.tp6;

public class WeightedEdge extends Edge {
	Double weight;

	public WeightedEdge(String from, String to, Double weight) {
		super(from, to);
		this.weight = weight;
	}

	public WeightedEdge(Node from, Node to, Double weight) {
		super(from, to);
		this.weight = weight;
	}

	public WeightedEdge(Node from, Node to) {
		super(from, to);
		/*
		Approche plus "réalistique"

		long R = 6371;
		Double phiun = from.getLat() * Math.PI / 180;
		Double phideux = to.getLat() * Math.PI / 180;

		Double deltaPhi = (to.getLat() - from.getLat()) * Math.PI / 180;
		Double deltaLambda = (to.getLng() - from.getLng()) * Math.PI / 180;

		Double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
				Math.cos(phiun) * Math.cos(phideux) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance =  R * c
		*/

		//Approche Euclidienne
		//Avec 1° ~ 111
		double distance = 111 * Math.sqrt(Math.pow((to.getLat()-from.getLat()),2) + Math.pow((to.getLng()-from.getLng()),2));


		this.weight = distance;
	}
}
