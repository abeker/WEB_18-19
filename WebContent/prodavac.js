var brojacProizvoda = 0;
function add3r(proizvod){
    if((brojacProizvoda % 3) === 0){        // u red idu 3 oglasa
        $('#glavna_strana tbody').append($('<tr></tr>'));
    }

    var td = $('<td class="'+ proizvod.naziv +'" style="border: 1px solid black">' +
                '<h2 align="center" style="color: darkgreen">'+ proizvod.naziv + '</h2>' +
                '<img height=200 width=150 style="margin-left: 10%" src="'+ proizvod.slika +'">' +
                '</br><p style="max-width: 200px">'+ proizvod.opis +'</p>' +
                '<h3 align="right" style="color: darkgreen">'+  proizvod.cena +' rsd</h3></br></td>');
    
    console.log(proizvod.status);
    var status_ogl = proizvod.status === "Dostavljen" || proizvod.status === "U realizaciji";
    if(aktivanKorisnik === "Prodavac" && status_ogl){
        // prodavac ne moze da brise oglase koji su 'u realizaciji' i 'dostavljeni'
    }
    else{
        var izmena = $('<img class="izmena" src="slike/edit.png" width="24" height="24" align="left" style="cursor: pointer">');
        var obrisi = $('<img class="obrisi" src="slike/remove.png" width="24" height="24" align="right" style="cursor: pointer">');
        td.append(izmena).append(obrisi);
    }

    td.click(clickTd(proizvod.naziv));
    $("#glavna_strana tbody:last-child").append(td);
    brojacProizvoda += 1;
}

function sakrijDiv(){
    $('div#div_prijava').hide();
    $('div#dugme_reg').hide();
    $('div#div_kategorije').hide();
    $('div#glavna_strana').hide();
    $('div#div_registracija').hide();
    $('div#zaglavlje_prodavac').hide();
    $('div#t_korisnici').hide();
    $('div#t_recenzija').hide();
    $('div#kupac_rec').hide();
    $('div#detalji_oglasa').hide();
    $('div#zaglavlje_kupac').hide();
    $('div#t_poruka').hide();
    $('#prijavljeni_nalozi').hide();
    $('div#prodavac_recenzije').hide();
}

var selectedCell;
function clickTd(oglas){
	return function() {
		console.log('kliknuo sam na '+ oglas);
        $('td.selected').removeClass('selected');
		selectedCell = oglas;					
        $(this).addClass('selected');
    };
}

function resetujP(){
    $('input[name="iNaziv"]').val('');
    $('input[name="iCena"]').val('');
    $('#iOpis').val('');
    $('input[name="iGrad"]').val('');
    $('input[name="iDatum"]').val('');
    $('#iSlika').val('');
}

