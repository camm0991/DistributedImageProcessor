package MetaheuristicsAndTools.AntColony;

import Interface.MetaHeuristics;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Carlos on 01/12/2014.
 */
public class AntColony implements MetaHeuristics {
    //Ant Colony Parameters ->
    private double alpha = 0.85;              //Pheromone influence
    private double beta = 1 - alpha;               //Selection probability
    private double basePheromoneValue = 1.0; //Initial pheromone value for all edges
    private double evaporationValue = 0.90;   //Evaporation value of the pheromone in the edges
    private int colonyPopulation = 5;
    private int antPopulation;              //Number of ants
    private int maxIterations = 20;             //Number of iterations
    //<- Ant Colony Parameters

    //Ant Colony Variables ->
    private ArrayList<Double> pPowers;
    private ArrayList<Integer> tasksList;
    private Random random = new Random();
    //<- Ant Colony Variables

    //Hive constructor that loads graph information into its Hash Table
    public AntColony(ArrayList<Double> pPowers, ArrayList<Integer> tasksList) {
        this.pPowers = new ArrayList<Double>(pPowers);
        this.antPopulation = pPowers.size();
        this.tasksList = new ArrayList<Integer>(tasksList);
    }

    public ArrayList<Integer> runTechnique() {

        ArrayList<Pair<Double, ArrayList<Ant>>> colonies = new ArrayList<Pair<Double, ArrayList<Ant>>>();
        ArrayList<Ant> antSet = new ArrayList<Ant>();

        //Paths for colonies
        ArrayList<AntPath> antPaths = new ArrayList<AntPath>();
        for (int j = 0; j < antPopulation; j++) {
            antPaths.add(new AntPath(basePheromoneValue, tasksList));
        }
        //Ciclo aqui
        ArrayList<Integer> finalAssignement = new ArrayList<Integer>();
        double maxFitness = 100.0;
        while (maxIterations-- > 0 && maxFitness > 1.0) {
            //Create the ants for each hive
            for (int i = 0; i < colonyPopulation; i++) {
                //antSet = new ArrayList<PhotoAnt>();
                for (int j = 0; j < antPopulation; j++) {
                    antSet.add(new Ant(j, pPowers.get(j)));
                }
                colonies.add(new Pair<Double, ArrayList<Ant>>(0.0, new ArrayList<Ant>(antSet)));
                antSet.clear();
            }

            //Make the run for every ant colony
            ArrayList<Pair<Integer, Integer>> id_value = new ArrayList<Pair<Integer, Integer>>();
            for (int i = 0; i < tasksList.size(); i++) {
                id_value.add(new Pair<Integer, Integer>(i, tasksList.get(i)));
            }

            int nextCity;
            int index = 0;
            ArrayList<Pair<Integer, Double>> coloniesValues = new ArrayList<Pair<Integer, Double>>();
            for (int ik = 0; ik < colonyPopulation; ik++) {
                ArrayList<Ant> ants = colonies.get(ik).getValue();
                ArrayList<Pair<Integer, Integer>> tempTaskList = new ArrayList<Pair<Integer, Integer>>(id_value);
                while (tempTaskList.size() > 0) {
                    int option = random.nextInt(antPopulation);
                    nextCity = getNextCity(new ArrayList<Pair<Integer, Integer>>(tempTaskList), antPaths.get(ants.get(option).getId()));
                    ants.get(option).addIds(id_value.get(nextCity).getKey());
                    ants.get(option).setLoad(ants.get(option).getLoad() + (id_value.get(nextCity).getValue() / ants.get(option).getPower()));
                    for (int i = 0; i < tempTaskList.size(); i++) {
                        if (tempTaskList.get(i).getKey().equals(nextCity)) {
                            tempTaskList.remove(i);
                            break;
                        }
                    }
                }
                double tWeight = 0.0;
                for (Ant i : ants) {
                    tWeight += i.getLoad();
                }
                coloniesValues.add(new Pair<Integer, Double>(index++, tWeight));
            }

            //Fitness for colonies
            double localLoad = 0.0;
            double totalLoad = 0.0;
            int colonyIndex = 0;
            for(Pair<Double, ArrayList<Ant>> i: colonies) {
                for (Ant p : i.getValue()) {
                    totalLoad += p.getLoad();
                }
                totalLoad /= antPopulation;
                for (Ant p : i.getValue()) {
                    localLoad += Math.abs(p.getLoad() - totalLoad);
                }
                localLoad /= antPopulation;
                colonies.set(colonyIndex++, new Pair<Double, ArrayList<Ant>>(localLoad, i.getValue()));
            }
            Collections.sort(colonies, new AntSorter());

            //evaporacion
            for (AntPath i : antPaths) {
                for (Bridge j : i.getPaths()) {
                    j.setPheromone(j.getPheromone() * evaporationValue);
                }
            }
            //actualizacion de feromonas
            ArrayList<Ant> j = new ArrayList<Ant>(colonies.get(0).getValue());
            for (int k = 0; k < antPaths.size(); k++) {
                AntPath l = antPaths.get(k);
                for (Integer m : j.get(k).getIds()) {
                    l.getPaths().get(m).setPheromone(l.getPaths().get(m).getPheromone() + (1 / j.get(k).getLoad()));
                }
            }
            int indexes = 0;
            for(Pair<Double, ArrayList<Ant>> i: colonies) {
                System.out.println("Colony " + indexes++ + " with fitness: " + i.getKey());
            }
            System.out.println("New one \n");
            maxFitness = colonies.get(0).getKey();
            if(maxIterations == 0) {
                ArrayList<Ant> x = colonies.get(0).getValue();
                for(int km = 0; km < tasksList.size(); km++) {
                    for(Ant ki: x) {
                        if(ki.getIds().contains(km)) {
                            finalAssignement.add(ki.getId());
                            break;
                        }
                    }
                }
            }
            colonies.clear();
        }
        System.out.println(finalAssignement);
        return finalAssignement;
    }

    private Integer getNextCity(ArrayList<Pair<Integer, Integer>> tempTaskList, AntPath antPaths) {
        //ArrayList<Integer> neighborCities = new ArrayList<Integer>(getNeighborhood(ant));
        //Calculates the total weight for each city in the neighborCities of the ant;
        double distanceSum = 0.0;
        for (Pair<Integer, Integer> task : tempTaskList) {
            distanceSum += task.getValue();
        }

        //Calculates the probability of being selected for each available edge of the ants neighborhood
        ArrayList<Pair<Integer, Double>> cityProbability = new ArrayList<Pair<Integer, Double>>();

        for (Pair<Integer, Integer> id_value: tempTaskList) {
            cityProbability.add(new Pair<Integer, Double>(id_value.getKey(), probability(distanceSum, antPaths.getPaths().get(id_value.getKey()).getPheromone(), antPaths.getPaths().get(id_value.getKey()).getAttractiveness())));
        }
        Collections.sort(cityProbability, new NodeSorter());
        return cityProbability.get(0).getKey();
    }

    /*
    *Calculates the selection probability of an edge according to:
    *Pxy(K) = (Txy)^alpha(Nxy)^beta / (Sum(iNeighbor(k) (Txy(i))^alpha(Nxy(i))^beta
    */
    private double probability(double dSum, double pValue, double eAttractiveness) {
        return ((Math.pow(pValue, alpha) * Math.pow(eAttractiveness, beta)) / dSum);
    }


}



