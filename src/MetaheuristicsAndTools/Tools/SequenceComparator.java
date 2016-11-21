package MetaheuristicsAndTools.Tools;
import java.util.Comparator;

/**
 * Created by Carlos on 28/11/2014.
 */
public class SequenceComparator implements Comparator<GenericMetaObject> {
    @Override
    public int compare(GenericMetaObject o1, GenericMetaObject o2) {
        return o1.getFitness().compareTo(o2.getFitness());
    }

}
