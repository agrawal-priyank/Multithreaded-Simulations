import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates more child actors
 * {@code WordCountInAFileActor} depending upon the number of files in the given
 * directory structure
 * 
 * @author Priyank
 */
public class Estimator extends UntypedActor {
	
	private String[] files = null;
	private ActorRef partner;
	private double p1 = 1.0, p2 = 0.99, p3 = 1.01;
	private int firstHalfCount, index = 0;
	Result[] result = new Result[10];
	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if(msg instanceof ActorRef){
			partner = (ActorRef) msg;
			files = getFiles();
			for(String file : files){
				firstHalfCount = 0;
				int size = file.length();
				String firstHalf = file.substring(0, size / 2);
				String secondHalf = file.substring(size / 2);
				firstHalfCount = CountVowels.getCount(firstHalf);								
				partner.tell(secondHalf, getSelf());				
			}			
		} else if(msg instanceof Integer){
			int secondHalfCount = ((Integer) msg).intValue();
			int estimatedCount = (int)(firstHalfCount * p1 * 2);
			int actualCount = firstHalfCount + secondHalfCount;
			if(estimatedCount < actualCount){
				p1 *= p3;				
			} else{
				p1 *= p2;
			}
			result[index++] = new Result(actualCount, estimatedCount, p1);
		} else{
			getContext().sender().tell(result, getContext().sender());
		}
	}
	
	private String[] getFiles() throws FileNotFoundException{
		File folder = new File("src/AkkaText");
		String[] files = new String[10];
		int index = 0;
		for(File file : folder.listFiles()){
			if(file.getName().equalsIgnoreCase(".DS_Store")){
				continue;
			}
			files[index++] = scanFile(file);
		}
		return files;
	}
	
	private String scanFile(File file) throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner scan = new Scanner(file);
		while(scan.hasNext()){
			sb.append(scan.nextLine());
		}
		scan.close();
		return sb.toString();
	}

}