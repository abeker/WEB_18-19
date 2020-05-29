package beans;

public class Recenzija {
	private String oglas;			// oglas nad kojim se vrsi recenzija
	private String recezent;		// onaj ko pise recenziju
	private String naslov_recenzije;
	private String sadrzaj_recenzije;
	private String slika;
	private boolean tacan_opis;
	private boolean ispostovan_dogovor;
	private boolean aktivna;			// za brisanje mi treba
	
	public Recenzija() {
		this.aktivna = true;
		this.slika = "";
	}
	
	public boolean isAktivna() {
		return aktivna;
	}
	public void setAktivna(boolean aktivna) {
		this.aktivna = aktivna;
	}
	public String getOglas() {
		return oglas;
	}
	public void setOglas(String oglas) {
		this.oglas = oglas;
	}
	public String getRecezent() {
		return recezent;
	}
	public void setRecezent(String recezent) {
		this.recezent = recezent;
	}
	public void setSlika(String slika) {
		this.slika = slika;
	}
	public String getNaslov_recenzije() {
		return naslov_recenzije;
	}
	public void setNaslov_recenzije(String naslov_recenzije) {
		this.naslov_recenzije = naslov_recenzije;
	}
	public String getSadrzaj_recenzije() {
		return sadrzaj_recenzije;
	}
	public void setSadrzaj_recenzije(String sadrzaj_recenzije) {
		this.sadrzaj_recenzije = sadrzaj_recenzije;
	}
	public String getSlika() {
		return slika;
	}
	public boolean isTacan_opis() {
		return tacan_opis;
	}
	public void setTacan_opis(boolean tacan_opis) {
		this.tacan_opis = tacan_opis;
	}
	public boolean isIspostovan_dogovor() {
		return ispostovan_dogovor;
	}
	public void setIspostovan_dogovor(boolean ispostovan_dogovor) {
		this.ispostovan_dogovor = ispostovan_dogovor;
	}
	
}
