package utn.totremont.worker;

import utn.totremont.LinkedList;

import java.time.Duration;
import java.time.Instant;

public class AddWorker extends Worker
{
    private final int MAX = 21;

    public AddWorker(int operations, int id, LinkedList list)
    {
        super(operations, id, list,WorkType.ADD);
    }

    @Override
    protected void action(StringBuilder result)
    {
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            this.list.addNode((int) (Math.random() * MAX));
        }
    }
}
