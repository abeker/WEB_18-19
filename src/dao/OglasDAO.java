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

import beans.Oglas;

public class OglasDAO {
	private HashMap<String, Oglas> oglasi;
	
	public OglasDAO() {
		super();
		this.oglasi = new HashMap<String, Oglas>();
		
		ucitajOglase();
	}
	
	public Oglas setPorucen(String naziv) {
		Oglas o = oglasi.get(naziv);
		o.setStatus("U realizaciji");
		
		return o;
	}
	
	public Oglas getOglasNaz(String naziv) {
		for (Oglas o : oglasi.values()) {
			if(naziv.equals(o.getNaziv()) && o.isAktivan()) {
				return o;
			}
		}
		
		return null;
	}
	
	public Collection<Oglas> getPretragaOglase(String n, String cO, String cD, String oO, String oD, 
			String dO, String dD, String g, String s){
		
		ArrayList<Oglas> lista_oglasa = new ArrayList<Oglas>();
		for (Oglas o : oglasi.values()) {
			if(o.getNaziv().toLowerCase().contains(n.toLowerCase()) && o.isAktivan()) {		// ako sadrzi taj naziv
				if(o.getGrad().contains(g)) {
					if(o.getStatus().contains(s)) {
						if(opsegCena(o.getCena(), cO, cD)) {
							if(opsegOcena(o.getLajkovi(), oO, oD)) {
								if(opsegDatum(o.getDatum_isticanja(), dO, dD)) {
									lista_oglasa.add(o);
								}
								else {
									continue;		// nije datum u opsegu => preskoci
								}
							}
							else {
								continue;		// nije ocena u opsegu => preskoci
							}
						}
						else {
							continue;		// nije cena u opsegu => preskoci
						}
					}
					else {
						continue;	// nije taj status => preskoci
					}
				}
				else {
					continue;	// ne sadrzi grad => preskoci
				}
			}
			else {
				continue;		// ne sadrzi taj naziv => preskoci
			}
		}
		
		return lista_oglasa;
	}
	
	public boolean opsegCena(double prava, String min, String max) {
		boolean flag = false;
		double minn, maxx;
		if(!min.equals("")) {
			minn = Double.parseDouble(min);
		}
		else {
			minn = 0;
		}
		if(!max.equals("")) {
			maxx = Double.parseDouble(max);
		}
		else {
			maxx = Double.MAX_VALUE;
		}
		
		if(prava >= minn && prava <= maxx) {
			flag = true;
		}
		
		return flag;
	}
	
	public boolean opsegOcena(int prava, String min, String max) {
		boolean flag = false;
		int minn, maxx;
		if(!min.equals("")) {
			minn = Integer.parseInt(min);
		}
		else {
			minn = 0;
		}
		if(!max.equals("")) {
			maxx = Integer.parseInt(max);
		}
		else {
			maxx = Integer.MAX_VALUE;
		}
		
		if(prava >= minn && prava <= maxx) {
			flag = true;
		}
		
		return flag;
	}
	
	public boolean opsegDatum(long pravi, String min, String max) {
		boolean flag = false;
		long minn, maxx;
		if(!min.equals("")) {
			minn = Long.parseLong(min);
		}
		else {
			minn = 0;
		}
		if(!max.equals("")) {
			maxx = Long.parseLong(max);
		}
		else {
			maxx = Long.MAX_VALUE;
		}
		
		if(pravi >= minn && pravi <= maxx) {
			flag = true;
		}
		
		return flag;
	}
	
	public Collection<String> getGradove(){
		ArrayList<String> gradovi = new ArrayList<String>();
		for (Oglas o : oglasi.values()) {
			if(!gradovi.contains(o.getGrad()))
				gradovi.add(o.getGrad());
		}
		
		return gradovi;
	}
	
	public Collection<Oglas> findAll(){
		ArrayList<Oglas> list = new ArrayList<Oglas>();
		for (Oglas oglas : oglasi.values()) {
			list.add(oglas);
		}
		return list;
	}
	
	public HashMap<String, Oglas> getOglasi() {
		return oglasi;
	}

	public void setOglasi(HashMap<String, Oglas> oglasi) {
		this.oglasi = oglasi;
	}

	public Oglas putOglas(Oglas novi, String naziv) {
		Oglas stari = oglasi.get(naziv);
		
		if(stari != null) {
			stari.setNaziv(novi.getNaziv());
			stari.setCena(novi.getCena());
			stari.setOpis(novi.getOpis());
			stari.setGrad(novi.getGrad());
			stari.setDatum_isticanja(novi.getDatum_isticanja());
			if(!novi.getSlika().equals("")) {			// ako sam uneo neku sliku => to je nova slia
				stari.setSlika(novi.getSlika());		// a ako nisam, ostaje ona stara
			}
			oglasi.remove(naziv);
			oglasi.put(stari.getNaziv(), stari);
			return stari;
		}
		else {
			return null;
		}
	}
	