$(document).ready(function() {

/* ********************************** POCETNA OD PRODAVCA ***************************************** */
$(document).on("click", "#korisnickaProdavac", function(){
    console.log('usao u korisnicku od prodavca');
    sakrijDiv();
    $('div#detalji_oglasa').hide();
    $('#oglPrijaviBtn').hide();
    $('div#zaglavlje_prodavac').show();
    $('#glavna_strana').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getProdavac",
        contentType : "application/json",
        success : function(response){
            $('h1#ime_prodavca').text("Username: " + response.username);
            var str = "Lajkova: " + response.lajkovi + "  |  Dislajkova: " + response.dislajkovi;
            $('h3#like').text(str);
        }
    });

    $.ajax({
        type : "get",
        url : "rest/prodavac/getOglase",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            for(var oglas of response){
                //console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });
});

/* ********************************** FILTRIRANJE OGLASA ****************************************** */

$(document).on("click", "#filtriraj_oglase", function(){
    $('#filter').slideToggle('fast', function(){
            
    });
});

$(document).on("click", "#aktivni_oglasi", function(){
    console.log('filtriranje oglasa - aktivni');
    sakrijDiv();
    $('#glavna_strana').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getFiltrirane?filter="+"a",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });

});
$(document).on("click", "#dostavljeni_oglasi", function(){
    console.log('filtriranje oglasa - dostavljeni');
    sakrijDiv();
    $('#glavna_strana').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getFiltrirane?filter="+"d",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });
});
$(document).on("click", "#uRealizaciji_oglasi", function(){
    console.log('filtriranje oglasa - urealizaciji');
    sakrijDiv();
    $('#glavna_strana').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getFiltrirane?filter="+"u",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });
});

/* ********************************** BRISANJE OGLASA ******************************************** */

$(document).on("click", "img.obrisi", function(){
    $.ajax({
        type : "delete",
        url : "rest/prodavac/delete?naziv="+selectedCell,
        contentType : "application/json",
        success : function(response){
            console.log(response);
            if(response == 1){           // sve je okej
                alert('Oglas['+ selectedCell +'] je uspesno obrisan.');
                if(aktivanKorisnik === "Administrator"){
                    refreshOglaseAdmin();
                }
                else{
                    refreshOglaseProdavac();
                }
            }
            else if(response == 3){       // u realizaciji
                alert('Oglas['+ selectedCell +'] nije moguce obrisati, jer je u'+ 
                'statusu >realizacije<');
            }
            else if(response == 2){       // dostavljen
                alert('Oglas['+ selectedCell +'] nije moguce obrisati, jer je u'+ 
                'statusu >dostavljen<');
            }
            else{
                alert('Nepoznat izvor greske.');
            }
        }
    });
});

/* ********************************** IZMENA OGLASA ******************************************** */

$(document).on("click", "img.izmena", function(){
    $('#oglas_izmena').css("display", "block");
    
    $.ajax({
        type : "get",
        url : "rest/prodavac/getProizvodIme?naziv=" + selectedCell,
        contentType : "application/json",
        success : function(response){
            console.log("Dobio sam " + response.naziv + ", cena: " + response.cena);
            $('input[name="iNaziv"]').val(response.naziv);
            $('input[name="iCena"]').val(response.cena);
            $('#iOpis').val(response.opis);
            $('input[name="iGrad"]').val(response.grad);
            let date = new Date(response.datum_isticanja);
            $('input[name="iDatum"]').val(date.toISOString().substr(0, 10));
        }
    });
});

$('button#oglas_izmeni').click(function(){
    $('#oglas_izmena').css("display", "none");

    var oglas = new Object();
    oglas.naziv = $('input[name="iNaziv"]').val();
    oglas.cena = $('input[name="iCena"]').val();
    oglas.opis = $('#iOpis').val();
    oglas.grad = $('input[name="iGrad"]').val();
    var dat = new Date($('input[name="iDatum"]').val());
    oglas.datum = dat.getTime();
    console.log(oglas.naziv+", "+oglas.cena+", "+oglas.opis+", "+oglas.grad+", "+oglas.datum);

    var greska = false;
    if(!oglas.naziv){
        alert('Morate uneti naziv oglasa.');
        greska = true;
        $('input[name="iNaziv"]').focus();
    }
    else if(!oglas.cena){
        alert('Morate uneti cenu oglasa.');
        greska = true;
        $('input[name="iCena"]').focus();
    }
    else if(!oglas.opis){
        alert('Morate uneti opis oglasa.');
        greska = true;
        $('#iOpis').focus();
    }

    if(!greska){
        preuzmiImg(posaljiOgl, oglas);
    }
});

/* ******************************* FUNKCIJE ZA PREUZIMANJE SLIKE ********************************* */
function preuzmiImg(posaljiO, oglas) {
	var fajlSlike;
	if (($("#iSlika"))[0].files.length > 0 ) {
	    fajlSlike = ($("#iSlika"))[0].files[0];
        
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
                    oglas.slika = "slike/"+($("#iSlika"))[0].files[0].name;
                    posaljiO(oglas);
                    resetujP();
                }
                else{
                    alert('Doslo je do greske prilikom ucitavanja slike.');
                }
	       }
	    });
	    
	} else {
        console.log('Usao sam u else...');
        oglas.slika = "";
        posaljiO(oglas);
        resetujP();
	}
}

function posaljiOgl(oglas){
	$.ajax({
		type: 'put',
		url: 'rest/prodavac/putOglas?naziv='+selectedCell,
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
                alert('Oglas['+ selectedCell +'] je uspesno izmenjen!');
                if(aktivanKorisnik === "Prodavac"){
                    refreshOglaseProdavac();
                }
                else{
                    refreshOglaseAdmin();
                }
            }
            else{
                alert('Oglas['+ selectedCell +'] nije izmenjen!');
            }
		}
	});
}
});

function refreshOglaseProdavac(){
    $.ajax({
        type : "get",
        url : "rest/prodavac/getOglase",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            for(var oglas of response){
                //console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });
}

function refreshOglaseAdmin(){
    $.ajax({
        type : "get",
        url : "rest/oglasi/findAll",
        contentType : "application/json",
        success : function(response){
            $('#t_pocetna tbody').empty();
            brojacProizvoda = 0;
            for(var oglas of response){
                //console.log(oglas.naziv);
                add3r(oglas);
            }
        }
    });
}