import java.io.*;
import java.util.ArrayList;

public class ProteinFactory {

    public ProteinFactory() {
    }

    static final AminoAcid ALANINE;
    static final AminoAcid CYSTEINE;
    static final AminoAcid GLYCINE;
    static final AminoAcid LEUCINE;
    static final AminoAcid ISOLEUCINE;
    static final AminoAcid METHIONINE;
    static final AminoAcid PROLINE;
    static final AminoAcid SERINE;
    static final AminoAcid THREONINE;
    static final AminoAcid VALINE;

    static {
        try {
            ALANINE = new AminoAcid("src/amino_acid/Alanine.txt",
                    2, 12, 10, 5, 4);
            CYSTEINE = new AminoAcid("src/amino_acid/Cysteine.txt",
                    2, 12, 10, 5, 4);
            GLYCINE = new AminoAcid("src/amino_acid/Glycine.txt",
                    1, 9, 7, 4, 3);
            LEUCINE = new AminoAcid("src/amino_acid/Leucine.txt",
                    5, 21, 19, 8, 7);
            ISOLEUCINE = new AminoAcid("src/amino_acid/l-Isoleucine.txt",
                    5, 21, 19, 8, 7);
            METHIONINE = new AminoAcid("src/amino_acid/Methionine.txt",
                    5, 19, 17, 8, 7);
            PROLINE = new AminoAcid("src/amino_acid/Proline.txt",
                    5, 13, 16, 3, 7);
            SERINE = new AminoAcid("src/amino_acid/Serine.txt",
                    2, 12, 10, 5, 4);
            THREONINE = new AminoAcid("src/amino_acid/Threonine.txt",
                    3, 15, 13, 6, 5);
            VALINE = new AminoAcid("src/amino_acid/Valine.txt",
                    4, 18, 16, 7, 6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void initAminoAcids() {
        ALANINE.symbol1 = "A";
        CYSTEINE.symbol1 = "C";
        GLYCINE.symbol1 = "G";
        ISOLEUCINE.symbol1 = "I";
        LEUCINE.symbol1 = "L";
        METHIONINE.symbol1 = "M";
        PROLINE.symbol1 = "P";
        SERINE.symbol1 = "S";
        THREONINE.symbol1 = "T";
        VALINE.symbol1 = "V";
    }

    static final int AMINO_ACID_COUNT = 10;

    static public class AminoAcid {
        int atomCount;
        StringBuffer atomBuffer;
        int bondCount = 0;
        ArrayList<Integer> bondList = new ArrayList<>();
        String name;
        String symbol1;
        ArrayList<Integer> termini = new ArrayList<>();
        int terminusC;
        int terminusHN;
        int terminusHO;
        int terminusN;
        int terminusO;

        public AminoAcid(
                String filePath, int terminusC, int terminusHN, int terminusHO, int terminusN, int terminusO)
                throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                name = reader.readLine();
                atomCount = Integer.parseInt(reader.readLine());
                atomBuffer = new StringBuffer(atomCount);
                for (int atomIdx = 0; atomIdx < atomCount; atomIdx++) {
                    atomBuffer.append(reader.readLine());
                }
                String bond;
                while ((bond = reader.readLine()) != null) {
                    String[] atoms = bond.split(" ");
                    bondList.add(Integer.parseInt(atoms[0]));
                    bondList.add(Integer.parseInt(atoms[1]));
                    bondCount++;
                }
            }
            termini.add(terminusHN);
            termini.add(terminusHO);
            termini.add(terminusO);
            termini.sort(null);
            this.terminusC = terminusC;
            this.terminusHN = terminusHN;
            this.terminusHO = terminusHO;
            this.terminusN = terminusN;
            this.terminusO = terminusO;
        }
    }

    static public class Protein {
        Integer atomCount;
        StringBuffer atomBuffer = new StringBuffer();
        ArrayList<String> bondList = new ArrayList<>();
        boolean complete = false;
        String name;
        int terminusC;

        public Protein(AminoAcid aminoAcid) {
            name = aminoAcid.symbol1;

            int min = Math.min(aminoAcid.terminusHO, aminoAcid.terminusO);
            atomBuffer.append(aminoAcid.atomBuffer, 0, min);
            int max = Math.max(aminoAcid.terminusHO, aminoAcid.terminusO);
            atomBuffer.append(aminoAcid.atomBuffer, min + 1, max);
            atomBuffer.append(aminoAcid.atomBuffer, max + 1, aminoAcid.atomCount);

            atomCount = aminoAcid.atomCount - 2;
            int bondCount = aminoAcid.bondCount;
            int terminusHO = aminoAcid.terminusHO;
            int terminusO = aminoAcid.terminusO;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                if (atom0 == terminusHO || atom0 == terminusO) {
                    continue;
                }
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (atom1 == terminusHO || atom1 == terminusO) {
                    continue;
                }
                int atomDecrement = 0;
                atomDecrement += (atom0 > terminusHO) ? 1 : 0;
                atomDecrement += (atom0 > terminusO) ? 1 : 0;
                atom0 -= atomDecrement;
                atomDecrement = 0;
                atomDecrement += (atom1 > terminusHO) ? 1 : 0;
                atomDecrement += (atom1 > terminusO) ? 1 : 0;
                atom1 -= atomDecrement;
                bondList.add(String.format("%d %d", atom0, atom1));
            }
            int newTerminusC = aminoAcid.terminusC;
            newTerminusC -= (newTerminusC > terminusHO) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusO) ? 1 : 0;
            terminusC = newTerminusC;
        }

