package utn.totremont.worker;

import utn.totremont.LinkedList;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiConsumer;

// Function that each thread will run
public abstract class Worker implements Runnable
{
    protected int operations = 0;
    protected int id = -1;
    protected final LinkedList list;
    protected final WorkType workType;
    protected BiConsumer<Worker,String> supervisor;
    protected final boolean verbose;

    protected Worker(int operationCount, int id, LinkedList list, WorkType workType, boolean verbose)
    {
        this.operations = operationCount;
        this.id = id;
        this.list = list;
        this.workType = workType;
        this.verbose = verbose;
    }

    public void subscribe(BiConsumer<Worker,String> supervisor)
    {
        this.supervisor = supervisor;
    }

    // What the worker actually do. It receives an StringBuilder to append additional info (optional).
    protected abstract void action(StringBuilder result);

    @Override
    public void run()
    {
        Instant beginning = Instant.now();
        StringBuilder result = new StringBuilder();
        this.action(result);
        double total = Duration.between(beginning, Instant.now()).getNano() / 1e6;
        result.append(String.format("*Hice %d operaciones %s en %1.3f milisegundos*", operations,workType.name(),total));
        if(supervisor != null) supervisor.accept(this,result.toString());
    }

    protected enum WorkType
    {
        ADD,REMOVE
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Worker)) return false;
        return this.id == ((Worker) obj).getId();
    }
}
