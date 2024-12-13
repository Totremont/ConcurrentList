package utn.totremont;

import utn.totremont.worker.Worker;

import java.util.HashSet;
import java.util.stream.Collectors;

// Receives results from threads
public class Supervisor
{
    private final HashSet<Worker> workers = new HashSet<>();
    private final StringBuilder results = new StringBuilder();

    public void supervise(Worker worker)
    {
        if(workers.add(worker)) worker.subscribe(this::onReceive);
    }

    // Execute scene
    public void execute()
    {
        results.append("\n==== Ejecución del escenario ====\n").append(String.format("Creando %d hilo(s)\n",workers.size()));
        if(workers.isEmpty())
        {
            results.append("No hay hilos por ejecutar. Adiós.");
            System.out.println(results);
            System.exit(0);
        }
        else for(Worker worker : workers)
        {
            new Thread(worker).start();
        }
    }

    //Receiving results from threads
    public synchronized void onReceive(Worker worker, String result)
    {
        results.append(String.format("Hilo %d dice: %s\n",worker.getId(),result));
        workers.remove(worker);
        if(workers.isEmpty())  // Execution ended
        {
            results.append("Todos los hilos han terminado. Adiós.");
            System.out.println(results);
            System.exit(0);

        }
    }
}
