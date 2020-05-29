function prikaziPoruku(p, uloga, primljene){
    var tr = $('<tr></tr>');
    var hashTag = $('<td><p style="color:blue;cursor:pointer" class="'+p.id+'">Prikazi</p></td>');
    var naziv = $('<td>'+p.naziv_oglasa+'</td>');
    var naslov = $('<td>'+p.naslov_poruke+'</td>');
    
    var korisnik;
    if(primljene){
        korisnik = $('<td>'+p.posiljalac+'</td>');
    }
    else{
        var procitano;
        console.log(p.procitana);
        if(p.procitana == true){
            console.log('procitana je poruka');
            procitano = $('<td><img src="slike/delivered.png" title="Procitana"></td>');
        }
        else{
            console.log('nije procitana');
            procitano = $('<td><img src="slike/sent.png" title="Poslata"></td>');
        }

        tr.append(procitano);
        korisnik = $('<td>'+p.primalac+'</td>')              //prikazujem primaoca
    }

    tr.append(hashTag).append(naziv).append(naslov).append(korisnik);
    
    if(primljene){
        var obrisi = $('<td><img id="'+p.id+'" class="brisanje" style="cursor:pointer" src="slike/remove.png" width="24" height="24"></td>');
        tr.append(obrisi);
        $('#tabela_poruka tbody').append(tr);
    }
    else{
        var izmeni = $('<td><span class="'+p.id+'"><img style="cursor:pointer" src="slike/edit.png" width="24" height="24"><span></td>');
        tr.append(izmeni);
        var obrisi = $('<td><img id="'+p.id+'" class="brisanje" style="cursor:pointer" src="slike/remove.png" width="24" height="24"></td>');
        tr.append(obrisi);
        $('#tabela_poslatih tbody').append(tr);
    }
    
    //console.log('uloga: '+uloga)
    if(uloga === "Kupac" && p.automatizovana != true){
        var btnOdg = $('<td><a class="'+p.id+'" id="'+p.posiljalac+'"> Odgovori </a></td>');
        tr.append(btnOdg);
    }
}

$(document).ready(function(){

/* ********************* KUPAC SALJE PORUKU PRODAVCU ************************************** */
$('#oglButt').click(function(){
    //console.log('sastavi poruku za prodavca:'+aktivni_prodavac+', akt_oglas:'+aktivni_oglas);
    if(aktivanKorisnik === "Kupac" || aktivanKorisnik === "Administrator"){
        $('#odgPrimalac').val(aktivni_prodavac);
        $('#odgOglas').val(aktivni_oglas);
        getAkt();
        ocistiPoljaPoruke();   
        $('#slanje_odg').css("display", "block");
    }
    else{
        alert('Morate biti prijavljeni da biste slali poruke.');
    }   
});

$('#odgPosalji').click(function(){
    //console.log('klikno da se posalje');
    $('#slanje_odg').css("display", "none");

    var naslov = $('input[name="odgNaslov"]').val();
    var sadrzaj = $('#odgSadrzaj').val();

    console.log("aktivan je "+akt.uloga);
    if(akt.uloga === "Kupac" || akt.uloga === "Administrator"){

        var poruka = {
                "naziv_oglasa" : aktivni_oglas,
                "naslov_poruke" : naslov,
                "sadrzaj_poruke" : sadrzaj,
        }
        $.ajax({
            type : "post",
            url : "rest/poruka/posaljiProdavcu?prodavac="+aktivni_prodavac,
            contentType : "application/json",
            data : JSON.stringify(poruka),
            success : function(response){
                if(response){
                    alert("Poruka je uspesno poslata");
                }else{
                    alert("Poruka nije uspesno poslata.");
                }
            }
        });
    }   
    else if(akt.uloga === "Prodavac"){
        var poruka = {
            "naziv_oglasa" : naziv_ogl,
            "naslov_poruke" : naslov,
            "sadrzaj_poruke" : sadrzaj,
        }
        $.ajax({
            type : "post",
            url : "rest/poruka/posaljiKupcu?kupac="+primalac_odg,
            contentType : "application/json",
            data : JSON.stringify(poruka),
            success : function(response){
                if(response){
                    alert('Uspesno ste odgovorili na poruku kupcu "'+primalac_odg+'"');
                    refreshPoslate();
                }else{
                    alert("Poruka nije uspesno poslata.");
                }
            }
        });
    }
});

/* ******************* PRIKAZ SVIH PORUKA KOD PRODAVCA ****************************** */
$('body').on("click", "#porProdavac", function(){
    //console.log('klikno na poruke prodavca');
    sakrijDivove();
    $('#t_poruka').show();

    $.ajax({
        type : "get",
        url : "rest/prodavac/getPoruke",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poruka tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    //console.log("salje: "+poruka.uloga_posiljaoca);
                    prikaziPoruku(poruka, poruka.uloga_posiljaoca, true);   // true -> primljene
                }
            }
        }
    });
    $.ajax({
        type : "get",
        url : "rest/prodavac/getPoslate",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poslatih tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    //console.log("salje: "+poruka.uloga_posiljaoca);
                    prikaziPoruku(poruka, false, false); // false1 -> ne prikazuj dugme 'odgovori'
                }                                        // false2 -> prikazi primaoca
            }
        }
    });
});

