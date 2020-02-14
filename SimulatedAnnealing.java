import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class SimulatedAnnealing {

    Tournament T;
    Ranking initialSolution;
    int maxNumNonImprove; // algorithm stops when specified number of new solutions have been looked at without a better solution being found
    int numberOfUphillMoves; // tracks number of uphill moves accepted during an SA run
    int iterationNum; // tracks total number of iterations of inner loop, i.e. number of rankings considered
    double initialTemp;
    int temperatureLength; // number of iterations at a given temperature
    double coolingRate; // rate at which temperature is reduced

    /**
     * SimulatedAnnealing constructor
     * Creates SimulatedAnnealing object - for given Tournament and initialSolution
     * 
     * @param T
     * @param initialSolutionR
     * @param maxNumNonImprove
     * @param initialTemp
     * @param temperatureLength
     * @param coolingRate
     */
    public SimulatedAnnealing(Tournament T, Integer[] initialSolutionR, int maxNumNonImprove, double initialTemp, int temperatureLength, double coolingRate){       

        this.T = T;
        this.initialSolution = new Ranking(T, initialSolutionR);
        this.maxNumNonImprove = maxNumNonImprove; 
        this.numberOfUphillMoves = 0;
        this.iterationNum = 0;

        this.initialTemp = initialTemp;
        this.temperatureLength = temperatureLength;
        this.coolingRate = coolingRate;
    }

    /**
     * runAlgorithm function
     * Runs SA algorithm and returns best solution Ranking object
     * 
     * @return Ranking xBest = best ranking found
     */
    public Ranking runAlgorithm(){

        // Initialise variables
        Random random = new Random();
        double temp = initialTemp;
        Ranking xNow = initialSolution;
        Ranking xBest = initialSolution;
        iterationNum = 0;

        // OUTER LOOP- while stopping criterion not met-
        int numNonImprove = 0;
        while (numNonImprove < maxNumNonImprove) {
            
            // INNER LOOP- check TL neighbours at each temperature
            for (int i = 0; i < temperatureLength; i++) {
                iterationNum ++;

                // Get neighbour and compute improvement in cost = (newCost - costNow)
                Ranking xNeighbour = xNow.getNeighbour();
                double changeInCost = (xNeighbour.cost - xNow.cost);

                // Downhill/flat move to better/equivalent solution- accept new solution
                if (changeInCost <= 0) {
                    xNow = xNeighbour;
                }else{
                    // Uphill move to worse solution- accept solution probabilistically
                    double q = random.nextDouble();
                    double x = (changeInCost / temp);
                    double prob = Math.pow( Math.E, -x);

                    // If q < e^(-changeInCost / T): accept change, update xNow
                    if (q < prob) {
                        xNow = xNeighbour;
                        numberOfUphillMoves ++;
                    }
                }

                // Check if new best solution found
                if (xNow.cost < xBest.cost) {
                    xBest = xNow;
                    numNonImprove = 0;
                }
                
                // Update or reset numNonImprove count
                if (changeInCost < 0) {
                    numNonImprove = 0;
                }else{
                    numNonImprove ++;
                }

                // Check stopping condition
                if (numNonImprove > maxNumNonImprove) {
                    break;
                }

            }
            // Decrease temp
            temp *= coolingRate;
        }
        return xBest;
    }


    /**
     * analyseSingleRun function
     * Run SA algorithm and output a csv summary of xNow at each iteration
     * 
     * @return void
     */
    public void analyseSingleRun(){

        // Create results array
        ArrayList<String> resultsArray = new ArrayList<String>();

        // Title rows
        resultsArray.add("Initial Temp:,"+initialTemp);
        resultsArray.add("Temperature Length:,"+temperatureLength);
        resultsArray.add("Cooling Rate:,"+coolingRate);
        resultsArray.add("Max number non improve:,"+maxNumNonImprove);
        resultsArray.add(""); // index 4

        // Header row
        resultsArray.add("Iteration,Kemeny Score");
        
        // Initialise variables
        Random random = new Random();
        double temp = initialTemp;
        Ranking xNow = initialSolution;
        Ranking xBest = initialSolution;
        iterationNum = 0;

        // Add initial solution to results array
        resultsArray.add(iterationNum + "," + xNow.cost);

        // OUTER LOOP- while stopping criterion not met-
        int numNonImprove = 0;
        while (numNonImprove < maxNumNonImprove) {
            
            // INNER LOOP- check TL neighbours at each temperature
            for (int i = 0; i < temperatureLength; i++) {
                iterationNum ++;

                // Get neighbour and compute improvement in cost = (newCost - costNow)
                Ranking xNeighbour = xNow.getNeighbour();
                double changeInCost = (xNeighbour.cost - xNow.cost);

                // Add neighbour to results array
                resultsArray.add(iterationNum + "," + xNeighbour.cost);

                // Downhill/flat move to better/equivalent solution- accept new solution
                if (changeInCost <= 0) {
                    xNow = xNeighbour;

                    // Update xBest and reset numNonImprove if better solution found
                    if (changeInCost < 0) {
                        xBest = xNow;
                        numNonImprove = 0;
                    }
                }else{
                    // Uphill move to worse solution- accept solution probabilistically
                    double q = random.nextDouble();
                    double x = (changeInCost / temp);
                    double prob = Math.pow( Math.E, -x);

                    // If q < e^(-changeInCost / T): accept change, update xNow
                    if (q < prob) {
                        xNow = xNeighbour;
                        numberOfUphillMoves ++;
                    }

                    // Check if stopping condition met
                    numNonImprove ++;
                    if (numNonImprove > maxNumNonImprove) {
                        break;
                    }
                }
            }
            // Decrease temp
            temp *= coolingRate;
        }

        // Add best solution summary info to resultsArray
        resultsArray.add(4, "Best solution:," + Arrays.toString(xBest.R).replaceAll(", ", "-"));
        resultsArray.add(5, "Best solution K:," + xBest.cost);
        resultsArray.add(6, "Best solution first reached on iteration:," + "\"=INDEX(A11:A"+(iterationNum+10)+",MATCH(B6, B11:B"+(iterationNum+10)+",0))\"");
        resultsArray.add(7, "Total Number of iterations:," + iterationNum);

        // Write contents to csv
        String filename = "Results/Single_Run T_"+initialTemp+", TL_"+temperatureLength+", CR_"+coolingRate+", N_"+maxNumNonImprove+".csv";
        try{
            // Create new file (overwrite if already exists)
            File file = new File(filename);
            file.createNewFile();
            FileWriter writer = new FileWriter(filename);
            PrintWriter out = new PrintWriter(writer);

            // Loop through resultsArray and write to file
            for (String row : resultsArray) {
                out.println(row);
            }
            out.close();
        }
        catch ( Exception e ) {
            System.out.println( e );
        }
    }


    /**
     * runAlgorithmXTimes function
     * Run algorithm x times and outputs csv outlining variation with given parameters
     * 
     * @return void
     */
    public void runAlgorithmXTimes (Tournament T, Integer[] initialSolutionR, int maxNumNonImprove, double initialTemp, int temperatureLength, double coolingRate, int x){

        ArrayList<String> resultsArray = new ArrayList<String>();

        // Title rows
        resultsArray.add("Initial Temp:,"+initialTemp);
        resultsArray.add("Temperature Length:,"+temperatureLength);
        resultsArray.add("Cooling Rate:,"+coolingRate);
        resultsArray.add("Max number non improve:,"+maxNumNonImprove);
        resultsArray.add("");
        resultsArray.add("Number of runs of algorithm (x):,"+x);

        // Header row
        resultsArray.add("Kemeny Score,Runtime,Num iterations,Num uphill moves,Best Solution");
        
        // Run algorithm and build results array 
        for (int i = 0; i < x; i++) {
            long SAx_startTime = System.currentTimeMillis();
            SimulatedAnnealing SAx = new SimulatedAnnealing(T, initialSolutionR, maxNumNonImprove, initialTemp, temperatureLength, coolingRate);
            Ranking SAx_Best = SAx.runAlgorithm();
            long SAx_endTime = System.currentTimeMillis();
            long SAx_runtime = SAx_endTime - SAx_startTime;

            // Populate results array
            resultsArray.add(
                SAx_Best.cost +","+  // Kemeny Score
                SAx_runtime +","+  // Runtime
                SAx.iterationNum +","+  // Num iterations
                SAx.numberOfUphillMoves +","+  // Num uphill moves
                " "+Arrays.toString(initialSolutionR).replaceAll(", ", "-") // Best solution
            );
        }

        // Add summary stats to resultsArray
        int startRow = 14;
        resultsArray.add(6, "Kemeny Score Average:," + "=AVERAGE(A"+startRow+":"+"A"+(startRow+x-1)+")");
        resultsArray.add(7, "Kemeny Score Standard Dev:," + "=STDEV(A"+startRow+":"+"A"+(startRow+x-1)+")");
        resultsArray.add(8, "Runtime Average (milliseconds):," + "=AVERAGE(B"+startRow+":"+"B"+(startRow+x-1)+")");
        resultsArray.add(9, "Num Iterations Average:," + "=AVERAGE(C"+startRow+":"+"C"+(startRow+x-1)+")");
        resultsArray.add(10, "Num Uphill Moves Average:," + "=AVERAGE(D"+startRow+":"+"D"+(startRow+x-1)+")");
        resultsArray.add(11, "");

        // Write results to csv
        String filename = "Results/RunXTimes T_"+initialTemp+", TL_"+temperatureLength+", CR_"+coolingRate+", N_"+maxNumNonImprove+", rep_"+x+".csv";
        try{
            // Create new file (overwrite if already exists)
            File file = new File(filename);
            file.createNewFile(); // creates file if doen't already exist
            FileWriter writer = new FileWriter(filename);
            PrintWriter out = new PrintWriter(writer);

            // Loop through resultsArray
            for (String row : resultsArray) {
                out.println(row);
            }
            out.close();
        }
        catch ( Exception e ) {
            System.out.println( e );
        }        
    }

    public static void main(String[] args) throws Exception {

        // Load tournament data from args
        Tournament T = new Tournament(args[0]);
        // T.printMatrix();

        // Construct initial solution using the order of participants in input file, and create Ranking R
        Integer[] initialSolutionR = new Integer[T.numParticipants];
        for (int i = 0; i < T.numParticipants; i++) {
            initialSolutionR[i] = i+1;
        }

        // Set algorithm parameters
        int maxNumNonImprove = 700; // algorithm stops when specified number of new solutions have been looked at without a better solution being found
        double initialTemp = 20;
        int temperatureLength = 75; // number of iterations at a given temperature
        double coolingRate = 0.99; // rate at which temperature is reduced
        
        // Run algorithm
        long startTime = System.currentTimeMillis();
        SimulatedAnnealing SA = new SimulatedAnnealing(T, initialSolutionR, maxNumNonImprove, initialTemp, temperatureLength, coolingRate);
        Ranking xBest = SA.runAlgorithm();
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;

        // Print stats
        System.out.println("Best solution found: "+xBest+"\n");
        for (int i = 0; i < xBest.R.length; i++) {
            System.out.println((i+1)+": "+T.participants.get(xBest.R[i]).getName());
        }
        System.out.println("\nKemeny Score of solution: "+xBest.cost);
        System.out.println("Algorithm Runtime: "+runtime+" milliseconds");
        System.out.println("Number of uphill moves made: "+SA.numberOfUphillMoves);
        System.out.println("Number of iterations: "+SA.iterationNum);


        ///////////////////////////////////////////////////////////////

        // ANALYSE SINGLE RUN
        // SimulatedAnnealing SA = new SimulatedAnnealing(T, initialSolutionR, maxNumNonImprove, initialTemp, temperatureLength, coolingRate);
        // SA.analyseSingleRun();

        // RUN ALGORITHM X TIMES TO SEE VARIATION FOR GIVEN PARAMETERS
        // int x = 1000;
        // SimulatedAnnealing SA = new SimulatedAnnealing(T, initialSolutionR, maxNumNonImprove, initialTemp, temperatureLength, coolingRate);
        // SA.runAlgorithmXTimes(T, initialSolutionR, maxNumNonImprove, initialTemp, temperatureLength, coolingRate, x);

    }
}