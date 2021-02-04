import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Classe qui représente notre serveur FTP
 */
public class TreeFTP {
    /**
     * Variable pour debug toutes les commandes réalisées dans la console avant d'avoir la réponse
     */
    private static final boolean DEBUG = false;
    /**
     * Socket Java qui va se connecter au FTP et va créer un reader et un writer
     */
    private Socket socket = null;
    /**
     * Reader pour la lecture des infos dans le socket
     */
    private BufferedReader reader = null;
    /**
     * Writer pour écrire des infos/envoyer des commandes dans le socket
     */
    private BufferedWriter writer = null;
    /**
     * Socket Java représentant le flux des données (fichiers, dossiers et raccourcis)
     */
    private Socket dataSocket = null;
    /**
     * Variable entière qui permet d'attribuer la "hauteur" des dossiers
     */
    public static int numeroDossier = 0;



    /**
     * Méthode pour se connecter au serveur FTP avec seulement l'IP
     * @param host Hôte du serveur FTP / IP du serveur
     * @throws IOException Chaine de caractères de la réponse
     */
    public synchronized void connect(String host) throws IOException {
        connect(host, 21, "anonymous", "anonymous");
    }

    /**
     * Méthode pour se connecter au serveur FTP sans utilisateur ni mot de passe
     * @param host Hôte du serveur FTP / IP du serveur
     * @param port Port du serveur FTP
     * @throws IOException Chaine de caractères de la réponse
     */
    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

