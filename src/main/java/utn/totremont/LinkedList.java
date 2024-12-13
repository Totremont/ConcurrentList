package utn.totremont;
import utn.totremont.strategy.FineGrainedStrategy;
import utn.totremont.strategy.LockFreeStrategy;
import utn.totremont.strategy.Strategy;

public class LinkedList
{

    private Strategy strategy = new FineGrainedStrategy();
    private Node HEAD = strategy.getHEAD();   //Default
    //private int size = 0;

    public void setStrategy(Strategy type)
    {
        if(type == null) throw new RuntimeException();
        this.strategy = type;
        this.HEAD = strategy.getHEAD();
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
