package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import beans.Korisnik;
import beans.Kupac;
import beans.Oglas;
import beans.Prodavac;
import dao.KorisnikDAO;
import dao.OglasDAO;

@Path("oglasi")
public class OglasService {
	
	@Context
	ServletContext ctx;
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("oglasi") == null) {
			ctx.setAttribute("oglasi", new OglasDAO());
		}
		if (ctx.getAttribute("korisnici") == null) {
			ctx.setAttribute("korisnici", new KorisnikDAO());
		}
	}
	
	@GET
	@Path("/getPretraga")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getPretraga(@QueryParam("naziv") String naziv, @QueryParam("cenaOd") String cenaOd, @QueryParam("cenaDo") String cenaDo,
			@QueryParam("ocenaOd") String ocenaOd, @QueryParam("ocenaDo") String ocenaDo, @QueryParam("datumOd") String datumOd, 
			@QueryParam("datumDo") String datumDo, @QueryParam("grad") String grad, @QueryParam("status") String status){
		
		/*System.out.println(naziv+", "+cenaOd+", "+cenaDo+", "+ocenaOd+", "+ocenaDo+", "+datumOd+", "+datumDo+", "+
				grad+", "+status);*/
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getPretragaOglase(naziv, cenaOd, cenaDo, ocenaOd, ocenaDo, datumOd, datumDo, grad, status);
	}
	
	@GET
	@Path("/pretragaKorisnika")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> pretragaKor(@QueryParam("ime") String ime, @QueryParam("grad") String grad){
		KorisnikDAO korDAO = (KorisnikDAO)ctx.getAttribute("korisnici");
		return korDAO.pretragaKorisnika(ime, grad);
	}
	
	@GET
	@Path("/findAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> findAll(@Context HttpServletRequest request){
		//System.out.println(">> usao u oglService fidAll <<");
		boolean admin = false;
		Korisnik korisnik;
		try{
			korisnik = (Korisnik)request.getSession(false).getAttribute("ulogovan");
			if(korisnik.getUloga().equals("Administrator")) {		// ako je admin, on na pocetnoj vidi i aktivne
				admin = true;										// i dostavljene i u realizaciji
			}
		}catch (NullPointerException e) {
			admin = false;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		ArrayList<Oglas> list_oglasa = new ArrayList<Oglas>();
		
		for (Oglas oglas : oglDAO.findAll()) {
			if(admin == false) {
				if(oglas.isAktivan() && oglas.getStatus().equals("Aktivan")) {
					list_oglasa.add(oglas);
				}
			}else {
				if(oglas.isAktivan()) {			// prikazujem i dostavljene i one u realizaciji
					list_oglasa.add(oglas);
				}
			}
		}
		
		if(admin == false) {
			KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
			for (Oglas o : list_oglasa) {
				for (Korisnik kor : korDAO.getKorisnici().values()) {
					if(kor.getUloga().equals("Prodavac")) {
						if(o.getVlasnik().equals(kor.getUsername())) {
							Prodavac p = (Prodavac)kor;
							p.getObjavljeni_oglasi().add(o.getNaziv());
						}					
					}
				}
			}
		}
		return list_oglasa;
	}
	
	@GET
	@Path("/getKategorijuOglasa")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> getKategorijuOglasa(@QueryParam("naziv") String naziv){
		System.out.println("Primio parametar: " + naziv);
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getKategorijuOglasa(naziv);
	}
	
	@POST
	@Path("/preuzmiSliku")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public boolean preuzmiSliku(@FormDataParam("fajl") InputStream uploadedInputStream,
			@FormDataParam("nazivFajla") String nazivSlike) {
		
		String uploadedFileLocation = ctx.getRealPath("/slike/" + nazivSlike);
		
		//System.out.println("ime fajla je:" + nazivSlike);
		//System.out.println("putanja do fajla je:" + uploadedFileLocation);
		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			//System.out.println("upao u catch");
			e.printStackTrace();
		}
		
		//System.out.println("VRACAM TRUE, preuzimanje slike");
		return true;
	}
	
	@GET
	@Path("/aktivniKorisnik")
	@Produces(MediaType.APPLICATION_JSON)
	public int aktivniKorisnik(@Context HttpServletRequest request) {
		Korisnik korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		if(korisnik.getUloga().equals("Kupac")) {
			return 3;
		}
		else if(korisnik.getUloga().equals("Prodavac")) {
			return 2;
		}
		else if(korisnik.getUloga().equals("Administrator")) {
			return 1;
		}
		else
			return -1;
	}
	
	@GET
	@Path("/getGradovi")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getGrad() {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getGradove();
	}
	
	@GET
	@Path("/getGradoveKorisnika")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getGradove() {
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		return korDAO.getGradoveKorisnika();
	}
	
	@GET
	@Path("/getOglaseKorisnika")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getOglaseKorisnika(@QueryParam("username") String username) {
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		return korDAO.getGradoveKorisnika();
	}
	
	@GET
	@Path("/getOglasNaziv")
	@Produces(MediaType.APPLICATION_JSON)
	public Oglas getOglasNaziv(@QueryParam("naziv") String naziv) {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		return oglDAO.getOglasNaz(naziv);
	}
	
	@POST
	@Path("/dodajLajk")
	@Produces(MediaType.APPLICATION_JSON)
	public int dodajLajk(@QueryParam("naziv") String naziv, @QueryParam("lajk") int lajk, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch(NullPointerException e) {
			return -1;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o.isAktivan() && o.getNaziv().equals(naziv)) {
				if(lajk == 1) {
					if(!dodelioLajk(korisnik.getUsername(), naziv)) {
						o.setLajkovi(o.getLajkovi()+1);
						return 1;
					}
					else {						// ako sam vec dodelio like -> skini like
						o.setLajkovi(o.getLajkovi()-1);
						o.getKorisnici_lajkovi().remove(korisnik.getUsername());
						return -2;			// dodelio si vec lajk
					}
				}
				else {
					if(!dodelioDislajk(korisnik.getUsername(), naziv)) {
						o.setDislajkovi(o.getDislajkovi()+1);
						return 1;
					}
					else {						// ako sam vec dodelio dislike -> skini dislike
						o.setDislajkovi(o.getDislajkovi()-1);
						o.getKorisnici_dislajkovi().remove(korisnik.getUsername());
						return -3; 		// dodelio si vec dislajk
					}
				}
			}
		}
		
		return -1;			// nepoznata greska
	}
	
	public boolean dodelioLajk(String username, String oglas) {
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o.getNaziv().equals(oglas)) {
				if(o.getKorisnici_lajkovi().contains(username)) {
					return true;			// dodelio lajk
				}
				else {
					o.getKorisnici_lajkovi().add(username);
					return false;
				}
			}
		}
		return false;
	}
	
	public boolean dodelioDislajk(String username, String oglas) {
		OglasDAO oglDAO = (OglasDAO)ctx.getAttribute("oglasi");
		for (Oglas o : oglDAO.getOglasi().values()) {
			if(o.getNaziv().equals(oglas)) {
				if(o.getKorisnici_dislajkovi().contains(username)) {
					return true;			// dodelio dislajk
				}
				else {
					o.getKorisnici_dislajkovi().add(username);
					return false;
				}
			}
		}
		return false;
	}
	
	@POST
	@Path("/oznaciOmiljeni")
	@Produces(MediaType.APPLICATION_JSON)
	public int oznaciOmiljeni(@QueryParam("oglasNaz") String nazivOgl, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		} catch (NullPointerException e) {
			return -1;		// niko nije ulogovan
		}
		
		if(!korisnik.getUloga().equals("Kupac")) {
			return -2;
		}
		
		boolean flag = false;
		Kupac kupac = (Kupac) korisnik;
		for (String s : kupac.getOmiljeni_oglasi()) {			// provera: dodao sam ga vec u omiljene
			if(s.equals(nazivOgl)) {
				flag = true;
			}
		}
		
		if(flag) {
			return -3;
		}
		kupac.getOmiljeni_oglasi().add(nazivOgl);
		
		/*System.out.println("ovo su omiljeni");
		for (String s : kupac.getOmiljeni_oglasi()) {			
			System.out.println(s + ", ");
		}*/
		
		return 1;
	}
	
	@POST
	@Path("/poruciProizvod")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean poruciProizvod(@QueryParam("naziv") String naziv, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		} catch (NullPointerException e) {
			return false;		// niko nije ulogovan
		}
		if(!korisnik.getUloga().equals("Kupac")) {
			return false;
		}
		
		boolean flag = false;
		Kupac kupac = (Kupac) korisnik;
		for (String s : kupac.getPoruceni_oglasi()) {			// provera: dodao sam ga vec u porucene
			if(s.equals(naziv)) {
				flag = true;
			}
		}
		if(flag) {
			return false;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		Oglas o = oglDAO.setPorucen(naziv);
		if(o == null) {
			return false;
		}
		System.out.println("Oglas ima status: " + o.getStatus());
		kupac.getPoruceni_oglasi().add(naziv);
		
		return true;
	}
	
	@GET
	@Path("/proveraVlasnika")
	public boolean checkVlasnik(@QueryParam("naziv") String naziv, @Context HttpServletRequest request) {
		Korisnik korisnik;
		try {
			korisnik = (Korisnik) request.getSession(false).getAttribute("ulogovan");
		}catch(NullPointerException e) {
			return false;
		}
		if(!korisnik.getUloga().equals("Prodavac")) {
			return false;
		}
		
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		if(korisnik.getUsername().equals(oglDAO.getOglasNaz(naziv).getVlasnik())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@GET
	@Path("/najpopularnijiOglasi")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Oglas> najpopularnijiOglasi(@Context HttpServletRequest request) {
		OglasDAO oglDAO = (OglasDAO) ctx.getAttribute("oglasi");
		KorisnikDAO korDAO = (KorisnikDAO) ctx.getAttribute("korisnici");
		
		ArrayList<Oglas> list_najpopularniji = new ArrayList<Oglas>();
		for (Oglas o : oglDAO.getOglasi().values()) {				// dodelim neki koef popularnosti svakom oglasu
			o.setPopularnost(0);
			for (Korisnik k : korDAO.getKorisnici().values()) {
				if(k.getUloga().equals("Kupac")) {
					if( ((Kupac)k).getOmiljeni_oglasi().contains(o.getNaziv()) ) {
						o.setPopularnost(o.getPopularnost()+1);
					}
				}
			}			
		}

		ArrayList<Oglas> pom_list = new ArrayList<Oglas>();
		for (Oglas o : oglDAO.getOglasi().values()) {			// prepisem listu oglasa u pomocnu listu
			pom_list.add(o);
		}

		for(int i = 0; i < 10; i++) {		// uzmem 10 'najpopularnijih'
			int max=0;
			String naziv = "";
			for (Oglas o : pom_list) {
				if(o.getPopularnost()>=max) {
					max = o.getPopularnost();			// ovde sam nasao max vrednost za popularnost
					naziv = o.getNaziv();
				}
			}
			list_najpopularniji.add(oglDAO.getOglasNaz(naziv));
			pom_list.remove(oglDAO.getOglasNaz(naziv));
		}
		
		System.out.println("Najpopularniji oglasi: ");
		for (Oglas o : list_najpopularniji) {
			System.out.println(o.getNaziv()+" : "+o.getPopularnost());
		}
		
		return list_najpopularniji;
	}
	
}
