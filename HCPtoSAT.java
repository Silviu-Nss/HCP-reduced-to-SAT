import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Nastasescu George-Silviu, 321CB
 */

public class HCPtoSAT {

    public static void main(String[] args) {

        Reader Rd = new Reader(args[0]);
        ArrayList<ArrayList<Integer>>  matrix = Rd.read();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File("bexpr.out")));
        }
        catch (IOException ex) {
            System.err.println("Nu se poate deschide bufferul");
            System.exit(1);
        }
        if(!gradMinimDoi(matrix)) {
            // Exista un nod cu gradul de incidenta mai mic decat 2
            // Asa ca scriem o expresie simpla si nesatisfiabila
            write("x1-2&~x1-2", bw);
            close(bw);
            return;
        }
            
        sortMatrix(matrix);
        doPartOne(matrix, bw);
        doPartTwo(matrix, bw);
        close(bw);
    }

    public static boolean gradMinimDoi(ArrayList<ArrayList<Integer>>  matrix) {
        for(int i = 1; i < matrix.size(); i++)
            if(matrix.get(i).size() < 2)
                return false;
        return true;
    }
    
    
    public static void doPartOne(ArrayList<ArrayList<Integer>>  matrix, BufferedWriter bw) {
        Integer dists = (matrix.size() - 1) / 2 + 1;
        for(int i = 1; i < matrix.size(); i++) {
            String conj = new String();    // conjunctie ce contine doar xi-j
            String disj = new String();    // disjunctie ce contine doar ai-j
            ArrayList<Integer> nod = matrix.get(i);
            conj += "(";
            for(int j = 0; j < nod.size(); j++) {
                // Construim conjunctia cu toate variabilele corespunzatoare 
                // muchiilor adiacente fiecarui nod, negate
                conj += "~x" + i + "-" + nod.get(j);
                if(j < nod.size() - 1)
                    conj += "&";
            }
            conj += ")";
            disj += "(";
            for(int j = 1; j <= dists; j++) {
                // Construim disjunctia cu toate variabilele corespunzatoare 
                // tuturor distantelor dintre nodului i si nodul 1
                disj += "a" + j + "-" + i;
                if(j < dists)
                    disj += "|";
            }
            disj += ")";
            write("(", bw);
            
            for(int j = 0; j < nod.size() - 1; j++) {
                // Stergem negatia unei variabile
                String one_true = conj.substring(0, 6 * j + 1) +
                                  conj.substring(6 * j + 2);
                for(int k = j + 1; k < nod.size();k++) {
                    // Stergem negatia altei variabile
                    String two_true = one_true.substring(0, 6 * k + i / 10) +
                                      one_true.substring(6 * k + 1 + i / 10);
                    write(two_true, bw);
                    if(j < nod.size() - 2 || k < nod.size() - 1)
                        write("|", bw);
                }
            }
            write(")&", bw);
            if(i > 1)
                write(disj + "&", bw);
        }
                
    }

    public static void doPartTwo(ArrayList<ArrayList<Integer>>  matrix, BufferedWriter bw) {
        Integer nr_nod  = matrix.size() - 1;
        Integer dists = (matrix.size() - 1) / 2 + 1;
        
        for(int i = 1; i <= nr_nod; i++){
        ArrayList<Integer> nod = matrix.get(i);
            for(int j = 0; j < nod.size(); j++){
                // Pentru fiecare muchie, scriem conjunctia ce determina
                // echivalenta dintre cele 2 variabile corespunzatoare
                if(nod.get(j) <= i)
                    continue;
                Integer n = nod.get(j);
                write("((x" + i + "-" + n + "|~x" + n + "-" + i +
                        ")&(~x" + i + "-" + n + "|x" + n + "-" + i + "))&", bw);
            }
        }
        ArrayList<Integer> nod1 = matrix.get(1);
        ArrayList<Integer> scrise;      // muchiile scrise ce pleaca din nodul 1
        scrise = new ArrayList<>(Collections.nCopies(nr_nod + 1, 0));
            
        for(int i = 0; i < nod1.size(); i++){
            Integer n = nod1.get(i);
            scrise.set(n, 1);
            write("((a1-"+n+"|~x1-"+n+")&(~a1-" + n + "|x1-" + n + "))&", bw);
        }
        for(int i = 1; i <= nr_nod; i++){
            // scriem distantele negate de lungime 1 corespunzatoare nodurilor
            // ce nu au muchie intre ele si nodul 1
            if(scrise.get(i) == 0)
                write("~a1-" + i + "&", bw);
        }
            
        for(int i = 2; i <= dists; i++){
            for(int j = 2; j <= nr_nod; j++){
                ArrayList<Integer> nod = matrix.get(j);
                // Scriem distanta pana la nodul j de distanta i, ai-j
                write("((a" + i + "-" + j + "|~((", bw);
                for(int k = 0; k < nod.size(); k++) {
                    // Pentru fiecare muchie dintre un nod diferit de 1 si j
                    // scriem distanta de lungime cu o unitate mai mica si
                    // muchia respectiva
                    Integer n = nod.get(k);
                    if(n == 1)
                        continue;
                    write("(a" + (i-1) + "-" + n + "&x" + n + "-" + j +")", bw);
                    if(k < nod.size() - 1)
                        write("|", bw);
                }
                write(")&~(", bw);
                for(int k = 1; k < i; k++) {
                    // Scriem distantele mai mici pana in nodul j
                    write("a" + k + "-" + j, bw);
                    if(k < i - 1)
                        write("|", bw);
                }
                
                // Acum facem acelasi lucru ca inainte, dar mutam negatia in
                // cealalta parte
                write(")))&(~a" + i + "-" + j + "|((", bw);
                for(int k = 0; k < nod.size(); k++) {
                    Integer n = nod.get(k);
                    if(n == 1)
                        continue;
                    write("(a" + (i-1) + "-" + n + "&x" + n + "-" + j +")", bw);
                    if(k < nod.size() - 1)
                        write("|", bw);
                }
                write(")&~(", bw);
                for(int k = 1; k < i; k++) {
                    write("a" + k + "-" + j, bw);
                    if(k < i - 1)
                        write("|", bw);
                }
                write("))))", bw);
                if(j < nr_nod)
                    write("&", bw);
            }
            if(i < dists)
                write("&", bw);
        }
    }
        
    public static void sortMatrix(ArrayList<ArrayList<Integer>> matrix) {
        Integer nr_nod  = matrix.size() - 1;
        for(int i = 1; i <= nr_nod; i++){
            ArrayList<Integer> nod = matrix.get(i);
            Collections.sort(nod);
        }
    }
    
    public static void write(String info, BufferedWriter bw) {
        try {
            bw.write(info);
        }
        catch (IOException ex) {
            System.err.println("Nu se poate scrie in buffer");
            System.exit(1);
        }
    }
    
    public static void close(BufferedWriter bw) {
        try {
            bw.close();
        }
        catch (IOException ex) {
            System.err.println("Nu se poate inchide bufferul");
            System.exit(1);
        }
    }
}
