function dodajRec(rec){
    console.log(rec.oglas+", "+rec.naslov_recenzije+", "+rec.recezent);
    var tr = $('<tr id="'+rec.oglas+'"></tr>');
    var oglas = $('<td>'+rec.oglas+'</td>');
    var naslov = $('<td>'+rec.naslov_recenzije+'</td>');
    var recezent = $('<td>'+rec.recezent+'</td>');

    tr.append(oglas).append(naslov).append(recezent);
    $('#tabela_recenzija tbody').append(tr);
}

function isprazniPolja(){
    $('#precOglas').empty();
    $('#precRecezent').empty();
    $('#precNaslov').empty();
    $('#precSadrzaj').empty();
    $('#precTacanOpis').empty();
    $('#precDogovor').empty();
}
function prikaziRecenzijuModalni(nazOglasa){
    //console.log('nazOglasa:'+nazOglasa);
    $('#recenzija_prikaz').css("display", "block");
    isprazniPolja();

    $.ajax({
        method : "get",
        url : "rest/prodavac/getRecenziju?naziv="+nazOglasa,
        contentType : "application/json",
        success : function(response){
            //console.log(response.recezent);
            $('#precOglas').html('<p>'+response.oglas+'</p>');
            $('#precRecezent').html('<p>'+response.recezent+'</p>');
            $('#precNaslov').html('<p>'+response.naslov_recenzije+'</p>');
            $('#precSadrzaj').html('<p>'+response.sadrzaj_recenzije+'</p>');
            if(response.tacan_opis){
                $('#precTacanOpis').html('<p> DA</p>');
            }
            else{
                $('#precTacanOpis').html('<p> NE</p>');
            }
            if(response.ispostovan_dogovor){
                $('#precDogovor').html('<p> DA</p>');
            }
            else{
                $('#precDogovor').html('<p> NE</p>');
            }
            if(response.slika){
                $('#precSlika').attr('src', response.slika);
            }
            else{
                $('#precSlika').attr('src', "slike/noimage.png");
            }
        }
    });
}

$(document).ready(function() {

/* *************************** PRIKAZ LISTE RECENZIJA PRODAVCA *********************************** */
$(document).on("click", "#list_recenzija", function(){
    console.log('lista recenzija - usao');
    sakrijDivove();
    $('#t_recenzija').show();
    $('#tabela_recenzija').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getListaRecenzija",
        contentType : "application/json",
        success : function(response){
            if(response){
                $('#tabela_recenzija tbody').empty();
                console.log('vratio sam');
                for(var rec of response){
                    console.log(rec.oglas);
                    dodajRec(rec);
                }
            }
            else{
                console.log('lista recenzija je prazna');
            }
        }
    });
});


/* ************ PRIKAZ JEDNE RECENZIJE(MODALNI) ********** */
$('#tabela_recenzija').on("click", "tr", function(){
    var nazOglasa = $(this).attr('id');
    prikaziRecenzijuModalni(nazOglasa);
});

/* ********** BRISANJE RECENZIJE ****************** */
$('body').on("click", "img#obrisiR", function(){
    console.log('usao u brisanje');

    var naz = $(this).attr('class');
    console.log("za oglas "+naz);
    $.ajax({
        type : "delete",
        url : "rest/prodavac/deleteRec?naziv="+naz,
        contentType : "applicatioin/json",
        success : function(response){
            if(response){
                alert("Uspesno ste obrisali recenziju.");
                refreshRecenzije();
            }
            else{
                alert("Recenzija nije uspesno obrisana.");
            }
        }
    });
});

$('body').on("click", "#oglRecenzijaObr", function(){
    console.log('usao u brisanje');

    console.log("za oglas "+aktivni_oglas);
    $.ajax({
        type : "delete",
        url : "rest/prodavac/deleteRec?naziv="+aktivni_oglas,
        contentType : "applicatioin/json",
        success : function(response){
            if(response){
                alert("Uspesno ste obrisali recenziju.");
                $('#oglRecenzija').show();
                $('#oglRecenzijaIzm').hide();
                $('#oglRecenzijaObr').hide();
            }
            else{
                alert("Recenzija nije uspesno obrisana.");
            }
        }
    });
});

/* ********** IZMENA RECENZIJE ****************** */
$('body').on("click", "img#izmeniR", function(){            // image
    console.log('usao u izmenu rec');
    $('#recenzija_izmena').css("display", "block");

    getAkt();
    var naz = $(this).attr('class');
    $.ajax({
        type : "get",
        url : "rest/prodavac/getRecenziju?naziv="+naz,
        contentType : "applicatioin/json",
        success : function(response){
            if(response){
                //console.log(response.oglas+", "+response.naslov_recenzije);
                $('input[name="recenNaslov"]').val(response.naslov_recenzije);
                $('#recenOpis').val(response.sadrzaj_recenzije);
                if(response.tacan_opis){
                    $('#recTda').prop( "checked", true );
                }else{
                    $('#recTne').prop( "checked", true );
                }
                if(response.ispostovan_dogovor){
                    $('#recDda').prop( "checked", true );
                }else{
                    $('#recDne').prop( "checked", true );
                }
            }
            else{
                console.log('Nisam pronasao recenziju');
            }
        }
    });
});

