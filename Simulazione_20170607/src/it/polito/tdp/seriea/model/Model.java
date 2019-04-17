package it.polito.tdp.seriea.model;

import java.util.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge> grafo;
	
	public List <Season> getSeason() {

		SerieADAO dao=new SerieADAO();
		return dao.listSeasons();
	}

	public List<Team> getTeam() {
		SerieADAO dao=new SerieADAO();
		return dao.listTeams();
	}

	public void caricaPartita(Season stagione) {
		SerieADAO dao=new SerieADAO();
		List<Match> classifica=dao.calcolaClassifica(stagione);
		
		grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		dao.popolaGrafo(grafo,stagione);
		
		
		for(Match m:classifica) {
			if (m.getFtr().compareTo("H")==0) {
				m.getHomeTeam().calcolaPunteggio();
			}
		}
		
	
		
	}

}
