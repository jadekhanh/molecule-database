import edu.bu.ec504.project.Atom;
import edu.bu.ec504.project.Molecule;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * A class represents the molecule database that works with the GUI.
 */
public class MDB {

    public HashMap<Integer, ArrayList<Molecule>> db;   // Molecule database
    public JTextArea outputTextArea; // Reference to the text area in the GUI

    /**
     * Constructs a database
     */
    public MDB(JTextArea outputTextArea) {
        this.db = new HashMap<>();
        this.outputTextArea = outputTextArea;
    }

    /**
     * Print database statistics to GUI
     */
    public void printDb() {
        // Print number of molecules
        int size = 0;
        for (ArrayList<Molecule> molecules : db.values()) {
            size += molecules.size();
        }
        outputTextArea.append("# of molecules: " + size + "\n\n");

        if (size == 0)
            return; // if database is empty, exit early

        // Print the list of molecules
        outputTextArea.append("List of molecules: " + "\n\n");
        for (Integer atomCount : this.db.keySet()) {
            ArrayList<Molecule> moleculesWithSameNumAtoms = this.db.get(atomCount);
            for (Molecule molecule : moleculesWithSameNumAtoms) {
                outputTextArea.append("Molecule name: " + molecule.moleculeName + "\n");
                outputTextArea.append("# of atoms: " + atomCount.toString() + "\n\n");
            }
        }

        // Print the largest and biggest molecules
        int maxAtoms = Integer.MIN_VALUE;
        int minAtoms = Integer.MAX_VALUE;
        Molecule largestMolecule = null;
        Molecule smallestMolecule = null;
        for (Map.Entry<Integer, ArrayList<Molecule>> entry : db.entrySet()) {
            int numAtoms = entry.getKey();
            if (numAtoms > maxAtoms) {
                maxAtoms = numAtoms;
                largestMolecule = entry.getValue().get(0); // only print 1 representative molecule
            }
            if (numAtoms < minAtoms) {
                minAtoms = numAtoms;
                smallestMolecule = entry.getValue().get(0); // only print 1 representative molecule
            }
        }
        outputTextArea.append("Smallest molecule: " + smallestMolecule.moleculeName + "\n");
        outputTextArea.append("Largest molecule: " + largestMolecule.moleculeName + "\n\n");

    }

    /**
     * Add a new molecule into the database
     */
    public void addMolecule(Molecule molecule) {
        if (molecule == null) {
            outputTextArea.append("molecule == null" + "\n\n");
            return;
        }
        int numAtoms = molecule.getNumAtoms();
        //test if molecule has an unconnected atom
        for(Atom a: molecule.getAtomArrayList())
            if(a.connected.isEmpty()) {
                outputTextArea.append("Error: molecule file is incorrect (contains unconnected atom)" + "\n\n");
                return;
            }

        if (this.db.containsKey(numAtoms)) {
            this.db.get(numAtoms).add(molecule);
        } else {
            ArrayList<Molecule> moleculesWithSameNumAtoms = new ArrayList<>();
            moleculesWithSameNumAtoms.add(molecule);
            this.db.put(numAtoms, moleculesWithSameNumAtoms);
        }
    }

    /**
     * Find isomorphic molecule from the database
     */
    public Molecule findMolecule(Molecule molecule) {
        // Retrieve the partitioned array list based on the number of atoms
        int numAtoms = molecule.getNumAtoms();
        if (!db.containsKey(numAtoms)) {
            outputTextArea.append("No ArrayList with correct # of atoms" + "\n\n");
            return null;
        }
        ArrayList<Molecule> moleculesWithSameNumAtoms = db.get(numAtoms);

        // Iterate through the array list of molecules with the same number of atoms
        for (Molecule dbMolecule : moleculesWithSameNumAtoms) {
            outputTextArea.append(dbMolecule.moleculeName + " vs " + molecule.moleculeName + "\n\n");
            Molecule result = dbMolecule.areMoleculesEqual(molecule);
            if (result != null) {
                return result; // Return the isomorphic molecule
            }
        }
        return null; // Return null if molecule not found
    }

