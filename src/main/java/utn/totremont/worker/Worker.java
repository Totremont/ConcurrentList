package utn.totremont.worker;

import utn.totremont.LinkedList;

// Function that each thread will run
public abstract class Worker implements Runnable
{
    int operationCount = 0;
    int id = -1;
    private volatile LinkedList list;

    public Worker(int operationCount, int id, LinkedList list) {
        this.operationCount = operationCount;
        this.id = id;
        this.list = list;
    }
}
