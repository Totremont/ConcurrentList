package utn.totremont.strategy;

import utn.totremont.Node;
import utn.totremont.utils.Pair;

import java.util.concurrent.locks.ReentrantLock;

public class OptimisticSynchronizationStrategy implements Strategy{

    @Override
    public Node addNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while (true) { // La única forma de salir (agregar nodo) es que se acepte la validación y se retorne el Node

            OptimisticSynchronizationNode pred = (OptimisticSynchronizationNode) HEAD;
            OptimisticSynchronizationNode curr = (OptimisticSynchronizationNode) pred.getNext();

            try {
                while (curr != null && curr.getKey() < key) {
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                if (curr != null) curr.lock();

                if (validate(pred, curr, HEAD)) {
                    if (curr != null && curr.getKey() == key) return curr;
                    else
                    {
                        OptimisticSynchronizationNode node = new OptimisticSynchronizationNode(value, curr);
                        pred.setNext(node);
                        return node;
                    }
                }
            } finally {
                pred.unlock();
                if (curr != null) curr.unlock();
            }
        }
    }


    @Override
    public Node removeNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while (true) { // La única forma de salir (eliminar nodo) es que se acepte la validación y se retorne el Node

            OptimisticSynchronizationNode pred = (OptimisticSynchronizationNode) HEAD;
            OptimisticSynchronizationNode curr = (OptimisticSynchronizationNode) pred.getNext();

            try {
                while (curr != null && curr.getKey() < key) {
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                if (curr != null) curr.lock();

                if (validate(pred, curr, HEAD))
                {
                    if (curr == null || curr.getKey() != key) return null;

                    pred.setNext(curr.getNext());
                    return curr;
                }
            } finally
            {
                pred.unlock();
                if (curr != null) curr.unlock();
            }
        }
    }


    @Override
    public Boolean contains(Object value, Node HEAD) {
        int key = value.hashCode();

        OptimisticSynchronizationNode pred = null;
        OptimisticSynchronizationNode curr = null;

        try {
            pred = (OptimisticSynchronizationNode) HEAD;
            curr = (OptimisticSynchronizationNode) pred.getNext();

            while (curr != null && curr.getKey() < key) {
                pred = curr;
                curr = (OptimisticSynchronizationNode) curr.getNext();
            }

            pred.lock();
            if (curr != null) curr.lock();

            if (validate(pred, curr, HEAD)) {
                return curr != null && curr.getKey() == key;
            } else {
                return false;
            }
        } finally {
            if (pred != null) pred.unlock();
            if (curr != null) curr.unlock();
        }
    }


    @Override
    public String name() {
        return "Optimistic Strategy";
    }

    @Override
    public Node getHEAD() {
        return new OptimisticSynchronizationNode(Integer.MIN_VALUE, null);
    }

    // Objetivo: ver que predv.next = currv
    public Boolean validate(Node predv, Node currv, Node HEAD){

        OptimisticSynchronizationNode node =  (OptimisticSynchronizationNode) HEAD;

        while(node.getKey() <= predv.getKey())
        {
            if(node == predv) return node.getNext() == currv;

            node = (OptimisticSynchronizationNode) node.getNext();
        }

        return false;
    }

    private class OptimisticSynchronizationNode extends Node {

        private final ReentrantLock lock;

        public OptimisticSynchronizationNode(Object value, Node next) {
            super(value, next);
            this.lock = new ReentrantLock();
        }

        public void lock(){ lock.lock(); }
        public void unlock(){ lock.unlock(); }

    }
}
