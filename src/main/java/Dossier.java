import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Dossier {
    String nom;
    int numeroDansHierarchie;
    List<Fichier> fichiers;
    List<Dossier> dossiers;
    List<Raccourci> raccourcis;

    public Dossier(){
        super();
        this.nom = "unknown";
        this.numeroDansHierarchie = -1;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    public Dossier(String nom){
        super();
        this.nom = nom;
        this.numeroDansHierarchie = -1;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    public Dossier(String nom, int numeroDansHierarchie){
        super();
        this.nom = nom;
        this.numeroDansHierarchie = numeroDansHierarchie;
        this.fichiers = new ArrayList<>();
        this.dossiers = new ArrayList<>();
        this.raccourcis = new ArrayList<>();
    }

    public void addFichier(Fichier fichier){
        if(!contientDejaFichier(fichier)) this.fichiers.add(fichier);
    }

    public void addFichiers(List<Fichier> fichiers){
        for(Fichier fichier : fichiers) if(!contientDejaFichier(fichier)) fichiers.add(fichier);
    }

    private boolean contientDejaFichier(Fichier fichier) {
        for(Fichier tmpFichier : fichiers){
            if(tmpFichier.getNom() == fichier.getNom()) return true;
        }
        return false;
    }

    public void addDossier(Dossier dossier){
        if(!contientDejaDossier(dossier)) this.dossiers.add(dossier);
    }

    private boolean contientDejaDossier(Dossier dossier) {
        for(Dossier tmpDossier : dossiers){
            if(tmpDossier.getNom() == dossier.getNom()) return true;
        }
        return false;
    }

    public void addRaccourci(Raccourci raccourci) {
        if(!contientDejaRaccourci(raccourci)) this.raccourcis.add(raccourci);
    }

    private boolean contientDejaRaccourci(Raccourci raccourci) {
        for(Raccourci tmpRaccourci : raccourcis){
            if(tmpRaccourci.getNom() == raccourci.getNom()) return true;
        }
        return false;
    }

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

    public String getNom() {
        return this.nom;
    }

    @Override
    public String toString() {
        String res = "", tab = "";
        if(this.numeroDansHierarchie == -1) return this.nom + " n'a pas de num";
        if(this.numeroDansHierarchie == 0) res += ".\n";
        else {
            for(int i = 0; i<numeroDansHierarchie; i++) tab += "-";
            res += tab + this.nom + "\n";
        }
        for(Fichier fichier : fichiers){
            res += tab + "- " + fichier.toString() + "\n";
        }
        for(Raccourci raccourci : raccourcis){
            res += tab + "-> " + raccourci.toString() + "\n";
        }
        for(Dossier dossier : dossiers){
            res += dossier.toString() + "\n";
        }
        return res;
    }
}
