package beans;

import java.util.ArrayList;

public class Kategorija {
	private String naziv;
	private String opis;
	private ArrayList<String> oglasi;		// lista oglasa iz te kategorije
	private boolean aktivna;
	
	
	public Kategorija() {
		oglasi = new ArrayList<String>();
		aktivna = true;
	}
	
	public String getNaziv() {
		return naziv;
	}
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	public String getOpis() {
		return opis;
	}
	public void setOpis(String opis) {
		this.opis = opis;
	}
	public ArrayList<String> getOglasi() {
		return oglasi;
	}
	public void setOglasi(ArrayList<String> oglasi) {
		this.oglasi = oglasi;
	}
	public boolean isAktivna() {
		return aktivna;
	}
	public void setAktivna(boolean aktivna) {
		this.aktivna = aktivna;
	}
	
	
}
