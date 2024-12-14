package utn.totremont.strategy;

import utn.totremont.Node;
import utn.totremont.utils.Pair;

import java.util.concurrent.locks.ReentrantLock;

public class OptimisticSynchronizationStrategy implements Strategy{

    @Override
    public Node addNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while(true){ // La unica forma de salir (agregar nodo) es que se acepte la validacion y se retorne el Node

            Pair<OptimisticSynchronizationNode> nodes = null;

            try {
                nodes = findPosition(HEAD, key);

                nodes.getPred().lock();
                if (nodes.hasCurr) nodes.getCurr().lock();

                if(!nodes.hasCurr){
                    // pred es el nodo final
                    OptimisticSynchronizationNode node = new OptimisticSynchronizationNode(value, null);
                    nodes.getPred().setNext(node);
                    return node;
                }else if(nodes.getCurr().getKey() > key){
                    if(validate(nodes.getPred(), nodes.getCurr(), HEAD)){ // Si no se cumple la validación, se reinicia el bucle
                        if(nodes.getCurr().getKey() == key){
                            return nodes.getCurr(); // Si ya está en la lista, no se agrega y retorna ese elemento
                        }else{
                            OptimisticSynchronizationNode node = new OptimisticSynchronizationNode(value, nodes.getCurr());
                            nodes.getPred().setNext(node);
                            return node;
                        }
                    }
                }

            }finally {
                assert nodes != null;
                nodes.getPred().unlock();
                if(nodes.hasCurr) nodes.getCurr().unlock();
            }
        }

    }

    @Override
    public Node removeNode(Object value, Node HEAD) {

        int key = value.hashCode();

        while(true){

            Pair<OptimisticSynchronizationNode> nodes = null;

            try {

                nodes = findPosition(HEAD, key);

                nodes.getPred().lock();
                if (nodes.hasCurr) nodes.getCurr().lock();

                if(nodes.getCurr().getKey() > key){
                    if(validate(nodes.getPred(), nodes.getCurr(), HEAD)){ // Si no se cumple la validación, se reinicia el bucle
                        if(nodes.getCurr().getKey() == key){ // Si es el elemento, se elimina y retorna
                            nodes.getPred().setNext(nodes.getCurr().getNext());
                            return nodes.getCurr();
                        }else return null;
                    }
                }

            }finally {
                assert nodes != null;
                nodes.getPred().unlock();
                if(nodes.hasCurr) nodes.getCurr().unlock();
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
        return new OptimisticSynchronizationNode(Integer.MIN_VALUE, null);
    }

    private Pair<OptimisticSynchronizationNode> findPosition(Node HEAD, int key){

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
    }

    // Objetivo: ver que predv.next = currv
    public Boolean validate(Node predv, Node currv, Node HEAD){

        OptimisticSynchronizationNode node =  (OptimisticSynchronizationNode) HEAD;

        while(node.getKey() <= predv.getKey()){
            if(node == predv) return node.getNext() == currv;

            node = (OptimisticSynchronizationNode) node.getNext();

            if(node == null) break;
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
