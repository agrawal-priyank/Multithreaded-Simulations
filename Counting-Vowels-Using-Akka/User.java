import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * Main class for your estimation actor system.
 *
 * @author Priyank
 */
public class User {

	public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create("EstimatonSystem");
		Props estimatorProps = Props.create(Estimator.class);
		Props counterProps = Props.create(FirstCounter.class);
		ActorRef estimatorNode = system.actorOf(estimatorProps, "Estimator_Node");
		ActorRef counterNode = system.actorOf(counterProps, "First_Counter_Node");
		estimatorNode.tell(counterNode, null);
		Thread.sleep(2000);
        Future<Object> future = Patterns.ask(estimatorNode, "Result", 2000);
		try{
			Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
	        Result[] result = (Result[])Await.result(future, timeout.duration());
	        for(Result output : result){
	        	if(output == null){
		        	throw new Exception();
	        	}
        		System.out.println(output.toString());
	        }
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			system.terminate();
		}
 	}
	
}