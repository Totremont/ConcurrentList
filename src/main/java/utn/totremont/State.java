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
        int option = 0;
        do {
            System.out.print("\rEscriba el número: ");
            option = input.nextInt();
        } while (option < 1 || option > 3);
        return option;
    }

    private void changeParameters()
    {
        System.out.flush();
        System.out.println("\n==== TP Programación Concurrente | Modificar ====\n");
        int option;
        do {
            System.out.printf("\rHilos [Actual: %d | Max: 15]: ",threadCount);
            option = input.nextInt();
        } while (option < 1 || option > 25);
        threadCount = option;

        do {
            System.out.printf("\rOperaciones por hilo [Actual: %d | Max: 20]: ", thOpCount);
            option = input.nextInt();
        } while (option < 1 || option > 20);
        thOpCount = option;

        System.out.printf("Proporción (ADD,REMOVE) [Actual: (%d,%d)]\n", opShare[0],opShare[1]);
        int[] values = new int[2];
        do {
            System.out.print("\rEscribir en formato <%,%> (ej: 25,75): ");
            String[] aux = input.nextLine().split(",");
            if(aux.length >= 2)
            {
                values[0] = Integer.parseInt(aux[0]);
                values[1] = Integer.parseInt(aux[1]);
            }

        } while (values[0] + values[1] != 100);
        opShare = values;

    }
}