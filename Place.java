
public class Place {
	
	private String numAvion;
	private int numPlace;
	private int Classe;
	private String position;
	
	
	public Place(String numAvion, int numPlace, int classe, String position) {
		super();
		this.numAvion = numAvion;
		this.numPlace = numPlace;
		Classe = classe;
		this.position = position;
	}
	
	public String getNumAvion() {
		return numAvion;
	}
	public int getNumPlace() {
		return numPlace;
	}
	public int getClasse() {
		return Classe;
	}
	public String getPosition() {
		return position;
	}
	
	public String toString(){
		return this.getNumAvion()+" "+this.getNumPlace()+" "+this.getPosition()+" "+this.getClasse();
	}

}
