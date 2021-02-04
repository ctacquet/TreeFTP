import java.util.StringTokenizer;

/**
 * Classe qui représente les raccourcis
 */
public class Raccourci {
    /**
     * Nom du raccourci
     */
    private String nom;

    /**
     * Constructeur qui crée un raccourci unknown
     */
    public Raccourci(){
        this.nom = "unknown";
    }

    /**
     * Constructeur qui crée un raccourci nom
     * @param nom Nom du raccourci
     */
    public Raccourci(String nom){
        this.nom = nom;
    }

    /**
     * Méthode qui récupère les informations du raccourci depuis infos
     * @param infos Chaine de caractères contenant les infos d'un fichier
     */
    public void getInformations(String infos){
        String delim = " ", nomRaccourci = "";
        StringTokenizer st = new StringTokenizer(infos,delim);
        int j = 0;
        while (st.hasMoreTokens()) {
            switch(j) {
                case 8:
                    nomRaccourci += st.nextToken();
                    break;
                case 10:
                    nomRaccourci += " (-> " + st.nextToken() + ")";
                    break;
                default:
                    st.nextToken();
                    break;
            }
            j++;
        }
        this.nom = nomRaccourci;
    }

    /**
     * Méthode qui donne le nom du raccourci
     * @return Chaine de caractères correspondant au nom du raccourci
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Override de la méthode toString pour n'afficher que le nom
     * @return Nom du raccourci
     */
    @Override
    public String toString() {
        return this.nom;
    }
}
