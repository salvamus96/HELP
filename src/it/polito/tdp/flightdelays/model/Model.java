package it.polito.tdp.flightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Model {
	
	private FlightDelaysDAO fdao;
	
	private List <Airline> airlines;
	private List <Airport> airports;
	
	private AirportIdMap airportIdMap;
	private AirlineIdMap airlineIdMap;
	
	private Graph <Airport, DefaultWeightedEdge> grafo;
	
	private List <OriginDestination> edges;
	
	private Simulatore sim;
	
	public Model () {
		
		this.fdao = new FlightDelaysDAO();
		this.airportIdMap = new AirportIdMap ();
		this.airlineIdMap = new AirlineIdMap();
		
		this.edges = new ArrayList<>();

		// caricamento delle linee aeree
		this.airlines = this.fdao.loadAllAirlines(this.airlineIdMap);
		this.airports = new ArrayList<>();
		
	}
	
	public void createGraph(Airline airline) {
					// grafo semplice, orientato e pesato
		this.grafo = new SimpleDirectedWeightedGraph <>(DefaultWeightedEdge.class);
		
		// caricamento vertici
		this.airports = this.fdao.getAllAirportFromAirline(airline, this.airportIdMap);
		
		Graphs.addAllVertices(grafo, this.airports);
		
		// caricamento archi
		this.edges = this.fdao.getAllEdges(airline, this.airportIdMap);

		for (OriginDestination od : this.edges)
			if (!od.getOrigin().equals(od.getDestination()))
				Graphs.addEdge(this.grafo, od.getOrigin(),  od.getDestination(), od.getWeight());
	
		System.out.println(grafo.vertexSet().size() + " " + grafo.edgeSet().size());
	
	}

	
	public List<OriginDestination> getWorstEdges() {
		if (this.grafo == null)
			throw new RuntimeException();

		Collections.sort(this.edges);
		return this.edges;
	}
	
	
	public List<Airline> getAllAirlines() {
		if (this.airlines != null)
			return this.airlines;
		
		return new ArrayList<>();
		}

	
	public void simula(List<Passeggero> passeggeri, Airline airline) {
		sim = new Simulatore();
		sim.init(passeggeri, this.airports);
		sim.run(airline, this.fdao, this.airportIdMap);
	}

	public List<Passeggero> getResulSim() {
		return sim.getResult();
	}
	
	
}
