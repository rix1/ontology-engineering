/**
 * Created by Rikard Eide on 18/03/15.
 * Description:
 */
public class Constraint {

    public static final int PARENT = 0;
    public static final int CHILD = 1;

    private String xmlText1;
    private String xmlText2;
    private String xmlText3;

    private int pos1;
    private int pos2;

    // Lets avoid nullpointers
    public Constraint() {
        this.xmlText1 = "";
        this.xmlText2 = "";
        this.xmlText3 = "";
        this.pos1 = 0;
        this.pos2 = 0;
    }

    public String createVerbalization(String child, String parent){

        String verbalization = "";

        if(pos1 == CHILD){
            verbalization += xmlText1 + " " + parent + " ";
        }else verbalization += xmlText1 + " " + child + " ";

        // Are these always disjoint? In case not, we got a second if:

        if(pos2 == CHILD){
            verbalization += xmlText2 + " " + parent;
        }else verbalization += xmlText2 + " " + child;

        verbalization += xmlText3;

        return verbalization;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "xmlText1='" + xmlText1 + '\'' +
                ", xmlText2='" + xmlText2 + '\'' +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                '}';
    }

    public void setXmlText1(String xmlText1) {
        this.xmlText1 = xmlText1;
    }

    public void setXmlText2(String xmlText2) {
        this.xmlText2 = xmlText2;
    }

    public void setXmlText3(String xmlText3) {
        this.xmlText2 = xmlText3;
    }

    public void setPos1(int pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(int pos2) {
        this.pos2 = pos2;
    }
}
