import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class TreeFTP {
    private static final boolean DEBUG = true;
    private Socket socket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private Socket dataSocket = null;
    private static int numeroDossier = 0;

    public synchronized void connect(String host) throws IOException {
        connect(host, 21, "anonymous", "anonymous");
    }

    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

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

        String réponse = readLine();
        //Code 220 - Service disponible pour nouvel utilisateur.
        if (!réponse.startsWith("220 ")) throw new IOException("FTP pas disponible. Erreur = " + réponse);

        sendLine("USER " + user);
        réponse = readLine();
        //Code 331 - Nom d'utilisateur reçu, mot de passe demandé.
        if (!réponse.startsWith("331 ")) throw new IOException("Problème saisie nom d'utilisateur. Erreur =  " + réponse);

        sendLine("PASS " + pass);
        réponse = readLine();
        if (!réponse.startsWith("230 ")) throw new IOException("Problème saisie mot de passe. Erreur = " + réponse);
    }

    public synchronized void disconnect() throws IOException {
        try {
            if(DEBUG) System.out.println("QUIT");

            sendLine("QUIT");
        } finally {
            socket.close();
        }
    }

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

    private String readLine() throws IOException {
        String line = reader.readLine();
        return line;
    }

    public synchronized String pwd() throws IOException {
        if(DEBUG) System.out.println("PWD");

        String dir = null;
        sendLine("PWD");
        String réponse = readLine();
        //Code 220 - "CHEMIN" créé.
        if (réponse.startsWith("257 ")) {
            int firstQuote = réponse.indexOf('\"');
            int secondQuote = réponse.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = réponse.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

    /**
     * Commande correspondante à un cd en Linux
     * @param dir Dossier destinataire
     * @return Vrai ou faux pour savoir si on est bien dans le dossier dir
     * @throws IOException
     */
    public synchronized void cwd(String dir) throws IOException {
        if(DEBUG) System.out.println("CWD " + dir);

        sendLine("CWD " + dir);
        String réponse = readLine();
        //Code 250 - Service fichier terminé.
        if(!réponse.startsWith("250 ")) throw new IOException("CWD error : " + réponse);
    }

    private synchronized void passageModePassif() throws IOException {
        if(DEBUG) System.out.println("PASV");

        sendLine("PASV");
        String réponse = readLine();
        if (!réponse.startsWith("227 ")) {
            throw new IOException("TreeFTP ne peut pas passer en mode passif: " + réponse);
        }

        String ip = null;
        int port = -1;
        int opening = réponse.indexOf('(');
        int closing = réponse.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = réponse.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("TreeFTP n'a pas réussi à connecter aux données: " + réponse);
            }
        }

        this.dataSocket = new Socket(ip, port);
    }

    /**
     * Liste des fichiers du dossier d
     * @param d Dossier à scanner
     * @throws IOException
     */
    public synchronized void list(Dossier d) throws IOException {
        passageModePassif();

        if(DEBUG) System.out.println("LIST " + d.getNom());

        sendLine("LIST");
        String réponse = readLine();
        if (réponse.startsWith("425 ")) throw new IOException("Utiliser PORT or PASV avant la commande LIST: " + réponse);
        // Code 150 - Here comes the directory listing.
        if (!réponse.startsWith("150 ")) throw new IOException("Le listing ne peut pas être fait: " + réponse);

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
        réponse = readLine();
        //Code 226 - Directory send OK.
        if(!réponse.startsWith("226 ")) throw new IOException("LIST error : " + réponse);
    }

    private synchronized boolean listAll(Dossier dossier) throws IOException {
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

    private int getType(String infos){
        if(infos.startsWith("-")) return 1;
        if(infos.startsWith("d")) return 2;
        if(infos.startsWith("l")) return 3;
        return 0;
    }

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
