package it.polito.tdp.seriea.model;

import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;


	public static List<Season> getSeason() {
		SerieADAO dao= new SerieADAO();
		return dao.listSeasons();
	}

	public static List<Team> getTeam() {
		SerieADAO dao= new SerieADAO();
		return dao.listTeams();
	}

	public String getClassifica(Season stagione) {
		
		String classifica=" ";
		grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		SerieADAO dao= new SerieADAO();
		dao.popolaGrafo(grafo,stagione);
		List<Team> team= new LinkedList<Team>(grafo.vertexSet());
		Collections.sort(team);
		for(Team t:team) {
			classifica+= t.getTeam() + " "+ t.getPunteggio()+"\n";
		}
		
		
		return classifica;
	}

	
		
	
		
	

}
