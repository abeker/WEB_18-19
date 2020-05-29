package beans;

public class Poruka {
	private String naziv_oglasa;
	private String posiljalac;
	private String naslov_poruke;
	private String sadrzaj_poruke;
	private String uloga_posiljaoca;
	private long datum_vreme;		// kada je poslata/primljena
	private int id;
	private String primalac;
	private boolean procitana;
	private boolean automatizovana;
	
	public Poruka() {
		this.primalac = "";
		this.naziv_oglasa = "";
		this.naslov_poruke = "";
		this.sadrzaj_poruke = "";
		this.procitana = false;
		this.automatizovana = false;
		this.setDatum_vreme( java.lang.System.currentTimeMillis() );
	}
	
	public boolean isAutomatizovana() {
		return automatizovana;
	}

	public void setAutomatizovana(boolean automatizovana) {
		this.automatizovana = automatizovana;
	}

	public boolean isProcitana() {
		return procitana;
	}

	public void setProcitana(boolean procitana) {
		this.procitana = procitana;
	}

	public String getPrimalac() {
		return primalac;
	}
	public void setPrimalac(String primalac) {
		this.primalac = primalac;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUloga_posiljaoca() {
		return uloga_posiljaoca;
	}
	public void setUloga_posiljaoca(String uloga_posiljaoca) {
		this.uloga_posiljaoca = uloga_posiljaoca;
	}
	public String getNaziv_oglasa() {
		return naziv_oglasa;
	}
	public void setNaziv_oglasa(String naziv_oglasa) {
		this.naziv_oglasa = naziv_oglasa;
	}
	public String getPosiljalac() {
		return posiljalac;
	}
	public void setPosiljalac(String posiljalac) {
		this.posiljalac = posiljalac;
	}

	public String getNaslov_poruke() {
		return naslov_poruke;
	}
	public void setNaslov_poruke(String naslov_poruke) {
		this.naslov_poruke = naslov_poruke;
	}
	public String getSadrzaj_poruke() {
		return sadrzaj_poruke;
	}
	public void setSadrzaj_poruke(String sadrzaj_poruke) {
		this.sadrzaj_poruke = sadrzaj_poruke;
	}

	public long getDatum_vreme() {
		return datum_vreme;
	}

	public void setDatum_vreme(long datum_vreme) {
		this.datum_vreme = datum_vreme;
	}
	
}
