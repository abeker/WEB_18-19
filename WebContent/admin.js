var brojacProizvoda = 0;
function add3row(proizvod){
    if((brojacProizvoda % 3) === 0){        // u red idu 3 oglasa
        $('#glavna_strana tbody').append($('<tr></tr>'));
    }

    /*<td style="border: 1px solid black">
        <h2 align="center" style="color: darkgreen">Nike</h3>
        </br>
        <img height=200 width=150 style="margin-left: 10%" src="slike/nike.jpg">
        </br>
        <p style="max-width: 200px">Jiusto odio dignissimos atque corrupti quos</p>
        <h3 align="right" style="color: darkgreen">Cena: 6500 rsd</h3>
    </td>*/

    var td = $('<td style="border: 1px solid black">' +
                '<h2 id="'+ proizvod.naziv +'">'+ proizvod.naziv + '</h2>' +
                '<img height=200 width=150 style="margin-left: 10%;vertical-align:middle" src="'+ proizvod.slika +'">' +
                '</br><p>'+ proizvod.opis +'</p>' +
                '<h3>'+  proizvod.cena +' rsd</h3></td>');

    $("#glavna_strana tbody:last-child").append(td);
    brojacProizvoda += 1;
}


/* ********** PROVERAVAM DA LI JE OVAJ USERNAME PRODAVAC/KUPAC/ADMIN ***************** */
var check_prodavac;
function checkUlogaProdavca(username){
    $.ajax({
        type : "get",
        url : "rest/prodavac/checkUlogaProdavac/"+username,
        contentType : "application/json",
        success: function(response){
            if(response == 0){  // prodavac je
                check_prodavac = true;       
            }
            else{           // nije prodavac
                check_prodavac = false;   
            }
        }
    });
}

/* ************ ZA PRIKAZ PROIZVODA TE KATEGORIJE ************* */
function clickFun(kat){
	return function() {
		//console.log('kliknuo sam na >>'+ kat + "<<");
        sakrijDivove();
        $('div#detalji_oglasa').hide();
        $('div#zaglavlje_kupac').hide();
        lista_oglasa = -1;
        $('div#glavna_strana').show();
        $('#t_pocetna').show();
        
        var URL = "rest/oglasi/getKategorijuOglasa?naziv="+kat;
        
        $.ajax({
            type : "get",
            url: URL,
            success : function(response){
                $('#t_pocetna tbody').empty();
                $('#btnRecenzija').hide();
                brojacProizvoda = 0;
                for(var oglas of response){
                    add3row(oglas);
                }
            } 
        });
    }
}

var selectedRow;
var lista_oglasa;
function clickTr(kat){
	return function() {
		console.log('kliknuo sam na '+ kat);
        $('tr.selected').removeClass('selected');
		selectedRow = kat;					
        $(this).addClass('selected');
        
        // pri selekciji dobavljam listu oglasa koja se prikazuje kod izmene(redosled izvrsavanja 
        // dva ajaxa zaredom je tesko kontrolisati)
        $.ajax({
        type : "get",
        url : "rest/admin/find?naziv=" + selectedRow,        // ovo je selektovani red
        success : function(response){
            $('input[name="naziv_izmena"]').val(response.naziv);
            $('#opis_izmena').val(response.opis);
            lista_oglasa = response.oglasi;           
        }
    });
    };
}

/* ********************** FUNKCIJE ZA PRIKAZ OGLASA *************** */
function ocistiPolja(){
    $('h2#oglNaslov').empty();
    $('img#like').empty();
    $('img#dislike').empty();
    $('p#oglVlasnik').empty();
    $('p#oglOpis').empty();
    $('p#oglGrad').empty();
    $('p#oglCena').empty();
}

