package beans;

import java.util.ArrayList;

public class Kupac extends Korisnik {
	private ArrayList<String> poruceni_oglasi;
	private ArrayList<String> uspesno_dostavljeni;
	private ArrayList<String> omiljeni_oglasi;
	private ArrayList<Poruka> poruke;
	private ArrayList<Poruka> poslate;
	
	public Kupac() {
		super();
		this.setUloga("Kupac");
		this.setDatum_reg(java.lang.System.currentTimeMillis());
		poruceni_oglasi = new ArrayList<String>();
		uspesno_dostavljeni = new ArrayList<String>();
		omiljeni_oglasi = new ArrayList<String>();
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

	public ArrayList<String> getPoruceni_oglasi() {
		return poruceni_oglasi;
	}
	
	public void setPoruceni_oglasi(ArrayList<String> poruceni_oglasi) {
		this.poruceni_oglasi = poruceni_oglasi;
	}
	
	public ArrayList<String> getUspesno_dostavljeni() {
		return uspesno_dostavljeni;
	}
	
	public void setUspesno_dostavljeni(ArrayList<String> uspesno_dostavljeni) {
		this.uspesno_dostavljeni = uspesno_dostavljeni;
	}
	
	public ArrayList<String> getOmiljeni_oglasi() {
		return omiljeni_oglasi;
	}
	
	public void setOmiljeni_oglasi(ArrayList<String> omiljeni_oglasi) {
		this.omiljeni_oglasi = omiljeni_oglasi;
	}
	
}
