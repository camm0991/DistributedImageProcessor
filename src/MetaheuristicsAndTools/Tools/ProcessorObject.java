package MetaheuristicsAndTools.Tools;

/**
 * Created by Carlos on 25/10/2014.
 */
public class ProcessorObject {
    private Integer id;
    private Double power;

    public ProcessorObject(ProcessorObject p) {
        this(p.getId(), p.getPower());
    }

    public ProcessorObject(Integer id, Double power) {
        this.id = id;
        this.power = power;
    }

    public Integer getId() {
        return id;
    }

    public Double getPower() {
        return power;
    }
}
