package utn.totremont.worker;

import utn.totremont.LinkedList;

public class AddWorker extends Worker
{

    public AddWorker(int operations, int id, LinkedList list,boolean verbose)
    {
        super(operations, id, list,WorkType.ADD,verbose);
    }

    @Override
    protected void action(StringBuilder result)
    {
        final int MAX = 21;
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            int value = (int) (Math.random() * MAX);
            this.list.addNode(value);
            if(verbose) result.append("Hice una operación ADD con el valor:").append(value).append("\n");

        }
    }
}
