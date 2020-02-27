package edu.escuelaing.arep.Server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import javax.imageio.ImageIO;

import edu.escuelaing.arep.DataBase.dataBase;
import edu.escuelaing.arep.DataBase.Impl.DataBaseImpl;
import edu.escuelaing.arep.annotations.AnnnotationHandler;
import org.apache.commons.io.FileUtils;

/**
 * This class manage each petition to the server
 */
public class httpHandler implements Runnable {

    private Socket clientSocket = null;
    static final String ROOT = System.getProperty("user.dir") + "/src/main/java/edu/escuelaing/arep/resources";
    static final String DEFAULT = "/index.html";
    static final String FILE_NOT_FOUND = "/NOT_FOUND.html";
    static final String METHOD_NOT_ALLOWED = "/NOT_SUPPORTED.html";
    static final String UNSUPPORTED_MEDIA_TYPE = "/NOT_SUPPORTED_MEDIA.html";
    private Map<String, AnnnotationHandler> webAnnoted;
    private dataBase dBase;

    /**
     * Worker constructor.
     * 
     * @param clntSocket client socket
     * @param webAnnoted el map de las anotaciones web String (url a manejar),
     *                   handler (de la anotacion)
     */
    public httpHandler(Socket clntSocket, Map webAnnoted) {
        clientSocket = clntSocket;
        this.webAnnoted = webAnnoted;
        dBase = new DataBaseImpl();
        

    }

    /**
     * Run method of the worker, here it manages the petition
     */
    public void run() {
        try {

            PrintWriter out = null;
            BufferedReader in = null;
            BufferedOutputStream dataOut = null;
            OutputStream outS = null;

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out = new PrintWriter(clientSocket.getOutputStream(), true); // devolver
            dataOut = new BufferedOutputStream(clientSocket.getOutputStream());
            outS = clientSocket.getOutputStream();
            String inputLine = in.readLine();
            String[] header = inputLine.split(" ");
            System.out.println("header " + header[1]);
            System.out.println("pet :"+header[0]);
            if (header[0].equals("GET")) {
                File rFile = null;
                if (header[1].equals(" ") || header[1].equals("") || header[1].equals("/")) {
                    rFile = new File(ROOT, DEFAULT);
                    respond(out, dataOut, rFile, "text/html", "200", ROOT + DEFAULT, outS);

                } 

                else if(header[1].contains("/ann")){
                        String response = getAnnotationResponse(header);
                        respondRaw(out, dataOut, response, "text/html", "200 OK");
                }
                else {

                    //reponderPeticion(header);
                    
                    String[] s = soportado(header[1]);
                    if (s[0].equals("ok")) {
                        rFile = null;
                        if(s[1]=="db"){
                            String res = dbResponse(header[1]);
                            rFile = new File(res);
                            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                            System.out.println(res);
                            
                            System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
                            respond(out, dataOut, rFile, "text/html", "200", res, outS);
                        }
                        else{
                            rFile = new File(ROOT + s[1] + header[1]);
                        
                            System.out.println("Path :"+ROOT + s[1] + header[1]);
                            if (rFile.exists()) {
                                respond(out, dataOut, rFile, s[2], "200", ROOT + s[1] + header[1], outS);
                            } else {
                                rFile = new File(ROOT, FILE_NOT_FOUND);

                                respond(out, dataOut, rFile, "text/html", "404", ROOT + FILE_NOT_FOUND, outS);
                            }
                        }
                    } 
                    else {
                        rFile = new File(ROOT, UNSUPPORTED_MEDIA_TYPE);
                        respond(out, dataOut, rFile, "text/html", "415", ROOT + UNSUPPORTED_MEDIA_TYPE, outS);

                    }
                }
            }

            else {

                File f = new File(ROOT, METHOD_NOT_ALLOWED);
                respond(out, dataOut, f, "text/html", "405", ROOT + METHOD_NOT_ALLOWED, outS);

            }
            out.close();
            in.close();
            dataOut.close();
            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error de io en responder: ");
            e.printStackTrace();
        }

        try {
            this.finalize();
        } catch (Throwable e) {
            System.out.println("Error al matar thread");
            e.printStackTrace();
        }
        

        

    }


    private String getAnnotationResponse(String[] header) {
        String petition = header[1];
        String param = "";
        String response = "";
        if (petition.contains("?")) {
            param = petition.substring(petition.indexOf("?") + 1, petition.length());
            petition=petition.substring(0, petition.indexOf("?"));
        }
        System.out.println("petition "+petition);
        if(webAnnoted.containsKey(petition)){
            if(param.equals("")){
                response = webAnnoted.get(petition).handle();
            }
            else{
                response = webAnnoted.get(petition).handle(param);
            }
        }
        return response;
        
    
    }

