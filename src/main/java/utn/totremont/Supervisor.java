package utn.totremont;

import utn.totremont.strategy.Strategy;
import utn.totremont.worker.Worker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Receives results from threads
public class Supervisor
{
    private final ArrayList<Worker> workers = new ArrayList<>();
    private final ArrayList<Strategy> strategies = new ArrayList<>();
    private final ArrayList<Double> times = new ArrayList();
    private final StringBuilder results = new StringBuilder();
    private final LinkedList list;

    //Strategy being executed now
    private int strategyIndex = 0;
    //strategies * runs per strategy = total
    private int runIndex = 0;
    private int runsPerStrategy = 0;
    private int totalRuns = 0;
    private Instant lastRunTime;
    private int workersFinish = 0;

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
        runsPerStrategy = 1;
        runIndex = 0;
        workersFinish = 0;
    }

    public void setStrategies(List<Strategy> strategies, int runsPerStrategy)
    {
        this.strategies.clear();
        this.strategies.addAll(strategies);
        this.runsPerStrategy = runsPerStrategy;
        totalRuns = runsPerStrategy * strategies.size();
    }

    // Execute scene
    //Synchronized Evita race conditions con onReceive al modificar el hashset.
    public synchronized void execute()
    {
        results.append("\n\n==== Ejecución del escenario ====\n");
        if(workers.isEmpty() || strategies.isEmpty() || runsPerStrategy <= 0)
        {
            results.append("\n=== No hay nada para ejecutar. Adiós. ===\n");
            System.exit(0);
        }
        forward();
        //Blocks calling object (App) until execution ends
        try
        {
            wait();
        }
        catch(Exception e)
        {
            System.out.println("Warning: thread calling execute() couldn't be blocked.");
        }
    }

    private synchronized void forward() //Continue with next run
    {
        workersFinish = 0;
        int aux = runIndex / runsPerStrategy;
        Strategy strategy = this.strategies.get(aux);
        if(aux == strategyIndex)
        {
            this.list.clear(); //Still on same strategy
        }
        else
        {
            strategyIndex = aux;
            this.list.setStrategy(strategy);
        }
        lastRunTime = Instant.now();
        results.append(String.format("\n=== Ejecutando %s en lista nueva. ===\n",strategy.name()));
        results.append(String.format("Corrida: %d/%d\n",(runIndex % runsPerStrategy)+1,runsPerStrategy));
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
            times.add(Duration.between(lastRunTime,Instant.now()).getNano() / 1e6);
            results.append("\n\n=== Todos los hilos han terminado. ===");
            results.append("\nEstado de la lista:\n");
            Node node = this.list.getFirst();
            int index = 0;
            if(node == null) results.append("Vacía.\n");
            else do
            {
                index++;
                results.append(String.format("[Elemento: %d | Valor: %d]\n", index, node.getKey()));
            } while((node = node.getNext()) != null);

            if(runIndex < (this.totalRuns - 1))
            {
                runIndex++;
                forward();
            }
            else    //Execution ended; Show overview
            {
                results.append("\n=== La ejecución ha concluído. ===\n");
                results.append("Resumen:\n");
                for (int i = 0; i < this.totalRuns; i++)
                {
                    results.append
                    (
                            String.format("Estrategia: %s (%d/%d) --> %1.3f milisegundos\n"
                                    ,strategies.get(i / runsPerStrategy).name(),
                                    (i % runsPerStrategy)+1, runsPerStrategy,times.get(i))
                    );
                }
                System.out.println(results);
                System.out.print("Presionar tecla para continuar.");
                //outputCSV(strategies.get(0),times.get(0));
                notifyAll();    //Wakes App() up
            }


        }
    }

    //Syncronized garantiza que no se comience con la siguiente ejecución antes de escribir el archivo.
    private synchronized void outputCSV(boolean headerOnly)
    {
        try(var rawWriter = new FileWriter("cl-results.csv");
            BufferedWriter writer = new BufferedWriter(rawWriter))
        {
            Strategy strategy = this.strategies.get(this.strategyIndex);
            if(headerOnly)
            {
                writer.write("Estrategia");
                for (int i = 0; i < this.workers.size(); i++) {
                    writer.write(String.format(",Hilo-%d-%s", i, this.workers.get(i).getWorkType().name()));
                }
                writer.write(",Supervisor\n\r");
            }
            else
            {
                writer.write(strategy.name());
                this.workers.forEach(it -> {
                    try {
                        writer.write("," + it.getLastRunTime());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch(IOException e)
        {
            //System.out.println("")
        }
    }

}
