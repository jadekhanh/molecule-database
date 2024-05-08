import molecule_input as mi
import random
import requests
import time

MIN_ISOMER_COUNT = 100

MAX_CID_SEGMENT_LEN = 4000  # maybe 3997
MAX_MOLECULE_COUNT = 10_000_000

TITIN = "C169719H270466N45688O52238S911"

C_count_max = 100
C_count_min = C_count_max // 2

H_C_ratio_max = 48 / 25
H_C_ratio_min = 17 / 15

N_C_ratio_max = 4 / 13
N_C_ratio_min = 0

O_C_ratio_max = 2 / 5
O_C_ratio_min = 0


def add_atom(atom: str, C_count: int, ratio_max: float, ratio_min: float):
    ret = ""
    ratio = ratio_min + random.random() * (ratio_max - ratio_min)
    count = int(C_count * ratio)
    if count > 1:
        ret += atom + str(count)
    elif count == 1:
        ret += atom
    return ret


def wait_for_api_ready(previous_time):
    if previous_time != None:
        while time.time() - previous_time < 0.2:
            pass
    return time.time()


if __name__ == "__main__":

    previous_time = None

    previous_formulae = [""]
    molecule_count = 0
    while molecule_count < MAX_MOLECULE_COUNT:

        molecular_formula = ""
        while molecular_formula in previous_formulae:
            molecular_formula = ""
            C_count = random.randint(C_count_min, C_count_max)
            molecular_formula += "C" + str(C_count)
            molecular_formula += add_atom("H", C_count, H_C_ratio_max, H_C_ratio_min)
            molecular_formula += add_atom("N", C_count, N_C_ratio_max, N_C_ratio_min)
            molecular_formula += add_atom("O", C_count, O_C_ratio_max, O_C_ratio_min)

        print(molecular_formula)

        url = (
            "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/fastformula/"
            + molecular_formula
            + "/cids/JSON"
        )
        previous_time = wait_for_api_ready(previous_time)
        response = requests.get(url)
        if response.status_code != 200:
            print("Failed to retrieve the page. Status code:", response.status_code)
            continue

        data = response.json()
        if "Fault" in data:
            print(molecular_formula + ": " + data["Fault"]["Message"])
            previous_formulae.append(molecular_formula)
            continue

        cids = data["IdentifierList"]["CID"]
        isomer_count = len(cids)
        if isomer_count < MIN_ISOMER_COUNT:
            # print(molecular_formula + ": Has only " + str(isomer_count) + " isomers")
            previous_formulae.append(molecular_formula)
            continue

        cid_segment = str(cids[0])
        cid_idx = 1
        while cid_idx < isomer_count:
            cid = cids[cid_idx]
            while len(cid_segment + "," + str(cid)) < MAX_CID_SEGMENT_LEN:
                cid_segment += "," + str(cid)
                cid_idx += 1
                cid = cids[cid_idx]
            url = (
                "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/"
                + cid_segment
                + "/property/Title,CanonicalSMILES/json"
            )
            previous_time = wait_for_api_ready(previous_time)
            response = requests.get(url)

            if response.status_code == 200:
                data = response.json()
                for molecule_struct in data["PropertyTable"]["Properties"]:
                    if "CanonicalSMILES" in molecule_struct:
                        molecule_name = (
                            molecular_formula + "_" + str(molecule_struct["CID"])
                        )
                        smiles = molecule_struct["CanonicalSMILES"]
                        if (
                            mi.writeMolecule(molecule_name, smiles, "simple_molecules")
                            == 0
                        ):
                            molecule_count += 1
                            mi.writeIsomorphic(
                                molecule_name, smiles, "simple_isomorphic"
                            )
            else:
                print("Failed to retrieve the page. Status code:", response.status_code)

            cid_segment = str(cids[cid_idx])
            cid_idx += 1

    print("goodbye")
