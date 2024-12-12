package utn.totremont.strategy;

import utn.totremont.Node;

public interface Strategy
{
    public Node addNode(Object value, Node HEAD);

    public Node removeNode(Object value, Node HEAD);

    public Boolean contains(Object value, Node HEAD);

    public String name();

}
