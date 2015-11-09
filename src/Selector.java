import java.util.HashMap;

class Selector {
	
	//Data members
	
	BitMap target;
	HashMap<String,Cluster> selection = new HashMap<String,Cluster>();
	
	//Constructor
	Selector(){}
	Selector(BitMap target) {
		this.target = target;
	}
	
	//Functions
	void genFromCluster(String input) {
		this.selection.get(input).map.genNewBitMap();
	}
	
	void target(BitMap target) {
		this.target = target;
	}
	
	void clear() {
		selection.clear();
		System.out.println("Selector is now empty");
	}
	
	void remove(String key) {
		if(selection.containsKey(key)) {
			selection.remove(key);
		} else {
			System.out.println("Selector key " + key + " was empty");
		}
	}
	
	String all(String key){return all(key,"");}
	
	String all(String key, String options){
		boolean reverse = false;
		if(options.contains("-rev")) {reverse = true;}
		
		selection.put(key, new Cluster(target,new int[target.pixels.length]));
		int[] hold = selection.get(key).selected;
		for(int i = 0; i< target.pixels.length; i++) {
			if(!reverse) {
				hold[i] = i;
			} else {
				hold[i] = target.pixels.length - (i+1);
			}
		}
		return key;
	}
}
