package edu.escuelaing.arep.annotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Clase que majea la ejecucion de metodos con anotacion @Web
 */
public class AnnnotationHandler {

    private Method m;

    /**
     * Constructore AnnnotationHandler
     *
     * @param method metodo a guardar.
     */
    public AnnnotationHandler(Method method) {
        this.m = method;
    }

    /**
     * Funcion que maneja la ejecucion de funciones con parameto para la anotacion custom web
     * @param arg el parametro que necesita el metodo guardado.
     * @return String la respuesta del metodo guardado
     */
    public String handle(String arg) {
        String ans = null;
        try {
            System.out.println("mehtod "+m+" args: "+arg);
            ans = (String) m.invoke(null, arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * Funcion que maneja la ejecucion de funciones sin parameto para la anotacion custom web
     * @return String la respuesta del metodo guardado
     */
    public String handle() {
        String ans = null;
        System.out.println("mehtod "+m);
        try {
            ans = (String) m.invoke(null, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ans;
    }
}
