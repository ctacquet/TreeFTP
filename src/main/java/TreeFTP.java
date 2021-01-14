import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class TreeFTP {
    private Socket socket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    public synchronized void connect(String host) throws IOException {
        connect(host, 21, "anonymous", "anonymous");
    }

    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

    public synchronized void connect(String host, int port, String user, String pass) throws IOException {
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
    public synchronized boolean cwd(String dir) throws IOException {
        sendLine("CWD " + dir);
        String response = readLine();
        //Code 250 - Service fichier terminé.
        return (response.startsWith("250 "));
    }

    /**
     * Liste des fichiers du dossier actuel
     * @return Réponse
     * @throws IOException
     */
    public synchronized String list() throws IOException {
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

        Socket dataSocket = new Socket(ip, port);

        sendLine("LIST");
        réponse = readLine();
        if (réponse.startsWith("425 ")) throw new IOException("Use PORT or PASV before LIST");

        DataInputStream input = new DataInputStream(dataSocket.getInputStream());
        String files = new String(), nextLine = input.readLine();
        while(nextLine != null){
            files = (new StringBuilder()).append(files).append(nextLine).append("\n").toString();
            nextLine = input.readLine();
        }
        input.close();
        return files;
    }

    /**
     * Liste des fichiers du dossier dir
     * @param dir Dossier destinataire
     * @return Réponse
     * @throws IOException
     */
    public synchronized String list(String dir) throws IOException {
        sendLine("LIST " + dir);
        String response = readLine();
        return response;
    }

    public static void main(String[] args) throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        System.out.println("TreeFTP est connecté à l'adresse [" + address + "]");
        if(treeFTP.cwd("ubuntu")){
            System.out.println("Vérification du cd dans /ubuntu : pwd = " + treeFTP.pwd());
            treeFTP.cwd("..");
        }
        System.out.println("LIST de " + treeFTP.pwd() + " :\n" + treeFTP.list());
        treeFTP.disconnect();
    }
}
