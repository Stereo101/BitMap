
class Demon {
	Selector sel;
	
	Demon(Selector target) {
		this.sel = target;
	}
	
	
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
	
}

