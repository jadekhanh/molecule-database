package edu.bu.ec504.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Represents a molecule containing atoms and edges.
 */
public class Molecule implements Serializable {

    //FIELDS
    public String moleculeName; // name of the molecule
    int numAtoms; // number of atoms in the molecule
    int numEdges; // number of edges in the molecule
    ArrayList<Atom> atomArrayList;  // list of atoms in the molecule
    int[] numElements; // array to store quantity of each element

    /**
     * Constructs a molecule by reading data from a file.
     *
     * @param moleculeFile the file containing the molecule.
     */
    public Molecule(String moleculeFile) {
        //Init values
        numElements = new int[119];

        int f, l;

        // Read and process input
        try (BufferedReader reader = new BufferedReader(new FileReader(moleculeFile))) { //Might want to move reader outside molecule class in the future to save space
            String line;
            moleculeName = reader.readLine(); //Reads name
            numAtoms = Integer.parseInt(reader.readLine()); //Reads # of atoms
            atomArrayList = new ArrayList<Atom>(numAtoms); //Creates arraylist of atoms
            for (int ii = 0; ii < numAtoms; ii++) {
                line = reader.readLine();
                PeriodicTable element = PeriodicTable.valueOf(line);
                int num = element.getAtomicNumber();  //Looks up element in Periodic Table
                atomArrayList.add(new Atom(line + numElements[num], num)); //Adds atom to list (with index)
                numElements[num]++; //Increases count for specific atom
            }
            while ((line = reader.readLine()) != null) {
                numEdges++; //Counts # of edges
                int beginIndex = line.indexOf(' ') + 1;
                f = Integer.parseInt(line.substring(0, beginIndex - 1)); //Reads first atom in edge
                l = Integer.parseInt(line.substring(beginIndex)); //Reads second atom in edge
                atomArrayList.get(f).addEdge(atomArrayList.get(l)); //Marks edge for first atom
                atomArrayList.get(l).addEdge(atomArrayList.get(f)); //Marks edge for second atom
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Compare two molecules and check if they are isomorphic.
     *
     * @param otherMolecule The molecule to compare with.
     * @return The first molecule if they are equal, otherwise null.
     */
    public Molecule areMoleculesEqual(Molecule otherMolecule) {

        // Compare the number of elements
        if (!Arrays.equals(this.numElements, otherMolecule.numElements)) {
//            System.out.println("different numElements");
            return null; // Number of elements is different, molecules are not equal
        }

        // Compare the number of edges
        if (this.numEdges != otherMolecule.numEdges) {
//            System.out.println("different numEdges");
            return null; // Number of edges is different, molecules are not equal
        }

        // Clean the atom list of any marked atoms
        for(Atom cleanAtom : otherMolecule.atomArrayList)
            cleanAtom.marked = false;
        // Compare the atom lists
        for(Atom dbAtom : this.atomArrayList) {
            boolean atomFound = false;
            for (Atom newAtom : otherMolecule.atomArrayList) {
                if (!newAtom.marked &&  newAtom.degree == dbAtom.degree && newAtom.elementType == dbAtom.elementType) { //Check if degrees and elements are the same
                    boolean sameConnected = true;
                    // Compare connected of each atom
                    //for each connected atom in dbAtom
                    boolean [] edgeMarked = new boolean[newAtom.connected.size()];
                    for (Atom.ElemOrderPair dbValues : dbAtom.connected.values()) {
                        boolean matchingEdgeIsFound = false;
                        int marker = 0;
                        //for each connected element in  input
                        for (Atom.ElemOrderPair newAtomValues : newAtom.connected.values()) {
                            //if its a match
                            if (!edgeMarked[marker] && dbValues.eType == newAtomValues.eType && dbValues.bondOrder == newAtomValues.bondOrder) {
                                //mark the newAtom edge as already found
                                edgeMarked[marker]=true;
                                matchingEdgeIsFound = true;
                                //go to next connected atom in dbAtom (break)
                                break;
                            }
                            marker++;
                        }
                        if (!matchingEdgeIsFound)
                            sameConnected = false;
                    }
                    if (sameConnected) {
                        atomFound = true;
                        newAtom.marked = true;
                        break;
                    }
                    //if connected isnt the same
                    //go to next newAtom;
                }
            }
            if(!atomFound)
                return null;
        }

        // If all comparisons passed, the molecules are equal
        return this;
    }



    /**
     * Finds the Most Similar Molecule
     */
    public int mostSimilar(Molecule otherMolecule) {

        int similarity=0; //counts similarity points

        // Points for an intersection of elements between this and otherMolecule
        for(int i=0;i<this.numElements.length;i++)
        {
            similarity+= Math.min( this.numElements[i], otherMolecule.numElements[i]);
        }

        //Points for same number of atoms
        if(this.numAtoms==otherMolecule.numAtoms)
        {
            similarity++;
        }

        //Points for same number of edges
        if(this.numEdges==otherMolecule.numEdges)
        {
            similarity++;
        }


        // Clean the otherMolecule atom list of any marked atoms
        for(Atom cleanAtom : otherMolecule.atomArrayList)
        {
            cleanAtom.marked = false;

            //clean the connected arraylist
            for(int i=0;i<cleanAtom.connectedMarked.size();i++)
            {
                cleanAtom.connectedMarked.set(i,false);
            }
        }
        // Clean the dbMolecule atom list of any marked atoms

        // Compare the atom lists
        for(Atom dbAtom : this.atomArrayList) {
            boolean matchingEdge=false;

            for (Atom newAtom : otherMolecule.atomArrayList) {

                if (!newAtom.marked && newAtom.elementType == dbAtom.elementType) { //Check if elements are the same
                    boolean [] edgeMarked = new boolean[newAtom.connected.size()];
                    // Compare connected of each atom
                    //for each connected atom in dbAtom
                    for (Atom.ElemOrderPair dbValues : dbAtom.connected.values()) {
                        int marker=0;

                        for (Atom.ElemOrderPair newAtomValues : newAtom.connected.values()) {
                            //if its a match
                            //if first 2 yes, add points for min of bondOrder between the 2
                            if (!newAtom.connectedMarked.get(marker) && !edgeMarked[marker] && dbValues.eType == newAtomValues.eType )
                            {
                                if( dbValues.bondOrder == newAtomValues.bondOrder)
                                {
                                    //mark the newAtom edge as already found
                                    newAtom.connectedMarked.set(marker,true);
                                    matchingEdge=true;
                                    edgeMarked[marker]=true;
                                    similarity++; //add a point for each edge that is the same
                                    break;
                                }

                            }
                            marker++;

                        }
                    }

                }

                if(matchingEdge)
                {
                    newAtom.marked=true;
                    break;
                }
            }

        }

        // If all comparisons passed, the molecules are equal
        return similarity;
    }

    /**
     * Returns the molecule if it contains subgraph
     * @param subgraph
     * @return
     */
    public Molecule isSubGraphPresent(Molecule subgraph) {
        //compare the number of elements
        for (int ii = 0; ii < numElements.length; ii++)
            if (this.numElements[ii] < subgraph.numElements[ii])
                return null;

        // Compare # of edges
        if (this.numEdges < subgraph.numEdges)
            return null;

        return extensiveSearch(subgraph);
    }
    public Molecule extensiveSearch(Molecule subgraph) {
        //Hashmap of candidates
        HashMap<Atom, ArrayList<Atom>> CandidateList = new HashMap<>();

        //For each atom in the array list
        for (Atom possibleCandidate : this.atomArrayList) {
            Object[] cndArray = possibleCandidate.connected.values().toArray();
            for (Atom keyAtom : subgraph.atomArrayList) {
                if (!keyAtom.equals(possibleCandidate)) {
                    if (keyAtom.elementType == possibleCandidate.elementType) {
                        if (keyAtom.degree <= possibleCandidate.degree) {
                            boolean validConnects = true;
                            boolean[] edgeMarked = new boolean[possibleCandidate.connected.size()];
                            for (Atom.ElemOrderPair keyValue : keyAtom.connected.values()) {
                                boolean edgeFound = false;
                                for (int aa = 0; aa < edgeMarked.length; aa++) {
                                    if (!edgeMarked[aa] && keyValue.eType == ((Atom.ElemOrderPair) cndArray[aa]).eType && keyValue.bondOrder == ((Atom.ElemOrderPair) cndArray[aa]).bondOrder) {
                                        edgeMarked[aa] = true;
                                        edgeFound = true;
                                        break;
                                    }
                                }
                                if (!edgeFound) {
                                    validConnects = false;
                                }
                            }
                            if (validConnects) {
                                //add atom to list
                                if (!CandidateList.containsKey(keyAtom)) {
                                    CandidateList.put(keyAtom, new ArrayList<>());
                                }
                                CandidateList.get(keyAtom).add(possibleCandidate);
                            }
                        }
                    }
                }
            }
        }
        //Look through list of candidates, and if any of the lists are empty return null
        for (Atom sweep : subgraph.atomArrayList)
            if (!CandidateList.containsKey(sweep))
                return null;

        //Un mark all atoms (marked = visited)
        for (Atom cleanAtom : this.atomArrayList)
            cleanAtom.marked = false;
        for (Atom cleanAtom : subgraph.atomArrayList)
            cleanAtom.marked = false;

        //Make a linked list of the BFS node
        LinkedList<subGraphNode> subgraphTraversal = new LinkedList<subGraphNode>();
        //head is the first atom (parent is null)
        subgraphTraversal.addFirst(new subGraphNode(null, subgraph.atomArrayList.get(0)));
        for (Atom c : CandidateList.get(subgraph.atomArrayList.get(0))) {
           subgraphTraversal.get(0).options.add(c);
        }
        subgraphTraversal.getFirst().self.marked = true;
        int pointer = 0;
        while(true) {
            subGraphNode current = subgraphTraversal.get(pointer);
            if(current.options.isEmpty()) {
                //If there are no options to choose from, move pointer backwards (to parent) and restart cycle
                //if parent is null return null
                if(current.parent == null)
                    return null;
                else
                    pointer = subgraphTraversal.indexOf(current.parent);
            }
            else {
                boolean mustReverse = false;
                int adjAdded = 0;
                //remove option (choose it)
                Atom path = current.options.remove(0);
                path.marked = true;
                //else add adjacent local nodes
                for(String k :current.self.connected.keySet()) {
                    for(Atom a: subgraph.atomArrayList) {
                        if(!a.marked && a.getName().equals(k)) {
                            a.marked = true;
                            subgraphTraversal.add(new subGraphNode(current,a));
                            adjAdded++;
                            //add options to adjacent (checking validity:not marked and connected to parent)
                            ArrayList<Atom> aList = CandidateList.get(a);
                            boolean candFound = false;
                            for(Atom cand: aList) {
                                if(!cand.marked) { //not marked
                                    if(cand.connected.containsKey(path.getName())) {  //is connected
                                        subgraphTraversal.getLast().options.add(cand);
                                        candFound = true;
                                    }
                                }
                            }
                            if(!candFound)
                                mustReverse = true;
                            break;
                        }
                    }
                }
                if(mustReverse) {
                    path.marked = false;
                    for(int ff = 0; ff < adjAdded;ff++) {
                        subgraphTraversal.getLast().self.marked = false;
                        subgraphTraversal.remove(subgraphTraversal.getLast());
                    }
                }
                else{
                    if(subgraphTraversal.getLast().equals(subgraphTraversal.get(pointer)))
                        return this;
                    pointer++;
                }
            }
        }
    }

    /**
     * Return number of atoms of the molecule
     */
    public int getNumAtoms() {
        return numAtoms;
    }

    public ArrayList<Atom> getAtomArrayList() {
        return  atomArrayList;
    }

    class subGraphNode implements Serializable {
        public subGraphNode parent;
        public ArrayList<Atom> options;
        public Atom self;
        public  subGraphNode(subGraphNode p, Atom s) {
            this.parent = p;
            this.self = s;
            this.options = new ArrayList<Atom>();
        }
    };

}

