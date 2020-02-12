public class Participant {

    private String id;
    private String name;

    public Participant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public String getID(){
        return this.id;
    }
    public String toString(){
        return "id: "+id+", name: "+name;
    }   
}
