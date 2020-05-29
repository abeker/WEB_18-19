package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Korisnik;
import beans.Kupac;
import beans.Oglas;
import beans.Poruka;
import beans.Prodavac;
import beans.Recenzija;
import dao.KorisnikDAO;
import dao.OglasDAO;

@Path("kupac")
public class KupacService {
	
	@Context 
	ServletContext ctx;
	
	@GET
	@Path("/getAktivnogKupca")
	@Produces(MediaType.APPLICATION_JSON)
	public Kupac getKupca(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			return null;
		}
		
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		Kupac kupac = (Kupac) korisnik;
		return kupac;
	}
	
	@GET
	@Path("/getListaOmiljenih")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getListaOmiljenih(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			return null;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Oglas> list_oglas = new ArrayList<Oglas>();
		Kupac kupac = (Kupac) korisnik;
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		for (String s : kupac.getOmiljeni_oglasi()) {
			Oglas o = oglDAO.getOglasNaz(s);
			if(o != null) {
				if(o.isAktivan()) {
					list_oglas.add(o);
				}
			}
		}
		
		return list_oglas;
	}
	
	@GET
	@Path("/getListaPorucenih")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getListaPorucenih(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			return null;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Oglas> list_oglas = new ArrayList<Oglas>();
		Kupac kupac = (Kupac) korisnik;
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		for (String s : kupac.getPoruceni_oglasi()) {
			Oglas o = oglDAO.getOglasNaz(s);
			if(o != null) {
				if(o.isAktivan()) {
					list_oglas.add(o);
				}
			}
		}
		
		return list_oglas;
	}
	
	@GET
	@Path("/getListaDostavljenih")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getListaDostavljenih(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			return null;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Oglas> list_oglas = new ArrayList<Oglas>();
		Kupac kupac = (Kupac) korisnik;
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		for (String s : kupac.getUspesno_dostavljeni()) {
			Oglas o = oglDAO.getOglasNaz(s);
			if(o != null) {
				if(o.isAktivan()) {
					list_oglas.add(o);
				}
			}
		}
		
		return list_oglas;
	}
	
	
	@POST
	@Path("/postaviDostavljen")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postaviDostavljen(@QueryParam("naziv") String naziv, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			return false;
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			System.out.println("Nisi kupac");
			return false;
		}
		
		Kupac kupac = (Kupac) korisnik;
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDAO.getOglasNaz(naziv);
		if(o == null) {
			System.out.println("Nisam nasao oglas sa tim nazivom");
			return false;
		}
		
		if(kupac.getPoruceni_oglasi().remove(naziv)) {
			o.setStatus("Dostavljen");
			kupac.getUspesno_dostavljeni().add(naziv);
		}
		else {
			System.out.println("nisam uspeo da obrisem oglas iz porucenih");
			return false;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		Prodavac prodavac = (Prodavac) korDAO.findUsername(o.getVlasnik());
		if(prodavac == null) {
			System.out.println("Nisam nasao ovog prodavca sa usernameom");
			return false;
		}
		if(prodavac.getObjavljeni_oglasi().remove(o.getNaziv())) {
			prodavac.getIsporuceni_oglasi().add(o.getNaziv());
		}
		else {
			System.out.println("nisam uspeo da ga dodam u isporucene");
			return false;
		}
		
		System.out.println("status oglasa " + o.getStatus());
		// posalji automatizovanu poruku prodavcu
		Poruka poruka = new Poruka();
		poruka.setNaziv_oglasa(o.getNaziv());
		poruka.setNaslov_poruke("Proizvod uspesno dostavljen");
		poruka.setSadrzaj_poruke("Postovani, </br>Vas proizvod \"" + o.getNaziv() + "\" uspesno je dostavljen kupcu \"" + kupac.getIme()+
							" " + kupac.getPrezime() + "\".</br>Grad: " + kupac.getGrad() + "</br>E-mail adresa: " +
							kupac.getMail());
		poruka.setPosiljalac(kupac.getUsername());
		poruka.setUloga_posiljaoca(kupac.getUloga());
		poruka.setAutomatizovana(true);
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		poruka.setId(id);
		prodavac.getPoruke().add(poruka);
		
		return true;
	}
	
	@POST
	@Path("/recenzija")
	//@Consumes(MediaType.APPLICATION_JSON)
	public boolean add(Recenzija recenzija, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return false;
		}
		if (!korisnik.getUloga().equals("Kupac")) {
			System.out.println("Nije kupac prijavljen");
			return false;
		}
		Kupac kupac = (Kupac) korisnik;
		
		/*System.out.println(recenzija.getNaslov_recenzije()+", "+recenzija.getOglas()+", "+recenzija.getRecezent()
		+", "+recenzija.getSadrzaj_recenzije()+", "+recenzija.getSlika()+", "+recenzija.isTacan_opis()
		+", "+recenzija.isIspostovan_dogovor());*/
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDAO.getOglasNaz(recenzija.getOglas());
		recenzija.setAktivna(true);
		if(o == null) {
			return false;
		}
		o.getRecenzije().add(recenzija);
		
		// posalji automatizovanu poruku prodavcu
		Poruka poruka = new Poruka();
		poruka.setNaziv_oglasa(o.getNaziv());
		poruka.setNaslov_poruke("Recenzija na oglas");
		poruka.setSadrzaj_poruke("Postovani, </br>Vas proizvod \"" + o.getNaziv() + "\" recenziran je od strane kupca \"" + kupac.getIme()+
							" " + kupac.getPrezime() + "\".</br>Grad: " + kupac.getGrad() + "</br>E-mail adresa: " +
							kupac.getMail());
		poruka.setPosiljalac(kupac.getUsername());
		poruka.setUloga_posiljaoca(kupac.getUloga());
		poruka.setAutomatizovana(true);
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		poruka.setId(id);
		
		String vlasnik = o.getVlasnik();
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		Prodavac prodavac = (Prodavac)korDAO.findUsername(vlasnik);
		if(prodavac == null) {
			System.out.println("Nisam nasao ovog prodavca sa username: " + vlasnik);
			return false;
		}
		prodavac.getPoruke().add(poruka);
		
		return true;
	}
	
	@PUT
	@Path("/recenzijaIzmena")
	//@Consumes(MediaType.APPLICATION_JSON)
	public boolean edit(Recenzija recenzija, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}
		catch(NullPointerException e) {
			System.out.println("Nije niko prijavljen");
			return false;
		}
		if (!korisnik.getUloga().equals("Kupac")) {
			System.out.println("Nije kupac prijavljen");
			return false;
		}
		Kupac kupac = (Kupac) korisnik;
		
		/*System.out.println(recenzija.getNaslov_recenzije()+", "+recenzija.getOglas()+", "+recenzija.getRecezent()
		+", "+recenzija.getSadrzaj_recenzije()+", "+recenzija.getSlika()+", "+recenzija.isTacan_opis()
		+", "+recenzija.isIspostovan_dogovor());*/
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDAO.getOglasNaz(recenzija.getOglas());
		if(o == null) {
			return false;
		}
		
		Recenzija r = o.getRecenzije().get(0);
		r.setNaslov_recenzije(recenzija.getNaslov_recenzije());
		r.setSadrzaj_recenzije(recenzija.getSadrzaj_recenzije());
		if(!recenzija.getSlika().equals("")) {		// ako sam uneo novu sliku			
			r.setSlika(recenzija.getSlika());
		}
		r.setTacan_opis(recenzija.isTacan_opis());
		r.setIspostovan_dogovor(recenzija.isIspostovan_dogovor());
		
		// posalji automatizovanu poruku prodavcu
		Poruka poruka = new Poruka();
		poruka.setNaziv_oglasa(o.getNaziv());
		poruka.setNaslov_poruke("Izmena recenzije");
		poruka.setSadrzaj_poruke("Postovani, </br>Recenzija na Vas proizvod \"" + o.getNaziv() + "\" izmenjena je od strane kupca \"" + kupac.getIme()+
							" " + kupac.getPrezime() + "\".</br>Grad: " + kupac.getGrad() + "</br>E-mail adresa: " +
							kupac.getMail());
		poruka.setPosiljalac(kupac.getUsername());
		poruka.setUloga_posiljaoca(kupac.getUloga());
		poruka.setAutomatizovana(true);
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		poruka.setId(id);
		
		String vlasnik = o.getVlasnik();
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		Prodavac prodavac = (Prodavac)korDAO.findUsername(vlasnik);
		if(prodavac == null) {
			System.out.println("Nisam nasao ovog prodavca sa username: " + vlasnik);
			return false;
		}
		prodavac.getPoruke().add(poruka);
		
		return true;
	}
	
	@POST
	@Path("/recProdavac")
	@Produces(MediaType.APPLICATION_JSON)
	public void recProd(@QueryParam("oglas") String oglas, @QueryParam("lajk") boolean lajk, @Context HttpServletRequest request) {
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		
		Oglas o = oglDAO.getOglasNaz(oglas);
		String vlasnik = o.getVlasnik();
		Prodavac prodavac = (Prodavac)korDAO.findUsername(vlasnik);
		if(lajk) {
			prodavac.setLajkovi(prodavac.getLajkovi()+1);
		}
		else {
			prodavac.setDislajkovi(prodavac.getDislajkovi()+1);
		}
	}
	
	@GET
	@Path("/imaRecenziju")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean ImaRec(@QueryParam("naziv") String oglas) {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDAO.getOglasNaz(oglas);
		if(!o.getRecenzije().isEmpty() && o.getRecenzije().get(0).isAktivna()) {
			return true;
		}
		else {
			return false;
		}
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
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Kupac kupac = (Kupac)korisnik;
		for (Poruka p : kupac.getPoruke()) {
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
		if(!korisnik.getUloga().equals("Kupac")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Kupac kupac = (Kupac)korisnik;
		for (Poruka p : kupac.getPoslate()) {
			list_poruka.add(p);
		}
		
		return list_poruka;
	}
	
}
