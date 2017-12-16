import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {

    public static List<SimulationEvent> events;
    public static Map<String, Integer> customersWithOrders = new HashMap<String, Integer>();
    public static List<Customer> currentCapacity = new ArrayList<Customer>();
    public static List<Customer> completedOrder = new ArrayList<Customer>();
    //public static ConcurrentLinkedList<Customer> orderList = new ConcurrentLinkedList<Customer>();
    public static List<Customer> orderList = new ArrayList<Customer>();

    public static int machineCapacity = 4;


    //create machines
    public static Machine coffeeMaker;
    public static Machine grill;
    public static Machine fryer;


    /**
     * Used by other classes in the simulation to log events
     *
     * @param event
     */
    public static void logEvent(SimulationEvent event) {
        events.add(event);
        System.out.println(event);
    }


    /**
     * Function responsible for performing the simulation. Returns a List of
     * SimulationEvent objects, constructed any way you see fit. This List will
     * be validated by a call to Validate.validateSimulation. This method is
     * called from Simulation.main(). We should be able to test your code by
     * only calling runSimulation.
     * <p>
     * Parameters:
     *
     * @param numCustomers    the number of customers wanting to enter the coffee shop
     * @param numCooks        the number of cooks in the simulation
     * @param numTables       the number of tables in the coffe shop (i.e. coffee shop capacity)
     * @param machineCapacity the capacity of all machines in the coffee shop
     * @param randomOrders    a flag say whether or not to give each customer a random order
     */
    public static List<SimulationEvent> runSimulation(
            int numCustomers, int numCooks,
            int numTables,
            int machineCapacity,
            boolean randomOrders) {
        //This method's signature MUST NOT CHANGE.


        //We are providing this events list object for you.
        //  It is the ONLY PLACE where a concurrent collection object is
        //  allowed to be used.
        events = Collections.synchronizedList(new ArrayList<SimulationEvent>());


        // Start the simulation
        logEvent(SimulationEvent.startSimulation(numCustomers,
                numCooks,
                numTables,
                machineCapacity));


        // Set things up you might need


        // Start up machines
        coffeeMaker = new Machine("CoffeeMaker", new Food("coffee", 100), machineCapacity);

        grill = new Machine("Grill", new Food("burger", 500), machineCapacity);

        fryer = new Machine("Fryer", new Food("fries", 350), machineCapacity);


        // Let cooks in
        Thread[] cooks = new Thread[numCooks];
        for (int i = 0; i < numCooks; i++) {
            cooks[i] = new Thread(
                    new Cook("Cook " + i));
        }
        for (int i = 0; i < numCooks; i++) {
            cooks[i].start();
        }


        // Build the customers.
        Thread[] customers = new Thread[numCustomers];
        LinkedList<Food> order;


        //if random order is not set them fixed order
        if (!randomOrders) {
            order = new LinkedList<Food>();
            order.add(FoodType.burger);
            order.add(FoodType.fries);
            order.add(FoodType.fries);
            order.add(FoodType.coffee);

            for (int i = 0; i < customers.length; i++) {
                customers[i] = new Thread(
                        new Customer("Customer " + i, order)
                );
            }
        }


        //else random order
        else {
            for (int i = 0; i < customers.length; i++) {
                Random rnd = new Random(27);
                int burgerCount = rnd.nextInt(3);
                int friesCount = rnd.nextInt(3);
                int coffeeCount = rnd.nextInt(3);
                order = new LinkedList<Food>();
                for (int b = 0; b < burgerCount; b++) {
                    order.add(FoodType.burger);
                }
                for (int f = 0; f < friesCount; f++) {
                    order.add(FoodType.fries);
                }
                for (int c = 0; c < coffeeCount; c++) {
                    order.add(FoodType.coffee);
                }
                customers[i] = new Thread(
                        new Customer("Customer " + (i), order)
                );
            }
        }


        // Now "let the customers know the shop is open" by
        //    starting them running in their own thread.
        for (int i = 0; i < customers.length; i++) {
            customers[i].start();
            //NOTE: Starting the customer does NOT mean they get to go
            //      right into the shop.  There has to be a table for
            //      them.  The Customer class' run method has many jobs
            //      to do - one of these is waiting for an available
            //      table...
        }


        try {
            // Wait for customers to finish
            //   -- you need to add some code here...

            //waits for the customer threads to end
            for (int i = 0; i < customers.length; i++) {
                customers[i].join();
            }

            // Then send cooks home...
            // The easiest way to do this might be the following, where
            // we interrupt their threads.  There are other approaches
            // though, so you can change this if you want to.
            for (int i = 0; i < cooks.length; i++)
                cooks[i].interrupt();
            for (int i = 0; i < cooks.length; i++)
                cooks[i].join();

        } catch (InterruptedException e) {
            System.out.println("Simulation thread interrupted.");
        }


        // Shut down machines
        logEvent(SimulationEvent.machineEnding(grill));
        logEvent(SimulationEvent.machineEnding(fryer));
        logEvent(SimulationEvent.machineEnding(coffeeMaker));


        // Done with simulation
        logEvent(SimulationEvent.endSimulation());

        return events;
    }

    /**
     * Entry point for the simulation.
     *
     * @param args the command-line arguments for the simulation.  There
     *             should be exactly four arguments: the first is the number of customers,
     *             the second is the number of cooks, the third is the number of tables
     *             in the coffee shop, and the fourth is the number of items each cooking
     *             machine can make at the same time.
     */
    public static void main(String args[]) throws InterruptedException {
        int numCustomers = 10;
        int numCooks = 1;
        int numTables = 5;
        boolean randomOrders = true;

        // Run the simulation and then
        //   feed the result into the method to validate simulation.
        System.out.println("Did it work? " +
                Validate.validateSimulation(
                        runSimulation(
                                numCustomers, numCooks,
                                numTables, machineCapacity,
                                randomOrders
                        )
                )
        );
    }
}