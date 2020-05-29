package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Administrator;
import beans.Kategorija;
import beans.Korisnik;
import beans.Kupac;
import beans.Oglas;
import beans.Poruka;
import beans.Prodavac;
import beans.Recenzija;
import dao.AdminDAO;
import dao.KorisnikDAO;
import dao.OglasDAO;

@Path("prodavac")
public class ProdavacService {
	
	@Context 
	ServletContext ctx;
	
	@POST
	@Path("/add")
	//@Consumes(MediaType.APPLICATION_JSON)
	public boolean add(Oglas oglas, @Context HttpServletRequest request) {
		//System.out.println(">> usao sam u add oglas <<");
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if (!korisnik.getUloga().equals("Prodavac"))
			return false;

		oglas.setVlasnik(korisnik.getUsername());
		OglasDAO oglDao = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDao.getOglasNaz(oglas.getNaziv());
		if (o == null) {
			oglDao.getOglasi().put(oglas.getNaziv(), oglas);
		}
		else {
			if (o.isAktivan() == false) {
				oglDao.getOglasi().remove(oglas.getNaziv());
				oglDao.getOglasi().put(oglas.getNaziv(), oglas);
			}
		}
		
		// ako nisam vec dodao taj oglas, dodaj ga sad kod vlasnika
		if (!((Prodavac)korisnik).getObjavljeni_oglasi().contains(oglas.getNaziv()))
			((Prodavac)korisnik).getObjavljeni_oglasi().add(oglas.getNaziv());
		
		return true;
	}
	
	@PUT
	@Path("/putOglas")
	public boolean putOglas(Oglas oglas, @QueryParam("naziv") String stariNaziv, @Context HttpServletRequest request) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if (korisnik.getUloga().equals("Kupac"))		// samo kupac ne moze da menja oglas(admin i prodavac mogu)
			return false;
		
		
		OglasDAO oglDao = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas tmp = oglDao.putOglas(oglas, stariNaziv);
		if(tmp != null) {
			// menjam taj oglas i kod prodavca(vlasnika)
			KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
			Prodavac vlasnikOglasa = (Prodavac) korDAO.findUsername(tmp.getVlasnik());	
			if(vlasnikOglasa != null) {
				if(vlasnikOglasa.getIsporuceni_oglasi().remove(stariNaziv)) {		// ako je u isporucenim, menjam ga
					vlasnikOglasa.getIsporuceni_oglasi().add(tmp.getNaziv());
				}
				if(vlasnikOglasa.getObjavljeni_oglasi().remove(stariNaziv)) {		// ako je u objavlj. menjam ga tu
					vlasnikOglasa.getObjavljeni_oglasi().add(tmp.getNaziv());
				}
			}
			
			int br;
			AdminDAO katDAO = (AdminDAO) ctx.getAttribute("kategorije");
			for (Kategorija k : katDAO.findAll()) {
				if(k.isAktivna()) {
					if((br = k.getOglasi().indexOf(stariNaziv)) != -1) {
						k.getOglasi().remove(br);
						k.getOglasi().add(br, tmp.getNaziv());
					}
				}
			}
			
			for (Korisnik k : korDAO.getKorisnici().values()) {
				if(k.getUloga().equals("Kupac")) {
					Kupac kupac = (Kupac)k;
					if( kupac.getPoruceni_oglasi().contains(stariNaziv) ) {		// menjam ga i u listama porucenih
						boolean bul = kupac.getPoruceni_oglasi().remove(stariNaziv);
						kupac.getPoruceni_oglasi().add(tmp.getNaziv());
						System.out.println("brisao porucene?? " + bul);
					}
					else if( kupac.getUspesno_dostavljeni().contains(stariNaziv) ) {    // isto i za liste dostavljenih
						kupac.getUspesno_dostavljeni().remove(stariNaziv);
						kupac.getUspesno_dostavljeni().add(tmp.getNaziv());
					}
				}
			}
			
			
			// slanje poruke administrator/prodavac
			if(korisnik.getUloga().equals("Administrator")) {
				Poruka p = new Poruka();
				p.setNaziv_oglasa(stariNaziv);
				p.setNaslov_poruke("Administrator izmenio oglas");
				p.setSadrzaj_poruke("Postovani, </br>Administrator \"" + korisnik.getIme()+" "+korisnik.getPrezime()+
						"\" je izmenio oglas \"" + stariNaziv + "\" koji ste Vi postavili.");
				p.setPosiljalac( korisnik.getUsername() );
				p.setUloga_posiljaoca(korisnik.getUloga());
				p.setAutomatizovana(true);
				Random rand = new Random();
				int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
				p.setId(id);
				
				Prodavac prodavac = (Prodavac)korDAO.findUsername(tmp.getVlasnik());
				prodavac.getPoruke().add(p);
				
				
				if(!tmp.getStatus().equals("Aktivan")) {
					// posalji poruku kupcu ako je bio u realizaciji ili dostavljen
					Poruka por = new Poruka();
					por.setNaziv_oglasa(stariNaziv);
					por.setNaslov_poruke("Administrator izmenio oglas");
					por.setSadrzaj_poruke("Postovani, </br>Administrator \"" + korisnik.getIme()+" "+korisnik.getPrezime()+
							"\" je izmenio oglas \"" + stariNaziv + "\" koji ste vi porucili.");
					por.setPosiljalac(korisnik.getUsername());	
					por.setAutomatizovana(true);
					por.setUloga_posiljaoca(korisnik.getUloga());
					int id_ = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
					por.setId(id_);
					
					for (Korisnik k : korDAO.getKorisnici().values()) {
						if(k.getUloga().equals("Kupac")) {
							Kupac kupac = (Kupac)k;
							if(kupac.getPoruceni_oglasi().contains(stariNaziv) || kupac.getUspesno_dostavljeni().contains(stariNaziv)) {
								kupac.getPoruke().add(por);				// saljem KUPCU oglasa, odnosno naruciocu
							}
						}
					}
				}
			}
			
		}
		else {
			return false;
		}
		
