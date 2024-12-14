package utn.totremont.strategy;

import utn.totremont.Node;
import utn.totremont.utils.Pair;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedStrategy implements Strategy
{

    @Override
    public Node addNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        Pair<FineGrainedNode> nodes = null;
        try
        {
            nodes = findPosition(HEAD,key);
            if(!nodes.hasCurr || nodes.getCurr().getKey() > key)
            {
                FineGrainedNode node = new FineGrainedNode(value,nodes.getCurr());
                nodes.getPred().setNext(node);
                return node;
            }
            else return nodes.getCurr();
        }
        finally
        {
            assert nodes != null;
            nodes.getPred().unlock();
            if(nodes.hasCurr) nodes.getCurr().unlock();
        }
    }

    @Override
    public Node removeNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        Pair<FineGrainedNode> nodes = null;
        try
        {
            nodes = findPosition(HEAD,key);
            if(nodes.hasCurr && nodes.getCurr().getKey() == key)
            {
                nodes.getPred().setNext(nodes.getCurr().getNext());
                return nodes.getCurr();
            }
            else return null;
        }
        finally
        {
            assert nodes != null;
            nodes.getPred().unlock();
            if(nodes.hasCurr) nodes.getCurr().unlock();
        }
    }

    @Override
    public Boolean contains(Object value, Node HEAD)
    {
        return null;
    }

    @Override
    public String name() {
        return "Fine grained strategy";
    }

    @Override
    public Node getHEAD()
    {
        return new FineGrainedNode(Integer.MIN_VALUE,null);
    }

    //Siempre encuentra el predecesor, mientras que el sucesor puede ser mayor, igual o null (no hay sucesor).
    // IMPORTANTE : Desbloquear los locks en el calling method
    private Pair<FineGrainedNode> findPosition(Node HEAD, int key)
    {
        FineGrainedNode pred = (FineGrainedNode) HEAD;
        FineGrainedNode curr = null;
        pred.lock();
        curr = pred.getNext() != null ? (FineGrainedNode) pred.getNext() : null;
        if(curr != null)
        {
            curr.lock();
            while (curr.getKey() < key)
            {
                pred.unlock();
                pred = curr;
                curr = curr.getNext() != null ? (FineGrainedNode) curr.getNext() : null;
                if (curr != null) curr.lock();
                else break;
            }
        }
        return new Pair<FineGrainedNode>(pred,curr);
    }

    private static class FineGrainedNode extends Node
    {
        private final ReentrantLock lock;

        public FineGrainedNode(Object value, Node next) {
            super(value, next);
            this.lock = new ReentrantLock();
        }

        public void lock(){lock.lock();}
        public void unlock(){lock.unlock();}

    }
}
