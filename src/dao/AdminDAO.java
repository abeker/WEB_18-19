package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Kategorija;

public class AdminDAO {
	
	private HashMap<String, Kategorija> kategorije;

	public AdminDAO() {
		super();
		this.kategorije = new HashMap<String, Kategorija>();
		
		try {
			ucitajKategorije();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Kategorija add(Kategorija kat) {
		Kategorija k = kategorije.get(kat.getNaziv());
		if(k == null) {
			kategorije.put(kat.getNaziv(), kat);
			sacuvajKategorije();
			return kat;
		}
		
		return null;
	}
	
	public Kategorija find(String naz) {
		Kategorija k = kategorije.get(naz);
		if(k != null) {
			if(k.isAktivna() == true) {
				return k;
			}
		}
		
		return null;
	}
	
	public Collection<String> getKatNames(){
		ArrayList<String> imena = new ArrayList<String>();
		for (Kategorija k : kategorije.values()) {
			if(k.isAktivna() == true)
				imena.add(k.getNaziv());
		}
		
		return imena;
	}
	
	public HashMap<String, Kategorija> getKategorije() {
		return kategorije;
	}
	
	public void setKategorije(HashMap<String, Kategorija> kategorije) {
		this.kategorije = kategorije;
	}
	
	public Collection<Kategorija> findAll() {
		return kategorije.values();
	}
	
	private String removeLastChar(String str) {
	    return str.substring(0, str.length() - 1);
	}
	
	public void sacuvajKategorije() {
		//System.out.println("usao u cuvanje kategorija");
		
		BufferedWriter bw=null;
		File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/kategorije.txt");
		String line="";
		
		try {
			for (Kategorija k : this.findAll()) {
				line  += ""+k.getNaziv()+";"+k.getOpis()+";";
				for (String oglas : k.getOglasi()) {
					line += oglas + ";";
				}
				line = removeLastChar(line);
				line+="\n";
				
			}
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(line);
			bw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private void ucitajKategorije() throws FileNotFoundException, IOException {
		//System.out.println("usao u citanje kategorija");
		BufferedReader in = null;
		
		try {
			File file = new File("C:/Users/Aleksandar Beker/WEB/Pocetni_REST/kategorija.txt");
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
					String opis = st.nextToken().trim();
					String oglas1 = st.nextToken().trim();
					String oglas2 = st.nextToken().trim();
					String oglas3 = st.nextToken().trim();
					String oglas4 = st.nextToken().trim();
					String oglas5 = st.nextToken().trim();
					String oglas6 = st.nextToken().trim();
					
					Kategorija kategorija = new Kategorija();
					
					kategorija.setNaziv(naziv);
					kategorija.setOpis(opis);
					kategorija.getOglasi().add(oglas1);
					kategorija.getOglasi().add(oglas2);
					kategorija.getOglasi().add(oglas3);
					kategorija.getOglasi().add(oglas4);
					kategorija.getOglasi().add(oglas5);
					kategorija.getOglasi().add(oglas6);
					
					kategorije.put(naziv, kategorija);
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
	
	/*public void writeJsonKategorija(String filename) throws Exception {
	    JSONObject sampleObject = new JSONObject();
	    sampleObject.put("naziv", "Odeca");
	    sampleObject.put("opis", "Ovo je odeca.");

	    JSONArray oglasi = new JSONArray();
	    oglasi.add("Markirana majica");
	    oglasi.add("Nemarkirana majica");
	    oglasi.add("Obicna majica");

	    sampleObject.put("oglasi", oglasi);
	    Files.write(Paths.get(filename), sampleObject.toJSONString().getBytes());
	}*/
	
}
