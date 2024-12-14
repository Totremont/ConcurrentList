package utn.totremont;

import utn.totremont.worker.Worker;

import java.util.HashSet;

// Receives results from threads
public class Supervisor
{
    private final HashSet<Worker> workers = new HashSet<>();
    private final StringBuilder results = new StringBuilder();
    private final LinkedList list;

    public Supervisor(LinkedList list)
    {
        this.list = list;
    }

    public void supervise(Worker worker)
    {
        if(workers.add(worker)) worker.subscribe(this::onReceive);
    }

    // Execute scene
    //Synchronized Evita race conditions con onReceive al modificar el hashset.
    public synchronized void execute()
    {
        results.append("\n==== Ejecución del escenario ====\n").append(String.format("Creando %d hilo(s)",workers.size()));
        if(workers.isEmpty())
        {
            results.append("\n=== No hay hilos por ejecutar. Adiós. ===");
            System.out.println(results);
            System.exit(0);
        }
        workers.forEach(it -> new Thread(it).start());
    }

    //Receiving results from threads
    public synchronized void onReceive(Worker worker, String result)
    {
        results.append(String.format("\n\n[Hilo %d dice]\n",worker.getId())).append(result);
        workers.remove(worker);
        if(workers.isEmpty())  // Execution ended
        {
            results.append("\n\n=== Todos los hilos han terminado. ===\n");
            results.append("\n\nEstado de la lista:\n");
            Node node = list.getHEAD().getNext();
            int index = 0;
            while(node != null)
            {
                results.append(String.format("[Index: %d | Valor: %d]\n", index, node.getKey()));
                index++;
                node = node.getNext();
            }
            System.out.println(results);
            System.out.println("\nPresionar tecla para continuar.");

        }
    }

    //Evita race conditions con onReceive

}
