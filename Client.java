
public class Client {
	
	private int idClient;
	private String nomClient;
	private String prenomClient;
	
	
	public Client(int idClient, String nomClient, String prenomClient) {
		super();
		this.idClient = idClient;
		this.nomClient = nomClient;
		this.prenomClient = prenomClient;
	}
	
	public int getIdClient() {
		return idClient;
	}
	public String getNomClient() {
		return nomClient;
	}
	public String getPrenomClient() {
		return prenomClient;
	}
	
	

}
