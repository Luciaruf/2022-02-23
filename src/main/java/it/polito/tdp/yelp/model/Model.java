package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	YelpDao dao;
	Graph<Review, DefaultWeightedEdge> graph;
	List<Arco> archi;

	public Model() {
		this.dao = new YelpDao();
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.archi = new ArrayList<>();
	}
	
	public List<String> getCities(){
		return this.dao.getCities();
	}
	
	public List<String> getLocaliCommerciali(String città){
		return this.dao.getLocaliCommerciali(città);
	}
	
	public Graph creaGrafo(String locale) {
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.archi = new ArrayList<>();
		
		String businessId = this.dao.getBusinessId(locale);
		
		Graphs.addAllVertices(this.graph, this.dao.getReviews(businessId));
		int peso = 0;
		
		for(Review r1 : this.graph.vertexSet()) {
			for(Review r2: this.graph.vertexSet()) {
				if(!r1.equals(r2)) {
					if(r1.getDate().isBefore(r2.getDate())) {
						peso =  (int) ChronoUnit.DAYS.between(r1.getDate(), r2.getDate());
						if(peso!=0) {
							archi.add(new Arco(r1,r2,peso));
						}
					}
				}
			}
		}
		
		for(Arco a : archi) {
			Graphs.addEdgeWithVertices(this.graph, a.getR1(), a.getR2(), a.getPeso());
		}
		
		return this.graph;
	}
	
	public List<Review> archiUscenti() {
		List<Review> trovata = new ArrayList<>();
		int cont = 0;
		
		for(Review r : this.graph.vertexSet()) {
			if(this.graph.outDegreeOf(r)>=cont) {
				cont = this.graph.outgoingEdgesOf(r).size();
			}
		}
		
		for(Review rr : this.graph.vertexSet()) {
			if(this.graph.outDegreeOf(rr)==cont) {
				trovata.add(rr);
			}
				
		}
		
		return trovata;
	}
	
}
