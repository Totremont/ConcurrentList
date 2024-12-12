package utn.totremont;
import utn.totremont.strategy.LockFreeStrategy;
import utn.totremont.strategy.Strategy;

public class LinkedList
{
    /*enum Strategy
    {
        FINE_GRAINED, OPTIMISTIC, LOCK_FREE;
    }*/

    private final Node HEAD = new Node(Integer.MIN_VALUE,null);
    private Strategy strategy = null;
    private int size = 0;

    public void setStrategy(Strategy type)
    {
        if(type == null) throw new RuntimeException();
        this.strategy = type;
    }
    public String getStrategy()
    {
        return strategy.name();
    }

    public Node addNode(Object value)
    {
        return strategy.addNode(value,HEAD);
    }

    public Node removeNode(Object value)
    {
        return strategy.removeNode(value,HEAD);
    }

    public boolean contains(Object value)
    {
        return strategy.contains(value,HEAD);
    }

    public boolean validate()   //
    {
        if(strategy instanceof LockFreeStrategy) return ((LockFreeStrategy) strategy).validate();
        else throw new UnsupportedOperationException("Provided strategy can't handle operation");
    }



}
