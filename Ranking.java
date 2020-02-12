import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


public class Ranking {

    public Tournament T;
    public Integer[] R;
    public int cost;

    /**
     * Ranking constructor
     * Creates Ranking object and calculates cost from R array 
     * 
     * @param Tournament T
     * @param Integer[] R = ranking array
     */
    public Ranking(Tournament T, Integer[] R) {
        this.T = T;
        this.R = R;
        this.cost = getCostFromScratch(R);
    }

    /**
     * Ranking constructor
     * Creates ranking object with cost parameter already set
     * 
     * @param Tournament T
     * @param Integer[] R
     * @param int cost
     */
    public Ranking(Tournament T, Integer[] R, int cost) {
        this.T = T;
        this.R = R;
        this.cost = cost;
    }

    /**
     * getCostFromScratch function
     * 
     * Based on Kemeny Score, which measures amount that ranking R disagrees with tournament T 
     * A ranking R disagrees with T on an edge (x,y) if x defeats y in T but y is ranked above x in R
     * We can measure how well R fits T by adding up all the weights of all the edges for which R disagrees with T
     * 
     * @param Integer[] R = ranking array
     * @return int k = Kemeny Score (best solution = ranking with lowest k)
     */
    private int getCostFromScratch(Integer[] R){

        int k = 0;

        // Check edges for disagreement
        for (int a = 1; a <= T.numParticipants; a++) {
            for (int b = a+1; b <= T.numParticipants; b++) {
                // Loop through contests, and increment Kemeny Score if tournament (a,b) disagrees with ranking- i.e. if b is above a in ranking
                int kChange = getkChange(R, a, b);
                if (kChange > 0) {
                    k += kChange;
                }
            }
        }
        return k;
    }

    /**
     * getNeighbour function
     * Returns neighbour of Ranking R, by swapping the position of a random pair of adjacent elements
     * 
     * @return Ranking neighbour
     */
    public Ranking getNeighbour(){

        // Get neighbour by swapping a random pair of adjacent elements
        Random random = new Random();
        int swap1 = random.nextInt(R.length-1);
        int swap2 = swap1 + 1;

        // Create newR array and swap element order
        Integer[] newR = Arrays.copyOf(R, R.length);
        newR[swap1] = R[swap2];
        newR[swap2] = R[swap1];

        // Get cost based on swapped indexes
        int newCost = getCostFromAdjacentSwap(newR, swap1, swap2);

        // Create and return neighbour Ranking object
        Ranking neighbour = new Ranking(T, newR, newCost);
        return neighbour;
    }

    /**
     * getCostFromAdjacentSwap function
     * Based on Kemeny Score as previously outlined, but calculates change in cost of a given neighbourhood swap.
     * 
     * @param Integer[] newR = R array after swap
     * @param int swap1 = first swap index
     * @param int swap2 = second swap index
     * @return int newCost = updated Kemeny Score
     */
    private int getCostFromAdjacentSwap(Integer[] newR, int swap1, int swap2){

        int newCost = cost;
        int s1 = Math.min(swap1, swap2);
        int s2 = Math.max(swap1, swap2);

        int a = newR[s1];
        int b = newR[s2];
        int kChange = getkChange(newR, a, b);

        newCost += kChange;
        return newCost;
    }

    /**
     * getkChange
     * Returns the change in Kemeny Score of a single contest between participants a & b
     * nb- returns positive weight when a beats b, and negative weight when b beats a
     * 
     * @param R = given ranking
     * @param a = participant number
     * @param b = participant number
     * @return int k = change in Kemeny Score (positive/negative weight of edge a,b)
     */
    private int getkChange(Integer[] R, int a, int b){

        // Get positions in ranking
        int rankA = Arrays.asList(R).indexOf(a);
        int rankB = Arrays.asList(R).indexOf(b);

        int k=0;
        if (T.matrix[a-1][b-1] > T.matrix[b-1][a-1]) {       
            // a won, so a should be before b in ranking
            if (rankA > rankB) {
                k += T.matrix[a-1][b-1];
            }else{
                k -= T.matrix[a-1][b-1];
            }
        }else if (T.matrix[b-1][a-1] > T.matrix[a-1][b-1]){
            // b won, so a should be after a in ranking
            if (rankA < rankB) {
                k += T.matrix[b-1][a-1];
            }else{
                k -= T.matrix[b-1][a-1];
            }
        }
        return k;
    }


    /**
     * getNeighbourRandomSwap function
     * Returns neighbour of Ranking R, by swapping the position of 2 random elements
     * 
     * @return Ranking neighbour
     */
    public Ranking getNeighbourRandomSwap(){

        Random random = new Random();
        Integer[] newR = Arrays.copyOf(R, R.length);

        // Get neighbour by swapping 2 random indexes
        int swap1 = random.nextInt(R.length);
        int swap2 = random.nextInt(R.length -1); // -1 because 1 less index available for swap

        // If swap2 is on/after swap1 index, increment by 1 to ensure all have equal probability of being selected
        if (swap1 <= swap2) {
            swap2 ++;
        }
        newR[swap1] = R[swap2];
        newR[swap2] = R[swap1];

        // Get cost based on swapped indexes
        int newCost = getCostFromRandomSwap(newR, swap1, swap2);

        // Create and return neighbour Ranking object
        Ranking neighbour = new Ranking(T, newR, newCost);
        return neighbour;
    }

    /**
     * getCostFromRandomSwap function
     * Based on Kemeny Score as previously outlined, but calculates change in cost of a given neighbourhood swap.
     * 
     * @param Integer[] newR = R array after swap
     * @param int swap1 = first swap index
     * @param int swap2 = second swap index
     * @return int newCost = updated Kemeny Score
     */
    private int getCostFromRandomSwap(Integer[] newR, int swap1, int swap2){

        int newCost = cost;
        int s1 = Math.min(swap1, swap2);
        int s2 = Math.max(swap1, swap2);

        // Check swapped indexes
        int a = newR[s1];
        int b = newR[s2];
        int kChange = getkChange(newR, a, b);
        newCost += kChange;

        // Check middle indexes:
        for (int i = s1+1; i <= (s2-1); i++) {
            // swap1 + middle index
            a = newR[s1];
            b = newR[i];
            kChange = getkChange(newR, a, b);
            newCost += kChange;

            // middle index + swap2
            a = newR[i];
            b = newR[s2];
            kChange = getkChange(newR, a, b);
            newCost += kChange;
        }
        return newCost;
    }

    public void printParticipants(){
        for (Integer r : R) {
            System.out.println(r + ": "+T.participants.get(r));
        }
    }

    @Override
    public String toString(){
        String s = "Ranking: "+Arrays.toString(R)+", Cost: "+cost;   
        return s;
    }
}



