from pysmiles import read_smiles
import networkx as nx
import random
import requests
import os
import time

# returns the order number of the edge that appears using nx.edge_list
def getOrder(line):
    # only allow integer bond order values
    order = line.split("{'order': ")[1].rstrip('}\n')
    return int(line.split("{'order': ")[1].rstrip('}\n')) if order.isdigit() else -1

# multiplies the edge pair by the order number
def processContent(content):
    new_content = ""
    for line in content:
        # check if the line contains bond order information
        if "{'order':" in line:
            bond_order = getOrder(line)
            # if bond order is invalid, then return error
            if bond_order == -1:
                return -1
            
            # duplicate the edge for value of bond order
            vertex_pair = ' '.join(line.split()[:2])
            for i in range(bond_order):
             new_content += vertex_pair + '\n'
        else:
            new_content += line
    return new_content


def scrambleLabels(graph):
    # create a mapping from old labels to new labels
    nodes = list(graph.nodes())
    shuffled_nodes = nodes.copy()
    random.shuffle(shuffled_nodes)
    new_map = dict(zip(nodes, shuffled_nodes))
    
    # use the mapping to generate a new graph with shuffled labels
    scrambled_graph = nx.relabel_nodes(graph, new_map)
    return scrambled_graph

def writeMolecule(mol_name, smiles, folder_name="molecules"):
    mol_with_H = read_smiles(smiles, explicit_hydrogen=True)
    file_path_mol = "./" + folder_name + "/" + mol_name + ".txt"
    
    nx.write_edgelist(mol_with_H, file_path_mol)
    
    # write metadata for mol
    nodes = mol_with_H.nodes(data='element')
    mol_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
      mol_data += node[1] + '\n'
      
      
    # read the generated edgelist
    with open(file_path_mol, 'r') as file:
        edgelist = file.read()
    
    # if theres an invalid edgelist, remove it from the file system
    modified_edgelist = processContent(edgelist.split('\n'))
    if modified_edgelist == -1:
        os.remove(file_path_mol)
        return -1
        
    # write metadata at the beginning and the original content
    with open(file_path_mol, 'w') as file:
        file.write(mol_data + modified_edgelist)
    
    return 0

def writeIsomorphic(mol_name, smiles, folder_name="isomorphic_test"):
    iso_graph = scrambleLabels(read_smiles(smiles, explicit_hydrogen=True))
    file_path_iso = "./" + folder_name + "/" + mol_name + "_iso.txt"
    
    nx.write_edgelist(iso_graph, file_path_iso)
    
    # write metadata for isomorphic mol
    nodes = sorted(iso_graph.nodes(data="element"))
    iso_data = mol_name + '\n' + str(len(nodes)) + '\n'
    for node in nodes:
        iso_data += node[1] + '\n'
        
    with open(file_path_iso, 'r') as file:
        isolist = file.read()
        
    modified_isolist = processContent(isolist.split('\n'))
    if modified_isolist == -1:
        os.remove(file_path_iso)
        return -1
    
    with open(file_path_iso, 'w') as file:
        file.write(iso_data + modified_isolist)
        
    return 0

def writeSMILES(smiles, mol_name, files):
    if files == 'B':
        if writeMolecule(mol_name, smiles) == 0:
            writeIsomorphic(mol_name, smiles)
    elif files == 'M':
        writeMolecule(mol_name, smiles)
    elif files == 'I':
        writeIsomorphic(mol_name, smiles)

def writePubChem(start, end, files):
    MAXSTEP = 100
    INCREMENT = min(end - start, MAXSTEP)
    
    for indx in range(start,end,INCREMENT):
        start_time = time.time()

        numbers = [str(i) for i in range(indx, indx + INCREMENT)]
        indexes = ",".join(numbers)
        
        # query from pubchem URL
        url = 'https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/' + indexes + '/property/Title,CanonicalSMILES/json'
        response = requests.get(url)

        # Check if the request was successful
        if response.status_code == 200:
            # Extract the json from the response
            page_text = response.json()
            
            for chemical in page_text['PropertyTable']['Properties']:
                # check if desired keys are in the json
                if 'CanonicalSMILES' in chemical:
                    mol_name = "molecule" + str(chemical['CID'])
                    smiles = chemical['CanonicalSMILES']
                    # ignore Hydrogen Molecules
                    if smiles == "[HH]":
                        continue
                    
                    print("molecule "+ str(chemical['CID']) + ": " +  mol_name + "\t" + "smiles: " + smiles)
                
                    if files == 'B':
                        if writeMolecule(mol_name, smiles) == 0:
                            writeIsomorphic(mol_name, smiles)
                    elif files == 'M':
                        writeMolecule(mol_name, smiles)
                    elif files == 'I':
                        writeIsomorphic(mol_name, smiles)
        else:
            print("Failed to retrieve the page. Status code:", response.status_code)

        while (time.time() - start_time < 0.3):
            pass

if __name__ == "__main__":
    # Welcome Message
    print("##################################################################################\n"
          "Welcome to the Molecule Input generator. Make sure this file is being run in the ./testcases directory.\n"
          "This script should be used in order to generate the required input files needed to test the main application. \n"
          "Any molecules with 1.5 order bonds will be ignored."
          "Inputs can be created using SMILES Strings (SS) or pulled from the PubChem Chemical API (PC). \n"
          "Both standard molecule and isomorphic molecule input files can be generated.\n"
          "Standard Molecules wil be placed in the ./molecules directory\n"
          "Isomorphic Molecules will be placed in the ./isomorphic_test directory\n"
          "##################################################################################\n")
    
    method = ""
    while True:
        method = input("Select an input method [SS/PC]: ").strip().upper()
        if method in ['SS', 'PC']:
            break
        else:
            print("ERROR: Invalid input. Please enter 'SS' for SMILES String or 'PC' for PubChem Chemical API.")
    
    inputType = ""
    while True:
        inputType = input("Select input types to create [M/I/B(default)]: ").strip().upper()
        if inputType in ['M', 'I']:
            break
        elif inputType == '':
            inputType = 'B'
            break
        else:
            print("ERROR: Invalid input type. Please enter 'M' for Molecule, 'I' for Isomorphic, 'B' for Both.")
            
    if method == 'SS':
        smiles = input("Enter the SMILES String: ")
        mol_name = input("Enter the name of the molecule: ")
        writeSMILES(smiles, mol_name, inputType)
    elif method == 'PC':
        while True:
            id_range_input = input('Enter the Chemical ID Range to grab in the format "start end": ').strip()
            try:
                start, end = map(int, id_range_input.split())
                if start >= end:
                    print("ERROR: The start value must be less than the end value. Please enter a valid range.")
                    continue
                if start < 0:
                    print("ERROR: Neither value can be less than zero. Please enter a valid range.")
                    continue
                writePubChem(start, end + 1, inputType)
                break
                
            except ValueError:
                print("Invalid range format. Please enter two integers in the format 'start, end'.")