    private String dbResponse(String petition) {
        String res="";
        if(petition.equals("ususarios")){
            String[] users = dBase.consultarUsuarios();
            for (String s:users){
                System.out.println(s);
            }
            res = createResponse(users);
            

        }
        System.out.println("-------------------------------------------------");
        
        return res;   
    }


    /**
     * This method crates the response with the info of the db on a html
     * @param usuarios the array of info to put on a html
     * @return the path of the html with the info
     */
    private static String createResponse(String[] usuarios) {
        try{
            File htmlTemplateFile = new File(ROOT+"/base.html");
            String htmlString = FileUtils.readFileToString(htmlTemplateFile);
            
            String ans = "";
            for (String s : usuarios) {
                ans += "<tr><td>" + s + "</td></tr>";

            }
            htmlString = htmlString.replace("$body", ans);
            File newHtmlFile = new File(ROOT+"/usuarios.html");
            FileUtils.writeStringToFile(newHtmlFile, htmlString);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ROOT+"/usuarios.html";

    }


    /**
     * checks if the petition is supported, checks for the file type
     * 
     * @param peticionGet the petition
     * @return string[] with the response, where to find the file, and the myme type
     */
    private static String[] soportado(String peticionGet) {
        String[] ans = new String[3];
        if (peticionGet.endsWith(".png")) {
            ans[0] = "ok";
            ans[1] = "/imgs";
            ans[2] = "image/png";
        }

        else if (peticionGet.endsWith(".jpg")) {
            ans[0] = "ok";
            ans[1] = "/imgs";
            ans[2] = "image/jpg";
        } else if (peticionGet.endsWith(".html")) {
            ans[0] = "ok";
            ans[1] = "";
            ans[2] = "text/html";
        } else if (peticionGet.endsWith(".js")) {
            ans[0] = "ok";
            ans[1] = "/js";
            ans[2] = "application/javascript";
        }
        else if(peticionGet.contains("/db/")){
            ans[0]="ok";
            ans[1]="db";
            ans[2]="text/html";

        } 
        else {
            ans[0] = "error";
            ans[1] = "";
            ans[2] = "text/html";
        }
        return ans;

    }

    /**
     * Este metodo responde la peticion al cliente hallando un archivo
     * @param out printwriter
     * @param dataOut bufferedoutputstream
     * @param response espuesta
     * @param type mime type
     * @param code codigo http
     * @param filePath path del archivo
     * @param outS outputstream
     */
    private static void respond(PrintWriter out, BufferedOutputStream dataOut, File response, String type, String code,
            String filePath, OutputStream outS) {
        String header = "HTTP/1.1 " + code + "\r\n" + "Access-Control-Allow-Origin: *\r\n" + "Content-type: " + type
                + "\r\n";

        String[] con = type.split("/");
        System.out.println("file :"+filePath);
        try {
            if (con[0].equals("image")) {
                BufferedImage image = ImageIO.read(response);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, con[1], os);
                byte[] r = os.toByteArray();
                header += "Connection: close\r\n" + "Content-length: " + r.length + "\r\n" + "\r\n";
                byte[] d = header.getBytes();
                dataOut.write(d);

                dataOut.write(os.toByteArray());
                dataOut.flush();
                dataOut.close();

            } else {
                header += "\r\n";
                out.println(header);
                BufferedReader reader = new BufferedReader(new FileReader(response));
                StringBuffer result = new StringBuffer();
                String res = "";
                while ((res = reader.readLine()) != null) {
                    result.append(res);
                }
                out.println(result);
                out.flush();
                out.close();
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("erro en envio");
            System.out.println(e);
        }
        out.flush();
        out.close();

    }

    /**
     * Este metodo responde la peticion al cliente devolviendo un html string
     * @param out printwriter
     * @param dataOut bufferedoutputstream
     * @param response String respuesta
     * @param type mime type
     * @param code codigo http
     */
    private static void respondRaw(PrintWriter out, BufferedOutputStream dataOut, String response, String type, String code) {
        String header = "HTTP/1.1 " + code + "\r\n" + "Access-Control-Allow-Origin: *\r\n" + "Content-type: " + type
                + "\r\n";

        try {
            
                header += "\r\n";
                out.println(header);               
                out.println(response);
                out.flush();
                out.close();
            
        } catch (Exception e) {
            System.out.println("erro en envio");
            System.out.println(e);
        }
        out.flush();
        out.close();

    }
}
