import java.sql.SQLException;


public class ProgrammePrincipal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Reservation r = new Reservation();
	        int action;
	        boolean exit = false;
	  	    while(!exit) {
	  	    	r.afficherMenu();
	  	    	action = LectureClavier.lireEntier("votre choix ?");
	  	    	switch(action) {
	  	    		case 0 : exit = true; break;
	  	    		case 1 : r.nouvelleReservation(); break;
	  	    		case 2 : r.afficherMesResa(); break;
	  	    	}
	  	    } 
			
		}catch(SQLException e){
			e.printStackTrace();
		}

	}

}
