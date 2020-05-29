package beans;

import java.util.ArrayList;

public class Prodavac extends Korisnik {
	private ArrayList<String> objavljeni_oglasi;	// cuvacu samo naziv oglasa
	private ArrayList<String> isporuceni_oglasi;		
	private ArrayList<Poruka> poruke;
	private ArrayList<Poruka> poslate;
	private int lajkovi;
	private int dislajkovi;
	private int prijave;
	
	public Prodavac() {
		super();
		this.setUloga("Prodavac");
		this.setDatum_reg( java.lang.System.currentTimeMillis() );
		objavljeni_oglasi = new ArrayList<String>();
		isporuceni_oglasi = new ArrayList<String>();
		poruke = new ArrayList<Poruka>();
		poslate = new ArrayList<Poruka>();
		lajkovi = 0;
		dislajkovi = 0;
		prijave = 0;
	}

	public int getPrijave() {
		return prijave;
	}

	public void setPrijave(int prijave) {
		this.prijave = prijave;
	}

	public ArrayList<Poruka> getPoslate() {
		return poslate;
	}

	public void setPoslate(ArrayList<Poruka> poslate) {
		this.poslate = poslate;
	}

	public ArrayList<String> getObjavljeni_oglasi() {
		return objavljeni_oglasi;
	}

	public void setObjavljeni_oglasi(ArrayList<String> objavljeni_oglasi) {
		this.objavljeni_oglasi = objavljeni_oglasi;
	}

	public ArrayList<String> getIsporuceni_oglasi() {
		return isporuceni_oglasi;
	}

	public void setIsporuceni_oglasi(ArrayList<String> isporuceni_oglasi) {
		this.isporuceni_oglasi = isporuceni_oglasi;
	}

	public ArrayList<Poruka> getPoruke() {
		return poruke;
	}
	
	public void setPoruke(ArrayList<Poruka> poruke) {
		this.poruke = poruke;
	}
	
	public int getLajkovi() {
		return lajkovi;
	}
	
	public void setLajkovi(int lajkovi) {
		this.lajkovi = lajkovi;
	}
	
	public int getDislajkovi() {
		return dislajkovi;
	}
	
	public void setDislajkovi(int dislajkovi) {
		this.dislajkovi = dislajkovi;
	}
	
}