    /**
     * Méthode pour se connecter au serveur FTP
     * Elle sera toujours appelée pour se connecter
     * @param host Hôte du serveur FTP / IP du serveur
     * @param port Port du serveur FTP
     * @param user Utilisateur qui va se connecter
     * @param pass Mot de passe de l'utilisateur
     * @throws IOException Chaine de caractères de la réponse
     */
    public synchronized void connect(String host, int port, String user, String pass) throws IOException {
        if(DEBUG){
            if(pass != "anonymous"){
                String passStar = "";
                for (int i=0; i<pass.length(); i++) passStar += "*";
                System.out.println("CONNECT to"+host+":"+port+" with USER="+user+" and PASS="+passStar);
            } else System.out.println("CONNECT to"+host+":"+port+" with USER="+user+" and PASS="+pass);
        }

        if (socket != null) {
            throw new IOException("TreeFTP est déjà connecté à un FTP");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String reponse = readLine();
        //Code 220 - Service disponible pour nouvel utilisateur.
        if (!reponse.startsWith("220 ")) throw new IOException("FTP pas disponible. Erreur = " + reponse);

        sendLine("USER " + user);
        reponse = readLine();
        //Code 331 - Nom d'utilisateur reçu, mot de passe demandé.
        if (!reponse.startsWith("331 ")) throw new IOException("Problème saisie nom d'utilisateur. Erreur =  " + reponse);

        sendLine("PASS " + pass);
        reponse = readLine();
        if (!reponse.startsWith("230 ")) throw new IOException("Problème saisie mot de passe. Erreur = " + reponse);
    }

    /**
     * Méthode pour se déconnecter du serveur FTP
     * @throws IOException Chaine de caractères de la réponse
     */
    public synchronized void disconnect() throws IOException {
        try {
            if(DEBUG) System.out.println("QUIT");

            sendLine("QUIT");
        } finally {
            socket.close();
        }
    }

    /**
     * Méthode qui permet d'envoyer des commandes au serveur FTP
     * @param line Commande à envoyer
     * @throws IOException Explication de la mauvaise réponse
     */
    private void sendLine(String line) throws IOException {
        if (socket == null) {
            throw new IOException("TreeFTP n'est pas connecté au FTP");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
        } catch (IOException e) {
            socket = null;
            throw e;
        }
    }

    /**
     * Méthode qui va envoyer à notre lecteur de String les lignes qu'on le reçoit du serveur FTP
     * @return Chaine de caractères de la réponse
     * @throws IOException Explication de la mauvaise réponse
     */
    private String readLine() throws IOException {
        String line = reader.readLine();
        return line;
    }

    /**
     * Méthode qui exécute la commande pwd sur Linux
     * @return Chaine de caractères qui nous donne le dossier actuel
     * @throws IOException Explication de la mauvaise réponse
     */
    public synchronized String pwd() throws IOException {
        if(DEBUG) System.out.println("PWD");

        String dir = null;
        sendLine("PWD");
        String reponse = readLine();
        //Code 220 - "CHEMIN" créé.
        if (reponse.startsWith("257 ")) {
            int firstQuote = reponse.indexOf('\"');
            int secondQuote = reponse.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = reponse.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

    /**
     * Commande correspondante à un cd en Linux
     * @param dir Dossier destinataire
     * @throws IOException Explication de la mauvaise réponse
     */
    public synchronized void cwd(String dir) throws IOException {
        if(DEBUG) System.out.println("CWD " + dir);

        sendLine("CWD " + dir);
        String reponse = readLine();
        //Code 250 - Service fichier terminé.
        if(!reponse.startsWith("250 ")) throw new IOException("CWD error : " + reponse);
    }

    /**
     * Méthode qui permet de passer en mode passif (PASV)
     * Elle crée si tout se passe bien le datasocket qui représente notre second flux.
     * @throws IOException Explication de la mauvaise réponse
     */
    private synchronized void passageModePassif() throws IOException {
        if(DEBUG) System.out.println("PASV");

        sendLine("PASV");
        String reponse = readLine();
        if (!reponse.startsWith("227 ")) {
            throw new IOException("TreeFTP ne peut pas passer en mode passif: " + reponse);
        }

        String ip = null;
        int port = -1;
        int opening = reponse.indexOf('(');
        int closing = reponse.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = reponse.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("TreeFTP n'a pas réussi à connecter aux données: " + reponse);
            }
        }

        this.dataSocket = new Socket(ip, port);
    }

    /**
     * Méthode qui liste les fichiers du dossier d
     * @param d Dossier à scanner
     * @throws IOException Explication de la mauvaise réponse
     */
    public synchronized void list(Dossier d) throws IOException {
        passageModePassif();

        if(DEBUG) System.out.println("LIST " + d.getNom());

        sendLine("LIST");
        String reponse = readLine();
        if (reponse.startsWith("425 ")) throw new IOException("Utiliser PORT or PASV avant la commande LIST: " + reponse);
        // Code 150 - Here comes the directory listing.
        if (!reponse.startsWith("150 ")) throw new IOException("Le listing ne peut pas être fait: " + reponse);

        BufferedReader input = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        String nextLine = input.readLine();
        while(nextLine != null){
            switch(getType(nextLine)){
                case 1:
                    //if(DEBUG) System.out.println("\tFound a file : " + nextLine);
                    Fichier fichier = new Fichier();
                    fichier.getInformations(nextLine);
                    d.addFichier(fichier);
                    break;
                case 2:
                    //if(DEBUG) System.out.println("\tFound a directory : " + nextLine);
                    Dossier dossier = new Dossier();
                    dossier.getInformations(nextLine);
                    d.addDossier(dossier);
                    break;
                case 3:
                    //if(DEBUG) System.out.println("\tFound a directory link : " + nextLine);
                    Raccourci raccourci = new Raccourci();
                    raccourci.getInformations(nextLine);
                    d.addRaccourci(raccourci);
                    break;
                default:
                    throw new IOException("Type de fichier inconnu : " + nextLine);
            }
            nextLine = input.readLine();
        }
        input.close();
        reponse = readLine();
        //Code 226 - Directory send OK.
        if(!reponse.startsWith("226 ")) throw new IOException("LIST error : " + reponse);
    }

    /**
     * Méthode qui va lister tous les fichiers juste après le premier appel à la méthode list
     * Elle va appeler récursivement la méthode list sur tous les dossiers présent dans dossier
     * @param dossier Dossier qui va subir listAll
     * @return Oui ou non si la méthode à lister quoique soit ou non
     * @throws IOException Explication de la mauvaise réponse
     */
    public synchronized boolean listAll(Dossier dossier) throws IOException {
        if(!dossier.dossiers.isEmpty()) {
            for(Dossier tmpDossier : dossier.dossiers){
                cwd(tmpDossier.nom);
                tmpDossier.numeroDansHierarchie = dossier.numeroDansHierarchie + 1;
                list(tmpDossier);
                if(!tmpDossier.dossiers.isEmpty()){
                    listAll(tmpDossier);
                }
                cwd("..");
            }
            return true;
        }
        return false;
    }

    /**
     * Méthode qui va différencier quel genre de fichiers nous avons récupérer dans infos
     * @param infos Réponse récupéré du 2ème flux contenant tous les fichiers etc
     * @return 1 si c'est un fichier, 2 si c'est un dossier et 3 si c'est un raccourci
     */
    private int getType(String infos){
        if(infos.startsWith("-")) return 1;
        if(infos.startsWith("d")) return 2;
        if(infos.startsWith("l")) return 3;
        return 0;
    }

    /**
     * Méthode main pour éxecuter un test global
     * @param args Argument en entrée (ip du serveur FTP)
     * @throws IOException Explication de la mauvaise réponse
     */
    public static void main(String[] args) throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        String dossierDeCommencement = "/maas-images/ephemeral";
        treeFTP.cwd(dossierDeCommencement);
        Dossier source = new Dossier(dossierDeCommencement, treeFTP.numeroDossier);
        treeFTP.list(source);
        treeFTP.listAll(source);
        System.out.println(source);
        treeFTP.disconnect();
    }
}
