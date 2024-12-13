package utn.totremont.strategy;

import utn.totremont.Node;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedStrategy implements Strategy
{

    @Override
    public Node addNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        FineGrainedNode pred = (FineGrainedNode) HEAD;
        FineGrainedNode curr = (FineGrainedNode) pred.getNext();
        try
        {
            curr.lock();
            while(curr.getKey() < key)
            {
                pred.unlock();
                pred = curr;
                curr = (FineGrainedNode) curr.getNext();
                curr.lock();
            }
            if(key == curr.getKey()) return curr;
            else return new FineGrainedNode(value, curr);
        }
        finally
        {
            pred.unlock();
            if(curr != null) curr.unlock();
        }
    }

    @Override
    public Node removeNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        FineGrainedNode pred = (FineGrainedNode) HEAD;
        FineGrainedNode curr = (FineGrainedNode) pred.getNext();
        try
        {
            curr.lock();
            while(curr.getKey() < key)
            {
                pred.unlock();
                pred = curr;
                curr = (FineGrainedNode) curr.getNext();
                curr.lock();
            }
            if(key == curr.getKey()) return curr;
            else return null;
        }
        finally
        {
            pred.unlock();
            if(curr != null) curr.unlock();
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
        return null;
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
