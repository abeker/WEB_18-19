function sakrijDivove(){
    $('div#div_prijava').hide();
    $('div#dugme_reg').hide();
    $('div#div_kategorije').hide();
    $('div#glavna_strana').hide();
    $('div#div_registracija').hide();
    $('div#zaglavlje_prodavac').hide();
    $('div#zaglavlje_kupac').hide();
    $('div#t_korisnici').hide();
    $('div#t_recenzija').hide();
    $('div#prodavac_recenzije').hide();
    $('div#kupac_rec').hide();
    $('div#detalji_oglasa').hide();
    $('div#t_poruka').hide();
    $('#prijavljeni_nalozi').hide();
    $('#btnRecenzija').hide();
}

var list_oglasa = -1;           // nisu ni omiljeni/poruceni/dostavljeni
$('document').ready(function(){

/* ******************************** CLICK OMILJENI OGLAS ********************************** */
$('body').on("click", "#oglOmiljeni", function(){
    console.log('oznacio sam omiljeni '+aktivni_oglas);

    $.ajax({
        type : "post",
        url : "rest/oglasi/oznaciOmiljeni?oglasNaz="+aktivni_oglas,
        contentType : "application/json",
        success : function(response){
            console.log(response);
            if(response == 1){
                alert('Oglas [' + aktivni_oglas + '] uspesno dodat u omiljene oglase.');
                $("#oglOmiljeni").hide();
            }
            else if(response == -1){
                alert('Morate biti ulogovani da biste dodali oglas u omiljene.');
            }
            else if(response == -2){
                alert('Morate biti ulogovani kao "Kupac" da biste dodali oglas u omiljene.');
            }
            else{
                alert('Oglas je vec dodat u listu omiljenih.');
            }
        }
    });
});

/* ******************************** CLICK PORUCI OGLAS ********************************** */
$('button#oglPoruciBtn').click(function(){
    console.log('klikno na naruci');

    $.ajax({
        type : "post",
        url : "rest/oglasi/poruciProizvod?naziv="+aktivni_oglas,
        contentType : "application/json",
        success : function(response){
            if(response){
                alert('Proizvod['+aktivni_oglas+'] je uspesno porucen.');
                $('button#oglPoruciBtn').hide();
                $('#oglDostavljenBtn').show();
            }
            else{
                alert('Doslo je do greske prilikom porucivanja proizvoda.');
            }
        }
    });
});

/* ******************************** CLICK DOSTAVLJEN ********************************** */
$('#oglDostavljenBtn').click(function(){
    console.log('Aktivni oglas(dostavljanje) ' + aktivni_oglas);

    $.ajax({
        type : "post",
        url : "rest/kupac/postaviDostavljen?naziv="+aktivni_oglas,
        contetType : "applicatio/json",
        success : function(response){
            if(response){
                alert('Oznacili ste da vam je proizvod uspesno dostavljen.');
                $('#oglDostavljenBtn').hide();
                $('#oglOmiljeni').hide();
                $('#oglRecenzija').show();
                $('#prikaziRec').show();
            }
            else{
                alert('Doslo je do greske prilikom dostavljanja.');
            }
        }
    });
});

/* ******************************** KORISNICKA OD KUPCA ********************************** */
$('body').on("click", "#korisnickaKupac", function(){
    prikazKorisnickaKupac();
});

/* ************************** LISTE OMILJENIH/PORUCENIH/DOSTAVLJENIH **************************** */
$('body').on("click", "#lista_omiljenih", function(){
    prikazKorisnickaKupac();
    list_oglasa = "Omiljeni";

    $.ajax({
        type : "get",
        url : "rest/kupac/getListaOmiljenih",
        contentType : "application/json",
        success : function(response){
            brojacProizvoda = 0;
            for(var o of response){
                add3row(o);
            }
        }
    });
});

$('body').on("click", "#lista_porucenih", function(){
    prikazKorisnickaKupac();
    list_oglasa = "Poruceni";

    $.ajax({
        type : "get",
        url : "rest/kupac/getListaPorucenih",
        contentType : "application/json",
        success : function(response){
            brojacProizvoda = 0;
            for(var o of response){
                add3row(o);
            }
        }
    });
});

$('body').on("click", "#lista_dostavljenih", function(){
    prikazKorisnickaKupac();     
    list_oglasa = "Dostavljeni";

    $.ajax({
        type : "get",
        url : "rest/kupac/getListaDostavljenih",
        contentType : "application/json",
        success : function(response){
            brojacProizvoda = 0;
            for(var o of response){
                add3row(o);
            }
        }
    });
});

});

function prikazKorisnickaKupac(){
    sakrijDivove();
    $('div#detalji_oglasa').hide();
    $('#glavna_strana').show();
    $('#t_pocetna tbody').empty();
    $('div#zaglavlje_kupac').show();

    $.ajax({
        type : "get",
        url : "rest/kupac/getAktivnogKupca",
        contentType : "application/json",
        success : function(response){
            console.log("Dobio sam: " + response.username);
            $('#imePrezime_kupac').text(response.ime + " " + response.prezime);
            $('#username_kupac').text(response.username);
        }
    });
}