		return true;
	}
	
	@GET
	@Path("/getProizvodIme")
	@Produces(MediaType.APPLICATION_JSON)
	public Oglas getProizvod(@QueryParam("naziv") String naziv) {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getOglasi().get(naziv);
	}
	
	@GET
	@Path("/getVlasnik")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVlasnik(@QueryParam("naziv") String naziv) {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getOglasi().get(naziv).getVlasnik();
	}
	
	@GET
	@Path("/getOglase")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getOglase(@Context HttpServletRequest request){
		//System.out.println("usao u getOglase");
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getOglase(korisnik.getUsername());
	}
	
	@GET
	@Path("/getFiltrirane")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getFiltrirane(@QueryParam("filter") String filter, @Context HttpServletRequest request){
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getFilter(filter, korisnik.getUsername());
	}
	
	@GET
	@Path("/getLajk")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Integer> getLajk(@Context HttpServletRequest request){
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		Prodavac prodavac = (Prodavac) korisnik;			// jer znam da je on prodavac
		ArrayList<Integer> lista = new ArrayList<Integer>();
		lista.add(prodavac.getLajkovi());
		lista.add(prodavac.getDislajkovi());
		
		//System.out.println(">>>>> lajkovi:"+lista.get(0));
		//System.out.println(">>>>> dislajkovi:"+lista.get(1));
		
		return lista; 
	}
	
	@GET
	@Path("/getProdavac")
	@Produces(MediaType.APPLICATION_JSON)
	public Prodavac getIme(@Context HttpServletRequest request){
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		Prodavac p = (Prodavac) korisnik;
		return p;
	}
	
	@DELETE
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public int delete(@QueryParam("naziv") String naziv, @Context HttpServletRequest request) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Prodavac") && !korisnik.getUloga().equals("Administrator")) {
			return -2;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		int ret = 0;	
		
		if(korisnik.getUloga().equals("Administrator")) {
			// posalji poruku prodavcu
			System.out.println("ADMIN BRISE OGLAS");
			Poruka poruka = new Poruka();
			poruka.setNaziv_oglasa(naziv);
			poruka.setNaslov_poruke("Administrator obrisao oglas");
			poruka.setSadrzaj_poruke("Postovani, </br>Administrator \"" + korisnik.getIme()+" "+korisnik.getPrezime()+
					"\" obrisao je oglas \"" + naziv +"\" koji ste Vi postavili.");
			poruka.setPosiljalac(korisnik.getUsername());
			poruka.setUloga_posiljaoca(korisnik.getUloga());
			poruka.setAutomatizovana(true);
			Random rand = new Random();
			int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
			poruka.setId(id);
			
			
			KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
			String vlasnik = oglDAO.getOglasNaz(naziv).getVlasnik();
			Prodavac prod = (Prodavac)korDAO.findUsername(vlasnik);
			prod.getPoruke().add(poruka);				// saljem PRODAVCU ovog oglasa
			
			Oglas o = oglDAO.getOglasNaz(naziv);
			if(!o.getStatus().equals("Aktivan")) {			// ako je oglas u realizaciji ili dostavljen
				// posalji poruku kupcu
				Poruka p = new Poruka();
				p.setNaziv_oglasa(naziv);
				p.setNaslov_poruke("Administrator obrisao oglas");
				p.setSadrzaj_poruke("Postovani, </br>Administrator \"" + korisnik.getIme()+" "+korisnik.getPrezime()+
						"\" je obrisao oglas \"" + naziv + "\" koji ste Vi porucili.");
				p.setPosiljalac(korisnik.getUsername());			
				p.setUloga_posiljaoca(korisnik.getUloga());
				p.setAutomatizovana(true);
				Random random = new Random();
				int id_ = random.nextInt(Integer.MAX_VALUE);			// napravim random broj
				p.setId(id_);
				
				for (Korisnik k : korDAO.getKorisnici().values()) {
					if(k.getUloga().equals("Kupac")) {
						Kupac kupac = (Kupac)k;
						if(kupac.getPoruceni_oglasi().contains(o.getNaziv()) || kupac.getUspesno_dostavljeni().contains(o.getNaziv())) {
							kupac.getPoruke().add(p);				// saljem KUPCU oglasa, odnosno naruciocu
						}
					}
				}
			}
			
			ret =  oglDAO.delete(naziv, true);		// true znaci da je admin u pitanju
		}
		else if(korisnik.getUloga().equals("Prodavac")) {
			// posalji poruku administratorima
			Poruka poruka = new Poruka();
			poruka.setNaziv_oglasa(oglDAO.getOglasNaz(naziv).getNaziv());
			poruka.setNaslov_poruke("Prodavac obrisao oglas");
			poruka.setSadrzaj_poruke("Postovani, </br>Prodavac \"" + korisnik.getIme()+" "+korisnik.getPrezime()+
					"\" je obrisao svoj oglas \"" + oglDAO.getOglasNaz(naziv).getNaziv() +"\".");
			poruka.setPosiljalac(korisnik.getUsername());
			poruka.setUloga_posiljaoca(korisnik.getUloga());
			poruka.setAutomatizovana(true);
			Random rand = new Random();
			int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
			poruka.setId(id);
			
			
			KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
			for (Korisnik k : korDAO.getKorisnici().values()) {
				if(k.getUloga().equals("Administrator")) {			// saljem poruku svim administratorima
					Administrator admin = (Administrator) k;
					admin.getPoruke().add(poruka);
				}
			}
			
			ret =  oglDAO.delete(naziv, false);		// false znaci da nije admin u pitanju(ne mogu brisati dostavljene i u_realizaciji)
		}
		return ret;
	}
	
	@GET
	@Path("/getListaRecenzija")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Recenzija> getRecenzije(@Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return null;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		ArrayList<Recenzija> list_recenzija = new ArrayList<Recenzija>();
		Prodavac prodavac = (Prodavac)korisnik;
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		for (String s : prodavac.getIsporuceni_oglasi()) {
			Oglas o = oglDAO.getOglasNaz(s);
			for (Recenzija r : o.getRecenzije()) {
				if(r.isAktivna()) {
					list_recenzija.add(r);
				}
			}
		}
		
		return list_recenzija;
	}
	
	@GET
	@Path("/getRecenziju")
	@Produces(MediaType.APPLICATION_JSON)
	public Recenzija getRecenziju(@QueryParam("naziv")String nazOglasa, @Context HttpServletRequest request){
		System.out.println("primljeno: "+nazOglasa);
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Recenzija rec = new Recenzija();
		Oglas ogl = oglDAO.getOglasNaz(nazOglasa);
		if(ogl != null) {
			if(ogl.isAktivan() && !ogl.getRecenzije().isEmpty()) {
				for (Recenzija r : oglDAO.getOglasNaz(nazOglasa).getRecenzije()) {
					if(r.isAktivna()) {
						rec = r;		// vracam prvu aktivnu na koju naidjem			
					}
				}
			}
		}
		
		System.out.println("ret: " + rec.getRecezent());
		return rec;
	}

	@GET
	@Path("/getRecProdavca")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Recenzija> getRecProdavca(@QueryParam("naziv")String nazOglasa, @Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return null;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Recenzija> list_rec = new ArrayList<Recenzija>();
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o != null) {
				if(o.getVlasnik().equals(oglDAO.getOglasNaz(nazOglasa).getVlasnik())) {
					if(o.isAktivan() && !o.getRecenzije().isEmpty()) {
						if(o.getRecenzije().get(0).isAktivna()) {
							list_rec.add(o.getRecenzije().get(0));
						}
					}
				}
			}
		}
		
		return list_rec;
	}
	
	@DELETE
	@Path("/deleteRec")
	public boolean deleteRec(@QueryParam("naziv") String nazivOgl) {
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		Oglas o = oglDAO.getOglasNaz(nazivOgl);
		if(!o.getRecenzije().isEmpty()) {
			o.getRecenzije().get(0).setAktivna(false);
		}
		
		return true;
	}
	
	@GET
	@Path("/getPoruke")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Poruka> getPoruke(@Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return null;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Prodavac prodavac = (Prodavac)korisnik;
		for (Poruka p : prodavac.getPoruke()) {
			list_poruka.add(p);
		}
		
		return list_poruka;
	}
	
	@GET
	@Path("/getPoslate")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Poruka> getPoslate(@Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return null;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Prodavac prodavac = (Prodavac)korisnik;
		for (Poruka p : prodavac.getPoslate()) {
			list_poruka.add(p);
		}
		
		return list_poruka;
	}
	
	@POST
	@Path("/prijavaNaloga")
	public int prijavi(@QueryParam("oglas") String oglas, @Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return -1;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return -1;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		
		boolean flag = false;
		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o.getNaziv().equals(oglas)) {
				if(o.getKorisnici_prijave().contains(korisnik.getUsername())) {
					flag = true;				// vec si prijavio ovaj oglas, ne mozes vise puta
				}
				else {
					o.getKorisnici_prijave().add(korisnik.getUsername());
				}
			}
		}
		
		if(!flag) {
			Prodavac prodavac = (Prodavac)korDAO.findUsername(oglDAO.getOglasNaz(oglas).getVlasnik());
			prodavac.setPrijave(prodavac.getPrijave()+1);
			
			for (Korisnik k : korDAO.getKorisnici().values()) {
				if(k.getUloga().equals("Administrator")) {
					Poruka upozorenje = saljiUpozorenje(k.getUsername());
					prodavac.getPoruke().add(upozorenje);
				}
			}
			
			return 1;
		}
		else {
			return 2;
		}
		
	}
	
	public Poruka saljiUpozorenje(String username) {
		Poruka poruka = new Poruka();
		poruka.setNaziv_oglasa("");
		poruka.setNaslov_poruke("<b>Prijava naloga</b>");
		poruka.setSadrzaj_poruke("Postovani, </br>Vas nalog je prijavljen od strane korisnika naseg sajta,</br>"
				+ "stoga smo duzni da Vam posaljemo upozorenje.");
		poruka.setPosiljalac(username);
		poruka.setUloga_posiljaoca("Administrator");
		poruka.setAutomatizovana(true);
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		poruka.setId(id);
		
		return poruka;
	}
	
	@GET
	@Path("/getPrijave")
	public int getPrijave(@Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return -2;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return -2;
		}
		
		Prodavac prodavac = (Prodavac)korisnik;
		if(prodavac.getPrijave() >= 3) {
			return -1;				// ne moze da postavlja oglase			
		}
		else {
			return 1;
		}
	}
	
	@GET
	@Path("/getSvePrijave")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Prodavac> getSvePrijave(@Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return null;
		}
		if(!korisnik.getUloga().equals("Administrator")) {
			return null;
		}
		
		ArrayList<Prodavac> list_prijavljeni = new ArrayList<Prodavac>();
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		for (Korisnik k : korDAO.getKorisnici().values()) {
			if(k.getUloga().equals("Prodavac")) {
				Prodavac prodavac = (Prodavac)k;
				if(prodavac.getPrijave() > 0) {
					list_prijavljeni.add(prodavac);
				}
			}
		}
		
		return list_prijavljeni;
	}
	
	@POST
	@Path("/ponistiPrijave")
	@Produces(MediaType.APPLICATION_JSON)
	public int ponistiPrijave(@QueryParam("username")String username, @Context HttpServletRequest request){
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return -1;
		}
		if(!korisnik.getUloga().equals("Administrator")) {
			return -1;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		Prodavac prodavac = (Prodavac)korDAO.findUsername(username);
		prodavac.setPrijave(0);
		
		return 1;
	}
	
	@GET
	@Path("/getImePrezime/{vlasnik}")
	@Produces(MediaType.APPLICATION_JSON)
	public Prodavac getImePrezime(@PathParam("vlasnik") String username) {
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		Korisnik kor = korDAO.findUsername(username);
		
		if(!kor.getUloga().equals("Prodavac")){
			return null;
		}
			
		return (Prodavac)kor;
	}
	
	@GET
	@Path("/getOglaseProdavac/{vlasnik}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getOglaseProdavac(@PathParam("vlasnik") String username) {
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		ArrayList<Oglas> oglasi = new ArrayList<Oglas>();

		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o.getVlasnik().equals(username)) {
				oglasi.add(o);
			}
		}
				
		return oglasi;
	}
	
	@GET
	@Path("/recenzijeProdavca/{vlasnik}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Recenzija> getRecProdavac(@PathParam("vlasnik") String username) {
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		ArrayList<Recenzija> recenzije = new ArrayList<Recenzija>();

		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o != null) {
				if(o.getVlasnik().equals(username) && o.isAktivan()) {
					for (Recenzija r : o.getRecenzije()) {
						if(r.isAktivna()) {
							recenzije.add(r);
						}
					}
				}
			}
		}
				
		return recenzije;			// vratim sve recenzije na oglase koje poseduje vlasnik (username)
	}
	
	@GET
	@Path("/checkRecenzija/{oglas}")
	public int checkRecenzija(@PathParam("oglas") String oglas, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return -1;
		}
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		Oglas ogl = oglDAO.getOglasNaz(oglas);
		if(!ogl.getRecenzije().isEmpty()) {
			for (Recenzija r : ogl.getRecenzije()) {
				if(r.getRecezent().equals(korisnik.getUsername()) && r.isAktivna()) {
					return 1;
				}
			}
		}
		
		return -1;
	}
	
	@GET
	@Path("/checkKorisnickaProdavac/{oglas}")
	public int checkKorisnickaProdavac(@PathParam("oglas") String oglas, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return -1;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return -1;
		}
		
		Kupac kupac = (Kupac)korisnik;
		int ret = 0;
		if(kupac.getPoruceni_oglasi().contains(oglas)) {			// ako je samo porucen
			ret  = 1;
			if(kupac.getOmiljeni_oglasi().contains(oglas)) {		// ako je istovremeno i omiljen i porucen
				ret = 11;
			}
			return ret;
		}
		else if(kupac.getUspesno_dostavljeni().contains(oglas)) {
			return 2;
		}
		else if(kupac.getOmiljeni_oglasi().contains(oglas)) {
			return 3;
		}
		
		return -1;
	}
	
	@GET
	@Path("/checkLikeDislike/{oglas}")
	public String checkLikeDislike(@PathParam("oglas") String oglas, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch (NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return "not";
		}
		
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		Oglas ogl = oglDAO.getOglasNaz(oglas);
		if(ogl == null) {
			return "greska";
		}
		String ret = "";
		if(ogl.getKorisnici_lajkovi().contains(korisnik.getUsername())) {		// lajkovao sam 
			ret = "like";
			if(ogl.getKorisnici_dislajkovi().contains(korisnik.getUsername())) {		// i dislajkovao
				ret = "both";
			}
			return ret;
		}
		else {		// nisam lajkovao 
			if(ogl.getKorisnici_dislajkovi().contains(korisnik.getUsername())) {  	// ali sam dislajkovao
				ret = "dislike";
			}
			else {			// nisam ni dislajkovao
				ret = "not";
			}
			
			return ret;
		}
	}
	
	@GET
	@Path("/checkUlogaProdavac/{username}")
	public int checkLikeDislike(@PathParam("username") String username) {
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		Korisnik kor = korDAO.findUsername(username);
		if(kor.getUloga().equals("Prodavac")) {
			return 0;
		}
		else if(kor.getUloga().equals("Administrator")) {
			return 1;
		}
		else if(kor.getUloga().equals("Kupac")) {
			return 2;
		}
		
		return -1;
	}
}