    /**
     * Find the most similar Molecule from the database
     */
    public Molecule similarMolecule(Molecule molecule) {

        // If an exact match is not found then find the most similar
        int maxResult=0;
        Molecule similar=null;
        for (Map.Entry<Integer, ArrayList<Molecule>> entry : db.entrySet()) {
            // Access the key and value of each entry
            Integer numberAtoms = entry.getKey();

            //only check for similarity if they have similar number of atoms within tolerance of 100
            if( (molecule.getNumAtoms()-100)<numberAtoms && numberAtoms<(molecule.getNumAtoms()+100) )
            {
                for (Molecule dbMolecule : db.get(numberAtoms)) {
                    int res = dbMolecule.mostSimilar(molecule);
                    if (res > maxResult) {
                        similar = dbMolecule; // save the similar molecule
                        maxResult = res;
                    }
                }
            }
        }

        return similar;
    }


    /**
     * Delete a given molecule if it is in the database
     */
    public boolean deleteMolecule(Molecule molecule){
        // Retrieve the partitioned array list based on the number of atoms
        int numAtoms = molecule.getNumAtoms();
        if (!db.containsKey(numAtoms)) {
            return false;
        }
        ArrayList<Molecule> moleculesWithSameNumAtoms = db.get(numAtoms);

        // Iterate through the array list of molecules with the same number of atoms
        for (Molecule dbMolecule : moleculesWithSameNumAtoms) {
            Molecule result = dbMolecule.areMoleculesEqual(molecule);
            if (result != null) {
                this.db.get(numAtoms).remove(dbMolecule);
                if( this.db.get(numAtoms).size()==0 )
                {
                    this.db.remove(numAtoms); //prevents array index error when key becomes empty
                }
                return true; // successfully delete the molecule
            }
        }
        return false; // Return false if molecule not in database
    }

    /**
     * Find subgraph
     */
    public ArrayList<Molecule> findSubgraph(Molecule molecule) {
        ArrayList<Molecule> returnList = new ArrayList<Molecule>();
        int startingNumber = molecule.getNumAtoms();
        for(int ii : db.keySet()) {
            if (ii >= startingNumber) {
                for(Molecule m: db.get(ii)) {
                    if(m.isSubGraphPresent(molecule) != null) {
                        returnList.add(m);
                        outputTextArea.append(m.moleculeName + "\n\n");
                    }
                }
            }
        }

        return returnList;
    }

    /**
     * Download Molecules from Pubchem in range [start, end]
     */
    public void downloadPubChem(String start, String end) {
        String scriptPath = "testcases/downloadPubChem.py";
        List<String> filenames = new ArrayList<>();

        try {
            // build Python script call
            ProcessBuilder builder = new ProcessBuilder("python", scriptPath, start, end);
            Process process = builder.start();

            // get Python filename output
            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

            String file;
            while ((file = reader.readLine()) != null) {
                outputTextArea.append("file created: " + file + "\n\n");
                filenames.add(file);
            }

            // add created files to the database
            for (String filename : filenames) {
                this.addMolecule(new Molecule(filename));
            }

            outputTextArea.append("Download complete!" + "\n\n");

        } catch (Exception e) {
            outputTextArea.append("Error downloading from PubChem" + "\n\n");
        }
    }

    /**
     * Add all molecules from a specified folder
     */
    public void addMultipleMolecules(String path) {
        File directory = new File(path);
        // Check if the directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            outputTextArea.append("Invalid directory: " + path + "\n\n");
            return;
        }
        // Get list of files in the directory
        File[] files = directory.listFiles();
        if (files == null) {
            outputTextArea.append("No files found inside directory: " + path + "\n\n");
            return;
        }
        // Iterate over the files in the directory
        for (File file : files) {
            // Check if the file is a text file
            if (file.isFile() && file.getName().endsWith(".txt")) {
                addMolecule(new Molecule(file.getAbsolutePath()));
            }
        }
        outputTextArea.append("Complete adding all molecules from directory!" + "\n\n");
    }

    /**
     * Save database to file system
     */
    public void save(String filename) throws IOException {
        FileOutputStream fileOutStream = new FileOutputStream(filename);
        ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
        objOutStream.writeObject(this.db);
        objOutStream.close();
        fileOutStream.close();
    }

    /**
     * Load database from file system
     */
    public void load(String filename) throws IOException {
        FileInputStream fileInStream = new FileInputStream(filename);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
        try {
            this.db = (HashMap<Integer, ArrayList<Molecule>>) objInStream.readObject();
            outputTextArea.append("Database loaded successfully." + "\n\n");
        } catch (IOException | ClassNotFoundException e) {
            outputTextArea.append("Error loading database: " + e.getMessage() + "\n\n");
        }
        objInStream.close();
        fileInStream.close();
    }

}
