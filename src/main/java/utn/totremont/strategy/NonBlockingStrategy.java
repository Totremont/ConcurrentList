package utn.totremont.strategy;

import utn.totremont.Node;
import utn.totremont.utils.Pair;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class NonBlockingStrategy implements Strategy {

    @Override
    public Node addNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        while (true)
        {
            Pair<NonBlockingNode> pair = find(((NonBlockingNode) HEAD),key);
            if(!pair.hasCurr || pair.getCurr().getKey() > key)
            {
                NonBlockingNode node = new NonBlockingNode(value,pair.getCurr());
                boolean valid = pair.getPred().next.compareAndSet(pair.getCurr(),node,false,false);
                if(valid) return node; //If not, retry
            }
            else return pair.getCurr();
        }
    }


    @Override
    public Node removeNode(Object value, Node HEAD)
    {
        int key = value.hashCode();
        while (true)
        {
            Pair<NonBlockingNode> pair = find(((NonBlockingNode) HEAD),key);
            if(pair.hasCurr && pair.getCurr().getKey() == key)
            {
                NonBlockingNode succ = (NonBlockingNode) pair.getCurr().getNext();
                boolean curr_marked = pair.getCurr().next.attemptMark(succ, true);
                if(curr_marked)
                {
                    pair.getPred().next.compareAndSet(pair.getCurr(), succ, false, false);
                    return pair.getCurr();
                }
            }
            else return null;
        }
    }


    @Override
    public Boolean contains(Object value, Node HEAD)
    {
        boolean marked;
        int key = value.hashCode();
        NonBlockingNode curr = (NonBlockingNode) HEAD;
        do curr = (NonBlockingNode) curr.getNext();
        while (curr != null && curr.getKey() < key);
        return curr != null && curr.getKey() == key && !curr.next.isMarked();
    }

    private Pair<NonBlockingNode> find(NonBlockingNode HEAD, int key)
    {
        NonBlockingNode pred = null, curr = null, succ = null;
        boolean[] curr_mark = {false};
        boolean curr_removed;
        retry: while (true) //Retry from the beginning...
        {
            pred = HEAD;
            curr = (NonBlockingNode) pred.getNext();
            while (curr != null && curr.getKey() < key)
            {
                succ = curr.next.get(curr_mark);
                while (curr_mark[0])    //If curr was deleted (logically)
                {
                    //Try to remove it physically
                    curr_removed = pred.next.compareAndSet(curr, succ, false, false);
                    if (!curr_removed) continue retry;  //Pred was also marked, retry...
                    curr = succ;
                    if(curr == null) break;
                    succ = curr.next.get(curr_mark);
                }
                pred = curr;
                curr = succ;
            }
            break retry;
        }
        return new Pair<>(pred, curr);
    }

    @Override
    public String name() {
        return "Non-Blocking Strategy";
    }

    @Override
    public Node getHEAD() {
        return new NonBlockingNode(Integer.MIN_VALUE, null);
    }

    public static class NonBlockingNode extends Node {

        private final AtomicMarkableReference<NonBlockingNode> next;

        public NonBlockingNode(Object value, Node next)
        {
            super(value, next);
            NonBlockingNode aux = next != null ? (NonBlockingNode) next : null;
            this.next = new AtomicMarkableReference<>(aux, false);
        }

        @Override
        public Node getNext()
        {
            return this.next.getReference();
        }

        @Override
        public void setNext(Node next)
        {
            throw new RuntimeException("Call to this method with this strategy is invalid. Use next's compareAndSet instead");
        }
    }
}
