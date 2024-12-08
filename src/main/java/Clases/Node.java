package Clases;

import java.util.concurrent.Semaphore;

public class Node {
    /*
    * GRANULARIDAD FINA
    * */

    public Object item;
    public int key;
    public Node next;
    private Semaphore lock;

    public Node(Object item){
        this.item = item;
        this.key = item.hashCode();
        lock = new Semaphore(1, true);
    }

    public void lock(){
        lock.acquireUninterruptibly();
    }

    public void unlock(){
        lock.release();
    }
}
