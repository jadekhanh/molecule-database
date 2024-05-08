# Group4: Molecule Database

# Description: 

A database of molecules that are entered in the format: 

[MOLECULE NAME]

[# OF VERTICES]

[LABEL OF VERTEX ID 0]

[LABEL OF VERTEX ID 1]

[LABEL OF VERTEX ID 2]

...

[LABEL OF VERTEX ID V]


Followed by edges 


[VERTEX ID a] [VERTEX ID b]


Here is an example molecule1.txt:


ethanol

9

C

C

O

H

H

H

H

H

H

0 1

0 3

0 4

0 5

1 2

1 6

1 7

2 8

**Group 4**

**Group members:** 
Hyunsoo Kim, Caelan Wong, Phuong Khanh Tran, Tristen Liu, Jason Calalang


# Project Implementation:

We decided to design the back end of the database storage with the main structure of a large HashMap, with an integer key and ArrayList of molecules as the value. Each key belongs to an array list containing all molecules with the same number of atoms. This feature simplifies future heuristics and is easily quantifiable from the input text file. The Molecule class itself contains its name, the number of atoms, the number of edges, and the count of each element. The count of each element is an array of integers where the index represents the atomic number, and the value is the count. For example, a molecule consisting of only 3 carbons and 2 hydrogen atoms would be represented with values 3 at index 6 and 2 at index 1. The rest of the array would be 0. 

The Molecule class would also have an array list of the Atom class. Each atom has its own unique name for identification, its atomic number, its degree, and a HashMap of all the atoms it is connected to. To represent connections, the key to the HashMap is the name of the connected atoms, and the value is a pair made of an atomic number and bond order. Bond order simply notes how many edges are between two specific atoms. Molecules are treated as undirected, so any two connected atoms will have an edge pair listed twice, once in each atom’s connected list. We keep track of specific features, like degree and element count, to aid comparison when specific molecules are queried. 

Since isomorphic molecules are equivalent to each other, we must test for isomorphism in each molecule. To save time on testing isomorphism for each molecule, we simply do not consider molecules with more or fewer atoms and those with different types of atoms and the number of edges. However, if these heuristics are all equal, we must perform an expensive test to see if the two molecules are isomorphic.


# Implemented features


**Can hold 10,000 molecules**

**Percentage:** Minimum Requirement

**How it was implemented:**

As detailed in the Project Implementation section, our molecule database is built using a HashMap:
public HashMap<Integer, ArrayList<Molecule>> db
In this structure, each integer key corresponds to an ArrayList containing molecules with the same number of atoms. This structure allows our database to efficiently manage up to 10,000 molecules if needed.



**Efficiently searches for a molecule up to graph isomorphism**

**Percentage:** Minimum Requirement

**How it was implemented:** 

Our database utilizes a hashmap-based organization where each key corresponds to the number of atoms in a molecule. The hashmap is structured so molecules with the same number of atoms are grouped together in an ArrayList, stored as the value for each key in the hashmap.  We were able to take advantage of this when searching for a molecule. We begin by examining the number of atoms we are searching for in the molecule. If no molecules are stored at the corresponding key in the database, we immediately know that the molecule is not present. If molecules are stored in this key of the hashmap, we only have to focus on these.

Next, we can analyze the specific characteristics of both the target molecule and each molecule sharing the same count of atoms. We use the function areMoleculesEqual() to compare the molecule we are searching for with the other molecules individually. Each molecule has an array, numElements, of size 118, where each index corresponds to an atomic number of an element. Within this array, each index holds a count representing the number of occurrences of each element in the molecule. We compare the numElements arrays of both molecules, and we can determine if they are not the same and if there are any inconsistencies. Afterward, we verify that each molecule's total number of edges matches. Following this comparison, we proceed with a deeper examination of each atom in both molecules. For every atom in the first molecule, we ensure a corresponding atom exists in the second molecule. 

We compare the atoms by looking at their atomic number, degree of edges, and bonds. We ensure that each bond of the atom is exactly the same as the bonds of the atom in the second molecule. If, at any point, inconsistencies between the two molecules arise, we conclude that the molecules are not identical. The function will only return that a molecule has been found in the database if it satisfies all the tests.





**Command-line User Interface**

**Percentage:** Minimum Requirement

**How it was implemented:**

To execute the program via the command-line interface, navigate to the directory where the md file is located. Once in the directory, users can run one of the following commands. Please ensure that the file or folder path provided is the complete path to the selected file or folder.

./md --addMolecule [FILE PATH]: This command adds the molecule specified in the file path to the database. For example, to add biotin from the file biotin.txt to the database, run: ./md --addMolecule /path/to/directory/biotin.txt. 

./md --addBulk [FOLDER PATH]: This command allows users to add multiple molecules in bulk from a specified folder. For example, to add all molecules inside the Molecules folder, simply run: ./md --addBulk path/to/Molecules.

./md --delete [FILE PATH]: This command deletes the molecule specified in the file path to the database. For example, to delete biotin from the file biotin.txt, run:./md --delete /path/to/directory/biotin.txt

./md --findMolecule [FILE PATH]: This command searches for the isomorphic molecule specified in the file path within the database. For instance, to find an isomorphic molecule to biotin within the database using the file biotin.txt, run: ./md --findMolecule /path/to/directory/biotin.txt. If the program finds an exact match, it will display “FOUND.” If not, it will show “NO EXACT MATCH FOUND” and return the most similar molecule to the given molecule within the database.

./md --findSubgraph [FILE PATH]: This command finds and outputs all molecules containing a subgraph provided in the file path. If no subgraph is found, the program outputs "No subgraph found."

./md --downloadPubChem start,end: This command downloads molecules from the PubChem database. "start" and "end" are indices or Compound ID (CID) numbers of the molecules in the PubChem database. For example, to download molecules 20-27, type: ./md --downloadPubChem 20,27

./md --printDb: This command prints the list of molecules inside the database and their number of atoms.

./md --verbose: Upon entering this command, all subsequent commands will display additional information about the database. If the user runs this command again, subsequent commands will not output additional information.

./md --quit: This command exits the program. Upon exiting, the molecule database is automatically saved in the project folder as molecule.db, and a confirmation message is displayed in the command interface. When the program is relaunched, the database is loaded, allowing the user to resume working with the previously saved data.

./md --printName: This command prints the name of the database.

./md --makeManySimple: This command generates 10 million molecule files, each having between 52 and 136 atoms. Generated molecules are saved in a folder called `simple` which is located in the parent directory of the project folder. Users do not need to specify a file or folder path for this feature.

./md --makeFewComplex: This command creates 10,000 million molecule files, each with over 10,000 atoms. Generated molecules are saved in a folder called `complex` that is located in the parent directory of the project folder. Users do not need to specify a file or folder path for this feature.

./md --addProteins [FILE PATH]: This command adds proteins created by the `--makeManySimple` and `--makeFewComplex` commands. Please make sure to select a file path, which is either the `complex` or `simple` directory.

./md --marco: This command pings the server to check if it is still alive.

The Main.java class, which facilitates the command-line interface, also includes a client-server connection feature. When the program is executed, it first attempts to determine whether it can function as a client or server and establishes connections accordingly.



**Stand-alone GUI**

**Percentage:** 15%

**How it was implemented:**

The graphical user interface (GUI), constructed using the built-in Java Swing JFrame class, facilitates program interactive operations. To launch the GUI, simply click "Run" on the GUI.java file if using an Integrated Development Environment (IDE) like IntelliJ. If using the terminal, at the group4 directory, please run the following command: ./gui. Upon initialization, the GUI presents 12 buttons for user interaction, accompanied by a sizable output area to display program results. Here are the functionalities of each button:

Choose File/Folder: Initiates a window allowing users to select a molecule file or folder for processing. Upon selection, the chosen path is displayed in the designated field.

Add Molecule: This button allows users to add a molecule to the database. The program reads the file specified by the user and adds the molecule to the database. Upon successful addition, a message "Molecule added: [molecule name]" appears. This button calls the function `addMolecule()` to add the molecule to the database.

Delete Molecule: This button allows users to delete a molecule from the database. The program reads the file specified by the user and deletes the molecule from the database. Upon successful deletion, a message “Successfully Deleted” appears. If the molecule was already gone from the database, the message “Molecule not in the database” appears. This button calls the `deleteMolecule()` function to delete.

Find Molecule: Users can select a molecule file and click this button to search for an isomorphic molecule in the database. Once the button is clicked, the findMolecule() method is called to handle the request. The GUI displays a "FOUND" message if an isomorphic molecule is found. If not found, it shows "NOT FOUND" and the program will call `similarMolecule()` method to return the name of the most similar molecule in the database.

Find Subgraph: To find a subgraph, the user selects a file containing the desired subgraph and then clicks on this button to initiate the search for all molecules containing the provided subgraph. Upon clicking the button, the GUI activates the `findSubgraph()` method to execute the operation.

Display Molecule: Similarly, users can select a molecule file and click this button to view the 2D Lewis structure of the molecule in a separate pop-up window. Note that the file must be in the correct format, and the molecule must be registered in the PubChem database for viewing. This button makes use of the following API URL that returns the image of the molecule: https://cactus.nci.nih.gov/chemical/structure/"molecule name"/"representation", where molecule name is the name of the molecule and representation is the desired returning format [1]. Once the button is clicked, the GUI reads the file to extract the molecule's name and creates the URL that can return the Lewis structure image of the molecule.

Download PubChem: Users can specify a range of CID indices (start, end) in the “Start,End CID Index” input section to download molecules from the 

PubChem database. For example, type 14,16 in the input section and click this button to download molecules 14-16. Please note that certain molecules without a title name or with non-integer bond orders, as indicated in the PubChem database, will be skipped during the download. Upon clicking this button, `downloadPubChem.py` will be called to handle the download operation.

Database Statistics: Clicking this option prints database statistics, including the total number of molecules, a list of molecules with their names and the number of atoms, and the names of the smallest and largest molecules in the database. This button activates the printDb() method inside the MoleculeDatabase class, which is responsible for the printing executions. 

Add Multiple Molecules: Users can select a folder containing multiple molecule text files and click this button to add all molecules in the specified folder to the database. This button invokes the `addMultipleMolecules()` method.

Make Simple Molecules: Users can click this button to generate 10 million molecule files, each having between 52 and 136 atoms. Please monitor the terminal output for progress updates. Molecules are saved in folder named `simple` that is located in the same directory as the project folder. This button calls the `manySimpleProteins()` function. 

Make Complex Molecules: Users can click this button to create 10,000 million molecule files, each with over 10,000 atoms. Please monitor the terminal output for progress updates. Molecules are saved in `complex` folder which is located in the same directory as the project folder. Upon clicking this button, `fewComplexProteins()` will be called. 

Add Proteins: Users can add protein molecules to the database after creating them from the `Make Simple Molecules` and `Make Complex Molecules` buttons. Please make sure to choose a file path, either from `complex` or `simple` folders, as indicated above. 

When the GUI is closed, the program automatically saves the working database as molecule.db in the same project folder. Upon reopening, the database loads automatically, and a message confirming successful loading is displayed.




**Downloads known compounds from an existing database (1,000)**

**Percentage:** 15%

**How it was implemented:**

The PubChem database is supported by a Power User Getaway (PUG) REST-style API, providing simple access to their database from third-party scripts. Specifically, our implementation utilizes the Compound ID (CID) to submit an HTTP request to “https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/1/property/Title,CanonicalSMILES/json”, and receives a JSON response with the CID, compound Title and Canonical SMILES representation of the compound. This request is carried out in a Python script, with supporting libraries such as PySMILES to read SMILES strings and NetworkX for molecular graph representation [2].

The script parses the JSON response for the compound’s CID, Title, and SMILES and generates valid molecule inputs for the database. PySMILES provides a helper function, read_smiles, which generates a NetworkX graph object of the molecule. Then, NetworkX is used to write an edge list for the graph, and metadata such as the Title of the compound, the number of atoms, and the atom names are placed in front of the edge list. This constitutes a valid molecule input format for our database, which is then saved as a *.txt file with the name [“molecule” + CID + “.txt”]. When interpreting SMILES Strings, any SMILES containing non-integer bond values (e.g., 1.5) will be ignored. Additionally, any PubChem CIDs without a Title index will also be ignored.

Two scripts are provided for generating input files, one that is meant to be called by the Main.java class (`downloadPubChem.py`) and one that is interactable through a CLI (molecule_input.py). The Main.java class provides a range of CIDs that are automatically inserted into the database with the command --downloadPubChem start,end. When utilizing the user-intended command-line interface, several other functions are also provided, such as generating input files based on a user-provided SMILES String and generating isomorphic test files to test the `--findMolecule` command. The user is prompted to input what functions they would like to use, and the generated molecule input files are created in their respective folders. 




**Implementation handles core operations on over 10 million molecules at a rate of 10 ops/sec**

**Percentage:** 30%

**How it was implemented:**

10 million molecules are unavailable in the PubChem database or any readily accessible database. Therefore, a `ProteinFactory` class is created to procedurally generate unique proteins from a set of amino acids. These proteins are saved to a designated location in the file system in the same format as the user input files, which then can be added to a database with the `--addProteins` command.

Because saving 10 million files in a single directory is too demanding, 100 child directories are created, each containing 100,000 protein files. The command for generating 10 million protein files is `--makeManySimple`, and the default location is ../simple. The reason for traveling to the parent directory is to hide the files from the IDE in use, which may throw an error in an attempt to index the files.

The number of atoms in each protein is 52 at minimum to ensure the uniqueness of each protein. The number of atoms in each protein is and 136 at maximum because no more is necessary to generate 10 million unique proteins.

The feature is not complete at the current time because the database and molecules' data structures are too memory-intensive. Adding 10 million molecules to the database simply did not succeed before the Java software crashed from an OutOfMemory error.

Dividing the database into multiple partitions is a solution in plan. They will be accompanied by a file with bloom filters, one for each partition. Before a partition is loaded, its bloom filter will be tested to rule out partitions that cannot contain the molecule. A canonicalization algorithm may be necessary to enforce a unique representation of isomorphic molecules. On the other hand, having multiple separate databases will be friendly to parallelization.




**Handles core operations on 10,000 complex molecules, each with over 10,000 atoms (rate of 10 ops/sec)**

**Percentage:** 30%

**How it was implemented:**

Molecules comprised of over 10,000 atoms are not available in the PubChem database, or in any readily accessible database. Therefore, a `ProteinFactory` class is created to procedurally generate unique proteins from a set of amino acids. These proteins are saved to a designated location in the file system, in a same format as the user input files, which then can be added to a database with the `--addProteins` command.

Because saving 10,000 protein files with over 10,000 atoms each in a single directory is too demanding, 10 child directories are created, which contains 1,000 protein files each. The command for generating 10 million protein files is `--makeFewComplex` and the default location is ../complex. The reason for traveling to the parent directory is to hide the files from the IDE in use, which may throw an error in an attempt to index the files.

The feature is not complete at the current time, because the data structures of the database and the molecules are too memory intensive. Adding 10,000 molecules with 10,000 atoms each to the database simply did not succeed before the Java software crashed from an OutOfMemory error.

Dividing the database into multiple partitions is a solution in plan. They will be accompanied by a file with bloom filters, one for each partition. Before a partition is loaded, its bloom filter will be tested to rule out partitions that cannot contain the molecule. Some kind of canonicalization algorithm may be necessary to enforce a unique representation of isomorphic molecules. On the other hand, having multiple separate databases will be friendly to parallelization.




**Searches for the most similar molecule to a given molecule if no exact match exists.**

**Percentage:** 30%

**How it was implemented:**

The process for finding the most similar molecule is only initiated when the `findMolecule()` returns null, indicating that the molecule is not in the database. It is implemented using a point system to keep track of similarities between molecules. The molecule that has the highest similarity score is the molecule that is deemed the most similar. 

Initially, the method looks at the numElements array of both molecules, which shows the count of each element in the molecule. A similarity point is added for each atom of the same element that they have in common. For example, if the first molecule has 2 Hydrogens, 4 Carbons, and 1 Nitrogen, and the second molecule has 1 hydrogen and 5 carbons, then 1 similarity point will be added for the hydrogen, and 4 points will be added from the carbons. 

Subsequently, points will be added if the molecules have the same number of atoms or edges. Following this, the molecules will be compared based on similar edges that they have between atoms. This aspect of the method employs a similar heuristic to the `AreMoleculesEqual()` method. 

Each edge originating from each atom in the first molecule is compared to each edge in the second molecule. If the method finds an edge that exactly matches the edge in the second molecule, then a point is added, the edge is marked as counted for, and the associated atom is marked as seen. The edges are considered identical if the elements involved and the degree of the edge (e.g., single bond, double bond, triple bond) are the same.

Given that our molecule database is organized as a HashMap where the value for each key is an ArrayList of molecules that have the same number of atoms as the key, we opted to compute the similarity score only for molecules with a number of atoms within 100 of the molecule we are searching for. This decision stems from the anticipation that molecules with a significantly larger or smaller number of atoms will exhibit substantial differences. By limiting the number scope of molecules considered for similarity scoring, we optimize efficiency when there is a wide variance in the number of atoms among the molecules in our database.





**Subgraph search (finds all molecules that contain the provided subgraph)**

**Percentage:** 30%

**How it was implemented:**

Subgraph search was implemented using the same heuristics as `findMolecule()`, where certain qualities of the molecule were used to eliminate any possible matches. If the subgraph contained more atoms or more of each type of element than the target molecule, the target molecule was no longer considered. The number of edges was also a factor. Once these preliminary tests were met, Each atom was tested so that there was at least one candidate in the target molecule. For example, if a carbon atom was connected to two hydrogen atoms with single bonds in the subgraph, then at least one carbon atom must be connected to at least two hydrogen atoms with a single bond. The candidates are stored on a HashMap, with the subgraph’s atom acting as a key and an array list of candidate atoms as the value. After all candidates are found, a linked list traverses through the subgraph, choosing one of the candidates to pair with and adding neighbors similar to Breadth First Search. If no available candidates are left to choose from during traversal, the linked list will traverse backward through its parent, choosing a different candidate as an option. If the linked list attempts to traverse on the head, that means that no subgraph exists in the molecule. If the subgraph is traversed through the entire linked list, however, this means that the subgraph does exist in the target molecule. This process is repeated for every possible molecule in the database.

# Changes from Initial Project Defense

We decided not to use the VF2++ algorithm or any database-implementing techniques stated in the Initial Project Defense because we found a sufficient way to build our molecule database using simple data structures such as HashMaps, LinkedLists, and ArrayLists. We also use the same data structures to implement search algorithms for finding isomorphic molecules and similar subgraphs.

# References
[1] “NCI/CADD Chemical Identifier Resolver.” Accessed: Apr. 29, 2024. [Online]. Available: https://cactus.nci.nih.gov/chemical/structure

[2] P. C. Kroon, “pckroon/pysmiles.” Apr. 29, 2024. Accessed: Apr. 29, 2024. [Online]. Available: https://github.com/pckroon/pysmiles

# Code

Link to branch with all complete Java 17 code (master): https://agile.bu.edu/gitlab//ec504/ec504_projects/group4

Link to all data necessary for project to run (drive link or repo link): None

Link to a folder containing all testing code utilized to observe the correctness of your code: None

# Work Breakdown
Hyunsoo Kim implemented the Main.java, the MoleculeDatabase.java, and the `ProteinFactory.java`. In addition, Hyunsoo worked to implement a partitioned database scheme with manual memory management,  Hyunsoo helped discover useful PubChem APIs and put together testing and benchmarking suite, and also contributed to the README.md file. 

Caelan Wong implemented the `mostSimilar()` method in Molecule.java and MoleculeDatabase.java to run whenever `findMolecule()` returns null. Also, Caelan helped with the early implementation of the `addMolecule()` method and created the `PeriodicTable.java` enum. In addition, Caelan implemented the `deleteMolecule()` function in the GUI and command line interface. Lastly, Caelan helped with the README.md.

Phuong Khanh Tran helped implement `MoleculeDatabase.java`, which initializes the database, designed `GUI.java`, which constructs the graphical user interface, and coded `MDB.java`, which creates the database that can work with the GUI. Additionally, Phuong contributed to writing the README.md and INSTALL.txt files.

Tristen Liu implemented the testcases directory providing test input files in order to test the basic functionalities of the Molecule Database, as well as the `downloadPubChem()` function that is used in order to automatically download compounds into the database. They also contributed to the README.md file. 

Jason Calalang implemented the `findSubgraph()` method in MoleculeDatabase.java and Molecule.java. They also helped implement the early design of the addMolecule() method and the logic behind the atom comparison in `AreMoleculesEqual()`. They also contributed to the README.md file.


All members signed this README.md.
