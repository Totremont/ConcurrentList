package utn.totremont;


import java.util.Scanner;

public class State
{
    private static int threadCount;
    private static int[] opShare = new int[3];
    private static int thOpCount;
    private final Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {
        State game = new State();
        game.home();

    }

    private void home()
    {
        while(true)
        {
            StringBuilder text = new StringBuilder("==== TP Programación Concurrente | Inicio ====\n\n");
            text.append("* Escenario actual *\n");
            text.append(String.format("Hilos: %d\n", threadCount));
            text.append(String.format("Operaciones por hilo: %d\n", thOpCount));
            text.append(String.format("Proporción (ADD,REMOVE): (%d,%d)\n", opShare[0], opShare[1]));
            text.append("\n-----------\n");
            System.out.flush();
            System.out.println(text);

            switch (getUserInput()) {
                case 1:
                    input.close();
                    break;
                case 2:
                    changeParameters();
                    break;
                case 3:
                    input.close();
                    System.exit(0);
                    break;
            }
        }

    }

    private int getUserInput()
    {
        StringBuilder text = new StringBuilder("¿Qué querés hacer?\n");
        text.append("[1] - CORRER escenario actual.\n");
        text.append("[2] - MODIFICAR escenario actual.\n");
        text.append("[3] - SALIR.\n");
        System.out.println(text);
        int[] option;
        do {
            System.out.print("\rEscriba el número: ");
            option = getOrParseInput(true,input);
        } while (option == null || option[0]  < 1 || option[0] > 3);
        return option[0];
    }

    private void changeParameters()
    {
        System.out.flush();
        System.out.println("\n==== TP Programación Concurrente | Modificar ====\n");
        int[] option;

        do {
            System.out.printf("\rOperaciones por hilo [Actual: %d | Max: 20]: ", thOpCount);
            option = getOrParseInput(true,input);
        } while (option == null || option[0] < 1 || option[0] > 20);
        thOpCount = option[0];

        System.out.printf("Proporción (ADD,REMOVE) [Actual: (%d,%d)]\n", opShare[0],opShare[1]);
        int[] values;
        do {
            System.out.print("\rEscribir en formato <%,%> (ej: 25,75): ");
            values = getOrParseInput(false,input);

        } while (values == null || (values[0] + values[1]) != 100);
        opShare = values;

        final int minThreads = values[0] < values[1] ? 100 / values[0] : 100 / values[1];

        System.out.printf("Considere que necesita como mínimo %d hilo(s) para poder conseguir la proporción indicada.\n",minThreads);

        do {
            System.out.printf("\rHilos [Actual: %d | Max: 15]: ",threadCount);
            option = getOrParseInput(true,input);
        } while (option == null || option[0] < 1 || option[0] > 25);
        threadCount = option[0];

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
                String[] aux = line.split(",");
                return new int[]{Integer.parseInt(aux[0]), Integer.parseInt(aux[1]) };
            }
        } catch(Exception e) {return null;}
    }
}