function prikaziOglas(oglas){
    sakrijDivove();
    $('#detalji_oglasa').show();
    ocistiPolja();

    //console.log('dat I: ' + oglas.datum_isticanja);
    var cena = oglas.cena + " rsd";
    $('h1#oglNaslov').html(oglas.naziv);
    $("img#oglSlika").attr("src",oglas.slika);
    $("p#p_like").html('<strong>Like:</strong>'+oglas.lajkovi+'<strong> Dislike:</strong>'+oglas.dislajkovi+'');
    $('p#oglVlasnik').html('<strong>Vlasnik: </strong><p id="'+oglas.vlasnik+'" class="inlineV">'+oglas.vlasnik+'</p>');
    $('p#oglOpis').html('<strong>Opis: </strong>'+ oglas.opis);
    $('p#oglGrad').html('<strong>Grad: </strong>'+ oglas.grad);
    $('p#oglCena').html('<strong>Cena: </strong>'+ cena);
    var date = new Date(oglas.datum_postavljanja);
    $('#oglDatP').val(date.toISOString().substr(0, 10));
    var date1 = new Date(oglas.datum_isticanja);
    $('#oglDatI').val(date1.toISOString().substr(0, 10));
}

var aktivni_oglas;
var aktivni_prodavac;
$(document).ready(function() {

$('span.close').click(function(){
    lista_oglasa = -1;
    $('#mod_addKat').css("display", "none");
    $('#mod_izmenaKat').css("display", "none");
    $('#mod_pretraga').css("display", "none");
    $('#slanje_poruke').css("display", "none");
    $('#slanje_odg').css("display", "none");
    $('#mod_oglas').css("display", "none");
    $('#oglas_izmena').css("display", "none");
    $('#recOglas').css("display", "none");
    $('#recProdavac').css("display", "none");
    $('#recenzija_prikaz').css("display", "none");
    $('#recenzija_izmena').css("display", "none");
    $('#prikaz_poruke').css("display", "none");
    $('#izmena_poruke').css("display", "none");
});

/* ******************************** DETALJI OGLASA ********************************** */
getAkt();
$('#t_pocetna').on("click", "h2", function(){
    //console.log('Kkiknuo sam na ' + $(this).attr('id'));

    getAkt();
    var naziv;
    naziv = $(this).attr('id');
    if(!naziv){
        naziv = selectedCell;           // zbog ovoga je prodavac.js ranije def nego admin.js
        console.log('class');           // zbog add3r() kod prodavca
    }
    console.log(naziv);
    
    checkLikeDislike(naziv);
    if(aktivanKorisnik === "Administrator"){
        console.log('Admin je');
        $('#oglButt').show();
        $('#oglPoruciBtn').hide();
        $('#oglPrijaviBtn').hide();
        $('#like_disl').show();
        $('#pracenje').hide();
        $('#oglDostavljenBtn').hide();
    }
    else if(aktivanKorisnik === "Prodavac"){
        console.log('Prodavac je');
        $('#oglButt').hide();
        $('#oglPoruciBtn').hide();
        $('#oglPrijaviBtn').hide();
        $('#like_disl').hide();
        $('#pracenje').hide();
        $('#oglDostavljenBtn').hide();
    }
    else if(aktivanKorisnik === "Kupac"){
        console.log('Kupac je');
        $('#oglButt').show();
        $('#like_disl').show();
        
        $('#oglPoruciBtn').show();
        $('#oglOmiljeni').show();
        $('#oglRecenzija').hide();
        $('#oglRecenzijaIzm').hide();
        $('#oglRecenzijaObr').hide();
        $('#oglDostavljenBtn').hide();
        
        $.ajax({
            type : "get",
            url : "rest/prodavac/checkKorisnickaProdavac/"+naziv,
            contentType : "application/json",
            success : function(response){
                if(response == -1){
                    console.log('Nije logovan ili nije kupac ili nije ni dost ni narucen ni omiljen');
                }
                else if(response == 1){     // porucen od strane ovog kupca
                    console.log('porucen ovom kupcu');
                    $('#oglDostavljenBtn').show();
                    $('#oglOmiljeni').show();
                    $('#oglPoruciBtn').hide();
                    $('#oglRecenzija').hide();
                    $('#oglRecenzijaIzm').hide();
                    $('#oglRecenzijaObr').hide();
                }
                else if(response == 11){        // porucen i omiljen istovremeno
                    console.log('porucen i omiljen');
                    $('#oglDostavljenBtn').show();
                    $('#oglOmiljeni').hide();
                    $('#oglPoruciBtn').hide();
                    $('#oglRecenzija').hide();
                    $('#oglRecenzijaIzm').hide();
                    $('#oglRecenzijaObr').hide();
                }
                else if(response == 2){     // dostavljen ovom kupcu
                    console.log('dostavljen ovom kupcu');
                    $('#oglDostavljenBtn').hide();
                    $('#oglOmiljeni').hide();
                    $('#oglPoruciBtn').hide();
                    checkRecenzija(naziv);
                }
                else if(response == 3){     // omiljen je ovom kupcu
                    console.log('omiljen ovom kupcu');
                    $('#oglDostavljenBtn').hide();
                    $('#oglOmiljeni').hide();
                    $('#oglRecenzija').hide();
                    $('#oglRecenzijaIzm').hide();
                    $('#oglRecenzijaObr').hide();
                    $('#oglPoruciBtn').show();
                }
            }
        });
    }
    else{
        $('#oglButt').show();
        $('#oglPoruciBtn').show();
        $('#oglPrijaviBtn').hide();
        $('#like_disl').show();
        $('#pracenje').hide();
        $('#oglDostavljenBtn').hide();
        $('#izmObrRec').hide();
    }
    
    $.ajax({
        type : "get",
        url : "rest/oglasi/getOglasNaziv?naziv="+naziv,
        contentType : "application/json",
        success : function(response){
            console.log('naziv oglasa: ' + naziv);
            prikaziOglas(response);
            aktivni_oglas = response.naziv;
            aktivni_prodavac = response.vlasnik;
            checkUlogaProdavca(response.vlasnik);       // korisntim u prodavac2
        }
    });
});

/* ******************************** LIKE / DISLIKE ********************************** */
$('body').on("click", "#lajk", function(){
    console.log("usao sam u lajkovanje");
    console.log('aktivan je: ' + aktivni_oglas);

    if(akt == -1){
        alert('Ne mozete da dodelite like ukoliko niste prijavljeni.');
    }
    else{
        var br = 1;         // like
        $.ajax({
            type : "post",
            url : "rest/oglasi/dodajLajk?naziv="+aktivni_oglas+"&lajk="+br,
            contentType : "application/json",
            success : function(response){
                if(response == 1){
                    $('#lajk').attr('src', 'slike/liked.png');
                    refreshOglas();
                }
                else if(response == -2){
                    console.log('Vec ste prethodno oznacili da vam se svidja oglas.');
                    $('#lajk').attr('src', 'slike/like.png');
                    refreshOglas();
                }
                else if(response == -3){
                    console.log('Vec ste prethodno oznacili da vam se ne svidja oglas.');
                }
                else{
                    console.log('Doslo je do greske nepoznatog izvora.');
                }
            }
        });
    }
});

$('body').on("click", "#dislajk", function(){
    console.log("usao sam u dislajkovanje");
    console.log('aktivan je: ' + aktivni_oglas);

    if(akt == -1){
        alert('Ne mozete da dodelite dislike ukoliko niste prijavljeni.');
    }
    else{
    var br = -1;            // dislike
        $.ajax({
            type : "post",
            url : "rest/oglasi/dodajLajk?naziv="+aktivni_oglas+"&lajk="+br,
            contentType : "application/json",
            success : function(response){
                if(response == 1){
                    $('#dislajk').attr('src', 'slike/disliked.png');
                    refreshOglas();
                }
                else if(response == -2){
                    console.log('Vec ste prethodno oznacili da vam se svidja oglas.');
                }
                else if(response == -3){
                    console.log('Vec ste prethodno oznacili da vam se ne svidja oglas.');
                    $('#dislajk').attr('src', 'slike/dislike.png');
                    refreshOglas();
                }
                else{
                    console.log('Doslo je do greske nepoznatog izvora.');
                }
            }
        });
    }
});

/* *********************************** DODAVANJE KATEGORIJA ************************************** */
$('button#add_kat').click(function(){
    $('#mod_addKat').css("display", "block");
    
    // treba da napunim multi select oglasima
    $.ajax({
        type : "get",
        url : "rest/oglasi/findAll",
        contentType: 'application/json',
        success : function(response){
            lista_oglasa = response;        // inicijalizujem listu oglasa
            $('#list_oglasa').empty();
            for(var oglas of response){
                var option = $('<option value="'+ oglas.naziv +'">'+ oglas.naziv +'</option>');
                $('#list_oglasa').append(option);
            }
        }

    });
});

$('button#dodaj_kat').click(function(){
    var naziv = $('input[name="naziv_kat"]').val();
    var opis = $('#opis_kat').val();
    var oglasi = $('#list_oglasa').val();
    console.log(naziv+" : "+opis+" : "+oglasi);
    lista_oglasa = -1;

    var obj = {
        "naziv" : naziv,
        "opis" : opis,
        "oglasi" : oglasi
    }

    $.ajax({
        type : "post",
        url : "rest/admin/addKat",
        data : JSON.stringify(obj),
        contentType : "application/json",
        success : function(response){
            $('#mod_addKat').css("display", "none");
            $('input[name="naziv_kat"]').val('');
            $('#opis_kat').val('');
            refreshKategorije();
        }
    });    
});

/* *********************************** BRISANJE KATEGORIJA ************************************** */
$('button#obrisi_kat').click(function(){
    var flag = false;
    if(typeof selectedRow === "undefined" || lista_oglasa == -1){
        alert('Morate prvo oznaciti kategoriju koju zelite da obrisete.');
        flag = true;
    }
    lista_oglasa = -1;

    if(!flag){
        $.ajax({
            type : "delete",
            url : "rest/admin/remove?naziv="+ selectedRow,
            success : function(response){
                alert('Uspesno obrisana kategorija '+ selectedRow +'!');
                refreshKategorije();
            }
        });
    }
});

/* ************************************ IZMENA KATEGORIJA *************************************** */
$('button#izm_kat').click(function(){
    var flag = false;
    if(typeof selectedRow === "undefined" || lista_oglasa == -1){
        alert('Morate prvo oznaciti kategoriju koju zelite da izmenite.');
        flag = true;
    }

    if(!flag){
    $('#mod_izmenaKat').css("display", "block"); 

    $.ajax({
        type : "get",
        url : "rest/oglasi/findAll",
        contentType: 'application/json',
        success : function(response){
            $('#list_izmena').empty();
            for(var oglas of response){
                var flag = "";
                for(var li of lista_oglasa){
                    //console.log("li je "+li+", dok je oglas "+oglas.naziv)
                    if(li === oglas.naziv)
                        flag="selected";
                }

                var option = $('<option value="'+ oglas.naziv +'" '+ flag +'>'
                + oglas.naziv +'</option>');
                $('#list_izmena').append(option);
            }
        }
    });
}
});

$('button#izmeni_kat').click(function(){
    var naziv = $('input[name="naziv_izmena"]').val();
    var opis = $('#opis_izmena').val();
    var oglasi = $('#list_izmena').val();
    console.log(naziv+" : "+opis+" : "+oglasi);

    lista_oglasa = -1;
    var obj = {
        "naziv" : naziv,
        "opis" : opis,
        "oglasi" : oglasi
    }

    $.ajax({
        type : "put",
        url : "rest/admin/putKat?naziv="+selectedRow,
        data : JSON.stringify(obj),
        contentType : "application/json",
        success : function(response){
            $('#mod_izmenaKat').css("display", "none");
            refreshKategorije();
        }
    });    

});

$(document).on("click", "#a_kat", function(){
    $('ul#strana div#kat').empty();
    $.ajax({
        type : 'get',
        url : 'rest/admin/getKat',
        contentType : 'application/JSON',
        success : function(response){
            for(var kat of response){
                //console.log(kat+", ");
                var li = $('<li class="str" id="'+ kat +'"><a>'+ kat +'</a></li>');     // id je naziv
                li.click(clickFun(kat));
                $('ul#strana div#kat').append(li);
            }
        }
    });

    $('#kat').slideToggle('fast', function(){
        
    });
});

/* ******************************* POCETNA STRANA ********************************************** */
$(document).on("click", "#pocetna", function(){
    sakrijDivove();
    $('div#detalji_oglasa').hide();
    $('div#zaglavlje_prodavac').hide();
    $('div#zaglavlje_kupac').hide();
    list_oglasa = -1;
    $('div#glavna_strana').show();
    $('#t_pocetna tbody').empty();
    $('#t_pocetna').show();


    //console.log('click na web kupovina');
    console.log(aktivanKorisnik);
    $.ajax({
        type : "get",
        url : "rest/oglasi/findAll",
        contentType: 'application/json',
        success : function(response){
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                if(aktivanKorisnik==="Administrator"){
                    add3r(oglas);
                }
                else{
                    add3row(oglas);
                }
            }
        }
    });
});

/* ************************ PRIKAZI NAJPOPULARNIJE ************************************ */
$('body').on("click", "#first_child", function(){
    console.log('daj najpopularnije!');
    sakrijDivove();
    $('div#detalji_oglasa').hide();
    $('div#zaglavlje_prodavac').hide();
    $('div#zaglavlje_kupac').hide();
    $('div#glavna_strana').show();
    $('#t_pocetna tbody').empty();
    $('#t_pocetna').show();

    $.ajax({
        type : "get",
        url : "rest/oglasi/najpopularnijiOglasi",
        contentType: 'application/json',
        success : function(response){
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                //console.log(oglas.naziv);
                add3row(oglas);
            }
        }
    });
});

