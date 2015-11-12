import javax.xml.bind.DatatypeConverter;

class Demon {
	Selector sel;
	
	Demon(Selector target) {
		this.sel = target;
	}
	
	//
	//Color
	//
	
	void color(String[] selIndex, String options) {
		for(String s : selIndex) {
			
			
			boolean undoable = options.contains("-u");
			if(undoable) {
				sel.target.histAdd();
			}
			
			byte[] hexbyte = {0,0,0};
			
			if(options.contains("-0x")) {
				hexbyte = DatatypeConverter.parseHexBinary(hexOp(options));
			}
			
			if(sel.selection.containsKey(s)) {
				Cluster clus = sel.selection.get(s);
				int[] hold = clus.selected;
				
				for(int i : hold) {
					clus.map.pixels[i][2] = hexbyte[0];
					clus.map.pixels[i][1] = hexbyte[1];
					clus.map.pixels[i][0] = hexbyte[2];
				}
			}
		}
	}
	void color(String selIndex) {color(new String[]{selIndex},"");}
	void color(String selIndex, String options) {color(new String[]{selIndex},options);}
	
	
	//
	//Black
	//
	
	void black(String[] selIndex, String options) {
		for(String s : selIndex) {
			boolean undoable = options.contains("-u");
			
			
			if(undoable) {
				sel.target.histAdd();
			}
			
			if(sel.selection.containsKey(s)) {
				Cluster clus = sel.selection.get(s);
				int[] hold = clus.selected;
				
				for(int i : hold) {
					clus.map.pixels[i][0] = (byte) 0;
					clus.map.pixels[i][1] = (byte) 0;
					clus.map.pixels[i][2] = (byte) 0;
				}
			}
		}
	}
	void black(String selIndex) {black(new String[]{selIndex},"");}
	void black(String selIndex, String options) {black(new String[]{selIndex},options);}
	
	

	//
	//Fuzz
	//
	void fuzz(String[] selIndex, String options) {
		for(String s : selIndex) {
			boolean undoable = options.contains("-u");
			
			
			if(undoable) {
				sel.target.histAdd();
			}
			
			if(sel.selection.containsKey(s)) {
				Cluster clus = sel.selection.get(s);
				int[] hold = clus.selected;
				
				
				for(int i = 0; i<hold.length; i++) {
					if(Math.random() > .5) {
						if(i-1 > 0) {
							BitMap.swapByteArray(clus.map.pixels[hold[i]],clus.map.pixels[hold[i-1]]);
						}
					} else {
						if(i+1 < hold.length) {
							BitMap.swapByteArray(clus.map.pixels[hold[i]],clus.map.pixels[hold[i+1]]);
						}
					}
				}
			}
		}
	}
	//Overload redirectors
	void fuzz(String selIndex) {fuzz(new String[]{selIndex},"");}
	void fuzz(String selIndex, String options) {fuzz(new String[]{selIndex},options);}
	
	
	
	
	//
	//SWAP
	//
	void swap(String[] selIndex1,String[] selIndex2, String options) {
		for(int s = 0; s<selIndex1.length && s<selIndex2.length; s++) {
			if(sel.selection.containsKey(selIndex1[s]) && sel.selection.containsKey(selIndex2[s])) {
				int[] hold1 = sel.selection.get(selIndex1[s]).selected;
				int[] hold2 = sel.selection.get(selIndex2[s]).selected;
				
				for(int i = 0; i<hold1.length && i<hold2.length; i++) {
					BitMap.swapByteArray(sel.target.pixels[hold1[i]],sel.target.pixels[hold2[i]]);
				}
			} else {
				System.out.println("ERROR, COULD NOT FIND SWAP PAIR (" + selIndex1[s] + "," + selIndex2[s] + ")");
			}
		}
	}
	//Overload redirectors
	void swap(String selIndex1, String selIndex2) {swap(new String[]{selIndex1},new String[]{selIndex2}, "");}
	void swap(String selIndex1, String selIndex2, String options) {swap(new String[]{selIndex1},new String[]{selIndex2}, options);}
	void swap(String[] selIndex1, String[] selIndex2) {swap(selIndex1,selIndex2, "");}
	
	
	//
	//
	//
	//String Manipulation Functions
	//
	//
	//
	
	static String hexOp(String input) {
		
		String[] inputAr = input.split("-0x");
		
		String hex = (inputAr[1].substring(0, 6));
		
		return hex;
	}
	
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

