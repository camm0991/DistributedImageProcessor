package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Carlos on 02/09/2014.
 */
public interface CallServer extends Remote{
    public ArrayList<Remote> getProcessor(ArrayList<Integer> imageWeights) throws RemoteException;
}
