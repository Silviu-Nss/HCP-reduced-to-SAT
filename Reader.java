import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Nastasescu George-Silviu, 321CB
 */
public class Reader
{
    private BufferedReader br;
  
    /**
     *
     * @param input = fisierul de input
     */
    public Reader(String input) {
        try {
            br = new BufferedReader(new FileReader(new File(input)));
        }
        catch (FileNotFoundException ex) {
            System.err.println("Nu se poate deschide fisierul");
            System.exit(1);
        }
    }
  
    /**
     *
     * @return = matricea ce contine listele de adiacenta
     */
    public ArrayList<ArrayList<Integer>> read() {
        ArrayList<ArrayList<Integer>>  matrix = new ArrayList<>();
        Integer nr_nod = null, nod1, nod2;
        String str = null;
        String[] noduri;
        try {
                nr_nod = Integer.parseInt(br.readLine());  // numarul de noduri
            }
            catch (IOException ex) {
                System.err.println("Nu se poate citi din buffer");
                System.exit(1);
            }
        for(int i = 0; i <= nr_nod; i++){
            ArrayList<Integer> nod = new ArrayList<>();
            matrix.add(nod);
        }
        for (;;) {
            try {
                str = br.readLine();
            } 
            catch (IOException ex) {
                System.err.println("Nu se poate citi din buffer");
                System.exit(1);
            }
            if (str == null) {
                break;
            }
            noduri = (str.split(" "));
            nod1 = Integer.parseInt(noduri[0]);       // primul nod al muchiei
            if(nod1 == -1)                   // am ajuns la capatul fisierului
                break;
            nod2 = Integer.parseInt(noduri[1]);    // al doilea nod al muchiei
            matrix.get(nod1).add(nod2);
            matrix.get(nod2).add(nod1);
        }
        
        try {
            br.close();
        } 
        catch (IOException ex) {
            System.err.println("Nu se poate inchide buffer-ul");
            System.exit(1);
        }
        return matrix;
    }
}
