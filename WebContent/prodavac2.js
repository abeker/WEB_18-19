// korisnicka strana prodavca, kada kupac klikne u okviru oglasa

$(document).ready(function(){

/* ************************* CLICK NA LINK PRODAVCA U OGLASU ******************************** */
var vlasnik;
$('p#oglVlasnik').on('click', 'p', function(){
    vlasnik = $(this).attr('id');
    getAkt();

    if(check_prodavac){      // ako je prodavac
        list_oglasa = -1;           // koristim u admin.js kod detalja oglasa
        sakrijDivove();
        $('div#detalji_oglasa').hide();
        $('div#zaglavlje_prodavac').show();
        $('#glavna_strana').show();
        $('#glavna_strana tbody').empty();

        if(aktivanKorisnik == "Kupac"){     // samo kupac moze da prijaviprodavca (aktivanKorisnik -> login.js)
            $('#oglPrijaviBtn').show();
        }
        else{
            $('#oglPrijaviBtn').hide();
        }

        $.ajax({
            type : "get",
            url : "rest/prodavac/getImePrezime/"+vlasnik,
            contentType : "application/json",
            success : function(response){
                $('h1#ime_prodavca').text(response.ime+" "+response.prezime);
                var str = "Lajkova: " + response.lajkovi + "  |  Dislajkova: " + response.dislajkovi;
                $('h3#like').text(str);
            }
        });

        ispisiOglase(vlasnik);
        $('#btnRecenzija').show();
    }
    else{
        console.log("Uloga prodavca je promenjena.");
        // pada ako sam mu promenio ulogu sa prodavac na kupac/admin
    }
});

$('#glavna_strana').on('click', '#btnRecenzija', function(){
    $('#prodavac_recenzije').show();
    $('#tab_recenzija').show();
    $(this).hide();

    $.ajax({
        type : "get",
        url : "rest/prodavac/recenzijeProdavca/"+vlasnik,
        contentType : "application/json",
        success : function(response){
            $('#tab_recenzija tbody').empty();
            for(var rec of response){
                addRec(rec);            // f-ja u oglas.js
            }   
        }
    });
});

$('#tab_recenzija tbody').on("click", "tr", function(){
    var nazOglasa = $(this).attr('id');
    var recezent = $(this).attr('class');
    console.log('usao u click');
    console.log(nazOglasa + " " + recezent);
    if(akt == -1){   // niko nije aktivan
        prikaziRecenzijuModalni(nazOglasa);
    }
    else {
        if(recezent !== akt.username){          // prikazujem tu recenziju, samo ako nisam ja taj
            prikaziRecenzijuModalni(nazOglasa);   // koji sam ostavio recenziju
        }
    }
});

$('#tab_recenzija tr').hover(function(){
    console.log('usao u hover');
    var recezent = $(this).attr('class');
    if(recezent === akt.username){    
        $(this).css('cursor', 'default');    
        $(this).css('background-color', 'white');
    }
});

});

function ispisiOglase(vlasnik){
    $.ajax({
        type : "get",
        url : "rest/prodavac/getOglaseProdavac/"+vlasnik,
        contentType : "application/json",
        success : function(response){
            brojacProizvoda = 0;
            for(var oglas of response){
                add3row(oglas);
            }  
        }
    });
}