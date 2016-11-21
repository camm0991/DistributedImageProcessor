package MetaheuristicsAndTools.Tabu;

import Interface.MetaHeuristics;
import MetaheuristicsAndTools.Tools.GenericMetaObject;
import MetaheuristicsAndTools.Tools.ProcessorObject;
import MetaheuristicsAndTools.Tools.SequenceComparator;

import java.util.*;

//import MetaheuristicsAndTools.Tools

/**
 * Created by Carlos on 23/10/2014.
 */
public class Tabu implements MetaHeuristics{

    //Tabu class variables
    private static final int neighborhoodSize = 20;
    private static final int tabuListSize = 20;
    private static final int maxIterations = 1000;
    private static int tasksCount;
    private static int processorCount;
    private Random random = new Random();
    private ArrayList<ProcessorObject> processorsInfo;
    private ArrayList<Integer> tasksList;
    private double total;

    public Tabu(ArrayList<Double> pPowers, ArrayList<Integer> tasksList) {
        processorsInfo = new ArrayList<ProcessorObject>();
        int index = 0;
        for (Double i : pPowers) {
            processorsInfo.add(new ProcessorObject(index++, Double.valueOf(i)));
        }
        this.tasksList = tasksList;
        tasksCount = this.tasksList.size();
        processorCount = this.processorsInfo.size();
        this.total = 0;
        for (ProcessorObject i : processorsInfo) {
            total += i.getPower();
        }
    }

    /**
     * The method will apply the Tabu search algorithm search for optimizing tasks assignment to processors
     */
    public ArrayList<Integer> runTechnique() {
        //**Begin of algorithm initialization**
        //TabuObject 'bestSequence'
        GenericMetaObject bestSequence = new GenericMetaObject();
        bestSequence.setSequence(firstSequence());
        firstFitness(bestSequence);

        //Arrays for containing the Tabu, candidate and neighborhood lists
        Queue<GenericMetaObject> tabuList = new LinkedList<GenericMetaObject>();
        ArrayList<GenericMetaObject> candidateList = new ArrayList<GenericMetaObject>();
        ArrayList<GenericMetaObject> neighborhood;
        //**End of initialization**

        //**Begin of Tabu algorithm**
        //Stop conditions
        //1.- Distance between processors load and the mean of the processors load
        //2.- The number of iterations
        int iterations = 1;
        while (bestSequence.getFitness() > 1.0 && iterations++ < maxIterations) {
            //Neighborhood of 'actualSequence'
            neighborhood = generateNeighborhood(bestSequence);
            //Loop for checking if the members of the neighborhood has not been banned as possible candidates
            for (GenericMetaObject i : neighborhood) {
                if (!tabuList.contains(i)) {
                    candidateList.add(i);
                }
            }
            //The candidate list is sorted based on the fitness
            Collections.sort(candidateList, new SequenceComparator());

            //If the first element of the candidate list is better than the 'bestSequence' we replace it
            if (candidateList.get(0).getFitness().compareTo(bestSequence.getFitness()) < 0) {
                //We ban the best one of the candidate list
                tabuList.add(candidateList.get(0));
                bestSequence = new GenericMetaObject(candidateList.get(0));
                //If the tabu list has more elements than the maximum, we eliminate the first ones that entered
                while (tabuList.size() > tasksCount) {
                    tabuList.remove();
                }
            }
            //Empty candidate list
            candidateList.clear();
        }
        //**End of algorithm**
        System.out.println("Best sequence reached for " + tasksCount + " tasks and " + processorsInfo.size() + " processors with (" + --iterations + ") iterations made: ");
        printStatics(bestSequence);
        ArrayList<Integer> assignment = new ArrayList<Integer>();
        for(ProcessorObject i: bestSequence.getSequence()) {
            assignment.add(i.getId());
        }
        return assignment;

    }

