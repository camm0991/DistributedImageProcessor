package MetaheuristicsAndTools.Tools;

/**
 * Created by Carlos on 27/11/2014.
 */
public class CalculatePower {

    public double getPower() {
        long time0 = System.nanoTime();
        for(int i = 0;  i < 2000000; i++);
        return  (System.nanoTime() - time0) / 1000000000.0;
    }
}
