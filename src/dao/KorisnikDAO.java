package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Administrator;
import beans.Korisnik;
import beans.Kupac;
import beans.Prodavac;

public class KorisnikDAO {
	private HashMap<String, Korisnik> korisnici;		// mapa svih korisnika sa username-om kao kljucem
	
	public KorisnikDAO() {
		super();
		this.korisnici = new HashMap<String, Korisnik>();
		ucitajKorisnike();					// ucitavam sve korisnike koje imam registrovane
	}

	public HashMap<String, Korisnik> getKorisnici() {
		return korisnici;
	}

	public void setKorisnici(HashMap<String, Korisnik> korisnici) {
		this.korisnici = korisnici;
	}
	
	public boolean proveraUsername(String username) {
		for (Korisnik kor : korisnici.values()) {
			if(kor.getUsername().equals(username)) {
				return false;
			}
		}
		
		return true;
	}
	
	public Collection<Korisnik> pretragaKorisnika(String ime, String grad){
		ArrayList<Korisnik> lista_kor = new ArrayList<Korisnik>();
		for (Korisnik k : korisnici.values()) {
			if(k.getIme().toLowerCase().contains(ime.toLowerCase())) {
				if(k.getGrad().toLowerCase().contains(grad.toLowerCase())) {
					lista_kor.add(k);
				}
			}
		}
		
		return lista_kor;
	}
	
	public boolean promenaUloge(String username, String novaUloga) {
		Korisnik korisnik = new Korisnik();
		korisnik = findUsername(username);
		if(korisnik == null) {				// ne postoji taj korisnik
			return false;
		}
		
		if(novaUloga.equals("Administrator")) {
			Administrator admin = new Administrator();
			admin.setUsername(korisnik.getUsername());
			admin.setIme(korisnik.getIme());
			admin.setPrezime(korisnik.getPrezime());
			admin.setPassword(korisnik.getPassword());
			admin.setBr_telefona(korisnik.getBr_telefona());
			admin.setGrad(korisnik.getGrad());
			admin.setMail(korisnik.getMail());
			admin.setDatum_reg(korisnik.getDatum_reg());
			admin.setUloga("Administrator");
			korisnici.remove(username);
			korisnici.put(username, admin);
		}
		else if(novaUloga.equals("Prodavac")) {
			Prodavac prodavac = new Prodavac();
			prodavac.setUsername(korisnik.getUsername());
			prodavac.setIme(korisnik.getIme());
			prodavac.setPrezime(korisnik.getPrezime());
			prodavac.setPassword(korisnik.getPassword());
			prodavac.setBr_telefona(korisnik.getBr_telefona());
			prodavac.setGrad(korisnik.getGrad());
			prodavac.setMail(korisnik.getMail());
			prodavac.setDatum_reg(korisnik.getDatum_reg());
			prodavac.setUloga("Prodavac");
			korisnici.remove(username);
			korisnici.put(username, prodavac);
		}
		else if(novaUloga.equals("Kupac")) {
			Kupac kupac = new Kupac();
			kupac.setUsername(korisnik.getUsername());
			kupac.setIme(korisnik.getIme());
			kupac.setPrezime(korisnik.getPrezime());
			kupac.setPassword(korisnik.getPassword());
			kupac.setBr_telefona(korisnik.getBr_telefona());
			kupac.setGrad(korisnik.getGrad());
			kupac.setMail(korisnik.getMail());
			kupac.setDatum_reg(korisnik.getDatum_reg());
			kupac.setUloga("Kupac");
			korisnici.remove(username);
			korisnici.put(username, kupac);
		}
		else {
			return false;
		}
		
		return true;
	}
	
	public Collection<Korisnik> getKorisnike(){
		ArrayList<Korisnik> kor = new ArrayList<Korisnik>();
		for (Korisnik k : korisnici.values()) {
			kor.add(k);
		}
		
		return kor;
	}
	
	public Collection<String> getGradoveKorisnika(){
		ArrayList<String> gradovi = new ArrayList<String>();
		for (Korisnik k : korisnici.values()) {
			if(!gradovi.contains(k.getGrad())) {
				gradovi.add(k.getGrad());
			}
		}
		
		return gradovi;
	}
	
	public Korisnik findUsername(String username) {
		return korisnici.get(username);
	}
	
	public Korisnik add(Korisnik k) {
		Korisnik kor = korisnici.get(k.getUsername());		
		if (kor == null) {						// ako ne postoji registrovan -> dodam ga
			korisnici.put(k.getUsername(), k);
			sacuvajKorisnika(k);
			return k;
		}
		return null;
	}

