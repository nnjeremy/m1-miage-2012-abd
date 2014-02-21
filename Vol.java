import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


public class Vol {
	
	private String numVol;
	private String dateHeure;
	

	
	public Vol(String numVol, String dateHeure) {
		super();
		this.numVol = numVol;
		this.dateHeure = dateHeure;
	}
	
	public String getNumVol() {
		return numVol;
	}
	
	public String getDateHeure() {
		return dateHeure;
	}
	

	
	
	
	

}