/* ******************************** PRIJAVLJENI NALOZI **************************************** */
$('body').on('click', '#li_prijavljeni', function(){
    console.log('lista prijavljenih naloga');
    sakrijDivove();
    $('#prijavljeni_nalozi').show();
    
    $.ajax({
        type : "get",
        url : "rest/prodavac/getSvePrijave",
        contentType : "application/json",
        success : function(response){
            if(!jQuery.isEmptyObject(response)){
                $('#tabela_prijavljeni tbody').empty();
                for(var nalog of response){
                    prikaziPrijavljene(nalog);
                }
            }
            else{
                $('#tabela_prijavljeni tbody').empty();
                alert('Nema prijavljenih naloga.');
            }
        }
    });
});

$('#tabela_prijavljeni').on('click', 'a', function(){
    var username = $(this).attr('class');

    $.ajax({
        type : "post",
        url : "rest/prodavac/ponistiPrijave?username="+username,
        contentType : "application/json",
        success : function(response){
            if(response == 1){
                alert('Uspesno ste ponistili sve prijave vezane za prodavca "'+username+'".');
                refreshPrijavljene();
            }
            else{
                alert('Niste uspesno ponistili prijave prodavca.');
            }
        }
    });
});

/* ****************************** PRIJAVA SUMNJIVOG NALOGA *************************************** */
$('#oglPrijaviBtn').click(function(){
    console.log('prijava naloga oglasa '+aktivni_oglas);

    $.ajax({
        type : "post",
        url : "rest/prodavac/prijavaNaloga?oglas="+aktivni_oglas,
        contentType : "application/json",
        success : function(response){
            if(response == 1){
                alert('Uspesno ste prijavili sumnjiv nalog.');
            }
            else if(response == 2){
                alert('Vec ste prethodno prijavili ovaj nalog.');
            }
            else{
                alert('Morate biti prijavljeni na nalog, da biste izvrsili prijavu.')
            }
        }
    });
});

