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
	
	static double pixDist(int pixela, int pixelb, int width) {
		int xa= pixela % width;
		int ya = pixela / width;
		int xb= pixelb % width;
		int yb = pixelb / width;
		
		return Math.pow(Math.pow(yb - ya, 2) + Math.pow(xb - xa, 2), .5);
	}
	
	String circle(String key, String options) {
		int len = this.target.pixels.length;
		
		int pixel = len/2 + 500;
		int width = (int)this.target.Width;
		
		int[] selectAr = new int[len];
		int index = 0;
		
		for(int i = 0; i<this.target.pixels.length; i++) {
			if(pixDist(i,pixel,width)%2 < Math.sin(i*.4) || pixDist(i,pixel,width)%2 < Math.cos(i*.2) ) {
				selectAr[index] = i;
				index++;
				//System.out.println("Added pix " + i);
			}
		}
		
		int[] fselectAr = new int[index];
		for(int i = 0; i<index; i++) {
			fselectAr[i] = selectAr[i];
		}
		
		selection.put(key, new Cluster(target, fselectAr));
		
		return key;
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
