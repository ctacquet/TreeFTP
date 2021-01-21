import java.util.StringTokenizer;

public class Raccourci {
    private String nom;

    public Raccourci(){
        this.nom = "unknown";
    }

    public Raccourci(String nom){
        this.nom = nom;
    }

    public void getInformations(String infos){
        String delim = " ", nomRaccourci = "";
        StringTokenizer st = new StringTokenizer(infos,delim);
        int j = 0;
        while (st.hasMoreTokens()) {
            if(j == 8) {
                nomRaccourci += st.nextToken();
            } else st.nextToken();
            j++;
        }
        this.nom = nomRaccourci;
    }

    public String getNom() {
        return this.nom;
    }

    @Override
    public String toString() {
        return this.nom;
    }
}
