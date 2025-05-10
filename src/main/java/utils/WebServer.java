package utils;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

public class WebServer {
    private static HttpServer server;
    private static final int[] PORTS = {8081, 8082, 8083, 8084, 8085}; // Liste de ports à essayer
    private static final String PDF_DIR = "src/main/resources/web/billets/";
    private static String localIpAddress;
    private static int usedPort;

    public static void start() {
        try {
            // Créer le dossier PDF s'il n'existe pas
            File pdfDir = new File(PDF_DIR);
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
                System.out.println("Dossier PDF créé : " + pdfDir.getAbsolutePath());
            }

            // Trouver l'adresse IP locale
            localIpAddress = getLocalIpAddress();
            if (localIpAddress == null) {
                localIpAddress = "localhost";
            }
            System.out.println("Adresse IP locale détectée : " + localIpAddress);
            
            // Trouver un port disponible
            usedPort = findAvailablePort();
            if (usedPort == -1) {
                throw new IOException("Aucun port disponible trouvé");
            }
            System.out.println("Port utilisé : " + usedPort);
            
            // Créer le serveur qui écoute sur toutes les interfaces
            InetSocketAddress address = new InetSocketAddress(usedPort);
            server = HttpServer.create(address, 0);
            
            // Gestionnaire pour les PDFs
            server.createContext("/billets/", exchange -> {
                try {
                    String requestPath = exchange.getRequestURI().getPath();
                    String fileName = requestPath.substring("/billets/".length());
                    File pdfFile = new File(PDF_DIR + fileName);
                    
                    System.out.println("Requête reçue pour : " + fileName);
                    System.out.println("Chemin complet : " + pdfFile.getAbsolutePath());
                    System.out.println("Le fichier existe : " + pdfFile.exists());

                    if (pdfFile.exists()) {
                        // Ajouter les en-têtes HTTP appropriés
                        exchange.getResponseHeaders().set("Content-Type", "application/pdf");
                        exchange.getResponseHeaders().set("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
                        exchange.getResponseHeaders().set("Pragma", "no-cache");
                        exchange.getResponseHeaders().set("Expires", "0");
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        
                        // Envoyer le fichier
                        long fileLength = pdfFile.length();
                        System.out.println("Taille du fichier : " + fileLength + " octets");
                        exchange.sendResponseHeaders(200, fileLength);
                        
                        try (FileInputStream fis = new FileInputStream(pdfFile);
                             OutputStream os = exchange.getResponseBody()) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            long totalBytesRead = 0;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                                os.flush();
                                totalBytesRead += bytesRead;
                            }
                            System.out.println("Fichier envoyé avec succès : " + totalBytesRead + " octets");
                        }
                    } else {
                        System.out.println("Fichier non trouvé : " + fileName);
                        String errorHtml = "<html><body><h1>PDF non trouvé</h1><p>Le billet demandé n'existe pas ou a été supprimé.</p></body></html>";
                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(404, errorHtml.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(errorHtml.getBytes("UTF-8"));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement de la requête : " + e.getMessage());
                    e.printStackTrace();
                    try {
                        String errorHtml = "<html><body><h1>Erreur serveur</h1><p>Une erreur est survenue lors du traitement de la requête.</p></body></html>";
                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(500, errorHtml.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(errorHtml.getBytes("UTF-8"));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Gestionnaire pour la racine
            server.createContext("/", exchange -> {
                try {
                    String welcomeHtml = "<html><body><h1>Serveur de Billets</h1><p>Ce serveur est utilisé pour servir les billets PDF.</p></body></html>";
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, welcomeHtml.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(welcomeHtml.getBytes("UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("Serveur web démarré sur http://" + localIpAddress + ":" + usedPort);
            System.out.println("Pour accéder aux billets, utilisez : http://" + localIpAddress + ":" + usedPort + "/billets/");
            
            // Vérifier que le serveur est accessible
            try {
                InetAddress.getByName(localIpAddress);
                System.out.println("Le serveur est accessible sur l'adresse IP : " + localIpAddress);
            } catch (Exception e) {
                System.err.println("Attention : Le serveur pourrait ne pas être accessible sur l'adresse IP : " + localIpAddress);
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur web: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int findAvailablePort() {
        for (int port : PORTS) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                System.out.println("Port " + port + " est déjà utilisé, essai du suivant...");
            }
        }
        return -1;
    }

    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Ignorer les interfaces de loopback, non actives et VirtualBox
                if (iface.isLoopback() || !iface.isUp() || 
                    iface.getDisplayName().contains("VirtualBox") || 
                    iface.getDisplayName().contains("VMware")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Prendre la première adresse IPv4 non loopback
                    if (addr.getHostAddress().indexOf(':') == -1) {
                        String ip = addr.getHostAddress();
                        System.out.println("Interface réseau trouvée : " + iface.getDisplayName() + " - IP : " + ip);
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getWebUrl() {
        return "http://" + localIpAddress + ":" + usedPort + "/billets/";
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Serveur web arrêté");
        }
    }
} 