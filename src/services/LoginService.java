package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Korisnik;
import beans.Kupac;
import dao.AdminDAO;
import dao.KorisnikDAO;

@Path("")
public class LoginService {

	@Context
	ServletContext ctx;
	
	public LoginService() {
		
	}
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("korisnici") == null) {
			ctx.setAttribute("korisnici", new KorisnikDAO());
		}
		if (ctx.getAttribute("kategorije") == null) {
			ctx.setAttribute("kategorije", new AdminDAO());
		}
	}
	
	@POST
	@Path("/registracija")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean registracija(Kupac k, @Context HttpServletRequest request) {
		System.out.println(">>> USAO U REGISTRACIJU <<<");
		
		KorisnikDAO korisnici = (KorisnikDAO) ctx.getAttribute("korisnici");	// uzmem listu iz konteksta aplikacije
		Kupac kupac = (Kupac) korisnici.add(k);		// dodam tog jednog korisnika
		
		if(kupac == null) {
			return false;
		}
		return true;
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int login(Korisnik k, @Context HttpServletRequest request) {
		System.out.println("login " + k.getUsername() + " " + k.getPassword());
		
		KorisnikDAO korisnici = (KorisnikDAO) ctx.getAttribute("korisnici");
		Korisnik logovani = korisnici.find(k.getUsername(), k.getPassword());
		
		if (logovani == null) {
			System.out.println("Nije pronadjen taj korisnik!!");
			
			return -1;		// -1 => nije se ulogovao
		}
		
		request.getSession().setAttribute("ulogovan", logovani);	// dobro se logovao -> otvorim mu sesiju
		System.out.println(request.getSession().getId() + "\nULOGA:  " + logovani.getUloga());
		
		if(logovani.getUloga().equals("Administrator")) {
			return 1;
		}
		else if(logovani.getUloga().equals("Prodavac")) {
			return 2;
		}
		else {
			return 3;
		}
	}
	
	@POST
	@Path("/logout")
	public void logout(@Context HttpServletRequest request) {
		request.getSession().invalidate();
	}
	
	@GET
	@Path("/getKorisnike")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> getKorisnike() {
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		return korDAO.getKorisnike();
	}
	
	@GET
	@Path("/getAktivan")
	@Produces(MediaType.APPLICATION_JSON)
	public Korisnik getAktivan(@Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
			return korisnik;
		} catch (NullPointerException e) {
			return null;		// niko nije ulogovan
		}
	}
	
	@GET
	@Path("/proveraUsername/{username}")
	public boolean proveriUsername(@PathParam("username")String username) {
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		return korDAO.proveraUsername(username);		// true- okej je sve(ne postoji taj username)
	}													// false- postoji taj username	
}