	public Korisnik find(String username, String pass) {
		Korisnik k = korisnici.get(username);
		
		if(k == null) {
			System.out.println("__Nije pronadjen__");
			return null;
		}
		else if(pass.equals(k.getPassword()) == false) {		// dobar username, los password
			System.out.println("__Nije dobar password unet__");
			return null;
		}
		
		return k;
	}
	
	public Collection<Korisnik> findAll() {
		return korisnici.values();
	}
	
	public void sacuvajKorisnika(Korisnik user) {
		System.out.println("usao u cuvanje");
		
		BufferedWriter bw=null;
		File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/korisnici.txt");
		String line="";
		try {
			
			line  += ""+user.getUsername()+";"+user.getPassword()+";"+user.getIme()+";"+user.getPrezime()+";"
					+user.getUloga()+";"+user.getBr_telefona()+";"+user.getGrad()+";"+user.getMail()+";"
					+user.getDatum_reg();
			line += "\n";
			
			for (Korisnik k : korisnici.values()) {
				if(k.getUloga().equals("Kupac") && !k.getUsername().equals(user.getUsername())) {
					line += k.getUsername()+";"+k.getPassword()+";"+k.getIme()+";"+k.getPrezime()+";"
							+k.getUloga()+";"+k.getBr_telefona()+";"+k.getGrad()+";"+k.getMail()+";"
							+k.getDatum_reg();
					line += "\n";
				}
			}
			
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(line);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void ucitajKorisnike() {
		BufferedReader in = null;
		System.out.println("__usao u ucitavanje korisnika__");
		
		ucitajAdmine();
		ucitajProdavce();
		try {
			File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/korisnici.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String username = st.nextToken().trim();
					String password = st.nextToken().trim();
					String ime = st.nextToken().trim();
					String prezime = st.nextToken().trim();
					String uloga = st.nextToken().trim();
					String tel = st.nextToken().trim();
					String grad = st.nextToken().trim();
					String mail = st.nextToken().trim();
					String datum = st.nextToken().trim();
					
					Kupac kupac = new Kupac();
					
					kupac.setUsername(username);
					kupac.setPassword(password);
					kupac.setIme(ime);
					kupac.setPrezime(prezime);
					kupac.setUloga(uloga);
					kupac.setBr_telefona(Long.parseLong(tel));
					kupac.setGrad(grad);
					kupac.setMail(mail);
					kupac.setDatum_reg(Long.parseLong(datum));
					
					korisnici.put(username, kupac);
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
		
		/*System.out.println("PROVERA UCITANIH: \n");
		for (Korisnik k : korisnici.values()) {
			System.out.println("username: " + k.getUsername() + ", password: " + k.getPassword() + "\n");
		}*/
	}

	private void ucitajProdavce() {
		BufferedReader in = null;
		
		try {
			File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/prodavci.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String username = st.nextToken().trim();
					String password = st.nextToken().trim();
					String ime = st.nextToken().trim();
					String prezime = st.nextToken().trim();
					String uloga = st.nextToken().trim();
					String tel = st.nextToken().trim();
					String grad = st.nextToken().trim();
					String mail = st.nextToken().trim();
					
					Prodavac prodavac = new Prodavac();
					
					prodavac.setUsername(username);
					prodavac.setPassword(password);
					prodavac.setIme(ime);
					prodavac.setPrezime(prezime);
					prodavac.setUloga(uloga);
					prodavac.setBr_telefona(Long.parseLong(tel));
					prodavac.setGrad(grad);
					prodavac.setMail(mail);
					
					korisnici.put(username, prodavac);
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
		
	}

	private void ucitajAdmine() {
		BufferedReader in = null;
		
		try {
			File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/administratori.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String username = st.nextToken().trim();
					String password = st.nextToken().trim();
					String ime = st.nextToken().trim();
					String prezime = st.nextToken().trim();
					String uloga = st.nextToken().trim();
					String tel = st.nextToken().trim();
					String grad = st.nextToken().trim();
					String mail = st.nextToken().trim();
					
					Administrator admin = new Administrator();
					
					admin.setUsername(username);
					admin.setPassword(password);
					admin.setIme(ime);
					admin.setPrezime(prezime);
					admin.setUloga(uloga);
					admin.setBr_telefona(Long.parseLong(tel));
					admin.setGrad(grad);
					admin.setMail(mail);
					
					korisnici.put(username, admin);
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
		
}
