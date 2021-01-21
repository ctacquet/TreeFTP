import java.util.StringTokenizer;

public class Fichier {
    private String nom;
    private String droits;

    public Fichier(){
        this.nom = "unknown";
    }

    public Fichier(String nom){
        this.nom = nom;
    }

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

    public String getNom() {
        return this.nom;
    }

    @Override
    public String toString() {
        return this.nom;
    }
}
