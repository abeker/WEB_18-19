var aktivniKorisnik;
function aktivnost(){
    $.ajax({
        type : "get",
        url : "rest/oglasi/aktivniKorisnik",
        success : function(response){
            console.log("aktivan je: " + response);
            if(response == 1){
                aktivniKorisnik = "Administrator";
            }
            else if(response == 2){
                aktivniKorisnik = "Prodavac";
            }
            else if(response == 3){
                aktivniKorisnik = "Kupac";
            }
            else{
                aktivniKorisnik = "";
            }
        }
    });
}

/* ****************** DODAVANJE RECENZIJE U KORISNICKU PRODAVCA *******************/
function addRec(rec){
    var tr = $('<tr id="'+rec.oglas+'" class="'+rec.recezent+'"></tr>');
    var oglas = $('<td>'+rec.oglas+'</td>');
    var naslov = $('<td>'+rec.naslov_recenzije+'</td>');
    var o = "Da";
    var dog = "Da";
    console.log(rec.tacan_opis + ", "+rec.ispostovan_dogovor);
    if(rec.tacan_opis == false){
        o = "Ne";
    }
    if(rec.ispostovan_dogovor == false){
        dog = "Ne";
    }
    var opis = $('<td>'+o+'</td>');
    var dogovor = $('<td>'+dog+'</td>');
    var recezent = $('<td>'+rec.recezent+'</td>');

    var izmeni = $('<td></td>');
    var obrisi = $('<td></td>');
    if(rec.recezent === akt.username){
        izmeni = $('<td><img id="izmeniR" class="'+rec.oglas+'" style="cursor: pointer" src="slike/edit.png" width="24" height="24"></td>');
        obrisi = $('<td><img id="obrisiR" class="'+rec.oglas+'" style="cursor: pointer" src="slike/remove.png" width="24" height="24"></td>');
    }

    tr.append(oglas).append(naslov).append(opis).append(dogovor).append(recezent).append(izmeni)
    .append(obrisi);
    
    $('#tab_recenzija tbody').append(tr);
}

function resetujPolja(){
    $('input[name="oNaziv"]').val('');
    $('input[name="oCena"]').val('');
    $('#oOpis').val('');
    $('input[name="oGrad"]').val('');
    $('input[name="oDatum"]').val('');
    $('#oSlika').val('');
}

$(document).ready(function(){

/* *********************************** DODAVANJE OGLASA **************************************** */
$(document).on("click", "#post_oglas", function(){
    $.ajax({
        type : "get",
        url : "rest/prodavac/getPrijave",
        contentType : "application/json",
        success : function(response){
            console.log('vratio: '+response);
            if(response == 1){
                console.log('MOZE da dodaje');
                $('#mod_oglas').css("display", "block");
                resetujPolja();
            }
            else if(response == -1){
                console.log('NE MOZE da dodaje');
                alert('Nije moguce dodavati nove oglase, jer je nalog markiran kao sumnjiv.');
            }
            else{
                alert('Doslo je do problema nepoznate prirode.');
            }
        }
    });
});

$('button#oglas_dodaj').click(function(){
    $('#mod_oglas').css("display", "none");

    var oglas = new Object();
    oglas.naziv = $('input[name="oNaziv"]').val();
    oglas.cena = $('input[name="oCena"]').val();
    oglas.opis = $('#oOpis').val();
    oglas.grad = $('input[name="oGrad"]').val();
    var dat = new Date($('input[name="oDatum"]').val());
    oglas.datum = dat.getTime();
    console.log(oglas.naziv+", "+oglas.cena+", "+oglas.opis+", "+oglas.grad+", "+oglas.datum);

    var greska = false;
    if(!oglas.naziv){
        alert('Morate uneti naziv oglasa.');
        greska = true;
        $('input[name="oNaziv"]').focus();
    }
    else if(!oglas.cena){
        alert('Morate uneti cenu oglasa.');
        greska = true;
        $('input[name="oCena"]').focus();
    }
    else if(!oglas.opis){
        alert('Morate uneti opis oglasa.');
        greska = true;
        $('#oOpis').focus();
    }

    if(!greska){
        preuzmiSliku(posaljiOglas, oglas);
    }

});

});

/* *************************** PREUZIMANJE SLIKE OGLAS ************************************ */  
function preuzmiSliku(posaljiOglas, oglas) {
	var fajlSlike;
	if (($("#oSlika"))[0].files.length > 0 ) {
	    fajlSlike = ($("#oSlika"))[0].files[0];
        
        console.log(fajlSlike.name);
	    var formData = new FormData();
	    formData.append("fajl", fajlSlike);
	    formData.append("nazivFajla", fajlSlike.name);

	    $.ajax({
	       url: "rest/oglasi/preuzmiSliku",
	       type: "POST",
	       data: formData,
	       processData: false,
	       contentType: false,
	       success: function(response) {
                console.log(response);
                if(response){
                    oglas.slika = "slike/"+($("#oSlika"))[0].files[0].name;
                    posaljiOglas(oglas);
                }
                else{
                    alert('Doslo je do greske prilikom ucitavanja slike.');
                }
	       }
	    });
	    
	} else {
        console.log('Usao sam u else...');
        alert('Obavezno je uneti sliku proizvoda.');

		/*//Check if editing or creating new
		if ($('#imageName').val()){
			newAd.imageSource = "data/images/" + $('#imageName').val();
			proceed(newAd);
		} else {
			$('div#imageErrorDiv').removeAttr('hidden');
			$('small#imageHelp').text('Ad must have an image.');
		}*/	
	}
}

