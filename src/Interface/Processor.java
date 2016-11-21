package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Carlos on 02/09/2014.
 */
public interface Processor extends Remote{
    public void processorAddress(String ip, Integer port) throws RemoteException;
}
