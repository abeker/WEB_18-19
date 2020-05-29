package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
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
import beans.Kategorija;
import beans.Korisnik;
import beans.Oglas;
import beans.Poruka;
import dao.AdminDAO;
import dao.KorisnikDAO;
import dao.OglasDAO;

@Path("admin")
public class AdminService {
	
	@Context
	ServletContext ctx;
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("kategorije") == null) {
			ctx.setAttribute("kategorije", new AdminDAO());
		}
	}
	
	@POST
	@Path("/addKat")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addKat(Kategorija kategorija, @Context HttpServletRequest request ) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if (!korisnik.getUloga().equals("Administrator"))
			return;
		
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("kategorije");
		Kategorija k = adminDao.getKategorije().get(kategorija.getNaziv());
		if(k == null) {
			adminDao.getKategorije().put(kategorija.getNaziv(), kategorija);
		}
		
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		for (String oglas : kategorija.getOglasi()) {
			Oglas o = oglDAO.getOglasNaz(oglas);
			o.getKategorije().add(kategorija.getNaziv());
		}
	}
	
	@GET
	@Path("/find")
	@Produces(MediaType.APPLICATION_JSON)
	public Kategorija find(@QueryParam("naziv") String ime ) {
		//System.out.println("primio sam kao parametar: " + ime);
		
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("kategorije");
		return adminDao.find(ime);
	}
	
	@DELETE
	@Path("/remove")
	public void removeKat(@QueryParam("naziv") String naz, @Context HttpServletRequest request ) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if (!korisnik.getUloga().equals("Administrator"))
			return;
		
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("kategorije");
		Kategorija k = adminDao.find(naz);
		if(k != null)
			k.setAktivna(false);

		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		for (String oglas : k.getOglasi()) {
			Oglas o = oglDAO.getOglasNaz(oglas);
			o.getKategorije().remove(k.getNaziv());
		}
	}
	
	@PUT
	@Path("/putKat")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putKat(Kategorija novaKat, @QueryParam("naziv") String staroIme, @Context HttpServletRequest request ) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if (!korisnik.getUloga().equals("Administrator"))
			return;
		
		System.out.println("usao u PUTKAT");
		System.out.println("Novi oglasi od kat");
		for (String o : novaKat.getOglasi()) {
			System.out.println(o);
		}
		
		// za oglase koji su deselektovani skini da pripadaju toj kategoriji!!
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("kategorije");
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		Kategorija k = adminDao.find(staroIme);
		if(k != null) {
			for (String oglas : k.getOglasi()) {
				Oglas o = oglDAO.getOglasNaz(oglas);
				if(o.getKategorije().contains(k.getNaziv()))
					o.getKategorije().remove(k.getNaziv());		// => obrisi tu kategoriju iz liste kategorija tog oglasa
			}
			k.setNaziv(novaKat.getNaziv());
			k.setOpis(novaKat.getOpis());
			k.setOglasi(novaKat.getOglasi());
			for (String oglas : novaKat.getOglasi()) {
				Oglas o = oglDAO.getOglasNaz(oglas);
				o.getKategorije().add(novaKat.getNaziv());
			}
			
			adminDao.getKategorije().remove(staroIme);
			adminDao.getKategorije().put(novaKat.getNaziv(), novaKat);
		}
	}
	
	@GET
	@Path("/getKat")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getKat(@Context HttpServletRequest request) {
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("kategorije");
		ArrayList<String> k = new ArrayList<String>();
		
		//System.out.println("Kategorije su:\n");
		for (String s : adminDao.getKatNames()) {
			//System.out.println(s);	
			k.add(s);
		}
		
		return k;
	} 
	
	@PUT
	@Path("/promeniUlogu")
	public boolean promeniUlogu(@QueryParam("username") String username, @QueryParam("novaUloga") String novaUloga,
			@Context HttpServletRequest request) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(!korisnik.getUloga().equals("Administrator")) {
			return false;
		}
		
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		return korDAO.promenaUloge(username, novaUloga);
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
		if(!korisnik.getUloga().equals("Administrator")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Administrator admin = (Administrator)korisnik;
		for (Poruka p : admin.getPoruke()) {
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
		if(!korisnik.getUloga().equals("Administrator")) {
			return null;
		}
		
		ArrayList<Poruka> list_poruka = new ArrayList<Poruka>();
		Administrator admin = (Administrator)korisnik;
		for (Poruka p : admin.getPoslate()) {
			list_poruka.add(p);
		}
		
		return list_poruka;
	}
}
