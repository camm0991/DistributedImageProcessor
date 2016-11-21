package MetaheuristicsAndTools.AntColony;

import javafx.util.Pair;

import java.util.Comparator;

/**
 * Author: Carlos
 * Date:  11/12/2014
 */
public class NodeSorter implements Comparator<Pair<Integer, Double>> {

    @Override
    public int compare(Pair<Integer, Double> integerDoublePair, Pair<Integer, Double> integerDoublePair2) {
        return integerDoublePair2.getValue().compareTo(integerDoublePair.getValue());
    }
}