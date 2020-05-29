var aktivanKorisnik = -1;       // trenutno niko

$(document).ready(function() {

$('button#btn_reg').click(function(){
    console.log('usao u btn reg');

    $('div#div_prijava').hide();
    $('div#dugme_reg').hide();
    $('div#div_kategorije').hide();
    $('div#glavna_strana').hide();
    $('div#div_registracija').show();
});

$(document).on("click", "#li_logout", function(){
    $.ajax({
        type: 'post',
        url: 'rest/logout',
        success: function() {               
            aktivanKorisnik = -1;
            alert("Korisnik se izlogovao!");
            $('#li_login').show();
            $('#li_logout').hide();
            $('div#div_prijava').show();
            $('div#dugme_reg').show();
            $('div#div_kategorije').hide();
            $('div#glavna_strana').hide();
            $('div#div_registracija').hide();
            $('#modifikacija').hide();
            $('#post_oglas').hide();
            $('#filtriraj_oglase').hide();
            $('div#zaglavlje_prodavac').hide();
            $('div#kupac_liste').hide();
            $('div#zaglavlje_kupac').hide();
            $('#list_recenzija').hide();
            $('#porAdmin').hide();
            $('#porProdavac').hide();
            $('#porKupac').hide();
            $('#li_prijavljeni').hide();
        }
    });
});

$('#forma').submit(function(event) {
    console.log("Usao u logovanje");
    event.preventDefault();

    var username = $('input[name="username"]').val();
    var password = $('input[name="password"]').val();
    //console.log(username + ' ' + password);

    if(!username || !password){
        alert("Neophodno je da unesete korisnicko ime i lozinku.");
        event.preventDefault();
    }
    else{
        var object = {
            "username" : username,
            "password" : password,
        }

        var ispravno = true;
        $.ajax({
            type: 'post',
            url: 'rest/login',
            contentType: 'application/json',
            data: JSON.stringify(object),          // pretvaram obj u JSON format (iako to vec jeste)
            success: function(response) {
                if(response == 1){   
                    getAktivan();
                    aktivanKorisnik = "Administrator";
                    $('li#modifikacija').show();
                    $('#li_logout').show();
                    $('#porAdmin').show();
                    $('#li_prijavljeni').show();
                    $('#porProdavac').hide();
                    $('#porKupac').hide();
                    $('#post_oglas').hide();
                    $('#list_recenzija').hide();
                    $('#korisnickaKupac').hide();
                    $('#korisnickaProdavac').hide();
                    $('div#kupac_liste').hide();
                    $('#li_login').hide();
                    $('#filter').hide();
                    alert("Administrator se uspesno ulogovao!");
                }
                else if(response == 2){
                    getAktivan();
                    aktivanKorisnik = "Prodavac";
                    $('#porProdavac').show();
                    $('#list_recenzija').show();
                    $('#korisnickaProdavac').show();
                    $('#post_oglas').show();
                    $('#li_logout').show();
                    $('#filtriraj_oglase').show();
                    $('#filter').show();
                    $('#li_prijavljeni').hide();
                    $('#porAdmin').hide();
                    $('#porKupac').hide();
                    $('li#modifikacija').hide();
                    $('#korisnickaKupac').hide();
                    $('div#kupac_liste').hide();
                    $('#li_login').hide();
                    alert("Prodavac se uspesno ulogovao!");
                }
                else if(response == 3){
                    getAktivan();
                    aktivanKorisnik = "Kupac";
                    $('#korisnickaKupac').show();
                    $('div#kupac_liste').show();
                    $('#li_logout').show();
                    $('#porKupac').show();
                    $('#li_prijavljeni').hide();
                    $('#porAdmin').hide();
                    $('#porProdavac').hide();
                    $('#korisnickaProdavac').hide();
                    $('#post_oglas').hide();
                    $('#list_recenzija').hide();
                    $('li#modifikacija').hide();
                    $('#li_login').hide();
                    $('#filter').hide();
                    alert("Kupac se uspesno ulogovao!");
                }
                else{
                    aktivanKorisnik = -1;
                    ispravno = false;
                    $('#err').text('Neispravno korisnicko ime / lozinka.');
                }

            
            if(ispravno){
                sakrijDivove();
                $('div#glavna_strana').show();
                    
                $.ajax({
                    type : "get",
                    url : "rest/oglasi/findAll",
                    contentType: 'application/json',
                    success : function(response){
                        $('#t_pocetna tbody').empty();
                        for(var oglas of response){
                            add3row(oglas);
                        }
                    }
                });
            }
            }
        });
    }

});

var postoji_username;
$('input[name="username_reg"]').on('input',function(){
    var username = $(this).val();
    $.ajax({
        type: "get",
        url : "rest/proveraUsername/"+username,
        contentType : "applicatoin/json",
        success : function(response){
            if(response == "true"){
                postoji_username = false;
            }
            else{
                postoji_username = true;
            }
        }
    }); 
});

$('#forma_reg').submit(function(event){
    console.log('usao u forma reg');
    event.preventDefault();

    var username = $('input[name="username_reg"]').val();
    var password = $('input[name="password_reg"]').val();
    var ime = $('input[name="ime_reg"]').val();
    var prezime = $('input[name="prezime_reg"]').val();
    var tel = $('input[name="tel_reg"]').val();
    var grad = $('input[name="grad_reg"]').val();
    var mail = $('input[name="mail_reg"]').val();
    //console.log(username+" "+password+" "+ime+" "+prezime+" "+tel+" "+grad+" "+mail);
    

    console.log('postoji: '+ postoji_username);
    var validno = true;
    if(!username){
        //alert("Unesite podatke vezane za polje 'korisnicko ime'.");
        $('#errReg').text('Neophodno je uneti korisnicko ime.');
        $('input[name="username_reg"]').focus();
        validno = false;
    }
    else if(postoji_username){
        $('#errReg').text('Uneto korisnicko ime vec postoji.');
        $('input[name="username_reg"]').focus();
        validno = false;
    }
    else if(!password){
        //alert("Unesite podatke vezane za polje 'lozinka'.");
        $('#errReg').text('Neophodno je uneti lozinku.');
        $('input[name="password_reg"]').focus();
        validno = false;
    }
    else if(!ime){
        //alert("Unesite podatke vezane za polje 'ime'.");
        $('#errReg').text('Neophodno je uneti ime korisnika.');
        $('input[name="ime_reg"]').focus();
        validno = false;
    }
    else if(!prezime){
        //alert("Unesite podatke vezane za polje 'prezime'.");
        $('#errReg').text('Neophodno je uneti prezime korisnika.');
        $('input[name="prezime_reg"]').focus();
        validno = false;
    }
    else if(!tel){
        //alert("Unesite podatke vezane za polje 'telefon'.");
        $('#errReg').text('Neophodno je uneti broj telefona.');
        $('input[name="tel_reg"]').focus();
        validno = false;
    }
    else if(!grad){
        //alert("Unesite podatke vezane za polje 'grad'.");
        $('#errReg').text('Neophodno je uneti grad korisnika.');
        $('input[name="grad_reg"]').focus();
        validno = false;
    }
    else if(!mail){
        //alert("Unesite podatke vezane za polje 'e-mail'.");
        $('#errReg').text('Neophodno je uneti e-mail adresu.');
        $('input[name="mail_reg"]').focus();
        validno = false;
    }

    if(validno == true){
        $('#errReg').text('');
        var object = {
            "username" : username,
            "password" : password,
            "ime" : ime,
            "prezime" : prezime,
            "br_telefona" : tel,
            "grad" : grad,
            "mail" : mail,
        }
        
        $.ajax({
            type: 'post',
            url: 'rest/registracija',
            contentType: 'application/json',
            data: JSON.stringify(object),          // pretvaram obj u JSON format (iako to vec jeste)
            success: function() {               
                alert("Korisnik uspesno kreiran!");
                $('#div_registracija').hide();
                $('#div_prijava').show();
                $('#dugme_reg').show();
            }
            });

    }

    event.preventDefault();    
});

});