package utn.totremont.worker;

import utn.totremont.LinkedList;

public class ContainsWorker extends Worker
{
    public ContainsWorker(int operations, int id, LinkedList list,boolean verbose, int maxRange)
    {
        super(operations, id, list,WorkType.CONTAINS,verbose,maxRange);
    }

    @Override
    protected void action(StringBuilder result)
    {
        final int MAX = 21;
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            int value = (int) (Math.random() * (maxRange+1));
            boolean contains = this.list.contains(value);
            if(verbose)
            {
                if (contains) result.append("[VERDADERO] ");
                else result.append("[FALSO] ");
                result.append(String.format("Hice una operación %s con el valor: %d\n", workType.name(), value));
            }

        }
    }
}
