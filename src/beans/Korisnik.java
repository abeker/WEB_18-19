package beans;

public class Korisnik{
	private String username;
	private String password;
	private String ime;
	private String prezime;
	private String uloga;			// kupac - administrator - prodavac		**postavljam iz tih klasa**
	private long br_telefona;
	private String grad;
	private String mail;
	private long datum_reg;			// datum registracije

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getUloga() {
		return uloga;
	}

	public void setUloga(String uloga) {
		this.uloga = uloga;
	}

	public long getBr_telefona() {
		return br_telefona;
	}

	public void setBr_telefona(long br_telefona) {
		this.br_telefona = br_telefona;
	}

	public String getGrad() {
		return grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public long getDatum_reg() {
		return datum_reg;
	}

	public void setDatum_reg(long datum_reg) {
		this.datum_reg = datum_reg;
	}
	
}
