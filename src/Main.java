import edu.bu.ec504.project.Molecule;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class Main {

    static MoleculeDatabase moleculeDb = null;
    static boolean verbose = false;
    static int MINUTE = 60 * 1000;

    public static void initDb(String dbName) throws IOException {
        // Load the database
        moleculeDb = new MoleculeDatabase();
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            moleculeDb.load(dbName);
        }
        moleculeDb.name = dbName;
        ProteinFactory.initAminoAcids();
    }

    public static void printVerbose(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    public static void commandHandler1(String cmd) throws IOException {
        switch (cmd) {
            case "--printDb":
                moleculeDb.printDb();
                break;
            case "--printName":
                System.out.println(moleculeDb.name);
                break;
            case "--verbose":
                if (verbose) {
                    System.out.println("verbose: true -> false");
                } else {
                    System.out.println("verbose: false -> true");
                }
                verbose = !verbose;
                moleculeDb.verbose = verbose;
                break;
            case "--makeManySimple":
                ProteinFactory.manySimpleProteins();
                break;
            case "--makeFewComplex":
                ProteinFactory.fewComplexProteins();
                break;
            case "--marco":
                System.out.println("polo");
                break;
            default:
                printVerbose("unrecognized command: " + cmd);
                break;
        }
    }

    public static void addProteins(String proteinPath) throws IOException {
        Files.walkFileTree(Paths.get(proteinPath), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!Files.isDirectory(file)) {
                    moleculeDb.addMolecule(new Molecule(proteinPath + "/" +
                            file.getParent().toString() + "/" + file.getFileName().toString()));
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void commandHandler2(String cmd, String moleculePath) throws IOException {
        switch (cmd) {
            case "--addMolecule":
                moleculeDb.addMolecule(new Molecule(moleculePath));
                break;
            case "--addProteins":
                try {
                    addProteins(moleculePath);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                break;
            case "--findMolecule":
                Molecule molecule = moleculeDb.findMolecule(new Molecule(moleculePath));
                if (molecule == null) {
                    System.out.println("NO EXACT MATCH FOUND");
                    molecule= moleculeDb.similarMolecule(new Molecule(moleculePath));
                    if(molecule!=null)
                    {
                        System.out.println(molecule.moleculeName + " is the most similar");
                    }
                } else {
                    printVerbose("FOUND");
                }
                break;
            case "--findSubgraph":
                ArrayList<Molecule> mList = moleculeDb.findSubgraph(new Molecule(moleculePath));
                if (mList.isEmpty())
                    System.out.println("No subgraphs found");
                else
                    for(Molecule m: mList)
                        System.out.println(m.moleculeName);
                break;
            case "--downloadPubChem":
                // Repurposed moleculePath should be in format "start,end"
                // Input format for the file path is "start,end" where 'start' and 'end' are the starting and ending CID indices of molecules in PubChem
                // For example, enter 12,24 to download molecules 12-24
                String[] indexes = moleculePath.split(",");
                if (indexes.length == 2) {
                    String start = indexes[0];
                    String end = indexes[1];
                    moleculeDb.downloadPubChem(start, end);
                    System.out.println("Download Complete!");
                } else {
                    printVerbose("invalid Input");
                }
                break;
            case "--addBulk":
                moleculeDb.addMultipleMolecules(moleculePath);
                break;
            case "--delete":
                boolean delete= moleculeDb.deleteMolecule(new Molecule(moleculePath));
                if(delete)
                    printVerbose("Successfully Deleted");
                else
                    System.out.println("Molecule not in the database");
                break;
            default:
                printVerbose("unrecognized command: " + cmd);
                break;
        }
    }

    /**
     * Method to run the client side of the program
     *
     * @param clientSocket
     * @param argument
     * @throws IOException
     */
    public static void runClient(Socket clientSocket, String argument) throws IOException {
        // Set up output stream to send data to the server
        OutputStream outStream = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(outStream);

        // Write the argument to the output stream
        writer.println(argument);
        writer.flush();

        // Close the streams
        writer.close();
        outStream.close();
    }

    /**
     * Method to run the server side of the program
     */
    public static void runServer(ServerSocket serverSocket, String cmd, String moleculePath,
                                 String dbName) throws IOException {
        // Continue processing commands until "--quit" command is received
        while (!cmd.equals("--quit")) {
            // Perform actions based on the received command
            if (moleculePath.isEmpty()) {
                commandHandler1(cmd);
            } else {
                commandHandler2(cmd, moleculePath);
            }

            // Accept incoming client connections
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("connection timed out");
                break;
            }
            InputStream inStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String[] args = reader.readLine().split(" ");

            // close the streams
            reader.close();
            inStream.close();
            clientSocket.close();

            // parse argument from client
            cmd = args[0];
            moleculePath = "";
            if (args.length > 1) {
                moleculePath = args[1];
            }
        }

        // save the database before exiting
        moleculeDb.save(dbName);
        System.out.println("Database saved successfully.");
        System.out.println("Goodbye");
    }

    /**
     * Main method to start the program
     */
    public static void main(String[] args) throws IOException {
        final int ARG_COUNT = args.length;

        // Get the port number from the command line arguments
        final int PORT_NUMBER = Integer.parseInt(args[0]);

        // get other command line arguments
        String cmd = args[1];
        String moleculePath = "";
        if (ARG_COUNT > 2) {
            moleculePath = args[2];
        }

        // run as client or server
        try (Socket clientSocket = new Socket("localhost", PORT_NUMBER)) {
            // If a client connection is successful, run the client side of the program
            runClient(clientSocket, cmd + " " + moleculePath);
        } catch (ConnectException e) {
            // If a client connection fails, run the server side of the program
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            serverSocket.setSoTimeout(5 * MINUTE);

            // Set the default filename for the database
            String dbName = "molecule.db";

            // Check if an alternative filename is provided as a command line argument
            if (ARG_COUNT > 3) {
                dbName = args[3];
            }

            // initialize database and start server
            initDb(dbName);
            runServer(serverSocket, cmd, moleculePath, dbName);
            serverSocket.close();
        }
    }
}
