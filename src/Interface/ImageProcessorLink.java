package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Carlos on 28/08/2014.
 */
public interface ImageProcessorLink extends Remote{
    public byte[] imageNegative(byte[] img) throws RemoteException;
    public byte[] imageGreyScale(byte[] img) throws RemoteException;
    public byte[] imageSepia(byte[] img) throws RemoteException;
    public Double processorPower() throws RemoteException;
    public String processorId() throws RemoteException;
}
