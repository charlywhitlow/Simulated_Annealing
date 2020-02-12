
public class Edge {

    public int a; // winner
    public int b; // loser
    public int weight; // amount a won by

    public Edge(int a, int b, int weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }


    @Override
    public boolean equals (Object o){
        // Return false if object not Edge
        if(!(o instanceof Edge)) 
            return false;
        // Return true if object values match
        Edge e = (Edge)o;
        return a==e.a && b==e.b && weight==e.weight;
    }

    @Override
    public String toString(){
        String s = "edge: ("+a+","+b+"): "+weight;
        return s;
    }

}