/* *********************** PRIKAZ JEDNE PORUKE PRODAVAC/KUPAC/ADMINISTRATOR ****************** */
function clearP(){
    $('#porukaPos').empty();
    $('#porukaOgl').empty();
    $('#porukaNasl').empty();
    $('#porukaSadr').empty();
    $('#porukaDat').empty();
}
$('#tabela_poruka').on('click', 'p', function(){
    console.log("Kliknuo na:"+ $(this).attr('class'));
    var id = $(this).attr('class');
    $.ajax({
        type : "get",
        url : "rest/poruka/getPoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            console.log("primio:"+response.naziv_oglasa);
            clearP();

            $('#porukaPos').html('<p>'+response.posiljalac+'</p>');
            $('#porukaOgl').html('<p>'+response.naziv_oglasa+'</p>');
            $('#porukaNasl').html('<p>'+response.naslov_poruke+'</p>');
            $('#porukaSadr').html('<p>'+response.sadrzaj_poruke+'</p>');
            let date = new Date(response.datum_vreme);
            $('#porukaDat').html('<p>'+date.toLocaleString()+'</p>');

            $('#prikaz_poruke').show();
        }
    });

});
$('#tabela_poslatih').on('click', 'p', function(){
    console.log("Kliknuo na:"+ $(this).attr('class'));
    var id = $(this).attr('class');
    $.ajax({
        type : "get",
        url : "rest/poruka/getPoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            console.log("primio:"+response.naziv_oglasa);
            clearP();

            $('#porukaPos').html('<p>'+response.posiljalac+'</p>');
            $('#porukaOgl').html('<p>'+response.naziv_oglasa+'</p>');
            $('#porukaNasl').html('<p>'+response.naslov_poruke+'</p>');
            $('#porukaSadr').html('<p>'+response.sadrzaj_poruke+'</p>');
            let date = new Date(response.datum_vreme);
            $('#porukaDat').html('<p>'+date.toLocaleString()+'</p>');

            $('#prikaz_poruke').show();
        }
    });

});


function ocistiPoljaPoruke(){
    $('input[name="odgNaslov"]').val("");
    $('#odgSadrzaj').val("");
}

function ocistiPoljaPoruke2(){
    $('input[name="porNaslov"]').val("");
    $('#porSadrzaj').val("");
}

/* ************** PRODAVAC ODGOVORA KUPCIMA ****************** */
var primalac_odg;       // treba mi kad odgovaram kupcu(gore), da znam ko je primalac      
var naziv_ogl;
$('#tabela_poruka tbody').on('click', 'a', function(){
    console.log("id:"+ $(this).attr('class'));
    getAkt();

    var id = $(this).attr('class');
    var primalac = $(this).attr('id');
    $.ajax({
        type : "get",
        url : "rest/poruka/getPoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            //console.log("primio:"+response.naziv_oglasa);
            if(response.naziv_oglasa === ""){        // ako nemam naziv oglasa => poruka je upucena
                $('#porPrimalac').val(primalac);            // preko kartice 'Korisnici'
                primalac_odg = primalac;
                naziv_ogl = response.naziv_oglasa;

                ocistiPoljaPoruke2();   
                $('#slanje_poruke').css("display", "block");
            }
            else{
                $('#odgPrimalac').val(primalac);            // preko kartice 'Korisnici'
                $('#odgOglas').val(response.naziv_oglasa);
                primalac_odg = primalac;
                naziv_ogl = response.naziv_oglasa;
                
                ocistiPoljaPoruke();   
                $('#slanje_odg').css("display", "block");
            }
        }
    });
});

/* ***************************** PRIKAZ SVIH PORUKA KOD KUPCA ******************************* */
$('body').on("click", "#porKupac", function(){
    sakrijDivove();
    $('#t_poruka').show();

    $.ajax({
        type : "get",
        url : "rest/kupac/getPoruke",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poruka tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    prikaziPoruku(poruka, poruka.uloga_posiljaoca, true);
                }
            }
        }
    });
    $.ajax({
        type : "get",
        url : "rest/kupac/getPoslate",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poslatih tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    //console.log("salje: "+poruka.uloga_posiljaoca);
                    prikaziPoruku(poruka, false, false); // false1 -> ne prikazuj da dugme 'odgovori'
                }                                        // false2 -> prikazi primaoca
            }
        }
    });
});

