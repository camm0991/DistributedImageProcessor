package MetaheuristicsAndTools.Genetic;

import Interface.MetaHeuristics;
import MetaheuristicsAndTools.Tools.GenericMetaObject;
import MetaheuristicsAndTools.Tools.ProcessorObject;
import MetaheuristicsAndTools.Tools.SequenceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Carlos on 26/10/2014.
 */
public class Genetic implements MetaHeuristics{

    private final int tournamentSize = 8;
    private final int populationSize = 18;
    private final int maxGenerations = 1000;
    private final double crossProb = 0.60;
    private final double mutateProb = 0.20;
    //Genetic class variables
    private Random random = new Random();
    private ArrayList<ProcessorObject> processorsInfo;
    private ArrayList<Integer> tasksList;
    private double total;
    private int tasksCount;
    private int processorCount;
    private StringBuilder sequence;

    public Genetic(ArrayList<Double> pPowers, ArrayList<Integer> tasksList) {
        processorsInfo = new ArrayList<ProcessorObject>();
        int index = 0;
        for (Double i : pPowers) {
            processorsInfo.add(new ProcessorObject(index++, Double.valueOf(i)));
        }
        this.tasksList = new ArrayList<Integer>(tasksList);
        this.tasksCount = this.tasksList.size();
        this.processorCount = this.processorsInfo.size();
        this.total = 0.0;
        for (ProcessorObject i : processorsInfo) {
            this.total += i.getPower();
        }
    }

    public ArrayList<Integer> runTechnique() {

        //First pop
        ArrayList<GenericMetaObject> population = new ArrayList<GenericMetaObject>();
        ArrayList<GenericMetaObject> newPopulation = new ArrayList<GenericMetaObject>();
        ArrayList<ProcessorObject> tempSequence;
        for (int i = 0; i < populationSize; i++) {
            tempSequence = newSequence();
            population.add(new GenericMetaObject(tempSequence, null, calculateFitness(tempSequence)));
        }
        Collections.sort(population, new SequenceComparator());
        GenericMetaObject bestIndividual = new GenericMetaObject(population.get(0));
        ArrayList<GenericMetaObject> parents;
        ArrayList<GenericMetaObject> children;

        int gens = 0;
        double theRand;
        while (bestIndividual.getFitness() > 1.0 && gens++ < maxGenerations) {
            //selection
            newPopulation.add(bestIndividual);

            while (newPopulation.size() < populationSize) {
                parents = new ArrayList<GenericMetaObject>(tournament(population));
                theRand = random.nextDouble();
                if (theRand > crossProb) {
                    children = new ArrayList<GenericMetaObject>(crossover(parents));
                    newPopulation.addAll(children);
                } else if (theRand > mutateProb) {
                    newPopulation.addAll(mutate(parents));
                } else {
                    newPopulation.addAll(parents);
                }
            }

            Collections.sort(newPopulation, new SequenceComparator());
            while (newPopulation.size() > populationSize) {
                newPopulation.remove(populationSize - 1);
            }

            bestIndividual = new GenericMetaObject(newPopulation.get(0));
            population = new ArrayList<GenericMetaObject>(newPopulation);
            newPopulation.clear();
        }
        System.out.println("Best sequence reached for " + tasksCount + " tasks and " + processorsInfo.size() + " processors with (" + --gens + ") iterations made: ");
        printStatics(bestIndividual);
        ArrayList<Integer> assignment = new ArrayList<Integer>();
        for(ProcessorObject i: bestIndividual.getSequence()) {
            assignment.add(i.getId());
        }
        return assignment;
    }

