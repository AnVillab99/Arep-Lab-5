package edu.escuelaing.arep.annotations;

/**
 * Clase para guardar funciones que necesiten la anotaicon web
 */
public class AnnotationMethods {

    /**
     * Metodo que devuleve un html comos string saludando, prueba de anotaciones con parametro
     * @param name String nombre a saludar
     * @return string el html del saludo custom
     */
    @Web("hola")
    public static String hola(String name) {
        return "<html>" +
                "<head>" +
                "<title>Hola</title>" +
                "</head>" +
                "<body>" +
                "<h1>Hola</h1>" +
                "<div>Hola, "+name+" hoy es un buen dia</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Metodo que devuleve un html comos string saludando, prueba de anotaciones
     * @return el html del saludo
     */
    @Web("hello")
    public static String hello() {
        return "<html>" +
                "<head>" +
                "<title>Hello</title>" +
                "</head>" +
                "<body>" +
                "<h1>Hello</h1>" +
                "<div>Hello desconcocido</div>" +
                "</body>" +
                "</html>";
    }
}
