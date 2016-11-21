package MetaheuristicsAndTools.AntColony;

/**
 * Created by Carlos on 01/12/2014.
 */
public class Bridge {
    private Double pheromone;
    private Double attractiveness;

    public Bridge(double pheromone, double attractiveness) {
        this.setPheromone(pheromone);
        this.setAttractiveness(attractiveness);
    }

    public Double getPheromone() {
        return pheromone;
    }

    public void setPheromone(Double pheromone) {
        this.pheromone = pheromone;
    }

    public Double getAttractiveness() {
        return attractiveness;
    }

    public void setAttractiveness(Double attractiveness) {
        this.attractiveness = attractiveness;
    }
}
