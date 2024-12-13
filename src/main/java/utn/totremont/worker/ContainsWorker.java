package utn.totremont.worker;

import utn.totremont.LinkedList;

public class ContainsWorker extends Worker
{
    private final int MAX = 21;

    public ContainsWorker(int operations, int id, LinkedList list)
    {
        super(operations, id, list,WorkType.ADD);
    }

    @Override
    protected void action(StringBuilder result)
    {
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            this.list.contains((int) (Math.random() * MAX));
        }
    }
}
