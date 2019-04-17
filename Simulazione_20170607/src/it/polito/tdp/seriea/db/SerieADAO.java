package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public void popolaGrafo(SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo, Season stagione) {
		String sql=" select m.HomeTeam as casa, m.AwayTeam as trasferta, ftr as result " + 
				"from matches as m , seasons as s, teams as t1, teams as t2 " + 
				"where t1.team=m.HomeTeam " + 
				"and t2.team=m.AwayTeam " + 
				"and s.season=m.Season " + 
				"and s.season=? ";
		
		double peso=0.0;
					
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, stagione.getSeason());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				String teamCasa=res.getString("casa");
				String teamTrasferta=res.getString("trasferta");
				String risultato=res.getString("result");
				
				Team casa=new Team(teamCasa);
				Team trasferta=new Team(teamTrasferta);
				
				
				if(!grafo.containsVertex(casa)) {
					grafo.addVertex(casa);
				}
				if(!grafo.containsVertex(trasferta)) {
					grafo.addVertex(trasferta);
				}
				if(risultato.compareTo("H")==0) {
					peso=1;
				}else if(risultato.compareTo("A") ==0) {
					peso=-1;
				}
				else if(risultato.compareTo("D")== 0) {
					peso=0;
				}
				Graphs.addEdge(grafo, casa, trasferta, peso);
				
				
			}
			
			conn.close();
			
		} catch (SQLException e) {
			throw new RuntimeException("errore Db");
			
		}
		
		
	}

	public List<Match> calcolaClassifica(Season stagione) {
		String sql=" select m.HomeTeam as casa, m.AwayTeam as trasferta, ftr as result " + 
				"from matches as m , seasons as s, teams as t1, teams as t2 " + 
				"where t1.team=m.HomeTeam " + 
				"and t2.team=m.AwayTeam " + 
				"and s.season=m.Season " + 
				"and s.season=? ";
		
		List<Match> risultato=new ArrayList<Match>();
		
		Connection conn = DBConnect.getConnection() ;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, stagione.getSeason());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				String teamCasa = res.getString("casa");
				String teamTrasferta = res.getString("trasferta");
				String result = res.getString("result");

				Team inCasa = new Team(teamCasa);
				Team inTrasferta = new Team(teamTrasferta);

				Match m = new Match(inTrasferta, inTrasferta, result);
				risultato.add(m);
			}

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return risultato;

	}


}
