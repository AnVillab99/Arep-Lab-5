package edu.escuelaing.arep.Server;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
//import com.google.gson.Gson;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.escuelaing.arep.DataBase.dataBase;
import edu.escuelaing.arep.DataBase.Impl.DataBaseImpl;
import edu.escuelaing.arep.annotations.AnnnotationHandler;
import edu.escuelaing.arep.annotations.Web;


public class httpServer {

    
    private int PORT;
    private int Threads =5;
    private dataBase db;
    
    static private Map<String,AnnnotationHandler> webAnnoted = new HashMap<String,AnnnotationHandler>();
    
    /**
     * Metodo principal, recibe los client socket y genera workers para manejarlos
     * 
     * @return
     * @throws IOException si el puerto del server esta ocupado
     */
    public void start() throws IOException {
        PORT = getPort();
        //Gson gson = new Gson();
        System.out.println("puerto "+PORT);
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(PORT);
        System.out.println("Abierto");
        Socket clientSocket = null;
        boolean conectado = true;
        ExecutorService executioner = Executors.newFixedThreadPool(Threads);
        while (conectado) {
            try{ 
                clientSocket = serverSocket.accept();
                System.out.println("Conectado");
                executioner.execute(new Thread(new httpHandler(clientSocket,webAnnoted)));
                // Thread t1 = new Thread(new httpHandler(clientSocket));
                // t1.start();              
            }
            catch(Exception e){System.out.println("error "+e);
                serverSocket.close();
        }
    }

        

    }

    private dataBase getDb(){
        if(db==null){
            db = new DataBaseImpl();

        }
        return db;
    }

    
    /**
     * This method return the port where the app works
     * @return int port
     */

    static int getPort() {
        if (System.getenv("PORT") != null) {
        return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set
    }
    
    
    /**
     * Este metodo revisa los metodos con anotaciones web para facilitar su ejecucion
     */
	public void checkFiles() {
        String path = "edu/escuelaing/arep/annotations";
        ArrayList<File> folders = new ArrayList<File>();
        try {
            ClassLoader classldr= Thread.currentThread().getContextClassLoader();
            if (classldr == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            Enumeration<URL> resources = classldr.getResources(path);
            while (resources.hasMoreElements()) {
                folders.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            } 
        
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkAnnotations(folders);
    }

        



    
    private void checkAnnotations(ArrayList<File> folders){
    for (File folder : folders) {
        if (folder.exists()) {
            for (String clase : folder.list()) {
                System.out.println(clase);
                if (clase.endsWith(".class")) {
                    Class<?> c=null;
                    try{
                        c = Class.forName("edu.escuelaing.arep.annotations."+clase.substring(0, clase.indexOf(".")));
                        Method[] methods = c.getMethods();
                        for (Method m : methods) {
                            if (m.isAnnotationPresent(Web.class)) {                                  
                                webAnnoted.put("/ann/" + m.getAnnotation(Web.class).value(), new AnnnotationHandler(m));
                            }
                        }
                    }
                    catch(ClassNotFoundException cs){
                        System.out.println("class not found exception : "+cs);
                    }
                
                }
            }
        }
    }
    }

    public httpServer(){
        db=getDb();
    }

}
