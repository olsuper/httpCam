package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class httpServer1 {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/info", new InfoHandler());
        HttpContext hc1 = server.createContext("/get", new GetHandler());
//        hc1.setAuthenticator(new BasicAuthenticator("get") {
//           @Override
//            public boolean checkCredentials(String user, String pwd) {
//                return user.equals("admin") && pwd.equals("password");
//            }
//        });
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("The server is running");
    }

    // http://localhost:8000/info
    static class InfoHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Use /get?hello=word&foo=bar to see how to handle url parameters";
            httpServer1.writeResponse(httpExchange, response.toString());
        }
    }

    static class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map<String,String> parms = httpServer1.queryToMap(httpExchange.getRequestURI().getQuery());

            if(!parms.get("run").isEmpty())
            {
                if(parms.get("run").toString().equals("rs")) {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        //Process pr = rt.exec("cmd /c dir");

                        //Process pr = rt.exec("cd /home/oleg/Downloads/chdkptp-r795");
                        Process pr = rt.exec("/home/oleg/Downloads/chdkptp-r795/chdkptp.sh -e=connect -e=rec -e=play -e=rec -e=shoot");

                        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                        String line = null;

                        while ((line = input.readLine()) != null) {
                            System.out.println(line);
                        }

                        int exitVal = pr.waitFor();
                        System.out.println("Exited with error code " + exitVal);


                    } catch (Exception e) {
                        System.out.println(e.toString());
                        e.printStackTrace();
                    }
                }

            }
            else
            {};

            response.append("<html><body>");
            response.append("run : " + parms.get("run") + "<br/>");
            response.append("</body></html>");
            httpServer1.writeResponse(httpExchange, response.toString());
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}
