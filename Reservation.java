import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Reservation {
	
	private final String CONN_URL = "jdbc:oracle:thin:@localhost:1521:XE";
	private final String USER = "system";
	private final String PASSWD = "123456";
	private static Connection conn;
	private Client c=null;
	private Map<Integer, Vol> tabVol = new HashMap();
	private Map<Integer, Place> tabPlace = new HashMap();
	
	public Reservation() throws SQLException{
		// Enregistrement du driver Oracle
  	    System.out.print("Chargement du driver... "); 
  	    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
  	    System.out.println("Driver chargé");
  	    
  	    // Etablissement de la connection
  	    System.out.print("Connexion à la BD... "); 
  	    this.conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
  	    System.out.println("Connexion effectuée");
  	    
  	    // Desactivation de l'autocommit
  	    conn.setAutoCommit(false);
  	    System.out.println("Autocommit désactivé");
	}
	
	private void identificationClient() throws SQLException{
		String nom, prenom;
		int idClient=0; 
		boolean trouve=false;
		System.out.println("*** Identification ***");
		System.out.print("Nom: "); 
		nom=LectureClavier.lireChaine();
		System.out.print("Prenom: "); 
		prenom=LectureClavier.lireChaine();
		//On recherche dans la base l'identificateur du client
		PreparedStatement pst = conn.prepareStatement("SELECT NumClient, NomC, PrenomC FROM CLIENT WHERE NomC=? AND PrenomC=?");
		pst.setString(1, nom); pst.setString(2, prenom);
		ResultSet rs = pst.executeQuery();
		while(rs.next() && !trouve){
			idClient = rs.getInt("NumClient");
			trouve = true;
		}
		if(!trouve)
			System.out.println("Client introuvable");
		else
			this.c = new Client(idClient, nom, prenom);
	}


	public void afficherMenu() throws SQLException {   
  	//Il faut que le client s'identifie
	while(this.c==null){
		this.identificationClient();
	}
	System.out.println("*** Choisir une action a effectuer : ***");
	System.out.println("0 : Quitter");
	System.out.println("1 : Effectuer une réservation");
	System.out.println("2 : Afficher vos réservations");
	}
	
	private void afficherVols(String depart, String destination) throws SQLException{
		System.out.println("### Liste des vols ###");
		String s="";
		PreparedStatement pst = conn.prepareStatement("SELECT * FROM VOL V WHERE V.Origine=? AND V.Destination=? ");
		pst.setString(1, depart); 
		pst.setString(2, destination);
		ResultSet rs = pst.executeQuery();
		int cpt = 1;
		System.out.print(" | " + "Choix" + " | "+ " NumVol " + " |   " + " Date / Heure" + "        | \n");
		System.out.print(" |-------------------------------------------| \n");
		while(rs.next()){
			if(this.getNbPlacesDispo(rs.getString(1))>0){
				this.tabVol.put(Integer.valueOf(cpt), new Vol(rs.getString(1), rs.getString(2) )); 
				System.out.print(" |   " + cpt + "   | "+ rs.getString(1) + "    |  " + rs.getString(2) + " | \n");
				cpt++;
			}
		}
	}
	
	
	private int getNbPlacesDispo(String numVol) throws SQLException{
		int nbPlaceTotal=0;
		int nbPlaceUtilise=0;
		PreparedStatement pstPlaceTotal = conn.prepareStatement("SELECT count(*) FROM VOL v, PLACE p WHERE v.NumAvion=p.NumAvion AND v.NumVol=?");
		pstPlaceTotal.setString(1, numVol);
		ResultSet rsPlaceTotal = pstPlaceTotal.executeQuery();
		while(rsPlaceTotal.next()){
			nbPlaceTotal=rsPlaceTotal.getInt(1);
		}
		PreparedStatement pstPlaceUtilise = conn.prepareStatement("SELECT count(*) FROM RESAPLACE r WHERE r.NumVol=?");
		pstPlaceUtilise.setString(1, numVol);
		ResultSet rsPlaceUtilise = pstPlaceUtilise.executeQuery();
		while(rsPlaceUtilise.next()){
			nbPlaceUtilise=rsPlaceUtilise.getInt(1);
		}
		return nbPlaceTotal-nbPlaceUtilise;
		
	}
	
	private void afficherPlaces(Vol v) throws SQLException{
		int cpt=1;
		PreparedStatement pstAvion = conn.prepareStatement("SELECT * FROM VOL v, AVION a, PLACE p WHERE v.NumVol=? AND v.DateVol=? AND a.NumAvion=v.NumAvion AND p.NumAvion=a.NumAvion AND p.NumPlace NOT IN (SELECT r2.NumPlace FROM RESAPLACE r2 WHERE r2.DateVol = v.DateVol AND r2.NumVol = v.NumVol)");
		pstAvion.setString(1, v.getNumVol()); 
		pstAvion.setTimestamp(2, Timestamp.valueOf(v.getDateHeure()));
		ResultSet rsPlaceTotal = pstAvion.executeQuery();
		System.out.print(" | " + "Choix" + " | "+ " Modèle Avion " + " |" + " NumPlace |" +  "  Position | \n");
		System.out.print(" |-----------------------------------------------| \n");
		while(rsPlaceTotal.next()){
			if(this.getNbPlacesDispo(rsPlaceTotal.getString(1))>0){
				Place p = new Place(rsPlaceTotal.getString("NumAvion"), rsPlaceTotal.getInt("NumPlace"), rsPlaceTotal.getInt("Classe"), rsPlaceTotal.getString("Position"));
				this.tabPlace.put(Integer.valueOf(cpt), p);
				System.out.print(" |   " + cpt + "   |     "+ rsPlaceTotal.getString("Modele") + "       |     " + rsPlaceTotal.getInt("NumPlace") + "    |  " + rsPlaceTotal.getString("Position") + "   | \n");
				cpt++;
			}
		}
	}
	
	private int ajouterReservation(int choix) throws SQLException{
		int nbResa = this.getMaxReservation()+1;
		PreparedStatement pstResa = conn.prepareStatement("INSERT INTO RESERVATION VALUES(?, ?, ?)");
		pstResa.setInt(1, nbResa);
		pstResa.setInt(2, this.c.getIdClient());
		pstResa.setTimestamp(3, Timestamp.valueOf("2012-11-08 20:50:00.0"));
		pstResa.executeUpdate();
		return nbResa;
	}
	
	public void nouvelleReservation() throws SQLException{
		System.out.println("*** Créer une nouvelle réservation pour le client "+ c.getNomClient() + " " + c.getPrenomClient() +" ***");
		System.out.print("Départ: "); 
		String depart =LectureClavier.lireChaine();
		System.out.print("Destination: ");
		String destination =LectureClavier.lireChaine();
		this.afficherVols(depart, destination);
		System.out.print("Choisissez votre vol: ");
		int numChoix = LectureClavier.lireEntier("");
		//On commence par créer une réservation
		int nbResa=this.ajouterReservation(numChoix);
		this.afficherPlaces(tabVol.get(Integer.valueOf(numChoix)));
		System.out.print("Choisissez votre place: ");
		int numChoixPlace = LectureClavier.lireEntier("");
		//On ajoute la réservation d'une place
		this.ajouterReservationPlace(numChoix, numChoixPlace, nbResa);
		System.out.println("Valider? (O/N)");
		String validation = LectureClavier.lireChaine();
		if(validation.equals("O"))
			this.validerReservation();
		else
			this.annulerReservation();
		this.tabPlace.clear();
		this.tabVol.clear();
	}
	
	private void ajouterReservationPlace(int numChoix, int numChoixPlace, int numResa) throws SQLException {
		Vol volChoisi = this.tabVol.get(numChoix);
		Place placeChoisi = this.tabPlace.get(numChoixPlace);
		System.out.println("INSERT INTO RESAPLACE VALUES("+volChoisi.getNumVol()+", "+Timestamp.valueOf(volChoisi.getDateHeure())+", "+placeChoisi.getNumPlace()+", "+numResa+", "+(float) 155.2+")");
		PreparedStatement pstResaPlace = conn.prepareStatement("INSERT INTO RESAPLACE VALUES(?, ?, ?, ?, ?)");
		pstResaPlace.setString(1, volChoisi.getNumVol());
		pstResaPlace.setTimestamp(2, Timestamp.valueOf(volChoisi.getDateHeure()));
		pstResaPlace.setInt(3, placeChoisi.getNumPlace());
		pstResaPlace.setInt(4, numResa);
		pstResaPlace.setFloat(5, (float) 155.2);
		pstResaPlace.executeUpdate();
	}
	
	public void afficherMesResa() throws SQLException {
		System.out.println("### Liste des réservations ###");
		String s="";
		PreparedStatement pst = conn.prepareStatement("SELECT rp.NumVol, rp.DateVol, rp.NumPlace FROM RESERVATION r, RESAPLACE rp WHERE r.NumClient=? AND r.NumResa=rp.NumResa ");
		pst.setInt(1, this.c.getIdClient()); 
		ResultSet rs = pst.executeQuery();
		System.out.print(" | "+ " NumVol " + "     |   " + " Date / Heure" + "        |    Numéro Place  | \n");
		System.out.print(" |----------------------------------------------------------| \n");
		while(rs.next()){
				System.out.print(" |   "+ rs.getString(1) + "      |  " + rs.getString(2) + " |  " + rs.getString(3) + "               | \n");
			}
		}

	private int getMaxReservation() throws SQLException{
		PreparedStatement pstAvion = conn.prepareStatement("SELECT MAX(NumResa) FROM Reservation");
		ResultSet rs = pstAvion.executeQuery();
		rs.next();
		return rs.getInt(1);
	}
	
	private void validerReservation() throws SQLException{
		this.conn.commit();
	}
	
	private void annulerReservation() throws SQLException{
		this.conn.commit();
	}

}