    /**
     * Method for calculating the fitness of a sequence
     *
     * @param sequence The sequence to be evaluated
     * @return The fitness value
     */
    private Double calculateFitness(ArrayList<ProcessorObject> sequence) {
        //Initialization of the variables
        double distFrom_pAverage = 0.0;
        double[] processorLoad = new double[processorCount];
        double pAverage = 0.0;
        double loadValue;
        for (int i = 0; i < processorLoad.length; i++) {
            processorLoad[i] = 0.0;
        }

        //The average of the processors load is calculated
        for (int i = 0; i < tasksCount; i++) {
            loadValue = tasksList.get(i) / sequence.get(i).getPower();
            processorLoad[sequence.get(i).getId()] += loadValue;
            pAverage += loadValue;
        }
        pAverage /= processorCount;

        //The deference between the processor load and the average of the processor load is calculated and returned
        for (int i = 0; i < processorLoad.length; i++) {
            distFrom_pAverage += Math.abs(processorLoad[i] - pAverage);
        }
        distFrom_pAverage /= processorCount;

        return (distFrom_pAverage);
    }

    private ArrayList<GenericMetaObject> tournament(ArrayList<GenericMetaObject> population) {
        ArrayList<GenericMetaObject> contestants = new ArrayList<GenericMetaObject>();
        ArrayList<GenericMetaObject> winners = new ArrayList<GenericMetaObject>();

        while (winners.size() < 2) {
            while (contestants.size() < tournamentSize) {
                contestants.add(population.get(random.nextInt(populationSize)));
            }
            Collections.sort(contestants, new SequenceComparator());
            winners.add(contestants.get(0));
            contestants.clear();
        }
        return winners;
    }

    private ArrayList<GenericMetaObject> crossover(ArrayList<GenericMetaObject> population) {
        ArrayList<ProcessorObject> k = new ArrayList<ProcessorObject>();
        ArrayList<GenericMetaObject> m = new ArrayList<GenericMetaObject>();
        int p, q, pInit, qInit;
        for (int i = 0; i < 2; i++) {
            p = random.nextInt(tasksCount - 1) + 1;
            q = tasksCount - p;
            pInit = random.nextInt(q);
            qInit = random.nextInt(p);
            k.addAll(population.get(0).getSequence().subList(pInit, pInit + p));
            k.addAll(population.get(1).getSequence().subList(qInit, qInit + q));
            m.add(new GenericMetaObject(k, null, calculateFitness(k)));
            k.clear();
        }
        return m;
    }

    private ArrayList mutate(ArrayList<GenericMetaObject> candidate) {
        GenericMetaObject k;
        ArrayList<GenericMetaObject> m = new ArrayList<GenericMetaObject>();
        for (int i = 0; i < 2; i++) {
            k = new GenericMetaObject(candidate.get(i));
            k.getSequence().set(random.nextInt(tasksCount), processorsInfo.get(random.nextInt(processorCount)));
            k.setFitness(calculateFitness(k.getSequence()));
            m.add(k);
        }
        return m;
    }


    private void printStatics(GenericMetaObject bestSequence) {
        //Initialization of variables
        ProcessorObject tempP_object;
        double[] tasks = new double[processorCount];
        double[] load = new double[processorCount];
        for (int i = 0; i < processorCount; i++) {
            tasks[i] = 0.0;
            load[i] = 0.0;
        }
        //Obtaining the load of each processor and the tasks assigned to it
        for (int i = 0; i < tasksCount; i++) {
            tempP_object = new ProcessorObject(bestSequence.getSequence().get(i));
            tasks[tempP_object.getId()] += 1;
            load[tempP_object.getId()] += tasksList.get(i) / tempP_object.getPower();
        }
        //Shows the statics
        System.out.println("Fitness: " + bestSequence.getFitness());
        for (int i = 0; i < processorCount; i++) {
            System.out.println("Processor[" + i + "]: (tasks: " + tasks[i] + " / load: " + load[i] + ")");
        }
    }

    //Init new sequence generator
    private ArrayList<ProcessorObject> newSequence() {
        ArrayList<ProcessorObject> sequence = new ArrayList<ProcessorObject>();
        for (int i = 0; i < tasksCount; i++) {
            sequence.add(processorsInfo.get(nextProcessor()));
        }
        return sequence;
    }

    private int nextProcessor() {
        double r = random.nextDouble() * total;
        int index = 0;
        double s = processorsInfo.get(index).getPower();
        while (s < r) {
            s += processorsInfo.get(++index).getPower();
        }
        return index;
    }

    public StringBuilder getSequence() {
        return sequence;
    }
//End new sequence generator
}

