package utn.totremont.worker;

import utn.totremont.LinkedList;
import utn.totremont.Node;

public class RemoveWorker extends Worker
{

    public RemoveWorker(int operations, int id, LinkedList list, boolean verbose, int maxRange)
    {
        super(operations, id, list,WorkType.REMOVE,verbose,maxRange);
    }

    @Override
    protected void action(StringBuilder result)
    {
        final int MAX = 21;
        for(int i = 0; i < this.operations; i++)
        {
            // Add numbers between 0 to max
            int value = (int) (Math.random() * (maxRange+1));
            Node success = this.list.removeNode(value);
            // Fue un nodo eliminado por esta operación?
            if(verbose)
            {
                if (success != null) result.append("[ELIMINADO] ");
                else result.append("[NO ELIMINADO] ");
                result.append(String.format("Hice una operación %s con el valor: %d\n", workType.name(), value));
            }
        }
    }
}
