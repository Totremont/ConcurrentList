package utn.totremont;
import utn.totremont.strategy.FineGrainedStrategy;
import utn.totremont.strategy.OptimisticSynchronizationStrategy;
import utn.totremont.strategy.Strategy;

public class LinkedList
{
    private Strategy strategy = new OptimisticSynchronizationStrategy();
    private volatile Node HEAD = strategy.getHEAD();   //Default

    public void setStrategy(Strategy type)
    {
        if(type == null) throw new RuntimeException("LinkedList: No strategy provided");
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

    public Node getFirst()
    {
        return HEAD.getNext();
    }

    public void clear()
    {
        HEAD = strategy.getHEAD();
    }
}
