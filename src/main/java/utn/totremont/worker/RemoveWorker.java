package utn.totremont.worker;

import utn.totremont.LinkedList;

public class RemoveWorker extends Worker
{
    private final int MAX = 21;

    public RemoveWorker(int operations, int id, LinkedList list)
    {
        super(operations, id, list,WorkType.REMOVE);
    }

    @Override
    protected void action(StringBuilder result)
    {
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            this.list.removeNode((int) (Math.random() * MAX));
        }
    }
}
