package edu.bu.ec504.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Atom implements Serializable {
    public Atom(String name,int elem){
        atomName = name;
        degree = 0;
        connected = new HashMap<>();
        connectedMarked= new ArrayList<>();
        elementType = elem;
        marked = false;
    }
    public void addEdge(Atom i) {
        degree++;
        if(connected.containsKey(i.getName())) {
            connected.put(i.getName(),new ElemOrderPair(i.elementType,connected.get(i.getName()).bondOrder+1));
        }
        else {
            connected.put(i.getName(), new ElemOrderPair(i.elementType, 1));
            connectedMarked.add(false);
        }
    }
    public String getName() {
        return this.atomName;
    }
    private String atomName;
    public int elementType;
    public int degree;
    public Map<String,ElemOrderPair> connected;
    public ArrayList<Boolean> connectedMarked;
    public boolean marked;


    class ElemOrderPair implements Serializable{
        public int eType;
        public int bondOrder;
        public ElemOrderPair(int eType, int bondOrder) {
            this.eType = eType;
            this.bondOrder = bondOrder;
        }
    }
}