$('body').on("click", "#oglRecenzijaIzm", function(){       // button
    console.log('usao u izmenu rec');
    $('#recenzija_izmena').css("display", "block");

    getAkt();
    $.ajax({
        type : "get",
        url : "rest/prodavac/getRecenziju?naziv="+aktivni_oglas,
        contentType : "applicatioin/json",
        success : function(response){
            if(response){
                //console.log(response.oglas+", "+response.naslov_recenzije);
                $('input[name="recenNaslov"]').val(response.naslov_recenzije);
                $('#recenOpis').val(response.sadrzaj_recenzije);
                if(response.tacan_opis){
                    $('#recTda').prop( "checked", true );
                }else{
                    $('#recTne').prop( "checked", true );
                }
                if(response.ispostovan_dogovor){
                    $('#recDda').prop( "checked", true );
                }else{
                    $('#recDne').prop( "checked", true );
                }
            }
            else{
                console.log('Nisam pronasao recenziju');
            }
        }
    });
});

/* **************** SLANJE RECENZIJE *************** */
$('#recenPosalji').click(function(){
    console.log('kliknuo na posalji izmenu rec');
    $('#recenzija_izmena').css("display", "none");

    var obj = new Object();
    obj.oglas = aktivni_oglas;
    obj.recezent = akt.username;
    obj.naslov = $('input[name="recenNaslov"]').val();
    obj.sadrzaj = $('#recenOpis').val();
    var opis = $( 'input[name="recenTacanOpis"]:checked' ).val();
    var dogovor = $( '[name="recenDogovor"]:checked' ).val();
    if(opis === "da"){
        obj.tacan_opis = true;
    }
    else{
        obj.tacan_opis = false;
    }
    if(dogovor === "da"){
        obj.dogovor = true;
    }
    else{
        obj.dogovor = false;
    }

    //console.log("tacan opis: " + obj.tacan_opis + ", dogovor: " + obj.dogovor);
    var greska = false;
    if(!obj.oglas){
        alert('Morate uneti naziv oglasa.');
        greska = true;
        $('input[name="recenOglas"]').focus();
    }
    else if(!obj.naslov){
        alert('Morate uneti naslov recenzije.');
        greska = true;
        $('input[name="recenNaslov"]').focus();
    }
    else if(!obj.sadrzaj){
        alert('Morate uneti sadrzaj recenzije.');
        greska = true;
        $('#recenOpis').focus();
    }
    
    if(!greska){
        preuzmiSlikuRec(posaljiRecenziju, obj);         // ova f-ja je kod oglasa
    }
});


/* ******************************** KUPAC RECENZIRA OGLAS ********************************** */
$('#oglRecenzija').click(function(){
    console.log('kliknuo na recenziranje');
    clearPolja();
    $('#recOglas').css("display", "block");
});

function clearPolja(){
    $('input[name="recNaslov"]').val('');
    $('#recOpis').val('');
}

$('#recPosalji').click(function(){
    console.log('Saljem recenziju na ' + aktivni_oglas +", salje: " + akt.username);
    
    var obj = new Object();
    obj.oglas = aktivni_oglas;
    obj.recezent = akt.username;
    obj.naslov = $('input[name="recNaslov"]').val();
    obj.sadrzaj = $('#recOpis').val();
    var opis = $( 'input[name="recTacanOpis"]:checked' ).val();
    var dogovor = $( '[name="recDogovor"]:checked' ).val();
    if(opis === "da"){
        obj.tacan_opis = true;
    }
    else{
        obj.tacan_opis = false;
    }
    if(dogovor === "da"){
        obj.dogovor = true;
    }
    else{
        obj.dogovor = false;
    }

    //console.log("tacan opis: " + obj.tacan_opis + ", dogovor: " + obj.dogovor);
    var greska = false;
    if(!obj.oglas){
        alert('Morate uneti naziv oglasa.');
        greska = true;
        $('input[name="recOglas"]').focus();
    }
    else if(!obj.naslov){
        alert('Morate uneti naslov recenzije.');
        greska = true;
        $('input[name="recNaslov"]').focus();
    }
    else if(!obj.sadrzaj){
        alert('Morate uneti sadrzaj recenzije.');
        greska = true;
        $('#recOpis').focus();
    }
    
    if(!greska){
        preuzmiSlikuRecenzija(posaljiRec, obj);
    }
});

/* ******************************** KUPAC RECENZIRA PRODAVCA ********************************** */
$('#recPosaljiProd').click(function(){
    console.log('saljem recenziju prodavca');

    var lajk = $( 'input[name="recProd"]:checked' ).val();
    console.log("lajk:" + lajk);
    if(lajk === "da"){
       lajk = true; 
    }
    else{
        lajk = false;
    }
    $.ajax({
        type : "post",
        url : "rest/kupac/recProdavac?oglas="+aktivni_oglas+"&lajk="+lajk,
        contentType : "application/json",
        success : function(response){
            $('#recProdavac').css("display", "none");
            $('#oglRecenzija').hide();
            $('#oglRecenzijaIzm').show();
            $('#oglRecenzijaObr').show();
        }
    });
});

});