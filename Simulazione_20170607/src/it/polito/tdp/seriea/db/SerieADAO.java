package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public  List<Team> listTeams() {
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
		String sql=" select HomeTeam as ht, AwayTeam as at, " + 
				"case " + 
				"when ftr=\"H\" then 1 " + 
				"when ftr=\"A\" then -1 " + 
				"else 0 " + 
				"end as peso " + 
				"from matches  " + 
				"where season=? ";
		
		

		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, stagione.getSeason());
			ResultSet res = st.executeQuery() ;
			Map<Team,Team> puntiTeam=new HashMap<>();
			
			while(res.next()) {
				String casa=res.getString("ht");
				String trasferta=res.getString("at");
				double peso=res.getDouble("peso");
				int punteggioCasa=0;
				int punteggioTrasferta=0;
				
				
				if (peso == 1) {
					punteggioCasa = 3;
					punteggioTrasferta=0;
					
				} else if (peso == -1) {
					punteggioCasa = 0;
					punteggioTrasferta=3;
					
				} else {
					punteggioCasa = 1;
					punteggioTrasferta=1;
				}

				Team t1=new Team(casa);
				Team t2=new Team(trasferta);
				
				if(!puntiTeam.containsKey(t1) ) {
					puntiTeam.put(t1, t1);
				
				}
				if(!puntiTeam.containsKey(t2)) {
					puntiTeam.put(t2, t2);
				}
				
				if(!grafo.containsVertex(t1)) {
					grafo.addVertex(puntiTeam.get(t1));
				}
				if(!grafo.containsVertex(t2)) {
					grafo.addVertex(puntiTeam.get(t2));
				}
				
				if(!grafo.containsEdge(t1, t2) ) {
					DefaultWeightedEdge edge = grafo.addEdge(puntiTeam.get(t1),puntiTeam.get(t2));
					grafo.setEdgeWeight(edge, peso);
					puntiTeam.get(t1).setPunteggio(punteggioCasa);
					puntiTeam.get(t2).setPunteggio(punteggioTrasferta);
			//	}
				} else {

					puntiTeam.get(t1).setPunteggio(punteggioCasa);
					puntiTeam.get(t2).setPunteggio(punteggioTrasferta);
				}
				
//					puntiTeam.get(t1).setPunteggio(punteggioCasa);
//					puntiTeam.get(t2).setPunteggio(punteggioTrasferta);
			}
			
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		
		}
		
		
	}

	


}