function posaljiOglas(oglas){
	$.ajax({
		type: 'post',
		url: 'rest/prodavac/add',
		contentType: 'application/json',
		data: JSON.stringify({
			"naziv": oglas.naziv,
			"cena" : oglas.cena,
			"opis": oglas.opis,
			"slika": oglas.slika,
	        "datum_isticanja": oglas.datum,
	        "grad": oglas.grad
		}),
		success: function(response) {
            console.log(response);
            if(response){
                alert('Oglas je uspesno dodat!');
                refreshOglase();
            }
            else{
                alert('Oglas nije dodat!');
            }
		}
	});
}

/* *********************** PREUZIMANJE SLIKE RECENZIJA(DODAVANJE) ***************************** */
function preuzmiSlikuRecenzija(posaljiRec, obj) {
	var fajlSlike;
	if (($("#recSlika"))[0].files.length > 0 ) {
	    fajlSlike = ($("#recSlika"))[0].files[0];
        
        //console.log(fajlSlike.name);
	    var formData = new FormData();
	    formData.append("fajl", fajlSlike);
	    formData.append("nazivFajla", fajlSlike.name);

	    $.ajax({
	       url: "rest/oglasi/preuzmiSliku",
	       type: "POST",
	       data: formData,
	       processData: false,
	       contentType: false,
	       success: function(response) {
                console.log(response);
                if(response){
                    obj.slika = "slike/"+($("#recSlika"))[0].files[0].name;
                    posaljiRec(obj);
                }
                else{
                    alert('Doslo je do greske prilikom ucitavanja slike.');
                }
	       }
	    });
	    
	} else {
        console.log('Usao sam u else...');
        obj.slika = "";
        posaljiRec(obj);
    }
}

function posaljiRec(obj){
    dodata_rec = true;
    $.ajax({
		type: 'post',
		url: 'rest/kupac/recenzija',
		contentType: 'application/json',
		data: JSON.stringify({
            "oglas": obj.oglas,
            "recezent" : obj.recezent,
			"naslov_recenzije" : obj.naslov,
			"sadrzaj_recenzije": obj.sadrzaj,
			"slika": obj.slika,
	        "tacan_opis": obj.tacan_opis,
	        "ispostovan_dogovor": obj.dogovor
		}),
		success: function(response) {
            //console.log(response);
            if(response){
                $('#recOglas').css("display", "none");
                $('#recProdavac').css("display", "block");
                //alert('Recenzija je uspesno dodata!');
            }
            else{
                $('#recOglas').css("display", "none");
                $('#recProdavac').css("display", "block");
                //alert('Recenzija nije dodata!');
            }
		}
	});
}

/* *********************** PREUZIMANJE SLIKE RECENZIJA(IZMENA) ***************************** */
function preuzmiSlikuRec(posaljiRecenziju, obj) {
    console.log('izm rec preuz sliku');
	var fajlSlike;
	if (($("#recenSlika"))[0].files.length > 0 ) {
	    fajlSlike = ($("#recenSlika"))[0].files[0];
        
        //console.log(fajlSlike.name);
	    var formData = new FormData();
	    formData.append("fajl", fajlSlike);
	    formData.append("nazivFajla", fajlSlike.name);

	    $.ajax({
	       url: "rest/oglasi/preuzmiSliku",
	       type: "POST",
	       data: formData,
	       processData: false,
	       contentType: false,
	       success: function(response) {
                console.log(response);
                if(response){
                    obj.slika = "slike/"+($("#recenSlika"))[0].files[0].name;
                    posaljiRecenziju(obj);
                }
                else{
                    alert('Doslo je do greske prilikom ucitavanja slike.');
                }
	       }
	    });
	    
	} else {
        console.log('Usao sam u else... izmena');
        obj.slika = "";
        posaljiRecenziju(obj);
    }
}

function posaljiRecenziju(obj){
    console.log('saljem rec');
    $.ajax({
		type: 'put',
		url: 'rest/kupac/recenzijaIzmena',
		contentType: 'application/json',
		data: JSON.stringify({
            "oglas": obj.oglas,
            "recezent" : obj.recezent,
			"naslov_recenzije" : obj.naslov,
			"sadrzaj_recenzije": obj.sadrzaj,
			"slika": obj.slika,
	        "tacan_opis": obj.tacan_opis,
	        "ispostovan_dogovor": obj.dogovor
		}),
		success: function(response) {
            alert('Uspesno ste izmenili recenziju na proizvod ['+obj.oglas+']');
            refreshRecenzije();
            $('#oglRecenzija').hide();
            $('#oglRecenzijaIzm').show();
            $('#oglRecenzijaObr').show();
		}
	});
}

function refreshRecenzije(){
    $.ajax({
        type : "get",
        url : "rest/prodavac/getRecProdavca?naziv="+aktivni_oglas,
        contentType : "application/json",
        success : function(response){
            //console.log("response: "+response);
            if(!jQuery.isEmptyObject(response)){
                $('#tab_recenzija tbody').empty();
                for(var rec of response){
                    addRec(rec);
                }
            }
            else{
                $('#tab_recenzija tbody').empty();
                console.log('nema rec');
            }
        }
    });
}