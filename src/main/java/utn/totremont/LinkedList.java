package utn.totremont;

public class LinkedList
{
    enum Strategy
    {
        FINE_GRAINED, OPTIMISTIC, LOCK_FREE;
    }

    private final Node HEAD = new Node(Integer.MIN_VALUE,null);
    private Strategy strategy = Strategy.FINE_GRAINED;
    private int size = 0;

    public void setStrategy(Strategy type){this.strategy = type;}
    public Strategy getStrategy(){return strategy;}

    public Node addNode(Object value)
    {
        throw new UnsupportedOperationException();
    }

    public Node removeNode(Object value){ throw new UnsupportedOperationException(); }

    public boolean contains(Object value){ throw new UnsupportedOperationException(); }

    public int size(){ return size; }

    public boolean validate()   //Differs with strategy
    {
        throw new UnsupportedOperationException();
    }



}