/* *********************** CLICK NA MODIFIKACIJU KATEGORIJA ************************************ */
$(document).on("click", "#modifikacija", function(){
    console.log('usao u modifikaciju');
    lista_oglasa = -1;
    sakrijDivove();
    $('div#div_kategorije').show();

    $.ajax({
        type: 'get',
        url: 'rest/admin/getKat',
        contentType: 'application/json',
        success: function(response) {
            $('#tab_kat tbody').empty();
            for(var k of response){         
                addTr(k);
            }    
        }
    });

});

});

function addTr(kat){
    //console.log("kategorija je:" + kat);
    var tr = $('<tr style="cursor: pointer" id="'+ kat +'"></tr>');
    tr.click(clickTr(kat));
    var td = $('<td>'+ kat +'</td>');
    tr.append(td);
    $('#tab_kat tbody').append(tr);
}

var akt;
function getAkt(){
    $.ajax({
        type : "get",
        url : "rest/getAktivan",
        contentType : "application/json",
        success : function(response){
            if(response !== undefined){         // ako sa servera posaljem null
                akt = response;
            }
            else{
                akt = -1;           // niko nije ulogovan
            }
        }
    });
}

function prikaziPrijavljene(prodavac){
    var tr = $('<tr></tr>');
    var username = $('<td>'+prodavac.username+'</td>');
    var ime = $('<td>'+prodavac.ime+'</td>');
    var prezime = $('<td>'+prodavac.prezime+'</td>');
    var grad = $('<td>'+prodavac.grad+'</td>');
    var mail = $('<td>'+prodavac.mail+'</td>');
    var br_prijava = $('<td>'+prodavac.prijave+'</td>');
    var btnReset = $('<td><a class="'+prodavac.username+'" title="Resetuj broj prijava"> Ponisti </a></td>');

    tr.append(username).append(ime).append(prezime).append(grad).append(mail).append(br_prijava);
    tr.append(btnReset);
    $('#tabela_prijavljeni tbody').append(tr);
}

