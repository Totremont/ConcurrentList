package Clases;

public class ConcurrentList {
    /*
    * GRANULARIDAD FINA
    * */

    public Node head;

    public ConcurrentList(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    public void add(Object o){
        int key = o.hashCode();
        Node pred = null;
        Node curr = null;

        try{
            pred = head;
            pred.lock();
            curr = pred.next;
            curr.lock();

            while(curr.key < key){
                pred.unlock();
                pred = curr;
                curr = curr.next;
                curr.lock();
            }

            if(key == curr.key) return;

            Node nuevo = new Node(o);
            nuevo.next = curr;
            pred.next = nuevo;
            return;

        }finally {
            if(pred != null) pred.unlock();
            if(curr != null) curr.unlock();
        }
    }

    public void remove(Object o){
        int key = o.hashCode();
        Node pred = null;
        Node curr = null; // Pred: anterior, curr: actual

        try{
            pred = head;
            pred.lock();
            curr = pred.next;
            curr.lock();

            while(curr.key < key){
                pred.unlock();
                pred = curr.next;
                curr = pred.next;
                curr.lock();
            }

            if(key == curr.key){
                pred.next = curr.next;
            }
        }finally {
            if(pred != null) pred.unlock();
            if(curr != null) curr.unlock();
        }

    }
}
