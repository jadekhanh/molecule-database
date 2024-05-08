# Testcases Directory

This directory should be used to generate test cases for the Molecule Database. `downloadPubChem.py` is a script used by the Molecule Database class in order to directly download chemical compounds from PubChem and should not be run by the user. `molecule_input.py` is a user-operated script that allows the user to generate input files based on user needs. 

In `molecule_input.py`, molecule input files can be generated from User-specified SMILES strings or from the Pub Chem database, and they can either be standard or isomorphic. PubChem chemicals are requested from their PUG REST API by their Compound ID (CID) and the response is in the form of a JSON file, which contains the Title of the compound as well as their representative SMILES String. An example can be found [here](https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/1/property/Title,CanonicalSMILES/json). When selected, generated isomorphic input files will have their atoms and edgelist representations scrambled from the standard order, in order to accurately test the find functions in the Molecule Database.

## Instructions:
1) In this directory, run `python molecule_input.py`
2) Follow the prompts in the CLI.
3) The generated molecules will be created in `./molecules` or `./isomorphic_test`, based on whether the selected file type is standard or isomorphic.

## Notes
- Any SMILES strings with aromatic bonds (1.5 order) will be ignored by the script.
- If the provided PubChem index does not have a provided `Title` in its JSON file, that Compound will be ignored by the script.

## Dependencies:
- [pysmiles](https://github.com/pckroon/pysmiles)
- [NetworkX](https://networkx.org/documentation/latest/index.html)

### Requirements
`$ pip install --user networkx[default]`
