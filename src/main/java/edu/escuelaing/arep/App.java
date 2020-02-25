package edu.escuelaing.arep;

import java.io.IOException;

import edu.escuelaing.arep.Server.httpServer;

/**
 * Hello world!
 *
 */
public class App {
    private static httpServer server;

    public static void main(String[] args) {
        server = new httpServer();
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Error server, seguro al crear");
            e.printStackTrace();
        }
        
    }
}
