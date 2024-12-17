package utn.totremont;

import utn.totremont.strategy.Strategy;
import utn.totremont.worker.Worker;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// Receives results from threads
public class Supervisor
{
    private final ArrayList<Worker> workers = new ArrayList<>();
    private final ArrayList<Strategy> strategies = new ArrayList<>();
    //Strategy being executed now
    private int strategyIndex = 0;
    private Instant lastRun;
    private int workersFinish = 0;
    private final ArrayList<Double> times = new ArrayList();
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

    public void clear()
    {
        workers.clear();
        strategies.clear();
        times.clear();
        strategyIndex = 0;
        workersFinish = 0;
    }

    public void setStrategies(List<Strategy> strategies)
    {
        this.strategies.addAll(strategies);
    }


    // Execute scene
    //Synchronized Evita race conditions con onReceive al modificar el hashset.
    public synchronized void execute(){
        results.append("\n\n==== Ejecución del escenario ====\n");
        if(workers.isEmpty() || strategies.isEmpty())
        {
            results.append("\n=== No hay hilos o estrategias por ejecutar. Adiós. ===");
            System.out.println(results);
            System.exit(0);
        }
        forward();
        //Blocks calling object (App) until execution ends
        try
        {
            wait();
        } catch(Exception e)
        {
            System.out.println("Warning: thread calling execute() couldn't be blocked.");
        }
    }

    private synchronized void forward()
    {
        Strategy strategy = this.strategies.get(strategyIndex);
        this.list.setStrategy(strategies.get(strategyIndex));
        lastRun = Instant.now();
        results.append(String.format("\n=== Ejecutando estrategia: %s en lista nueva. ===\n",strategy.name()));
        results.append(String.format("Creando %d hilo(s)\n",workers.size()));
        workers.forEach(it -> new Thread(it).start());
    }

    //Receiving results from threads
    public synchronized void onReceive(Worker worker, String result)
    {
        results.append(String.format("\n[Hilo %d dice]\n",worker.getId())).append(result);
        workersFinish++;
        if(workersFinish == workers.size())  // Execution ended
        {
            times.add(strategyIndex, Duration.between(lastRun,Instant.now()).getNano() / 1e6);
            results.append("\n\n=== Todos los hilos han terminado. ===\n");
            results.append("Estado de la lista:\n");
            Node node = this.list.getFirst();
            int index = 0;
            if(node == null) results.append("Vacía.\n");
            else do
            {
                index++;
                results.append(String.format("[Elemento: %d | Valor: %d]\n", index, node.getKey()));
            } while((node = node.getNext()) != null);
            if((strategyIndex + 1) == strategies.size())
            {
                results.append("\n=== La ejecución ha concluído. ===\n");
                results.append("Resumen:\n");
                for (int i = 0; i < strategies.size(); i++) {
                    results.append("Estrategia: ")
                            .append(strategies.get(i).name())
                            .append(" --> ").append(String.format("%1.3f milisegundos\n", times.get(i)));
                }
                System.out.println(results);
                System.out.print("Presionar tecla para continuar.");
                outputCSV(strategies.get(0),times.get(0));
                notifyAll();    //Wakes App() up
            }
            else //Another strategy needs to be executed
            {
                strategyIndex++;
                workersFinish = 0;
                forward();
            }


        }
    }

    private synchronized void outputCSV(Strategy strategy, double time)
    {
        FileWriter writer;
        try
        {
            writer = new FileWriter("cl-results.csv");
            writer.write("Estrategia");
            for(int i = 0; i < this.workers.size(); i++)
            {
                writer.write(String.format(",Hilo-%d-%s",i,this.workers.get(i).getWorkType().name()));
            }
            writer.write(",Supervisor\n\r" + strategy.name());
            this.workers.forEach(it -> {
                try {
                    writer.write("," + it.getLastRunTime());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.write("," + time);
            writer.close();
            System.out.println("Output file created");
        } catch(IOException e){}
    }

}
