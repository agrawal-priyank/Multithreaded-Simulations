import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {

	private final String cookName;
	public List<Food> foodCooked = new ArrayList<Food>();
	private Customer customer;
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String cookName) {
		this.cookName = cookName;
	}

	public String toString() {
		return cookName;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		try {
			while(true) {
				synchronized(Simulation.orderList){
					while(true){
						if(Simulation.orderList.size() == 0)
							Simulation.orderList.wait();
							else
								break;
					}
					customer = Simulation.orderList.remove(0);
					Simulation.orderList.notifyAll();
//					customer = Simulation.orderList.getHead().get().item;
//					Simulation.orderList.remove(customer);
//					Simulation.orderList.notifyAll();
				}
				
				for(int i = 0; i < customer.getOrder().size(); i++){
					Food orderedFood = customer.getOrder().get(i);
					String test = orderedFood.name;
					if(test.equals("burger")){
						synchronized(Simulation.grill.foodList){
							Simulation.grill.makeFood(this, customer.getOrderNum());
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.burger , customer.getOrderNum()));
							Simulation.grill.foodList.notifyAll();
						}
					} else if(test.equals("fries")){
						synchronized(Simulation.fryer.foodList){
							Simulation.fryer.makeFood(this,customer.getOrderNum());		
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.fries , customer.getOrderNum()));
							Simulation.fryer.foodList.notifyAll();
						}
					} else if(test.equals("coffee")){
						synchronized(Simulation.coffeeMaker.foodList){
							Simulation.coffeeMaker.makeFood(this,customer.getOrderNum());
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.coffee , customer.getOrderNum()));
							Simulation.coffeeMaker.foodList.notifyAll();
						}
					}
				}
				
				synchronized(foodCooked){
					synchronized(Simulation.completedOrder){
						while(!(foodCooked.size() == customer.getOrder().size())){
							foodCooked.wait();
							foodCooked.notifyAll();
						}
						customer.setOrderStatus(true);
						Simulation.completedOrder.add(customer);
						Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customer.getOrderNum()));
						Simulation.completedOrder.notifyAll();
					}
				}
				foodCooked = new LinkedList<Food>();
			}
		}
		catch(InterruptedException e) {
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}