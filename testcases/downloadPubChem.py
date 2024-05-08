from pysmiles import read_smiles
import networkx as nx
import random
import requests
import os
import time
import sys

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

def writeMolecule(chemID, mol_name, smiles):
    mol_with_H = read_smiles(smiles, explicit_hydrogen=True)
    file_path_mol = "./testcases/molecules/molecule" + chemID + ".txt"
    
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
    
    # print filepath to pick up in Java
    print(file_path_mol)
    return 0

def writePubChem(start, end):
    MAXSTEP = 100
    INCREMENT = min(end - start, MAXSTEP)
    # range(start, end, step) --> Change values for number of molecules required
    for indx in range(start,end,INCREMENT):
        start_time = time.time()

        numbers = [str(i) for i in range(indx, indx + INCREMENT)]
        # if HH in numbers:
        #     numbers.remove(HH)
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
                if 'CanonicalSMILES' in chemical and 'Title' in chemical:
                    chemID = str(chemical["CID"])
                    mol_name = str(chemical['Title'])
                    smiles = chemical['CanonicalSMILES']
                    # ignore Hydrogen Molecules
                    if smiles == "[HH]":
                        continue
                    
                    # print("molecule "+ str(chemical['CID']) + ": " +  mol_name + "\t" + "smiles: " + smiles)
                
                    writeMolecule(chemID, mol_name, smiles)
        else:
            print("Failed to retrieve the page. Status code:", response.status_code)

        while (time.time() - start_time < 0.3):
            pass

if __name__ == "__main__":
    # get start and end index from Java call
    start = int(sys.argv[1])
    end = int(sys.argv[2])
    if start >= end or start < 0:
        pass
    else:
        writePubChem(start, end + 1)

    
    
