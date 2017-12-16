import akka.actor.UntypedActor;

/**
 * This actor reads the file, counts the vowels and sends the result to
 * Estimator. 
 *
 * @author Priyank
 */
public class FirstCounter extends UntypedActor {
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String){
			int count = CountVowels.getCount((String)msg);
			getSender().tell(count, null);
		}
	}
	
}