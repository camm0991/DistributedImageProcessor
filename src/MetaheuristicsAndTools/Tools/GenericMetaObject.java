package MetaheuristicsAndTools.Tools;

import java.util.ArrayList;

/**
 * Created by Carlos on 28/11/2014.
 */
public class GenericMetaObject {
    private ArrayList<ProcessorObject> sequence;
    private Double fitness;
    private double[] loads;

    public GenericMetaObject() {}

    public GenericMetaObject(ArrayList<ProcessorObject> sequence, double[] loads, Double fitness) {
        this.setSequence(new ArrayList<ProcessorObject>(sequence));
        this.setLoads(loads);
        this.setFitness(new Double(fitness));
    }

    public GenericMetaObject(GenericMetaObject annealingObject) {
        this(annealingObject.getSequence(), annealingObject.getLoads(),annealingObject.getFitness());
    }

    //Getters and Setters
    public ArrayList<ProcessorObject> getSequence() {
        return sequence;
    }

    public void setSequence(ArrayList<ProcessorObject> sequence) {
        this.sequence = sequence;
    }

    public Double getFitness() {
        return fitness;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public double[] getLoads() {
        return loads;
    }

    public void setLoads(double[] loads) {
        this.loads = loads;
    }
}
