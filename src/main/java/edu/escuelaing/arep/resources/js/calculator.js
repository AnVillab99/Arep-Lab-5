
calculator = (function () {
    var datos;

    return {

        ponerDatos: function (datos2) {
            var dat = JSON.parse(datos2);

        },
        obtenerDatos: function () {     
            console.log("d");
            window.location = window.location.href+"db/usuarios";
        }
    };  
})();
calculadora = (function () {
    return {      
        getResultadosR: function (url,datas, callback) {
            console.log("pidiendo get");
            $.get( "url", function( data ) {callback(data);});
        }
    };

})();