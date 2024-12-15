package utn.totremont.strategy;

import utn.totremont.Node;
import utn.totremont.utils.Pair;

import java.util.concurrent.locks.ReentrantLock;

public class OptimisticSynchronizationStrategy implements Strategy{

    @Override
    public Node addNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while(true){ // La unica forma de salir (agregar nodo) es que se acepte la validacion y se retorne el Node

            OptimisticSynchronizationNode pred = (OptimisticSynchronizationNode) HEAD;
            OptimisticSynchronizationNode curr = (OptimisticSynchronizationNode) pred.getNext();

            try {

                while(curr.getKey() < key){
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                curr.lock();

                if(validate(pred, curr, HEAD)){
                    if(curr.getKey() == key) return null;
                    else {
                        OptimisticSynchronizationNode node = new OptimisticSynchronizationNode(value, curr);
                        pred.setNext(node);
                        return node;
                    }
                }

            }finally {
                pred.unlock();
                curr.unlock();
            }
        }

    }

    @Override
    public Node removeNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while(true){ // La unica forma de salir (agregar nodo) es que se acepte la validacion y se retorne el Node

            OptimisticSynchronizationNode pred = (OptimisticSynchronizationNode) HEAD;
            OptimisticSynchronizationNode curr = (OptimisticSynchronizationNode) pred.getNext();

            try {

                while(curr.getKey() < key){
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                curr.lock();

                if(validate(pred, curr, HEAD)){
                    if(curr.getKey() != key) return null;
                    else {
                        pred.setNext(curr.getNext());
                        return curr;
                    }
                }

            }finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    @Override
    public Boolean contains(Object value, Node HEAD) {
        return null;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public Node getHEAD() {
        return new OptimisticSynchronizationNode(Integer.MIN_VALUE, new OptimisticSynchronizationNode(Integer.MAX_VALUE, null));
    }

    /*private Pair<OptimisticSynchronizationNode> findPosition(Node HEAD, int key){

        OptimisticSynchronizationNode pred = (OptimisticSynchronizationNode) HEAD;
        OptimisticSynchronizationNode curr = null;

        curr = pred.getNext() != null ? (OptimisticSynchronizationNode) pred.getNext() : null;
        if(curr != null)
        {
            while (curr.getKey() < key)
            {
                pred = curr;
                curr = curr.getNext() != null ? (OptimisticSynchronizationNode) curr.getNext() : null;
            }
        }
        return new Pair<OptimisticSynchronizationNode>(pred,curr);
    }*/

    // Objetivo: ver que predv.next = currv
    public Boolean validate(Node predv, Node currv, Node HEAD){

        OptimisticSynchronizationNode node =  (OptimisticSynchronizationNode) HEAD;

        while(node.getKey() <= predv.getKey()){
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
