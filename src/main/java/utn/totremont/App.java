package utn.totremont;


import utn.totremont.strategy.FineGrainedStrategy;
import utn.totremont.strategy.NonBlockingStrategy;
import utn.totremont.strategy.OptimisticSynchronizationStrategy;
import utn.totremont.worker.AddWorker;
import utn.totremont.worker.ContainsWorker;
import utn.totremont.worker.RemoveWorker;
import utn.totremont.strategy.Strategy;

import java.util.ArrayList;
import java.util.Scanner;

public class App
{
    //Scenario
    private static int threadCount = 10;
    private static int[] opShare = {40,30,30};  // ADD,REMOVE
    private static int thOpCount = 10;
    private static boolean verbose = false;
    private final static ArrayList<Strategy> strategies = new ArrayList<>();
    private static int runsPerStrategy = 5;
    private static int range = 25;


    private final static LinkedList list = new LinkedList();
    private final static Supervisor supervisor = new Supervisor(list);
    private final Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {
        try
        {
            App game = new App();
            game.home();
        }
        catch (Exception e)
        {
            System.out.println("Algo salió mal.\n" + e.getMessage());
        }

    }

    public App()
    {
        strategies.add(new FineGrainedStrategy());
        strategies.add(new OptimisticSynchronizationStrategy());
        strategies.add(new NonBlockingStrategy());
    }

    private void home() throws InterruptedException
    {
        loop: while(true)
        {
            System.out.flush();
            clearConsole();
            showParameters();
            switch (getUserInput())
            {
                case 1:
                    begin();
                    break;
                case 2:
                    changeParameters();
                    break;
                case 3:
                    input.close();
                    System.exit(0);
                    break loop;
            }
        }
    }

    private void showParameters()
    {
        final StringBuilder text = new StringBuilder();
        text.append("\n==== TP Programación Concurrente ====\n");
        text.append("\n=== Escenario actual ===\n");
        text.append(String.format("Hilos: %d\n", threadCount));
        text.append(String.format("Operaciones por hilo: %d\n", thOpCount));
        text.append(String.format("Proporción de hilos (ADD,REMOVE,CONTAINS): (%d,%d,%d)\n", opShare[0], opShare[1],opShare[2]));
        text.append(String.format("¿Hilos verbosos?: %s\n", verbose ? "SI" : "NO"));
        text.append("Estrategias: ");
        for(Strategy strategy : strategies) text.append(strategy.name()).append(", ");
        if(!strategies.isEmpty())
        {
            int size = text.length();
            text.delete(size - 2,size); //delete last ", ".
        }
        text.append("\nCorridas por estrategia: ").append(runsPerStrategy);
        text.append(String.format("\nRango de valores en lista: [0-%d]",range));
        text.append("\n-----------\n");
        System.out.print(text);

    }

    private int getUserInput()
    {
        final StringBuilder text = new StringBuilder("¿Qué querés hacer?\n");
        text.append("[1] - CORRER escenario actual.\n");
        text.append("[2] - MODIFICAR escenario actual.\n");
        text.append("[3] - SALIR.\n");
        System.out.println(text);
        int[] option;
        do
        {
            System.out.print("Escriba el número: ");
            option = getOrParseInput(true,input);
        } while (option == null || option[0]  < 1 || option[0] > 3);
        return option[0];
    }

    private void begin()
    {
        supervisor.clear();
        int[] workers =
                {
                        Math.round(threadCount * (opShare[0] / 100f)), // ADD
                        0,  // REMOVE
                        0, // CONTAINS
                };
        workers[1] = Math.round(threadCount * (opShare[1] / 100f)) + workers[0];
        workers[2] = threadCount;
        int innerIndex = 0;
        //Create workers
        for(int i = 0 ; i < threadCount; i++)
        {
            if(i < workers[innerIndex])
            {
                switch(innerIndex)
                {
                    case 0:
                        supervisor.supervise(new AddWorker(thOpCount,(i+1),list,verbose,range));
                        break;
                    case 1:
                        supervisor.supervise(new RemoveWorker(thOpCount,(i+1),list,verbose,range));
                        break;
                    default:
                        supervisor.supervise(new ContainsWorker(thOpCount,(i+1),list,verbose,range));
                        break;
                }
            } else
            {
                innerIndex++;
                i--;    //Otherwise you will miss a worker
            }
        }
        supervisor.setStrategies(strategies,runsPerStrategy);
        supervisor.execute();

        // Will block and wait until execute has finished to request an input.
        input.nextLine();
    }

