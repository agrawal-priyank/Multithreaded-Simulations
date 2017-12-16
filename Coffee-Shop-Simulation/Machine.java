import java.util.ArrayList;
import java.util.List;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a machineCap: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its machineCap, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
    public final String machineName;
    public final Food machineFoodType;

    //YOUR CODE GOES HERE...
    int machineCap;
    List<Food> foodList;

    /**
     * The constructor takes at least the name of the machine,
     * the Food item it makes, and its machineCap.  You may extend
     * it with other arguments, if you wish.  Notice that the
     * constructor currently does nothing with the machineCap; you
     * must add code to make use of this field (and do whatever
     * initialization etc. you need).
     */
    public Machine(String machineName, Food food, int capacity) {
        //YOUR CODE GOES HERE...
        this.machineFoodType = food;
        this.machineCap = capacity;
        this.foodList = new ArrayList<Food>();
        this.machineName = machineName;
    }


    /**
     * This method is called by a Cook in order to make the Machine's
     * food item.  You can extend this method however you like, e.g.,
     * you can have it take extra parameters or return something other
     * than Object.  It should block if the machine is currently at full
     * machineCap.  If not, the method should return, so the Cook making
     * the call can proceed.  You will need to implement some means to
     * notify the calling Cook when the food item is finished.
     */
    public void makeFood(Cook name, int orderNum) throws InterruptedException {
        //YOUR CODE GOES HERE...
        foodList.add(machineFoodType);
        Thread curr = new Thread(new CookAnItem(name, orderNum));
        curr.start();
    }

    //THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
    private class CookAnItem implements Runnable {
        Cook currentCook;

        public CookAnItem(Cook currentCook, int orderNum) {
            this.currentCook = currentCook;

        }

        public void run() {
            Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
            try {
                Thread.sleep(machineFoodType.cookTimeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
            //remove from list of foods to be completed for machine
            synchronized (foodList) {
                synchronized (currentCook.foodCooked) {
                    int l = foodList.size();
                    foodList.remove(l - 1);
                    foodList.notifyAll();
                    currentCook.foodCooked.add(machineFoodType);
                    currentCook.foodCooked.notifyAll();
                }
            }
        }
    }

    public String toString() {
        return machineName;
    }
}