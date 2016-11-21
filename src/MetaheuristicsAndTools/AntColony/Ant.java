package MetaheuristicsAndTools.AntColony;

import java.util.ArrayList;

/**
 * Created by Carlos on 01/12/2014.
 */
public class Ant {
    private Double load;
    private Double power;
    private ArrayList<Integer> ids;
    private int id;

    Ant(int id, double power) {
        this.ids = new ArrayList<Integer>();
        this.setLoad(0.0);
        this.power = power;
        this.id = id;
    }

    public Double getLoad() {
        return load;
    }

    public void setLoad(Double load) {
        this.load = load;
    }

    public Double getPower() {
        return power;
    }

    public Integer getId() {
        return id;
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public void addIds(Integer id) {
        this.ids.add(id);
    }
}
