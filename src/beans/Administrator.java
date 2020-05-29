package beans;

import java.util.ArrayList;

public class Administrator extends Korisnik{
	private ArrayList<Poruka> poruke;
	private ArrayList<Poruka> poslate;
	
	public Administrator() {
		super();
		this.setDatum_reg(java.lang.System.currentTimeMillis());
		this.setUloga("Administrator");
		poruke = new ArrayList<Poruka>();
		poslate = new ArrayList<Poruka>();
	}

	public ArrayList<Poruka> getPoslate() {
		return poslate;
	}

	public void setPoslate(ArrayList<Poruka> poslate) {
		this.poslate = poslate;
	}

	public ArrayList<Poruka> getPoruke() {
		return poruke;
	}

	public void setPoruke(ArrayList<Poruka> poruke) {
		this.poruke = poruke;
	}
}
