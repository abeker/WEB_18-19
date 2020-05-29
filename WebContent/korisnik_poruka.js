function dodajKorisnika(korisnik, poruka, uloga){
    var usernameTd = $('<td>'+ korisnik.username +'</td>');
    var imeTd = $('<td>'+ korisnik.ime +'</td>');
    var prezimeTd = $('<td>'+ korisnik.prezime +'</td>');
    var kontaktTd = $('<td>'+ korisnik.br_telefona +'</td>');
    var gradTd = $('<td>'+ korisnik.grad +'</td>');
    var mailTd = $('<td>'+ korisnik.mail +'</td>');
    
    //console.log("Poruka: " + poruka + ", Uloga: "+ uloga);
    var ulogaTd = $('<td></td>');
    var sel = $('<select onchange="getVal(this)"></select>');
    var optA = $('<option>Administrator</option>');
    var optP = $('<option>Prodavac</option>');
    var optK = $('<option>Kupac</option>');
    if(uloga && aktivan !== -1){
        if(korisnik.uloga==="Administrator"){
            if(korisnik.username === aktivan.username){         // ne moze da menja svoju ulogu
                ulogaTd.append(korisnik.uloga);
            }
            else{
                sel.append(optA).append(optP).append(optK);
                ulogaTd.append(sel);    
            }
        }
        else if(korisnik.uloga==="Prodavac"){
            sel.append(optP).append(optA).append(optK);
            ulogaTd.append(sel);
        }
        else{
            sel.append(optK).append(optA).append(optP);
            ulogaTd.append(sel);
        }
    }
    else{
        ulogaTd.append(korisnik.uloga);
    }

    var messTd;
    if(poruka && aktivan !== -1){
        if(aktivan.uloga === "Administrator"){       // aktivan je admin
            if(korisnik.username === aktivan.username){         // ne moze da salje sam sebi
                messTd = $('<td>&nbsp;</td>');
            }
            else{
                messTd = $('<td><img class="sastavi_poruku" title="Sastavi poruku" width="26" height="26" style="cursor:pointer" src="slike/posalji32.png"></td>');
            }
        }
        if(aktivan.uloga==="Prodavac"){          // aktivan prodavac
            if(korisnik.uloga==="Administrator"){
                messTd = $('<td><img class="sastavi_poruku" title="Sastavi poruku" width="26" height="26" style="cursor:pointer" src="slike/posalji32.png"></td>');
            }
            else{
                messTd = $('<td></td>');
            }
        }
        if(aktivan.uloga==="Kupac"){         // aktivan kupac
            if(korisnik.uloga==="Prodavac"){
                messTd = $('<td><img class="sastavi_poruku" title="Sastavi poruku" width="26" height="26" style="cursor:pointer" src="slike/posalji32.png"></td>');
            }
            else{
                messTd = $('<td></td>');
            }
        }
    }

    var tr = $('<tr></tr>');
    tr.append(usernameTd).append(imeTd).append(prezimeTd).append(ulogaTd).append(kontaktTd)
    .append(gradTd).append(mailTd);
    
    if(poruka){
        tr.append(messTd);
    }
    tr.click(clickRow(korisnik.username));
    $('#tabela_korisnika tbody').append(tr);
}

/* ******************** PROMENA ULOGE ***************************** */
function getVal(sel){
    //console.log("ONCHANGE: " + sel.value);
    $.ajax({
        type : "put",
        url : "rest/admin/promeniUlogu?username="+selRow+"&novaUloga="+sel.value,
        contentType : "application/json",
        success : function(response){
            if(response){
                alert("Uspesno je promenjena uloga korisnika " + selRow + ".");
            }
            else{
                alert("Desio se problem prilikom promene uloge korisnika " + selRow +".");
            }
        }
    });
}

var selRow;
function clickRow(korisnik){
	return function() {
        console.log('Kliknuo na: ' + korisnik);
        $('tr.selected').removeClass('selected');
		selRow = korisnik;					
        $(this).addClass('selected');
    };
}

