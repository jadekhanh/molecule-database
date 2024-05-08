package edu.bu.ec504.project;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Molecule> moleculeArrayList = new ArrayList<Molecule>();
        final String filename="example.txt";
        File directory = new File("Molecules");
        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            // Get list of files in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        moleculeArrayList.add(new Molecule(file.getPath()));
                    }
                }
            }
        }
        System.out.println(moleculeArrayList.size());
        Molecule tester = new Molecule(filename);

        Molecule res=null;
        int similar=0;
        for(Molecule mol: moleculeArrayList)
        {
            int temp=mol.mostSimilar(moleculeArrayList.get(2));

            System.out.println(mol.moleculeName+" has "+ temp + " points");
            if(temp>similar)
            {
                similar=temp;
                res=mol;
            }
        }
        System.out.println("Most similar is "+ res.moleculeName);


        for(Molecule f: moleculeArrayList) {
            Molecule testing = new Molecule(filename);
            if (moleculeArrayList.get(0).areMoleculesEqual(testing) == null) {
                System.out.println("err");
            }
        }
    }

}
