import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.*;

/**
 * Classe qui nous permet de réaliser tous les tests JUnit
 */
public class Tests {
    /**
     * Permet de vérifier que la connexion à un serveur FTP fonctionne
     * @throws IOException Explication de la mauvaise réponse
     */
    @Test
    public void verifConnectionExistante() throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        treeFTP.disconnect();
        assert(true);
    }

    /**
     * Permet de vérifier que si l'on se connecte à un IP qui n'est pas correct on récupère bien une erreur
     * @throws IOException Explication de la mauvaise réponse
     */
    @Test(expected=IOException.class)
    public void verifConnectionInexistante() throws IOException {
        String address = "IP_INEXISTANTE";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
    }

    /**
     * Permet de vérifier que les commandes cd et pwd fonctionne
     * @throws IOException Explication de la mauvaise réponse
     */
    @Test
    public void verifCwd_Pwd() throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        String dossierDeCommencement = "/maas-images/ephemeral";
        treeFTP.cwd(dossierDeCommencement);
        assertEquals(dossierDeCommencement, treeFTP.pwd());
        treeFTP.disconnect();
    }

    /**
     * Vérification que la commande list fonctionne
     * On test sur un dossier qui comporte 2 dossiers
     * @throws IOException Explication de la mauvaise réponse
     */
    @Test
    public void verifList() throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        String dossierDeCommencement = "/maas-images/ephemeral";
        Dossier source = new Dossier(dossierDeCommencement, treeFTP.numeroDossier);
        treeFTP.list(source);
        treeFTP.disconnect();
        assertTrue(source.dossiers.size() > 0);
    }

    /**
     * Vérification que la commande listAll fonctionne
     * On ne vérifie pas le contenu mais on vérifie qu'elle ne renvoie pas d'exception
     * @throws IOException Explication de la mauvaise réponse
     */
    @Test
    public void verifListAll() throws IOException {
        String address = "ftp.ubuntu.com";
        TreeFTP treeFTP = new TreeFTP();
        treeFTP.connect(address);
        String dossierDeCommencement = "/maas-images/ephemeral";
        Dossier source = new Dossier(dossierDeCommencement, treeFTP.numeroDossier);
        treeFTP.list(source);
        treeFTP.listAll(source);
        treeFTP.disconnect();
        assert(true);
    }
}