	public int delete(String naziv, boolean admin) {
		Oglas oglas = oglasi.get(naziv);
		
		
		if(admin == false) {
			if(oglas != null && oglas.isAktivan()) {
				if(oglas.getStatus().equals("Dostavljen")) {
					return 2;
				}
				else if(oglas.getStatus().equals("U realizaciji")) {
					return 3;
				}
				else {
					oglas.setAktivan(false);
					return 1;
				}
			}
		}
		else {
			if(oglas != null && oglas.isAktivan()) {
				if(oglas.getStatus().equals("Dostavljen")) {
					oglas.setAktivan(false);
					return 1;
				}
				else if(oglas.getStatus().equals("U realizaciji")) {
					oglas.setAktivan(false);
					return 1;
				}
				else {
					oglas.setAktivan(false);
					return 1;
				}
			}
		}
		
		return -1;
	}
	
	public Collection<Oglas> getOglase(String username){
		ArrayList<Oglas> mojiOglasi = new ArrayList<Oglas>();
		for (Oglas oglas : oglasi.values()) {
			if(oglas.isAktivan() && oglas.getVlasnik().equals(username)) {
				mojiOglasi.add(oglas);
			}
		}
		
		return mojiOglasi;
	}
	
	public Collection<Oglas> getFilter(String filter, String username){
		ArrayList<Oglas> mojiOglasi = new ArrayList<Oglas>();
		if(filter.equals("a")) {			
			for (Oglas oglas : oglasi.values()) {
				if(oglas.isAktivan() && oglas.getVlasnik().equals(username) && oglas.getStatus().equals("Aktivan")) {
					mojiOglasi.add(oglas);
				}
			}
		}
		else if(filter.equals("d")) {
			for (Oglas oglas : oglasi.values()) {
				if(oglas.isAktivan() && oglas.getVlasnik().equals(username) && oglas.getStatus().equals("Dostavljen")) {
					mojiOglasi.add(oglas);
				}
			}
		}
		else {
			for (Oglas oglas : oglasi.values()) {
				if(oglas.isAktivan() && oglas.getVlasnik().equals(username) && oglas.getStatus().equals("U realizaciji")) {
					mojiOglasi.add(oglas);
				}
			}
		}
		
		return mojiOglasi;
	}
	
	public Oglas add(Oglas oglas) {
		Oglas o = oglasi.get(oglas.getNaziv());
		if (o == null) {
			oglasi.put(oglas.getNaziv(), oglas);
			sacuvajOglase();
			return oglas;
		}
		if (o.isAktivan() == false) {
			oglasi.remove(oglas.getNaziv());
			oglasi.put(oglas.getNaziv(), oglas);
			sacuvajOglase();
			return oglas;
		}
		return null;
	}
	
	public Collection<Oglas> getKategorijuOglasa(String naziv){
		//System.out.println("Naziv je:" + naziv);
		ArrayList<Oglas> odgovarajuci = new ArrayList<Oglas>();
		for (Oglas oglas : oglasi.values()) {
			for (String kategorija : oglas.getKategorije()) {
				System.out.println(kategorija);
				
				if(kategorija.trim().equals(naziv.trim()) && oglas.getStatus().equals("Aktivan") && 
						oglas.isAktivan()) {
					odgovarajuci.add(oglas);
				}
			}
		}
		
		return odgovarajuci;
	}
	
	public void sacuvajOglase() {
		//System.out.println("usao u cuvanje oglasa");
		
		BufferedWriter bw=null;
		File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/oglasi.txt");
		String line="";
		
		
		// treba promeniti putanju do slike sa apsolutne na lokalnu
		try {
			for (Oglas o : this.findAll()) {
				line  += ""+o.getNaziv()+";"+o.getCena()+";"+o.getOpis()+";"+o.getLajkovi()+";"
						+o.getDislajkovi()+";"+o.getSlika()+";"+o.getDatum_postavljanja()+";"
						+o.getDatum_isticanja()+";"+o.getStatus()+";"+o.getVlasnik()+";";
				for (String k : o.getKategorije()) {
					line += k + ";";
				}
				line += o.getGrad()+";"+o.getPopularnost();
				line+="\n";
				
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
	
	private void ucitajOglase() {
		//System.out.println("usao u citanje oglasa");
		BufferedReader in = null;
		
		try {
			File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/oglasi.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String naziv = st.nextToken().trim();
					String cena = st.nextToken().trim();
					String opis = st.nextToken().trim();
					String lajkovi = st.nextToken().trim();
					String dislajkovi = st.nextToken().trim();
					String slika = st.nextToken().trim();
					String dat_post = st.nextToken().trim();
					String dat_ist = st.nextToken().trim();
					String status = st.nextToken().trim();
					String prodavac = st.nextToken().trim();
					String kategorija = st.nextToken().trim();
					String grad = st.nextToken().trim();
					String popular = st.nextToken().trim();
					
					Oglas oglas = new Oglas();
					
					oglas.setNaziv(naziv);
					oglas.setCena(Double.parseDouble(cena));
					oglas.setOpis(opis);
					oglas.setLajkovi(Integer.parseInt(lajkovi));
					oglas.setDislajkovi(Integer.parseInt(dislajkovi));
					oglas.setSlika(slika);
					oglas.setDatum_postavljanja(Long.parseLong(dat_post));
					oglas.setDatum_isticanja(Long.parseLong(dat_ist));
					oglas.setStatus(status);
					oglas.setVlasnik(prodavac);
					oglas.getKategorije().add(kategorija);
					oglas.setGrad(grad);
					oglas.setPopularnost(Integer.parseInt(popular));
					
					oglasi.put(naziv, oglas);
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
