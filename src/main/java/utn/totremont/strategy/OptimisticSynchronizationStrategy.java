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
                // Navegar por la lista hasta encontrar la posición donde insertar el nodo
                while (curr != null && curr.getKey() < key) {
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                if (curr != null) curr.lock();

                if (validate(pred, curr, HEAD)) {
                    // Verificar si el nodo ya existe en la posición correcta
                    if (curr != null && curr.getKey() == key) return null;
                    else {
                        // Crear un nuevo nodo y enlazarlo
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
                // Navegar por la lista hasta encontrar la posición del nodo a eliminar
                while (curr != null && curr.getKey() < key) {
                    pred = curr;
                    curr = (OptimisticSynchronizationNode) curr.getNext();
                }

                pred.lock();
                if (curr != null) curr.lock();

                if (validate(pred, curr, HEAD)) {
                    // Si el nodo no existe, retornar null
                    if (curr == null || curr.getKey() != key) return null;

                    // Actualizar la referencia del nodo previo para saltar al siguiente nodo
                    pred.setNext(curr.getNext());
                    return curr; // Retornar el nodo eliminado
                }
            } finally {
                pred.unlock();
                if (curr != null) curr.unlock();
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
