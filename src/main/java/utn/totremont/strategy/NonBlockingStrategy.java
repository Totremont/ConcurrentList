package utn.totremont.strategy;

import utn.totremont.Node;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class NonBlockingStrategy implements Strategy {

    @Override
    public Node addNode(Object value, Node HEAD) {
        int key = value.hashCode();

        NonBlockingNode pred;
        NonBlockingNode curr;

        while (true) { // hasta que funcione el cas
            pred = (NonBlockingNode) HEAD;
            curr = (NonBlockingNode) pred.getNext();

            while (curr != null && curr.getKey() < key) {
                pred = curr;
                curr = (NonBlockingNode) curr.getNext();
            }

            if (curr != null && curr.getKey() == key) { // ya está en la lista
                return curr;
            }

            NonBlockingNode newNode = new NonBlockingNode(value, curr);

            if (pred.next.compareAndSet(curr, newNode, false, false)) {
                return newNode;
            }

        }
    }


    @Override
    public Node removeNode(Object value, Node HEAD) {
        int key = value.hashCode();

        NonBlockingNode pred, curr, succ;

        while (true) { // hasta que acepte el cas
            pred = (NonBlockingNode) HEAD;
            curr = (NonBlockingNode) pred.getNext();

            while (curr != null && curr.getKey() < key) {
                pred = curr;
                curr = (NonBlockingNode) curr.getNext();
            }

            if (curr == null || curr.getKey() != key) {
                return null;
            }

            succ = (NonBlockingNode) curr.getNext();

            if (!curr.next.attemptMark(succ, true)) {
                continue;
            }

            if (pred.next.compareAndSet(curr, succ, false, false)) {
                return curr;
            }
        }
    }


    @Override
    public Boolean contains(Object value, Node HEAD) {
        int key = value.hashCode();
        NonBlockingNode curr = (NonBlockingNode) HEAD.getNext();

        while (curr != null && curr.getKey() < key) {
            curr = (NonBlockingNode) curr.getNext();
        }

        return curr != null && curr.getKey() == key && !curr.next.isMarked();
    }

    @Override
    public String name() {
        return "Non-Blocking strategy";
    }

    @Override
    public Node getHEAD() {
        return new NonBlockingNode(Integer.MIN_VALUE, null);
    }

    public class NonBlockingNode extends Node {

        private AtomicMarkableReference<NonBlockingNode> next;

        public NonBlockingNode(Object value, Node next) {
            super(value, next);
            this.next = new AtomicMarkableReference<NonBlockingNode>((NonBlockingNode) super.getNext(), false);
        }

        @Override
        public Node getNext() {
            return this.next.getReference();
        }

    }
}
