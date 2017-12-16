public class Result {
	
	private int actualCount;
	private int estimatedCount;
	private double p1;
	
	public Result(int actualCount, int estimatedCount, double p1){
		this.actualCount = actualCount;
		this.estimatedCount = estimatedCount;
		this.p1 = p1;
	}
	
	public String toString(){
		return "Actual count: " + actualCount + " " 
				+ "Estimated count: " + estimatedCount + " "
				+ "P1: " + p1;
	}

}