        public void write(String folderName) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(folderName + "/" + name + ".txt"));
            writer.write(name + "\n");
            writer.write(atomCount.toString());
            writer.write("\n");
            for (int ii = 0; ii < atomCount; ii++) {
                writer.write(atomBuffer.charAt(ii) + "\n");
            }
            for (String b : bondList) {
                writer.write(b + "\n");
            }
            writer.close();
        }
    }

    public static void addAminoAcid(Protein protein, AminoAcid aminoAcid, boolean isLast) {
        if (protein.complete) {
            System.out.println("cannot add to completed protein");
            return;
        }
        protein.name += aminoAcid.symbol1;
        if (isLast) {
            int baseIdx = protein.atomCount;
            int indexH = aminoAcid.atomBuffer.indexOf("H");
            protein.atomBuffer.append(aminoAcid.atomBuffer, 0, indexH);
            protein.atomBuffer.append(aminoAcid.atomBuffer, indexH + 1, aminoAcid.atomCount);
            protein.atomCount += aminoAcid.atomCount - 1;
            protein.bondList.add(String.format("%d %d", protein.terminusC, baseIdx + aminoAcid.terminusN));
            int bondCount = aminoAcid.bondCount;
            int terminusHN = aminoAcid.terminusHN;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                if (atom0 == terminusHN) {
                    continue;
                } else if (atom0 > terminusHN) {
                    atom0--;
                }
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (atom1 == terminusHN) {
                    continue;
                } else if (atom1 > terminusHN) {
                    atom1--;
                }
                protein.bondList.add(String.format("%d %d", baseIdx + atom0, baseIdx + atom1));
            }
            protein.complete = true;
        } else {
            int baseIdx = protein.atomCount;
            int start = -1;
            for (Integer t : aminoAcid.termini) {
                protein.atomBuffer.append(aminoAcid.atomBuffer.substring(start + 1, t));
                start = t;
            }
            protein.atomBuffer.append(aminoAcid.atomBuffer.substring(start + 1));
            protein.atomCount += aminoAcid.atomCount - 3;
            protein.bondList.add(String.format("%d %d", protein.terminusC, baseIdx + aminoAcid.terminusN));
            int bondCount = aminoAcid.bondCount;
            int terminusHN = aminoAcid.terminusHN;
            int terminusHO = aminoAcid.terminusHO;
            int terminusO = aminoAcid.terminusO;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (aminoAcid.termini.contains(atom0) || aminoAcid.termini.contains(atom1)) {
                    continue;
                }
                int atomDecrement = 0;
                atomDecrement += (atom0 > terminusHN) ? 1 : 0;
                atomDecrement += (atom0 > terminusHO) ? 1 : 0;
                atomDecrement += (atom0 > terminusO) ? 1 : 0;
                atom0 -= atomDecrement;
                atomDecrement = 0;
                atomDecrement += (atom1 > terminusHN) ? 1 : 0;
                atomDecrement += (atom1 > terminusHO) ? 1 : 0;
                atomDecrement += (atom1 > terminusO) ? 1 : 0;
                atom1 -= atomDecrement;
                protein.bondList.add(String.format("%d %d", baseIdx + atom0, baseIdx + atom1));
            }
            int newTerminusC = aminoAcid.terminusC;
            newTerminusC -= (newTerminusC > terminusHN) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusHO) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusO) ? 1 : 0;
            protein.terminusC = baseIdx + newTerminusC;
        }
    }

    public static AminoAcid getAminoAcid(int id) {
        return switch (id) {
            case 9 -> LEUCINE;      // 22
            case 8 -> ISOLEUCINE;   // 22
            case 7 -> METHIONINE;   // 20
            case 6 -> VALINE;       // 19
            case 5 -> THREONINE;    // 17
            case 4 -> PROLINE;      // 17
            case 3 -> SERINE;       // 14
            case 2 -> CYSTEINE;     // 14
            case 1 -> ALANINE;      // 13
            default -> GLYCINE;     // 10
        };
    }

    public static Protein generateProtein(int seed, int chainLen) {
        Protein protein = new Protein(getAminoAcid(seed % AMINO_ACID_COUNT));
        for (int ii = 0; ii < chainLen - 2; ii++) {
            seed /= AMINO_ACID_COUNT;
            addAminoAcid(protein, getAminoAcid(seed % AMINO_ACID_COUNT), false);
        }
        seed /= AMINO_ACID_COUNT;
        addAminoAcid(protein, getAminoAcid(seed % AMINO_ACID_COUNT), true);
        return protein;
    }

    public static void manySimpleProteins() throws IOException {
        int simpleIdx = 0;
        File simpleDir = new File("../simple");
        for (int ii = 0; ii < 100; ii++) {
            System.out.println(ii + "% done generating simple proteins (< 137 atoms)");
            File simpleDirNested = new File(simpleDir, "simple" + ii);
            if (simpleDirNested.mkdirs()) {
                System.out.println("made new directory: " + simpleDirNested);
            }
            for (int jj = 0; jj < 100000; jj++) {
                Protein protein = generateProtein(simpleIdx, 7);
                protein.write(simpleDirNested.toString());
                simpleIdx++;
            }
        }
        System.out.println("done");
    }

    public static void fewComplexProteins() throws IOException {
        int complexIdx = 0;
        File complexDir = new File("../complex");
        for (int ii = 0; ii < 10; ii++) {
            System.out.println(ii + "0% done generating complex proteins (>= 10006 atoms)");
            File complexDirNested = new File(complexDir, "complex" + ii);
            if (complexDirNested.mkdirs()) {
                System.out.println("made new directory: " + complexDirNested);
            }
            for (int jj = 0; jj < 1000; jj++) {
                Protein protein = generateProtein(complexIdx, 1429);
                protein.name = "protein" + complexIdx;
                protein.write(complexDirNested.toString());
                complexIdx++;
            }
        }
        System.out.println("done");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("hello world");
        manySimpleProteins();
        fewComplexProteins();
        System.out.println("goodbye");
    }
}