/* ***************************** PRIKAZ SVIH PORUKA KOD ADMINA ******************************* */
$('body').on("click", "#porAdmin", function(){
    sakrijDivove();
    $('#t_poruka').show();

    $.ajax({
        type : "get",
        url : "rest/admin/getPoruke",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poruka tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    prikaziPoruku(poruka, poruka.uloga_posiljaoca, true);
                }
            }
        }
    });
    $.ajax({
        type : "get",
        url : "rest/admin/getPoslate",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poslatih tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    //console.log("salje: "+poruka.uloga_posiljaoca);
                    prikaziPoruku(poruka, false, false); // false1 -> ne prikazuj da dugme 'odgovori'
                }                                        // false2 -> prikazi primaoca
            }
        }
    });
});

/* ************************** OBRISI PORUKU IZ INBOX/OUTBOX *************************************** */
$('#tabela_poruka').on("click", "img.brisanje", function(){
    console.log('brisem');

    var id = $(this).attr('id');
    $.ajax({
        type : "delete",
        url : "rest/poruka/deletePoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            if(response){
                alert('Poruka je uspesno obrisana.');
                refreshPrimljene();
            }
            else{
                alert('Poruka nije uspesno obrisana.');
            }
        }
    });
});

$('#tabela_poslatih').on("click", "img.brisanje", function(){
    console.log('brisem');

    var id = $(this).attr('id');
    console.log(id);
    $.ajax({
        type : "delete",
        url : "rest/poruka/deletePoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            if(response){
                alert('Poruka je uspesno obrisana.');
                refreshPoslate();
            }
            else{
                alert('Poruka nije uspesno obrisana.');
            }
        }
    });
});

/* ***************************** IZMENI PORUKU IZ OUTBOX-a *************************************** */
var id_poslate;
$('#tabela_poslatih').on("click", "span", function(){
    //console.log('izmena poslatih');
    var id = $(this).attr('class');
    id_poslate = id;
    $.ajax({
        type : "get",
        url : "rest/poruka/getPoruku?id="+id,
        contentType : "application/json",
        success : function(response){
            console.log('Oglas: '+response.naslov_poruke);
            if(response.naziv_oglasa !== ""){
                $('#izm_naslov_ogl').show();
            }
            else{
                $('#izm_naslov_ogl').hide();
            }
            $('#izmPrimalac').val(response.primalac);
            $('#izmOglas').val(response.naziv_oglasa);
            $('input[name="izmNaslov"]').val(response.naslov_poruke);
            $('#izmSadrzaj').val(response.sadrzaj_poruke);
            $('#izmena_poruke').css("display", "block");
        }
    });
});

$('#izmenuPosalji').click(function(){
    //console.log('saljem izmenu');

    var primalac = $('#izmPrimalac').val();
    var oglas = $('#izmOglas').val();
    var naslov = $('input[name="izmNaslov"]').val();
    var sadrzaj = $('#izmSadrzaj').val();

    var obj = {
        "primalac" : primalac,
        "naziv_oglasa" : oglas,
        "naslov_poruke" : naslov,
        "sadrzaj_poruke" : sadrzaj,
    }
    $.ajax({
        type : "put",
        url : "rest/poruka/izmenaPoslate?id="+id_poslate,
        data : JSON.stringify(obj),
        contentType : "application/json",
        success : function(response){
            if(response){
                alert('Poruka uspesno izmenjena.');
                refreshPoslate();
                $('#izmena_poruke').css("display", "none");
            }
            else{
                alert('Poruka nije uspesno izmenjena');
                $('#izmena_poruke').css("display", "none");
            }
        }
    })
});

});

function refreshPoslate(){
    $.ajax({
        type : "get",
        url : "rest/poruka/getPoslate",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poslatih tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    prikaziPoruku(poruka, false, false); 
                }                                        
            }
        }
    });
}

function refreshPrimljene(){
    $.ajax({
        type : "get",
        url : "rest/poruka/getPrimljene",
        contentType : "application/json",
        success : function(response){
            $('#tabela_poruka tbody').empty();
            if(!jQuery.isEmptyObject(response)){
                for(var poruka of response){
                    prikaziPoruku(poruka, poruka.uloga_posiljaoca, true); 
                }                                        
            }
        }
    });
}