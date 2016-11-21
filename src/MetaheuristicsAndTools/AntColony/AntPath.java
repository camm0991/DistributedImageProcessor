package MetaheuristicsAndTools.AntColony;

import java.util.ArrayList;

/**
 * Created by Carlos on 02/12/2014.
 */
public class AntPath {
    private ArrayList<Bridge> paths;

    AntPath(double defaultF, ArrayList<Integer> tasks) {
        setPaths(new ArrayList<Bridge>());
        for (int i = 0; i < tasks.size(); i++) {
            getPaths().add(new Bridge(defaultF, (1.0 / tasks.get(i))));
        }
    }

    public ArrayList<Bridge> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Bridge> paths) {
        this.paths = paths;
    }
}