$(document).ready(function(){
    getAktivan();

$(document).on("click", "#id_korisnici", function(){
    console.log('click na korisnike');
    if(aktivan==-1){            // niko nije aktivan
        $('#id_poruka').hide();
        $('#id_uloga').hide();
    }
    else{           // neko je aktivan
        $('#id_poruka').show();
        if(aktivan.uloga==="Administrator"){               // samo ako je admin, moze da menja uloge
            $('#id_uloga').show();
        }
        else{
            $('#id_uloga').hide();
        }
    }
    
    prikaziTabelu(false, false);
});


/* ******************************** PORUKA ****************************************************** */
$(document).on("click", "#id_poruka", function(){
    getAktivan();
    prikaziTabelu(true, false);
});

function ocistiPoljaPoruke(){
    $('input[name="porNaslov"]').val("");
    $('#porSadrzaj').val("");
}

$(document).on("click", "img.sastavi_poruku", function(){
    $('#porPrimalac').val(selRow);
    ocistiPoljaPoruke();
    $('#slanje_poruke').css("display", "block");
});

$('button#porPosalji').click(function(){
    $('#slanje_poruke').css("display", "none");

    var primalac = $('#porPrimalac').val();
    var naslov = $('input[name="porNaslov"]').val();
    var sadrzaj = $('#porSadrzaj').val();
    
    var poruka = {
        "naziv_oglasa" : "",
        "naslov_poruke" : naslov,
        "sadrzaj_poruke" : sadrzaj,
    }
    $.ajax({
        type : "post",
        url : "rest/poruka/posaljiPoruku?primalac="+primalac,
        contentType : "application/json",
        data : JSON.stringify(poruka),
        success : function(response){
            if(response == 1){
                alert('Poruka je uspesno poslata korisniku "'+primalac+'".');
                refreshPoslate();
            }
            else if(response == -1){
                alert('Niste uneli odgovarajuce korisnicko ime.');
            }
            else{
                alert('Poruka nije poslata, doslo je do greske.');
            }
        }
    });

});

/* ******************************** ULOGA ****************************************************** */
$(document).on("click", "#id_uloga", function(){
    console.log('usao u promenu uloge');
    getAktivan();
    prikaziTabelu(false, true);
});

/* ************************ PRETRAGA KORISNIKA ******************************* */
$('#searchIme').on('input',function(){
    //console.log($(this).val());
    sakrijDivove();
    $("div#detalji_oglasa").hide();
    $("div#t_korisnici").show();

    var ime = $(this).val();
    var grad = $('#searchGrad').val();

    $.ajax({
        type : "get",
        url : "rest/oglasi/pretragaKorisnika?ime="+ime+"&grad="+grad,
        contentType : "application/json",
        success : function(response){
            $('#tabela_korisnika tbody').empty();
            for(var k of response){
                //console.log(k.username);
                dodajKorisnika(k, false, false);
            }   
        }
    });
});

$('#searchGrad').on('input',function(){
    //console.log($(this).val());
    sakrijDivove();
    $("div#detalji_oglasa").hide();
    $("div#t_korisnici").show();

    var ime = $('#searchIme').val();
    var grad = $(this).val();

    $.ajax({
        type : "get",
        url : "rest/oglasi/pretragaKorisnika?ime="+ime+"&grad="+grad,
        contentType : "application/json",
        success : function(response){
            $('#tabela_korisnika tbody').empty();
            for(var k of response){
                //console.log(k.username);
                dodajKorisnika(k, false, false);
            }
        }
    });
});

});

var aktivan;            // ovde mi je trenutno aktivan korisnik
function getAktivan(){
    $.ajax({
        type : "get",
        url : "rest/getAktivan",
        contentType : "application/json",
        success : function(response){
            if(response !== undefined){         // ako sa servera posaljem null
                aktivan = response;
            }
            else{
                aktivan = -1;           // niko nije ulogovan
            }
        }
    });
}

function prikaziTabelu(poruka, uloga){
    sakrijDivove();
    $("div#detalji_oglasa").hide();
    $("div#t_korisnici").show();

    $.ajax({
        type : "get",
        url : "rest/getKorisnike",
        contentType : "application/json",
        success : function(response){
            $('#tabela_korisnika tbody').empty();
            for(var k of response){
                //console.log(k.username);
                dodajKorisnika(k, poruka, uloga);
            }
        }
    });
}