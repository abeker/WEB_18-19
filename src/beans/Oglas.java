package beans;

import java.util.ArrayList;

public class Oglas {
	private String naziv;
	private double cena;
	private String opis;
	private int lajkovi;
	private int dislajkovi;
	private String slika;						
	private long datum_postavljanja;
	private long datum_isticanja;
	private boolean aktivan;
	private ArrayList<Recenzija> recenzije;
	private String grad;
	
	private String status;						//  U realizaciji,  Dostavljen, Aktivan
	private String vlasnik;						// ko ga je postavio
	private ArrayList<String> kategorije;		
	private int popularnost;
	private ArrayList<String> korisnici_prijave;		// oni koji su prijavljivali ovaj oglas
	private ArrayList<String> korisnici_lajkovi;		
	private ArrayList<String> korisnici_dislajkovi;		
	

	public Oglas() {
		lajkovi = 0;
		dislajkovi = 0;
		this.setDatum_postavljanja( java.lang.System.currentTimeMillis() );
		status = "Aktivan";
		kategorije = new ArrayList<String>();
		recenzije = new ArrayList<Recenzija>();
		korisnici_prijave = new ArrayList<String>();
		korisnici_lajkovi = new ArrayList<String>();
		korisnici_dislajkovi = new ArrayList<String>();
		aktivan = true;
		popularnost = 0;
	}

	public ArrayList<String> getKorisnici_lajkovi() {
		return korisnici_lajkovi;
	}
	
	public void setKorisnici_lajkovi(ArrayList<String> korisnici_lajkovi) {
		this.korisnici_lajkovi = korisnici_lajkovi;
	}
	
	public ArrayList<String> getKorisnici_dislajkovi() {
		return korisnici_dislajkovi;
	}
	
	public void setKorisnici_dislajkovi(ArrayList<String> korisnici_dislajkovi) {
		this.korisnici_dislajkovi = korisnici_dislajkovi;
	}

	public ArrayList<String> getKorisnici_prijave() {
		return korisnici_prijave;
	}
	
	public void setKorisnici_prijave(ArrayList<String> korisnici_prijave) {
		this.korisnici_prijave = korisnici_prijave;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVlasnik() {
		return vlasnik;
	}

	public void setVlasnik(String vlasnik) {
		this.vlasnik = vlasnik;
	}

	public ArrayList<String> getKategorije() {
		return kategorije;
	}

	public void setKategorije(ArrayList<String> kategorije) {
		this.kategorije = kategorije;
	}

	public int getPopularnost() {
		return popularnost;
	}

	public void setPopularnost(int popularnost) {
		this.popularnost = popularnost;
	}

	public String getNaziv() {
		return naziv;
	}
	
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	
	public double getCena() {
		return cena;
	}
	
	public void setCena(double cena) {
		this.cena = cena;
	}
	
	public String getOpis() {
		return opis;
	}
	
	public void setOpis(String opis) {
		this.opis = opis;
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
	
	public String getSlika() {
		return slika;
	}
	
	public void setSlika(String slika) {
		this.slika = slika;
	}
	
	public long getDatum_postavljanja() {
		return datum_postavljanja;
	}
	
	public void setDatum_postavljanja(long datum_postavljanja) {
		this.datum_postavljanja = datum_postavljanja;
	}
	
	public long getDatum_isticanja() {
		return datum_isticanja;
	}
	
	public void setDatum_isticanja(long datum_isticanja) {
		this.datum_isticanja = datum_isticanja;
	}
	
	public boolean isAktivan() {
		return aktivan;
	}
	
	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}
	
	public ArrayList<Recenzija> getRecenzije() {
		return recenzije;
	}
	
	public void setRecenzije(ArrayList<Recenzija> recenzije) {
		this.recenzije = recenzije;
	}
	
	public String getGrad() {
		return grad;
	}
	
	public void setGrad(String grad) {
		this.grad = grad;
	}
	
}