function checkRecenzija(naziv){
    $.ajax({
        type : "get",
        url : "rest/prodavac/checkRecenzija/"+naziv,
        contentType : "application/json",
        success : function(response){
            if(response == 1){      // recenzirao sam ovaj oglas
                $('#oglRecenzija').hide();
                $('#oglRecenzijaIzm').show();
                $('#oglRecenzijaObr').show();
            }
            else{
                $('#oglRecenzija').show();
                $('#oglRecenzijaIzm').hide();
                $('#oglRecenzijaObr').hide();
            }
        }
    });
}

function checkLikeDislike(naziv){
    $.ajax({
        type : "get",
        url : "rest/prodavac/checkLikeDislike/"+naziv,
        contentType : "application/json",
        success : function(response){
            if(response === "like"){
                $('#lajk').attr('src', 'slike/liked.png');
                $('#dislajk').attr('src', 'slike/dislike.png');
            }     
            else if(response === "dislike"){
                $('#lajk').attr('src', 'slike/like.png');
                $('#dislajk').attr('src', 'slike/disliked.png');
            }
            else if(response === "both"){
                $('#lajk').attr('src', 'slike/liked.png');
                $('#dislajk').attr('src', 'slike/disliked.png');
            }
            else{
                $('#lajk').attr('src', 'slike/like.png');
                $('#dislajk').attr('src', 'slike/dislike.png');
            }
        }
    });
}

