import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class NicoA3Server {
    public static void main(String[] args) throws IOException {
        // read routing table from file
        BufferedReader br = new BufferedReader(new FileReader("src/rt.txt"));
        String serverIP = br.readLine(); // ServerIP
        int numNeighbors = Integer.parseInt(br.readLine().trim()); // Neighbor Count
        String[] neighborIPs = br.readLine().split(" "); // Neighbor Ips
        int[] neighborDistances = new int[numNeighbors]; // Distance from neighbors
        String[] intparser = br.readLine().split(" "); // Temporary storage of string to parse string to int
        HashMap<String, Integer> distancesMap = new HashMap<String, Integer>(); // Create hashmap to represent distance relation
        HashMap<String, String> nextHopMap = new HashMap<String, String>(); // hashmap to represent the next hop to reach a destination network.
        int numEntities; //Num of entities in table
        int[] distances; // Distance to each entity
        String[] destNetworks; // List of destination networks
        String[] nextHops; // List of next hops.

        for (int i = 0; i < numNeighbors; i++) // Populating neighbor distances
        {
            neighborDistances[i] = Integer.parseInt(intparser[i]);
        }

        numEntities = Integer.parseInt(br.readLine());
        destNetworks = br.readLine().split(" ");
        nextHops = br.readLine().split(" "); 
        distances = new int[numEntities]; 
        
        intparser = br.readLine().split(" ");

        for (int i = 0; i < numEntities; i++) // Populates numentities
        {
            distances[i] = Integer.parseInt(intparser[i]);
        }

        br.close(); // If properly formatted, the end of the rt file should have been reached here.

        // Populating distance map
        for (int i = 0; i < numEntities; i++) {
            distancesMap.put(destNetworks[i], distances[i]);
        }

        for (int i = 0; i < numEntities; i++) {
            nextHopMap.put(destNetworks[i], nextHops[i]);
        }

        while (true) 
        {
            // create server socket
            ServerSocket serverSocket = new ServerSocket(4321);
            System.out.println("Server started");
            // wait for client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Dvr Message Received");
            
            //Start of data extraction.
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // read message from client
            String dvrServerIP = in.readLine(); // Server ip. should equal serverIP
            String dvrClientIP = in.readLine(); // Client ip.
            int dvrEntities = Integer.parseInt(in.readLine()); // Num of entities
            String[] dvrDestinationIPs = in.readLine().split(" "); // List of destination ips.
            int[] dvrDistances = new int[dvrEntities]; // Distances between destination ips.
            HashMap<String, Integer> dvrDistancesMap = new HashMap<String, Integer>();

            intparser = in.readLine().split(" ");

            for (int i = 0; i < dvrEntities; i++) // Populates dvrDistances
            {
                dvrDistances[i] = Integer.parseInt(intparser[i]);
            }

            in.close(); // If properly formatted, bufferedreader in should have reached the end of the file by now.

            for (int i = 0; i < numEntities; i++) // Puts the distance between a node and the node into a hashmap.
            {
                dvrDistancesMap.put(dvrDestinationIPs[i], dvrDistances[i]);
            }
            //Prints the dvr message.
            printDVR(dvrServerIP,dvrClientIP,dvrEntities,dvrDestinationIPs,dvrDistances);
            int clientDistToServer = neighborDistances[Arrays.asList(neighborIPs).indexOf(dvrClientIP)]; //Client neighbor distance to server

            //End of data extraction.
            String sIncrement;
            int totaldist;
            for (int i = 0; i < numEntities; i++) //Update hashmaps that show relationships
            {
                sIncrement = dvrDestinationIPs[i];
                totaldist = dvrDistancesMap.get(sIncrement) + clientDistToServer; //Total distance from server -> client -> destination
                if (distancesMap.get(sIncrement) != totaldist) // check for non-equivalent distances, makes sure theres no discrepencies between client and server data.
                {
                    if (nextHopMap.get(sIncrement).equals(dvrClientIP)) // if next hop == client ip, and distances do not line up, update distance.
                    {
                        distancesMap.put(sIncrement, totaldist);
                        distances[i] = clientDistToServer + dvrDistancesMap.get(sIncrement);
                    } 
                    else if (totaldist < distancesMap.get(sIncrement)) // If next hop isn't client ip and distance is shorter, update
                    {
                        nextHopMap.put(sIncrement, dvrClientIP);
                        distancesMap.put(sIncrement, totaldist);
                        distances[i] = clientDistToServer + dvrDistancesMap.get(sIncrement);
                        nextHops[i] = dvrClientIP;

                    }
                }
            }
            
            //Write to RT file to replace routing table.
            File rt = new File("src/rt.txt");
            BufferedWriter rtout = new BufferedWriter(new FileWriter (rt,false));
            rtout.write(serverIP);
            rtout.newLine();
            rtout.write(String.valueOf(numNeighbors));
            rtout.newLine();
            rtout.write(String.join(" ", neighborIPs).replace(",", "").replace("[" , "").replace("]","").trim());
            rtout.newLine();
            rtout.write(String.join(" ",Arrays.toString(neighborDistances).replace(",", "").replace("[" , "").replace("]","").trim()));
            rtout.newLine();
            rtout.write(String.valueOf(numEntities));
            rtout.newLine();
            rtout.write(String.join(" ",Arrays.toString(destNetworks).replace(",", "").replace("[" , "").replace("]","").trim()));
            rtout.newLine();
            rtout.write(String.join(" ",Arrays.toString(nextHops).replace(",", "").replace("[" , "").replace("]","").trim()));
            rtout.newLine();
            rtout.write(String.join(" ",Arrays.toString(distances).replace(",", "").replace("[" , "").replace("]","").trim()));
            rtout.close();
            System.out.println(serverIP);
            printrt(); //Prints routing table file.

            serverSocket.close();


        }

    }

    public static void printDVR(String dvrServerIp, String dvrClientIP,
    int dvrEntities, String[] dvrDestinationIPs, int[] dvrDistances)
    {
        System.out.println("Printing dvr message");
        System.out.println(dvrServerIp);
        System.out.println(dvrClientIP);
        System.out.println(dvrEntities);
        System.out.println(Arrays.toString(dvrDestinationIPs).replace(",", "").replace("[" , "").replace("]","").trim());
        System.out.println(Arrays.toString(dvrDistances).replace(",", "").replace("[" , "").replace("]","").trim());
    }
    public static void printrt()
    {
        System.out.println("Printing routing table");
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/rt.txt"));
            String reader;
            while((reader = br.readLine()) != null)
            {
                System.out.println(reader);
            }
            br.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        catch(IOException ex)
        {
            System.out.println("IO Exception");
            System.exit(0);
        }
        
    }
}
