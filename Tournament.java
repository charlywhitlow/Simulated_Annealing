import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


public class Tournament {

    int numParticipants;
    HashMap<Integer, Participant> participants; // participants[i] = (String id, String name)
    int[][] matrix; // score matrix

    /**
     * Tournament constructor
     * Creates Tournament object from input text file
     * 
     * @param String fileName = input file with tournament data in specified format
     * @throws Exception
     */
    public Tournament(String fileName) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(fileName));

        // Add participants to participants hashmap
        numParticipants = Integer.parseInt(in.readLine().split("\\s")[0]);
        participants = new HashMap<Integer, Participant>();
        for (Integer i = 1; i <= numParticipants; i++) { // starting at index 1 so index 1 = participant 1
            String s = in.readLine();
            String id = s.split(",")[0];
            String name = s.split(",")[1];
            Participant p = new Participant(id, name);
            participants.put(i, p);
        }

        // Ignore line with additional info about how tournament generation
        in.readLine();

        // Build matrix representation of tournament
        matrix = new int[numParticipants][numParticipants];
        String s = in.readLine();
        while (s != null) {
            int weight = Integer.parseInt(s.split(",")[0]);
            int a = Integer.parseInt(s.split(",")[1])-1; // -1 because input numbering starts at 1 but 0 in matrix
            int b = Integer.parseInt(s.split(",")[2])-1;
            matrix[a][b] = weight;
            s = in.readLine();
        }
        in.close();
    }

    /**
     * buildEdgesArray function
     * @return HashMap<Integer, Edge> edges = array of edge objects for a given ranking
     */
    private HashMap<Integer, Edge> buildEdgesArray(){

        HashMap<Integer, Edge> edges = new HashMap<Integer, Edge>();

        int edgeIndex = 0;
        for (int i = 0; i < numParticipants; i++) {
            for (int j = i+1; j < numParticipants; j++) {

                // Get position of participants in ranking
                int a = i+1;
                int b = j+1;
                int winner, loser, weight;

                // Loop through edges, and increment Kemeny Score if tournament (a,b) disagrees with ranking 
                if (matrix[i][j] > matrix[j][i]) {
                    winner = a;
                    loser = b;
                    weight = matrix[i][j];
                }else if (matrix[j][i] > matrix[i][j]){
                    winner = b;
                    loser = a;
                    weight = matrix[j][i];
                }else{
                    winner = a;
                    loser = b;
                    weight = 0;
                }

                // Create new edge and add to edges
                Edge e = new Edge(winner, loser, weight);
                edges.put(edgeIndex, e);
                edgeIndex ++;
            }
        }
        return edges;
    }

    /**
     * printMatrix function
     * @return void
     */
    public void printMatrix(){
        for (int i = 0; i < numParticipants; i++) {
            for (int j = 0; j < numParticipants; j++) {
                System.out.print(matrix[i][j]+" ");
                if (matrix[i][j] < 10) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * printEdges function
     */
    public void printEdges(){

        HashMap<Integer, Edge> edges = buildEdgesArray();        
        for (int edge = 0; edge < edges.size(); edge++) {
            System.out.println(edges.get(edge).toString());
        }
    }

}
