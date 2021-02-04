import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe qui représente les dossiers
 */
public class Dossier {
    /**
     * Nom du dossier
     */
    String nom;
    int numeroDansHierarchie;
    /**
     * Liste des fichiers du dossier
     */
    List<Fichier> fichiers;
    /**
     * Liste des dossiers du dossier
     */
    List<Dossier> dossiers;
    /**
     * Liste des raccourcis du dossier
     */
    List<Raccourci> raccourcis;

    /**
     * Constructeur de base qui crée un dossier unknown
     */
    public Dossier(){
        super();
        this.nom = "unknown";
        this.numeroDansHierarchie = -1;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    /**
     * Constructeur qui crée un dossier nom
     * Il manque ici le numero dans la hierarchie
     * @param nom Nom du dossier
     */
    public Dossier(String nom){
        super();
        this.nom = nom;
        this.numeroDansHierarchie = -1;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    /**
     * Constructeur qui crée un dossier nom
     * @param nom Nom du dossier
     * @param numeroDansHierarchie Position du dossier dans la hiérarchie
     */
    public Dossier(String nom, int numeroDansHierarchie){
        super();
        this.nom = nom;
        this.numeroDansHierarchie = numeroDansHierarchie;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    /**
     * Méthode pour ajouter un fichier
     * @param fichier Fichier à ajouter
     */
    public void addFichier(Fichier fichier){
        if(!contientDejaFichier(fichier)) this.fichiers.add(fichier);
    }

    /**
     * Méthode pour ajouter des fichiers (liste de fichiers)
     * @param fichiers Liste des fichiers à ajouter
     */
    public void addFichiers(List<Fichier> fichiers){
        for(Fichier fichier : fichiers) if(!contientDejaFichier(fichier)) fichiers.add(fichier);
    }

    /**
     * Méthode qui vérifie si le fichier n'est pas déjà présent dans le dossier
     * @param fichier Fichier à chercher dans le dossier
     * @return Vrai ou faux selon si il est déjà présent ou pas
     */
    private boolean contientDejaFichier(Fichier fichier) {
        for(Fichier tmpFichier : fichiers){
            if(tmpFichier.getNom() == fichier.getNom()) return true;
        }
        return false;
    }

    /**
     * Méthode pour ajouter un dossier
     * @param dossier Dossier à ajouter
     */
    public void addDossier(Dossier dossier){
        if(!contientDejaDossier(dossier)) this.dossiers.add(dossier);
    }

    /**
     * Méthode qui vérifie si le dossier n'est pas déjà présent dans le dossier
     * @param dossier Dossier à chercher dans le dossier
     * @return Vrai ou faux selon si il est déjà présent ou pas
     */
    private boolean contientDejaDossier(Dossier dossier) {
        for(Dossier tmpDossier : dossiers){
            if(tmpDossier.getNom() == dossier.getNom()) return true;
        }
        return false;
    }

    /**
     * Méthode pour ajouter un raccourci
     * @param raccourci Raccourci à ajouter
     */
    public void addRaccourci(Raccourci raccourci) {
        if(!contientDejaRaccourci(raccourci)) this.raccourcis.add(raccourci);
    }

    /**
     * Méthode qui vérifie si le raccourci n'est pas déjà présent dans le dossier
     * @param raccourci Raccourci à chercher dans le dossier
     * @return Vrai ou faux selon si il est déjà présent ou pas
     */
    private boolean contientDejaRaccourci(Raccourci raccourci) {
        for(Raccourci tmpRaccourci : raccourcis){
            if(tmpRaccourci.getNom() == raccourci.getNom()) return true;
        }
        return false;
    }

    /**
     * Méthode qui récupère les informations du dossier depuis infos
     * @param infos Chaine de caractères contenant les infos d'un fichier
     */
    public void getInformations(String infos){
        String delim = " ", nomDossier = "";
        StringTokenizer st = new StringTokenizer(infos,delim);
        int j = 0;
        while (st.hasMoreTokens()) {
            if(j >= 8) {
                nomDossier += st.nextToken();
            } else st.nextToken();
            j++;
        }
        this.nom = nomDossier;
    }

    /**
     * Méthode qui donne le nom du dossier
     * @return Chaine de caractères correspondant au nom du dossier
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Override de la méthode toString pour afficher tout ce que contient un dossier
     * Dans l'ordre nous affichons les fichiers, les raccourcis et les dossiers
     * Elle fait également appel à elle même pour tous les dossiers présent dans le dossier actuel
     *
     * /!\ Actuellement il y a des barres qui sont en trop dans l'affichage
     * J'ai fait plusieurs essais pour cacher ce problème mais je n'arrivais pas à les enlever au bon endroit
     * @return Nom du fichier
     */
    @Override
    public String toString() {
        String res = "", tab = "";
        int nFichiers=0, nRaccourcis=0, nDossiers=0;
        if(this.numeroDansHierarchie == -1) return this.nom + " n'a pas de num";
        if(this.numeroDansHierarchie == 0) res += ".\n";
        else {
            for(int i = 0; i<numeroDansHierarchie; i++){
                if(fichiers.size() + raccourcis.size() + dossiers.size() > 0) tab += "│  ";
                else tab += "   ";
            }
            res += this.nom + "\n";
        }
        for(nFichiers = 0; nFichiers < fichiers.size(); nFichiers++){
            if(nFichiers < fichiers.size()-1) res += tab + "├── " + fichiers.get(nFichiers).toString() + "\n";
            else res += tab + "└── " + fichiers.get(nFichiers).toString() + "\n";
        }
        for(nRaccourcis = 0; nRaccourcis < raccourcis.size(); nRaccourcis++){
            if(nRaccourcis < raccourcis.size()-1) res += tab + "├── " + raccourcis.get(nRaccourcis).toString() + "\n";
            else res += tab + "└── " + raccourcis.get(nRaccourcis).toString() + "\n";
        }
        for(nDossiers = 0; nDossiers < dossiers.size(); nDossiers++){
            if(nDossiers < dossiers.size()-1) res += tab + "├── " + dossiers.get(nDossiers).toString();
            else res += tab + "└── " + dossiers.get(nDossiers).toString();
        }
        return res;
    }
}
