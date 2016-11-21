package MetaheuristicsAndTools.AntColony;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Author: Carlos
 * Date:  11/12/2014
 */
public class AntSorter implements Comparator<Pair<Double, ArrayList<Ant>>> {
    @Override
    public int compare(Pair<Double, ArrayList<Ant>> o1, Pair<Double, ArrayList<Ant>> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }
}