package Server;

import Interface.CallServer;
import Interface.ImageProcessorLink;
import Interface.MetaHeuristics;
import Interface.Processor;
import MetaheuristicsAndTools.Tools.CalculatePower;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Author: Carlos
 * Date:  02/09/2014
 */
public class Dispatcher implements Processor, CallServer, Runnable {

    private static ArrayList<Registry> processorList = new ArrayList<>();
    private static double processingPower;
    private static Hashtable<Integer, String> options;
    private static int metaH;
    private static boolean processing;


    private Dispatcher() {
    }

    public static void main(String[] p) {
        //Init default metaheuristic and options table
        options = new Hashtable<>();
        options.put(1, "Tabu");
        options.put(2, "Genetic");
        options.put(3, "SimulatedA");
        options.put(4, "AntColony");
        metaH = 1;
        processing = false;

        String[] args = {"localhost", "8000"};
        try {
            if (args.length == 2) {
                processingPower = new CalculatePower().getPower();
                //Set server interfaces and registry
                System.out.print("Initializing...");
                System.setProperty("java.rmi.server.hostname", args[0]);
                //Create registry
                Registry registry = LocateRegistry.createRegistry(Integer.valueOf(args[1]));
                Dispatcher callServerInterface = new Dispatcher();
                Dispatcher processorInterface = new Dispatcher();
                //Register interface
                CallServer callServer = (CallServer) UnicastRemoteObject.exportObject(callServerInterface, 0);
                Processor processor = (Processor) UnicastRemoteObject.exportObject(processorInterface, 0);

                registry.bind("CallServer", callServer);
                registry.bind("Processor", processor);
                (new Thread(new Dispatcher())).start();
                System.out.print("Done\nReady to accept connections\n");
            } else {
                System.out.println("Expected more arguments");
                System.out.println("Server serverIP serverPort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Server console
    public void run() {
        Scanner consoleReader = new Scanner(System.in);
        String s, m;

        //Server information
        System.out.print("Metaheuristic optimizer V 1.0" +
                "\nMetaheuristic '" + options.get(metaH) + "' loaded" +
                "'\nWrite 'help' for usage\nReady!\n>> ");
        while (consoleReader.hasNext()) {
            s = consoleReader.nextLine();
            if (s.equals("help")) {
                System.out.print("Usage is:" +
                        "\n-'actual' -> Shows active metaheuristic" +
                        "\n-'set x' -> Changes actual metaheuristic where x can be:" +
                        "\n -Tabu" +
                        "\n -SimulatedA" +
                        "\n -Genetic" +
                        "\n-'help' -> Shows this help\n>> ");
            } else if (s.equals("actual")) {
                System.out.print("Metaheuristic in use: '" + options.get(metaH) + "'\n>> ");
            } else if (s.contains("set")) {
                if (s.split(" ").length > 1) {
                    m = s.split(" ")[1];

                    if (options.containsValue(m)) {
                        Set set = options.entrySet();
                        for (Object aSet : set) {
                            Map.Entry entry = (Map.Entry) aSet;
                            if (entry.getValue().equals(m)) {
                                metaH = Integer.valueOf(entry.getKey().toString());
                                break;
                            }
                        }
                        if (!processing) {
                            System.out.print("Metaheuristic changed to '" + options.get(metaH) + "'\n>> ");
                        } else {
                            System.out.print("Change of metaheuristic to '" + options.get(metaH) + "'  will take effect after actual assignment\n>> ");
                        }
                    } else {
                        System.out.print("Metaheuristic not found!\n>> ");
                    }
                } else {
                    System.out.print("Wrong syntax, write help for usage\n>> ");
                }
            } else {
                System.out.print(s + "\n>> ");
            }
        }
    }

    //Dynamic class loader for the Server console
    private static MetaHeuristics activeOne(ArrayList<Double> arg1, ArrayList<Integer> arg2) {
        try {
            Class<?> ChosenMetaheuristic = Class.forName("MetaheuristicsAndTools." + options.get(metaH) + "." + options.get(metaH));
            return (MetaHeuristics) ChosenMetaheuristic.getConstructor(ArrayList.class, ArrayList.class).newInstance(arg1, arg2);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processorAddress(String ip, Integer port) throws RemoteException {
        try { //Add processor registry
            Registry registry = LocateRegistry.getRegistry(ip, port);
            processorList.add(registry);
            ImageProcessorLink p = (ImageProcessorLink) registry.lookup("ImageProcessorLink");
            double rPower = processingPower / p.processorPower();
            System.out.print("\rNew processor registered (" + ip + ":" + port + ") with a relative power of " + rPower + "\n>> ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Here comes the metaheuristic
    @Override
    public ArrayList<Remote> getProcessor(ArrayList<Integer> imageWeights) throws RemoteException {
        try {
            processing = true;
            ArrayList<Double> pPower = new ArrayList<>();
            for (Registry i : processorList) {
                ImageProcessorLink p = (ImageProcessorLink) i.lookup("ImageProcessorLink");
                pPower.add(processingPower / p.processorPower());
            }
            System.out.println("\nAvailable powers: " + pPower);

            //Here we call the metaheuristic
            System.out.println("Used metaheuristic: " + options.get(metaH));
            MetaHeuristics mH = activeOne(pPower, imageWeights);
            assert mH != null;
            ArrayList<Integer> assignment = new ArrayList<>(mH.runTechnique());
            ArrayList<Remote> pAssignment = new ArrayList<>();
            for (Integer i : assignment) {
                pAssignment.add(processorList.get(i).lookup("ImageProcessorLink"));
            }
            processing = false;
            return pAssignment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
