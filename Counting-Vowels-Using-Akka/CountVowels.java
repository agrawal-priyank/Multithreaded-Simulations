public class CountVowels {
	
	public static int getCount(String str){
		int count = 0;
		for(char ch : str.toCharArray()){
			ch = Character.toLowerCase(ch);
			if(ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'y'){
				count++;
			}
		}
		return count;
	}
	
}