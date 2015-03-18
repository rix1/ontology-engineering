import java.util.StringTokenizer;

/**
 * Created by Rikard Eide on 18/03/15.
 * Description:
 */
public class Constraint {

    private String text1;
    private String text2;

    private int pos1;
    private int pos2;


    // Just set default values just in case
    public Constraint(){
        text1 = "";
        text2 = "";

        pos1 = 0;
        pos2 = 0;

    }

    public String createVerbalization(String first, String second){

//        System.out.println("Creating verbalization... first: " + first + ", second: " + second);
//        System.out.println("Matching with... text1: " + text1+ ", text2: " + text2);

        String verbalization = "";

        if(pos1 >= 1){
            verbalization += first + " " + text1;
        }else verbalization += text1 + " " + first;

        verbalization += " ";

        if(pos2 >= 1){
            verbalization += text2 + " " + second;
        }else verbalization += second + " " + text2;

//        System.out.println("Verbalization complete: " + verbalization);
        return verbalization;
    }

/*  TODO: Format string
    public stringFormat(String verbalization){
        StringTokenizer tk = new StringTokenizer(verbalization);
        verbalization = tk.nextToken("/");
        verbalization = " " + verbalization + " ";
        verbalization += labelFor(clazz);
    }
*/


    @Override
    public String toString() {
        return "Constraint{" +
                "text1='" + text1 + '\'' +
                ", text2='" + text2 + '\'' +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                '}';
    }

    public void setText1(String text1) {
//        System.out.println("Text 1 set: " + text1);
        this.text1 = text1;
    }

    public void setText2(String text2) {
//        System.out.println("Text 2 set: " + text2);
        this.text2 = text2;

    }

    public void setPos1(int pos1) {
//        System.out.println("Pos 1 set: " + pos1);
        this.pos1 = pos1;
    }

    public void setPos2(int pos2) {
//        System.out.println("Pos 2 set: " + pos2);
        this.pos2 = pos2;
    }
}
