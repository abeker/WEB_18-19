$(document).ready(function(){

/* ************************* PRETRAGA OGLASA ******************************* */

$(document).on("click", "#li_pretraga", function(){
    console.log('usao u pretragu');

    $.ajax({
        type : "get",
        url : "rest/oglasi/getGradovi",
        contentType : "application/json",
        success : function(response){
            $('#pGrad').empty();
            var opt = $('<option></option>');
            $('#pGrad').append(opt);
            for(var grad of response){
                var option = $('<option>'+ grad +'</option>');
                $('#pGrad').append(option);
            }
            $('#mod_pretraga').css("display", "block");
        }
    });

    $.ajax({
        type : "get",
        url : "rest/oglasi/getGradoveKorisnika",
        contentType : "application/json",
        success : function(response){
            $('#p2Grad').val('');
            for(var grad of response){
                var option = $('<option>'+ grad +'</option>');
                $('#p2Grad').append(option);
            }

            
        }
    });
});

$(document).on("click", "button#pretrazi", function(){
    console.log('pretrazujem...');
    sakrijDiv();

    var naziv = $('input[name="pNaziv"]').val();
    var cenaOd = $('input[name="cenaOd"]').val();
    var cenaDo = $('input[name="cenaDo"]').val();
    var ocenaOd = $('input[name="ocenaOd"]').val();
    var ocenaDo = $('input[name="ocenaDo"]').val();
    var dat = new Date($('input[name="datumOd"]').val());
    var datumOd = dat.getTime();
    var dat1 = new Date($('input[name="datumDo"]').val());
    var datumDo = dat1.getTime();
    var grad = $('#pGrad').val();
    var status = $('#pStatus').val();
    console.log(naziv+", "+cenaOd+", "+cenaDo+", "+ocenaOd+", "+ocenaDo+", "+datumOd+", "+datumDo
            + ", "+grad+", "+status);

    var ispravno = true;

    var flag = false;       // postavim flag, da znam da dole vratim u ""
    var flag1 = false;
    if(cenaDo === ""){          // ako nisam uneo cenu DO, znaci da imam samo donju granicu
        cenaDo = cenaOd+1;
        flag = true;
    }
    if(ocenaDo === ""){          
        ocenaDo = ocenaOd+1;
        flag1 = true;
    }

    if(cenaOd > cenaDo){
        ispravno = false;
        $('#errCena').text('Cena OD mora biti veca od cene DO');
        $('input[name="cenaOd"]').focus();
    }
    else{
        $('#errCena').text('');
    }
    if(ocenaOd > ocenaDo){
        ispravno = false;
        $('#errOcena').text('Ocena OD mora biti veca od ocene DO');
        $('input[name="ocenaOd"]').focus();
    }
    else{
        $('#errOcena').text('');
    }
    if(datumOd > datumDo){
        ispravno = false;
        $('#errDatum').text('Datum OD mora biti veci od datuma DO');
        $('input[name="datumOd"]').focus();
    }
    else{
        $('#errDatum').text('');
    }

    if(isNaN(datumOd)){
        datumOd = "";
    }
    if(isNaN(datumDo)){
        datumDo = "";
    }
    if(flag){
        cenaDo="";
    }
    if(flag1){
        ocenaDo="";
    }

    console.log(datumOd+", "+datumDo);

    if(ispravno){
        var str = "naziv="+naziv+"&cenaOd="+cenaOd+"&cenaDo="+cenaDo+"&ocenaOd="+ocenaOd+"&ocenaDo="+ocenaDo+
            "&datumOd="+datumOd+"&datumDo="+datumDo+"&grad="+grad+"&status="+status;
        $.ajax({
            type : "get",
            url : "rest/oglasi/getPretraga?"+str,
            contentType : "application/json",
            success : function(response){
                $('#glavna_strana').show();
                $('#mod_pretraga').css("display", "none");
                $('#t_pocetna tbody').empty();
                brojacProizvoda = 0;
                for(var oglas of response){
                    console.log(oglas.naziv);
                    add3row(oglas);
                }
                ponistiPolja();
            }
        });
    }

});

});

function ponistiPolja(){
    console.log('usao u ponistavanje polja');
    $('input[name="pNaziv"]').val('');
    $('input[name="cenaOd"]').val('');
    $('input[name="cenaDo"]').val('');
    $('input[name="ocenaOd"]').val('');
    $('input[name="ocenaDo"]').val('');
    $('input[name="datumOd"]').val('');
    $('input[name="datumDo"]').val('');
    $('#pGrad').val('');
    $('#pStatus').val('');
}