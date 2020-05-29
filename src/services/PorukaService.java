package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Administrator;
import beans.Korisnik;
import beans.Kupac;
import beans.Poruka;
import beans.Prodavac;
import dao.KorisnikDAO;

@Path("poruka")
public class PorukaService {
	
	@Context
	ServletContext ctx;
	
	@POST
	@Path("/posaljiProdavcu")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean posaljiProdavcu(Poruka poruka,@QueryParam("prodavac")String prodavac, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return false;
		}
		if(!korisnik.getUloga().equals("Kupac") && !korisnik.getUloga().equals("Administrator")) {
			return false;
		}
		
		Poruka por = new Poruka();
		por.setNaslov_poruke(poruka.getNaslov_poruke());
		por.setNaziv_oglasa(poruka.getNaziv_oglasa());
		por.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
		por.setPosiljalac(korisnik.getUsername());
		por.setUloga_posiljaoca(korisnik.getUloga());
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		por.setId(id);
		por.setPrimalac(prodavac);
		
		Prodavac primalac = (Prodavac)korDAO.findUsername(prodavac);
		primalac.getPoruke().add(por);
		if(korisnik.getUloga().equals("Kupac")) {
			((Kupac)korisnik).getPoslate().add(por);			
		}
		else {
			((Administrator)korisnik).getPoslate().add(por);
		}
		
		
		return true;
	}
	
	@POST
	@Path("/posaljiKupcu")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean posaljiKupcu(Poruka poruka,@QueryParam("kupac")String kupac, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return false;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return false;
		}
		
		Poruka por = new Poruka();
		por.setNaslov_poruke(poruka.getNaslov_poruke());
		por.setNaziv_oglasa(poruka.getNaziv_oglasa());
		por.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
		por.setPosiljalac(korisnik.getUsername());
		por.setUloga_posiljaoca(korisnik.getUloga());
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		por.setId(id);
		por.setPrimalac(kupac);
		
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		Kupac primalac = (Kupac)korDAO.findUsername(kupac);
		primalac.getPoruke().add(por);
		((Prodavac)korisnik).getPoslate().add(por);			
		
		return true;
	}
	
	@GET
	@Path("/getPoruku")
	@Produces(MediaType.APPLICATION_JSON)
	public Poruka getPoruku(@QueryParam("id") int id, @Context HttpServletRequest request) {
		Korisnik k;
		try {
			k = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return null;
		}
		
		if(k.getUloga().equals("Administrator")) {
			System.out.println("admin je");
			Administrator admin = (Administrator)k;
			for (Poruka p : admin.getPoruke()) {
				if(id == p.getId()) {
					System.out.println("inbox");
					if(!p.getPosiljalac().equals(k.getUsername())) {	// pod uslovom da ja nisam posiljalac(da ne citam svoje poslate)
						p.setProcitana(true); 				// ovde setujem da je poruka procitana
					}
					return p;
				}
			}
			for (Poruka p : admin.getPoslate()) {
				if(id == p.getId()) {
					System.out.println("outbox");
					return p;
				}
			}
		}
		if(k.getUloga().equals("Prodavac")) {
			System.out.println("Prodavac je");
			Prodavac prodavac = (Prodavac)k;
			for (Poruka p : prodavac.getPoruke()) {
				if(id == p.getId()) {
					System.out.println("inbox");
					if(!p.getPosiljalac().equals(k.getUsername())) {
						p.setProcitana(true); 				// ovde setujem da je poruka procitana
					}
					return p;
				}
			}
			for (Poruka p : prodavac.getPoslate()) {
				if(id == p.getId()) {
					System.out.println("outbox");
					System.out.println(p.getNaziv_oglasa() +", "+p.getNaslov_poruke());
					return p;
				}
			}
		}
		if(k.getUloga().equals("Kupac")) {
			System.out.println("kupac je");
			Kupac kupac = (Kupac)k;
			for (Poruka p : kupac.getPoruke()) {
				if(id == p.getId()) {
					System.out.println("inbox");
					if(!p.getPosiljalac().equals(k.getUsername())) {
						p.setProcitana(true); 				// ovde setujem da je poruka procitana
					}
					return p;
				}
			}
			for (Poruka p : kupac.getPoslate()) {
				if(id == p.getId()) {
					System.out.println("outbox");
					return p;
				}
			}
		}
	
		return null;
	}
	
	@DELETE
	@Path("/deletePoruku")											// brisem primljene poruke
	public boolean obrisiPor(@QueryParam("id") int id, @Context HttpServletRequest request) {
		Korisnik k;
		try {
			k = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return false;
		}
		
		if(k.getUloga().equals("Administrator")) {
			Administrator admin = (Administrator)k;
			for (Poruka p : admin.getPoruke()) {	// trazim i u primljenim i u poslatim 
				if(id == p.getId()) {				// jer ne znam unapred da li brisem iz inboxa ili outboxa
					return admin.getPoruke().remove(p);
				}
			}
			for (Poruka p : admin.getPoslate()) {
				if(id == p.getId()) {
					return admin.getPoslate().remove(p);		// obrisi iz outboxa
				}
			}
		}
		if(k.getUloga().equals("Prodavac")) {
			Prodavac prodavac = (Prodavac)k;
			for (Poruka p : prodavac.getPoruke()) {
				if(id == p.getId()) {
					return prodavac.getPoruke().remove(p);
				}
			}
			for (Poruka p : prodavac.getPoslate()) {
				if(id == p.getId()) {
					return prodavac.getPoslate().remove(p);
				}
			}
		}
		if(k.getUloga().equals("Kupac")) {
			Kupac kupac = (Kupac)k;
			for (Poruka p : kupac.getPoruke()) {
				if(id == p.getId()) {
					return kupac.getPoruke().remove(p);
				}
			}
			for (Poruka p : kupac.getPoslate()) {
				if(id == p.getId()) {
					return kupac.getPoslate().remove(p);
				}
			}
		}
		
		return false;
	}
	
	@POST
	@Path("/posaljiPoruku")
	@Consumes(MediaType.APPLICATION_JSON)
	public int posaljiPoruku(Poruka poruka,@QueryParam("primalac")String primalac, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return -1;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		boolean nasao = false;
		for (Korisnik k : korDAO.getKorisnici().values()) {
			if(k.getUsername().toLowerCase().equals(primalac)) {
				nasao = true;
				break;
			}
		}
		
		if(!nasao) {
			return -2;			// nema tog username-a
		}
		
		Poruka por = new Poruka();
		por.setNaslov_poruke(poruka.getNaslov_poruke());
		por.setNaziv_oglasa(poruka.getNaziv_oglasa());
		por.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
		por.setPosiljalac(korisnik.getUsername());
		por.setUloga_posiljaoca(korisnik.getUloga());
		Random rand = new Random();
		int id = rand.nextInt(Integer.MAX_VALUE);			// napravim random broj
		por.setId(id);										// => postavim ga za id poruke
		por.setPrimalac(primalac);

		// dodajem poruku u outbox onoga koji je poslao
		if(korisnik.getUloga().equals("Administrator")) {
			((Administrator)korisnik).getPoslate().add(por);
		}
		else if(korisnik.getUloga().equals("Prodavac")) {
			((Prodavac)korisnik).getPoslate().add(por);
		}
		else {
			((Kupac)korisnik).getPoslate().add(por);
		}
		
		// dodajem u inbox nekog korisnika
		for (Korisnik k : korDAO.getKorisnici().values()) {
			if(k.getUsername().equals(primalac)) {
				if(k.getUloga().equals("Administrator")) {
					((Administrator)k).getPoruke().add(por);
					break;
				}
				else if(k.getUloga().equals("Prodavac")) {
					((Prodavac)k).getPoruke().add(por);
					break;
				}
				else if(k.getUloga().equals("Kupac")) {
					((Kupac)k).getPoruke().add(por);
					break;
				}
			}
		}
		
		return 1;
	}
	
	@PUT
	@Path("/izmenaPoslate")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean izmenaPoslate(Poruka poruka, @QueryParam("id") int id, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return false;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		if(korisnik.getUloga().equals("Administrator")) {
			Administrator admin = (Administrator)korisnik;
			for (Poruka p : admin.getPoslate()) {		// menjam u svom outboxu
				if(id == p.getId()) {
					p.setPrimalac(poruka.getPrimalac());
					p.setNaziv_oglasa(poruka.getNaziv_oglasa());
					p.setNaslov_poruke(poruka.getNaslov_poruke());
					p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
				}
			}
			Korisnik k = korDAO.findUsername(poruka.getPrimalac());
			if(k.getUloga().equals("Administrator")) {
				Administrator a = (Administrator)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu admina
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
			else if(k.getUloga().equals("Kupac")) {
				Kupac a = (Kupac)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu kupca
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
			else {
				Prodavac a = (Prodavac)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu kupca
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
		}
		
		
		else if(korisnik.getUloga().equals("Prodavac")) {
			Prodavac prodavac = (Prodavac)korisnik;
			for (Poruka p : prodavac.getPoslate()) {		// menjam u svom outboxu
				if(id == p.getId()) {
					p.setPrimalac(poruka.getPrimalac());
					p.setNaziv_oglasa(poruka.getNaziv_oglasa());
					p.setNaslov_poruke(poruka.getNaslov_poruke());
					p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
				}
			}
			Korisnik k = korDAO.findUsername(poruka.getPrimalac());
			if(k.getUloga().equals("Administrator")) {
				Administrator a = (Administrator)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu admina
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
			else if(k.getUloga().equals("Kupac")) {
				Kupac a = (Kupac)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu kupca
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
		}
		
		
		else {
			Kupac kupac = (Kupac)korisnik;
			for (Poruka p : kupac.getPoslate()) {		// menjam u svom outboxu
				if(id == p.getId()) {
					p.setPrimalac(poruka.getPrimalac());
					p.setNaziv_oglasa(poruka.getNaziv_oglasa());
					p.setNaslov_poruke(poruka.getNaslov_poruke());
					p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
				}
			}
			Korisnik k = korDAO.findUsername(poruka.getPrimalac());
			if(k.getUloga().equals("Prodavac")) {
				Prodavac a = (Prodavac)k;
				for (Poruka p : a.getPoruke()) {		// menjam u inboxu prodavca
					if(id == p.getId()) {
						p.setPrimalac(poruka.getPrimalac());
						p.setNaziv_oglasa(poruka.getNaziv_oglasa());
						p.setNaslov_poruke(poruka.getNaslov_poruke());
						p.setSadrzaj_poruke(poruka.getSadrzaj_poruke());
					}
				}
			}
		}
		
		return true;
	}
	
	@GET
	@Path("/getPoslate")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Poruka> getPoslate(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		if(korisnik.getUloga().equals("Administrator")) {
			Administrator admin = (Administrator)korisnik;
			for (Poruka poruka : admin.getPoslate()) {
				list_poruka.add(poruka);
			}
		}
		else if(korisnik.getUloga().equals("Prodavac")) {
			Prodavac prodavac = (Prodavac)korisnik;
			for (Poruka poruka : prodavac.getPoslate()) {
				list_poruka.add(poruka);
			}
		}
		else {
			Kupac kupac = (Kupac)korisnik;
			for (Poruka poruka : kupac.getPoslate()) {
				list_poruka.add(poruka);
			}
		}
		return list_poruka;
	}
	
	@GET
	@Path("/getPrimljene")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Poruka> getPrimljene(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik)request.getSession().getAttribute("ulogovan");
		}catch (NullPointerException e) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		if(korisnik.getUloga().equals("Administrator")) {
			Administrator admin = (Administrator)korisnik;
			for (Poruka poruka : admin.getPoruke()) {
				list_poruka.add(poruka);
			}
		}
		else if(korisnik.getUloga().equals("Prodavac")) {
			Prodavac prodavac = (Prodavac)korisnik;
			for (Poruka poruka : prodavac.getPoruke()) {
				list_poruka.add(poruka);
			}
		}
		else {
			Kupac kupac = (Kupac)korisnik;
			for (Poruka poruka : kupac.getPoruke()) {
				list_poruka.add(poruka);
			}
		}
		return list_poruka;
	}
	
	
	
}