    private void changeParameters()
    {
        clearConsole();
        System.out.println("\n==== Modificar parámetros====\n");
        int[] option;
        do
        {
            System.out.printf("Operaciones por hilo [Actual: %d | Max: 20]: ", thOpCount);
            option = getOrParseInput(true,input);
        }
        while (option == null || option[0] < 1 || option[0] > 20);
        thOpCount = option[0];

        System.out.printf("Proporción (ADD,REMOVE,CONTAINS) [Actual: (%d:%d:%d)]\n", opShare[0],opShare[1],opShare[2]);
        int[] values;
        do
        {
            System.out.println("Tenga en cuenta que los valores deben sumar 100%");
            System.out.print("Escribir en formato <%:%:%> (ej: 60:30:10): ");
            values = getOrParseInput(false,input);

        } while (values == null || (values[0] + values[1] + values[2]) != 100);
        opShare = values;

        final int minThreads = 100 / min(values);

        System.out.printf("Necesita como mínimo %d hilo(s) para alcanzar la proporción indicada.\n",minThreads);

        do
        {
            System.out.printf("Hilos [Actual: %d | Max: 15]: ",threadCount);
            option = getOrParseInput(true,input);
        }
        while (option == null || option[0] < 1 || option[0] > 15);
        threadCount = option[0];

        System.out.printf("¿Los hilos son verbosos? [Actual: %s]\n",verbose ? "SI" : "NO");
        do
        {
            System.out.print("Escribir 1 para SI, 0 para NO: ");
            option = getOrParseInput(true,input);
        }
        while (option == null || option[0] < 0 || option[0] > 1);
        verbose = option[0] == 1;

        do
        {
            System.out.printf("Corridas por estrategia [Actual: %d | Max: 10]: ",runsPerStrategy);
            option = getOrParseInput(true,input);
        }
        while (option == null || option[0] < 0 || option[0] > 10);
        runsPerStrategy = option[0];

        do
        {
            System.out.printf("Rango de valores en lista [Actual: [0-%d] | Max: [0-40]]: \n",range);
            System.out.println("Establece el rango de valores que los hilos pueden utilizar al realizar las operaciones en la lista.");
            System.out.println("Un valor menor implica mayores colisiones entre operaciones, lo que aumenta el uso de exclusión mutua");
            System.out.print("Ingrese el valor máximo: ");
            option = getOrParseInput(true,input);
        }
        while (option == null || option[0] < 0 || option[0] > 40);
        range = option[0];

    }

    //Este es un wrapper para manejar inputs inválidos de forma cómoda, sin tener que usar try-catchs.
    // Get : Obtiene un int (index 0) ; Parse : Obtiene 2 ints <%,&> (Proporción ADD,REMOVE).
    private int[] getOrParseInput(boolean get, Scanner in)
    {
        try {
            String line = in.nextLine();    //Consume toda la línea y evita que se acumulen tokens.
            if (get) return new int[]{Integer.parseInt(line)};
            else
            {
                String[] aux = line.split(":");
                return new int[]{Integer.parseInt(aux[0]), Integer.parseInt(aux[1]),Integer.parseInt(aux[2]) };
            }
        } catch(Exception e) {return null;}
    }

    private void clearConsole()
    {
        try {
            String os = System.getProperty("os.name");
            Runtime.getRuntime().exec(os.contains("Windows") ? "cls" : "/bin/sh clear");
        }
        //Silently catch exception
        catch (Exception e)
        {
            //e.getMessage();
        }
    }

    private int min(int[] values)   //3 values
    {
        if(values[0] < values[1])
            if(values[0] < values[2]) return values[0];
            else return values[2];
        else if(values[1] < values[2]) return values[1];
            else return values[2];
    }
}