# Accepted feedback: Changes that were proposed in peer review and they you ended up adopting, together with a brief explanation why you chose to accept them.

**Change 0:** 
Previously, the file chooser always opened the home folder instead of the last accessed folder. We updated the file chooser to remember the previously selected folder, allowing users to revisit it without needing to navigate again. We acknowledged this feedback as navigating to a working directory each time users reselected a file could be cumbersome.

**Change 1:** 
We implemented a user-friendly terminal output message when the GUI encounters a molecule not found in the PubChem database or misspelled, rather than displaying the entire stack trace. We incorporated this feedback to provide users with simpler error messages.

**Change 2:** 
One student suggested adding a feature allowing users to add multiple molecules from a specified directory. We agreed with this suggestion as it would enhance convenience compared to adding molecules one file at a time. As a result, we introduced a new command/button that enables users to specify a folder, prompting the program to add all molecules within that folder to the database. 

**Change 3:** 
Multiple students noted that downloadPubChem was not working, which came as an oversight due to the Lab Computer’s default outdated version of Python (2.7.5). Since the function relies on a script that requires Python3, it did not run correctly without updating the Python version. This functionality is restored via the command $ `module load anaconda/3`, which loads Python 3.7.10. Additionally, this module comes with networkx installed, therefore removing the pip install requirement.

**Change 4** 
One student suggested adding a delete feature to our database so that a user can remove duplicates or any other unwanted molecules. We agreed that this is an obvious feature that our database should have and as a result we implemented a delete button to our GUI and a delete function to the command line interface. A user needs to specify a file path to an unwanted molecule file and then will have the ability to remove that molecule from the database.

# Rejected feedback: Changes that were proposed in peer review and they you ended up not adopting, together with a brief explanation why you chose to reject them.


**Change 0:** 
One reviewer suggested simplifying the search process by requesting only the molecule name instead of the entire file path to the molecule text file. They proposed implementing backend functionality to automatically retrieve the text file from the Molecules directory. We are hesitant to implement this feedback because we want users to have the flexibility to add molecule files from any location on the machine.

**Change 1:** 
One comment mentioned that the `--makeManySimple` command takes too long to generate the protein files on lab computers. Optimizing the command is not a high priority, and there is very little that can be done to make the lab computers run faster. Therefore, the comment is rejected.


# Other changes: Changes that you implemented, but were not mentioned in peer review, together with a brief explanation for why you implemented them.
**Change 0:** 
We revised our INSTALL.txt and README.md to improve clarity and understanding.

**Change 1:** 
After testing with an extensive variety of molecules, we identified that `--findMolecule` misrepresented two molecules as equal, so we improved the comparison logic to perform a more extensive search. The test cases labeled TestSame1.txt and TestSame2.txt have identical atoms, but the order of connection is different. Our database should detect this. This is currently an unimplemented feature under the branch “improve-isomorphism”.

**Change 2:** 
We have incorporated GUI buttons for `Make Simple Molecules`, `Make Complex Molecules`, and `Add Proteins`. These buttons enable users to execute the respective functionalities previously exclusive to the command line interface.

**Change 3:** 
A dedicated branch, “23-database-partition”, is created for implementing a partitioned database. The purpose is to solve the out-of-memory error caused by adding a very large number of molecules and/or very complex molecules. It features partitioning by the ratio of Carbon and Hydrogen atoms to all atoms in the molecule, and keeping a number of partitions in the memory and swapping out old ones as needed.