    /**
     * Method for generating the neighborhood of a sequence by permutations
     *
     * @param actualSequence The reference array to generate neighbors from
     * @return Returns an array of neighbors
     */
    private ArrayList<GenericMetaObject> generateNeighborhood(GenericMetaObject actualSequence) {

        //Array that will contain the neighbors
        ArrayList<GenericMetaObject> neighborhood = new ArrayList<GenericMetaObject>();
        Set<Integer> positionsToChange = new HashSet<Integer>();
        //Temporal array to hold a neighbor sequence of 'actualSequence'
        //Loop for generating new neighbors by permuting the 'actualSequence' and adding only different ones
        while (positionsToChange.size() < neighborhoodSize) {
            positionsToChange.add(random.nextInt(tasksCount));
        }
        for (Integer i : positionsToChange) {
            neighborhood.add(recalculateFitness(actualSequence.getSequence(), actualSequence.getLoads(), i));
        }
        return neighborhood;
    }

    private void printStatics(GenericMetaObject bestSequence) {
        //Initialization of variables
        ArrayList<ProcessorObject> tempP_object = new ArrayList<ProcessorObject>(bestSequence.getSequence());
        StringBuilder assembledSequence = new StringBuilder("");
        double[] tasks = new double[processorsInfo.size()];
        for (int i = 0; i < processorsInfo.size(); i++) {
            tasks[i] = 0.0;
        }
        //Obtaining the load of each processor and the tasks assigned to it
        for (int i = 0; i < tasksCount; i++) {
            tasks[tempP_object.get(i).getId()] += 1;
        }
        //Shows the statics
        System.out.println("Fitness: " + bestSequence.getFitness());
        for (int i = 0; i < processorsInfo.size(); i++) {
            System.out.println("Processor[" + i + "]: (tasks: " + tasks[i] + " / load: " + bestSequence.getLoads()[i] + ")");
        }
    }

    private GenericMetaObject recalculateFitness(ArrayList<ProcessorObject> oldSequence, double[] oldSequenceLoad, Integer index) {
        //Initialization of the variables
        ArrayList<ProcessorObject> newSequence = new ArrayList<ProcessorObject>(oldSequence);
        double[] oldProcessorLoad = oldSequenceLoad.clone();
        double[] newProcessorLoad = oldProcessorLoad.clone();
        ProcessorObject newProcessor = processorsInfo.get(random.nextInt(processorCount));

        newProcessorLoad[oldSequence.get(index).getId()] -= tasksList.get(index);
        newSequence.set(index, newProcessor);
        newProcessorLoad[newSequence.get(index).getId()] += tasksList.get(index);

        double pAverage = 0.0;
        for (int i = 0; i < processorCount; i++) {
            pAverage += newProcessorLoad[i];
        }
        pAverage /= processorCount;
        double distFrom_pAverage = 0.0;
        for (int i = 0; i < processorCount; i++) {
            distFrom_pAverage += Math.abs(newProcessorLoad[i] - pAverage);
        }

        distFrom_pAverage /= processorCount;

        return (new GenericMetaObject(newSequence, newProcessorLoad, distFrom_pAverage));
    }

    private void firstFitness(GenericMetaObject bestSequence) {
        ArrayList<ProcessorObject> sequence = new ArrayList<ProcessorObject>(bestSequence.getSequence());
        double distFrom_pAverage = 0.0;
        double[] processorLoad = new double[processorsInfo.size()];
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
        pAverage /= processorsInfo.size();

        //The deference between the processor load and the average of the processor load is calculated and returned
        for (int i = 0; i < processorLoad.length; i++) {
            distFrom_pAverage += Math.abs(processorLoad[i] - pAverage);
        }
        bestSequence.setLoads(processorLoad);
        bestSequence.setFitness(distFrom_pAverage / processorsInfo.size());
    }

    //Init first sequence generator
    private ArrayList<ProcessorObject> firstSequence() {
        ArrayList<ProcessorObject> fSequence = new ArrayList<ProcessorObject>();
        for (int i = 0; i < tasksCount; i++) {
            fSequence.add(processorsInfo.get(nextProcessor()));
        }
        return fSequence;
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
    //End first sequence generator
}