function refreshPrijavljene(){
    $.ajax({
        type : "get",
        url : "rest/prodavac/getSvePrijave",
        contentType : "application/json",
        success : function(response){
            if(!jQuery.isEmptyObject(response)){
                $('#tabela_prijavljeni tbody').empty();
                for(var nalog of response){
                    prikaziPrijavljene(nalog);
                }
            }
            else{
                $('#tabela_prijavljeni tbody').empty();
                console.log('Nema prijavljenih naloga.');
            }
        }
    });
}

function refreshOglas(){
    $.ajax({
        type : "get",
        url : "rest/oglasi/getOglasNaziv?naziv="+aktivni_oglas,
        contentType : "application/json",
        success : function(response){
            prikaziOglas(response);
        }
    });
}

function refreshKategorije(){
    $.ajax({
        type: 'get',
        url: 'rest/admin/getKat',
        contentType: 'application/json',
        success: function(response) {
            $('#tab_kat tbody').empty();
            $('ul#strana div#kat').empty();
            for(var k of response){         
                addTr(k);
                var li = $('<li class="str" id="'+ k +'"><a>'+ k +'</a></li>');     // id je naziv
                li.click(clickFun(k));
                $('ul#strana div#kat').append(li);
            }    
        }
    });
}

function refreshOglase(){
    sakrijDivove();
    $('div#detalji_oglasa').hide();
    $('div#zaglavlje_prodavac').hide();
    $('div#zaglavlje_kupac').hide();
    list_oglasa = -1;
    $('div#glavna_strana').show();
    $('#t_pocetna tbody').empty();
    $('#t_pocetna').show();

    $.ajax({
        type : "get",
        url : "rest/oglasi/findAll",
        contentType: 'application/json',
        success : function(response){
            brojacProizvoda = 0;
            $('#btnRecenzija').hide();
            for(var oglas of response){
                if(aktivanKorisnik==="Administrator"){
                    add3r(oglas);
                }
                else{
                    add3row(oglas);
                }
            }
        }
    });
}