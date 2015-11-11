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
	
	//
	//
	//
	//Class control functions
	//
	//
	//
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
	
	
	//
	//Box
	//
	
	String box(String key, String options) {
		int x = 0;
		int y = 0;
		int boxHeight = 0;
		int boxWidth = 0;
		
		if(options.contains("-x")) {x = intOp(options,"-x");}
		if(options.contains("-y")) {x = intOp(options,"-y");}
		if(options.contains("-h")) {x = intOp(options,"-h");}
		if(options.contains("-w")) {x = intOp(options,"-w");}
		
		
		
		int len = this.target.pixels.length;
		int width = (int)this.target.Width;
		
		int[] selectAr = new int[len];
		int index = 0;
		
		
		
		for(int i = 0; i<len; i++) {
			if((i%width > width-boxWidth + x) && (i/width > width-boxHeight + y)) {
				System.out.println(i);
				selectAr[index] = i;
				index++;
			}
		}
		
		int[] fselectAr = new int[index];
		for(int i = 0; i<index; i++) {
			fselectAr[i] = selectAr[i];
		}
		
		selection.put(key, new Cluster(target, fselectAr));
		return key;
	}
	
	//
	//Circle
	//
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
	
	//
	//
	//
	//Selection Math
	//
	//
	//
	

	static double pixDist(int pixela, int pixelb, int width) {
		int xa= pixela % width;
		int ya = pixela / width;
		int xb= pixelb % width;
		int yb = pixelb / width;
		
		return Math.pow(Math.pow(yb - ya, 2) + Math.pow(xb - xa, 2), .5);
	}
	
	
	//
	//
	//
	//String Manipulation
	//
	//
	//
	
	static int intOp(String input, String option) {
		String[] inputAr = input.split(option);
		
		int out = -999;
		
		if(inputAr.length >1) {
			out = 0;
			
			for(int i = 0; Character.isDigit(inputAr[1].charAt(i)) && i<inputAr[1].length(); i++) {
				out = out*10 + (int)(inputAr[1].charAt(i) - '0');
			}
		}
		
		return out;
	}
	
	static double doubleOp(String input,String option) {
		String[] inputAr = input.split(option);
		
		int out = -999;
		int place = 0;
		if(inputAr.length >1) {
			boolean placeCount = false;
			out = 0;
			
			for(int i = 0; (Character.isDigit(inputAr[1].charAt(i)) || inputAr[1].charAt(i) == '.') && i<inputAr[1].length(); i++) {
				if(inputAr[1].charAt(i) == '.') {
					placeCount = true;
				} else {
					out = out*10 + (int)(inputAr[1].charAt(i) - '0');
					if(placeCount) {place++;};
				}
			}
		}
		
		return out * Math.pow(10, -place);
	}
}
