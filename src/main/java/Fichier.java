import java.util.StringTokenizer;

/**
 * Classe qui représente les fichiers
 */
public class Fichier {
    /**
     * Nom du fichier
     */
    private String nom;

    /**
     * Constructeur qui crée un fichier unknown
     */
    public Fichier(){
        this.nom = "unknown";
    }

    /**
     * Constructeur qui crée un fichier nom
     * @param nom Nom du fichier
     */
    public Fichier(String nom){
        this.nom = nom;
    }

    /**
     * Méthode qui récupère les informations du fichier depuis infos
     * @param infos Chaine de caractères contenant les infos d'un fichier
     */
    public void getInformations(String infos){
        String delim = " ", nomFichier = "";
        StringTokenizer st = new StringTokenizer(infos,delim);
        int j = 0;
        while (st.hasMoreTokens()) {
            if(j >= 8) {
                nomFichier += st.nextToken();
            } else st.nextToken();
            j++;
        }
        this.nom = nomFichier;
    }

    /**
     * Méthode qui donne le nom du fichier
     * @return Chaine de caractères correspondant au nom du fichier
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Override de la méthode toString pour n'afficher que le nom
     * @return Nom du fichier
     */
    @Override
    public String toString() {
        return this.nom;
    }
}
