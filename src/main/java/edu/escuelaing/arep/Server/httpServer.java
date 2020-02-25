package edu.escuelaing.arep.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
//import com.google.gson.Gson;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class httpServer {

    
    int PORT;
    int Threads =5;
    
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
                executioner.execute(new Thread(new httpResponder(clientSocket)));
                // Thread t1 = new Thread(new httpResponder(clientSocket));
                // t1.start();              
            }
            catch(Exception e){System.out.println("error "+e);
                serverSocket.close();
        }
    }

        

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


	

}
