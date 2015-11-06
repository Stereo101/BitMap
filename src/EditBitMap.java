import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Byte;
import java.util.Scanner;




public class EditBitMap {
	public static void main(String[] args) throws IOException {
		
		Console.start();
		
		/*
		BitMap lain = new BitMap("input",1);
		Selector choice = new Selector(lain);
		Demon belial = new Demon(choice);
		choice.all("1","-r");
		choice.all("2");
		belial.swap("1","2");
		
		lain.genNewBitMap("output");
		*/
	}
}

class BitMap {
	long FileSize;
	long reserved;
	long DataOffset;
	
	long Size;
	long Width;
	long Height;
	
	long Planes;
	long BitCount;
	long NumberOfColors;
	
	long Compression;
	long ImageSize;
	long XpixelsPerM;
	
	long YpixelsPerM;
	long ColorsUsed;
	long ColorsImportant;
	
	long SizeColorTable;
	byte[] Header;
	byte[][] pixels;
	
	byte[][][] pixelsHistory = new byte[100][][];
	int histCount = 0;
	int redos = 0;
	
	String outputName = "output";
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		//b.append("reserved ").append(reserved).append("\n");
		//b.append("DataOffset ").append(DataOffset).append("\n");
		//b.append("Size ").append(Size).append("\n");
		
		b.append("Width ").append(Width).append("\n");
		b.append("Height ").append(Height).append("\n");
		//b.append("Planes ").append(Planes).append("\n");
		
		b.append("BitCount ").append(BitCount).append("\n");
		b.append("NumberOfColors ").append(NumberOfColors).append("\n");
		//b.append("Compression ").append(Compression).append("\n");
		
		b.append("ImageSize ").append(ImageSize).append("\n");
		//b.append("XpixelsPerM ").append(XpixelsPerM).append("\n");
		//b.append("YpixelsPerM ").append(YpixelsPerM).append("\n");
		
		//b.append("ColorsUsed ").append(ColorsUsed).append("\n");
		//b.append("ColorsImportant ").append(ColorsImportant).append("\n");
		//b.append("SizeColorTable ").append(SizeColorTable).append("\n");
		
		//b.append("Size Header ").append(Header.length).append("\n");
		b.append("Number of pixels ").append(pixels.length).append("\n");
		
		return b.toString();
	}
	
	//Main constructor
	//
	//Reads in the first 54 bytes of a Bitmap and stores them as variables
	//
	//Slide and Div are 2 modifiers that change how the pixelArray is loaded
	//Using slide = div = 1 will load in correctly
	public BitMap(String name, double slide) {
		File file = new File("temp.txt");
		Path path = Paths.get(file.getAbsolutePath());
		
		byte[] data = null;
		
		try {
			data = Files.readAllBytes(path.resolveSibling(name + ".bmp"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileSize = longFromRange(3,4,data);
		reserved = longFromRange(7,4,data);
		DataOffset = longFromRange(11,4,data);
		Size = longFromRange(15,4,data);
		Width = longFromRange(19,4,data);
		Height = longFromRange(23,4,data);
		Planes = longFromRange(27,2,data);
		BitCount = longFromRange(29,2,data);
		
		switch((int)BitCount) {
		case(1): NumberOfColors=1; break;
		case(4): NumberOfColors=16; break;
		case(8): NumberOfColors=256; break;
		case(16): NumberOfColors=65536; break;
		case(24): NumberOfColors=16777216; break;
		}
		
		Compression = longFromRange(31,4,data);
		ImageSize = longFromRange(35,4,data);
		XpixelsPerM = longFromRange(39,4,data);
		YpixelsPerM = longFromRange(43,4,data);
		ColorsUsed = longFromRange(47,4,data);
		ColorsImportant = longFromRange(51,4,data);
		SizeColorTable = BitCount >=8 ? 0 : 4*BitCount;
		
		Header = new byte[(int)DataOffset];
		for(int i = 0; i<(int)DataOffset; i++) {
			Header[i] = data[i];
			//if(i%8 == 0) { System.out.println();}
			//System.out.printf("%5s" ,(Integer.toHexString(((int)Header[i]) & 0xFF)));
		}
		
		
		pixels = new byte[(int)((FileSize - DataOffset)/(BitCount/8))][(int)BitCount/8];
		for(int i = 0, t = 0; i<(int)((FileSize-DataOffset)/(BitCount/8)); i++) {
			for(int q = 0; q<(int)(BitCount/8); q++,t++) {
				pixels[i][q] = data[(int)((double)t*slide)%(int)(FileSize-DataOffset)];
			}
		}
		
		pixelsHistory[0] = deepCopyPixels(pixels);
	}
	
	
	
	
	
	//Creates a new bitmap in local directly
	//Combines header and pixelArray back together into a valid bitmap
	void genNewBitMap() throws IOException{genNewBitMap(outputName);}
	void genNewBitMap(String name) throws IOException {
		outputName = name;
		byte[] out = new byte[(int)FileSize];
		for(int i = 0; i<(int)Header.length; i++) {
			out[i] = Header[i];
		}
		
		for(int i = Header.length; i<(int) (FileSize - DataOffset - 1); i++) {
			out[i] = pixels[i/(int)(BitCount/8)][i%(int)(BitCount/8)];
		}
		
		FileOutputStream fos = new FileOutputStream(outputName + ".bmp");
		fos.write(out);
		fos.close();
	}
	
	
	//For Each Pixel, Flip Coin: Heads:Swap with previous; Tails:Swap with next;
	void fuzzByPixelDemon(int repeat) {
		for(int rep = 0; rep < repeat; rep++) {
			for(int i = 0; i< pixels.length;i++) {
				if(Math.random() > .5) {
					if(i-1 > 0) {
						swapByteArray(pixels[i],pixels[i-1]);
					}
				} else {
					if(i+1 < pixels.length) {
						swapByteArray(pixels[i],pixels[i+1]);
					}
				}
			}
		}
	}
	
	
	//For Each Row, Flip Coin: Heads:Swap with previous row; Tails:Swap with next row;
	void fuzzByRowDemon(int repeat, double widthMultiplier) {
		
		double width = Width*widthMultiplier;
		
		for(int rep = 0; rep < repeat; rep++) {
				for(int i = 0; i < (int)(Height -1)/widthMultiplier; i++) {
					
					if(Math.random() > .5) {
						if(i+1 < (int) width){
							for(int q = 0; q < (int)Width; q++) {
								swapByteArray(pixels[(int)(i*width + q)%(int)pixels.length],pixels[(int)((i+1)*width + q)%(int)pixels.length]);
							}
						}
					} else {
						if(i-1 > 0){
							for(int q = 0; q < (int)Width - 1; q++) {
								swapByteArray(pixels[((int)(i*width) + q)%(int)pixels.length],pixels[((int)((i-1)*width) + q)%(int)pixels.length]);
							}
						}
					}
				}
			}
	}
	
	//For Each Column, Flip Coin: Heads:Swap with previous column; Tails:Swap with next column;
	void fuzzByColDemon(int repeat) {
		for(int rep = 0; rep < repeat; rep++) {
				for(int i = 0; i < (int)Width -1; i++) {
					
					if(Math.random() > .5) {
						if(i+1 < (int) Width){
							for(int q = 0; q < (int)Height; q++) {
								swapByteArray(pixels[i + q*(int)Width],pixels[(i+1) + q*(int)Width]);
							}
						}
					} else {
						if(i-1 > 0){
							for(int q = 0; q < (int)Height; q++) {
								swapByteArray(pixels[i + q*(int)Width],pixels[(i-1) + q*(int)Width]);
							}
						}
					}
				}
			}
	}
	
	//
	void slideDemon(int repeat) {
		for(int rep = 0; rep < repeat; rep++) {
			for(int i = 0; i < (int)(Width*1.4) ; i++) {
				
				if(Math.random() > .5) {
					if(i+1 < (int) Width){
						for(int q = 0; q < (int)Height + 40; q++) {
							swapByteArray(pixels[(i + q*(int)((float)Width*1))%pixels.length],pixels[((i+1) + q*(int)((float)Width*1.001925))%pixels.length]);
						}
					}
				} else {
					if(i-1 > 0){
						for(int q = 0; q < (int)Height; q++) {
							swapByteArray(pixels[(i + q*(int)((float)Width*1))%pixels.length],pixels[((i-1) + q*(int)((float)Width*1))%pixels.length]);
						}
					}
				}
			}
		}
	}
	
	
	void ColorDemon(int repeat, int shiftAmount, boolean doComplement, boolean randomSwap, int pushAmount,int colorByteToSwap1, int colorByteToSwap2) {
		for(int rep = 0; rep < repeat; rep++) {
			for(int i = 0; i<pixels.length; i++) {
				byte hold;
				if(randomSwap){colorByteToSwap1 = (int)(Math.random() * pixels[0].length); colorByteToSwap2 = (int)(Math.random() * pixels[0].length);}
				hold = pixels[i][colorByteToSwap1];
				hold =  (byte)(hold << shiftAmount);
				if(doComplement) {hold = (byte) (~hold);}
				pixels[i][colorByteToSwap1] = pixels[i][colorByteToSwap2];
				pixels[(i+pushAmount)%pixels.length][colorByteToSwap2] = hold;
			}
		}
	}
	
	//
	void CarelessSwapDemon(int repeat,int shiftDistance, boolean abs) {
		for(int rep = 0; rep < repeat; rep++) {
			int space = pixels.length;
			
			
			if(abs) {
				for(int i = 0; i<space; i++) {
					swapByteArray(pixels[Math.abs((-i + (i*(int)Width)))%space],pixels[Math.abs((-i + (i*(int)Width)+shiftDistance))%space]);
				}
				
				for(int i = 0; i<space; i++) {
					swapByteArray(pixels[Math.abs((i + (i*(int)Width)))%space],pixels[Math.abs((i + (i*(int)Width)+shiftDistance))%space]);
				}
			} else {
				for(int i = 0; i<space; i++) {
					swapByteArray(pixels[(-i + (i*(int)Width))%space],pixels[(-i + (i*(int)Width)+shiftDistance)%space]);
				}
				
				for(int i = 0; i<space; i++) {
					swapByteArray(pixels[(i + (i*(int)Width))%space],pixels[(i + (i*(int)Width)+shiftDistance)%space]);
				}
			}
		}
		
	}
	
	//Reverse Height and Width Values
	//	Isnt that interesting unless they are only slightly different
	void DimensionDemon() {
		byte hold;
		
		final int s = 5;
		final int q = 0;
		
		hold = Header[13+s];
		Header[13+s] = Header[17+s];
		Header[17+s] = (byte)(hold << q);
		
		hold = Header[14+s];
		Header[14+s] = Header[18+s];
		Header[18+s] = (byte)(hold << q);
		
		hold = Header[15+s];
		Header[15+s] = Header[19+s];
		Header[19+s] = (byte)(hold << q);
		
		hold = Header[16+s];
		Header[16+s] = Header[20+s];
		Header[20+s] = (byte)(hold << q);
	}
	
	//Takes a function and changes pixels in a psuedo graph thing
	//SHOULD BE NEAT
	//Not really implemented at all yet
	void MathDemon(){
		for(int i = 0; i<pixels.length; i++) {
			double x = i%(int)Width;
			double y = i/(int)Width;
			if(400*Math.cos(x * Math.pow(.995, (double)x))> y) {
				pixels[i][0] = pixels[i][2] = pixels[i][1] = (byte)(pixels[i][0]&0x00);
			}
		}
	}
	
	//Loads the pixelArray, from the pixelArray. Makes fractals sometimes
	void reloaderDemon(int repeat, double slide){
		for(int rep = 0; rep < repeat; rep ++) {
			for(int i = 0, t = 0; i<(int)((FileSize-DataOffset)/(BitCount/8)); i++) {
				for(int q = 0; q<(int)(BitCount/8); q++,t++) {
					pixels[i][q] = pixels[((int)((double)t*slide)%(int)(FileSize-DataOffset))/3][((int)((double)t*slide)%(int)(FileSize-DataOffset))%3];
				}
			}
		}
	}
	
	void histAdd() {
		histCount++;
		pixelsHistory[histCount] = deepCopyPixels(pixels);
		redos = 0;
	}
	
	void histUndo() {
		if(histCount > 0) {
			histCount --;
			redos ++;
			pixels = deepCopyPixels(pixelsHistory[histCount]);
		} else {
			System.out.println("Could not undo");
		}
	}
	
	void histRedo() {
		if(redos > 0) {
			histCount++;
			pixels = deepCopyPixels(pixelsHistory[histCount]);
		} else {
			System.out.println("Could not redo");
		}
	}
	
	
	////
	////PURE FUNCTIONS
	////
	
	//Reads in a number from string of bytes little endian
	//Mainly used in the contructure to get values from the header
	static long longFromRange(int start, int length, byte[] data) {
		long shiftbuild = 0;
		
		for(int q = length; q>0; q--) {
			shiftbuild = (long) (shiftbuild << 8 | data[start + q -2] & 0xFF);
		}
		//System.out.println(shiftbuild);
		return shiftbuild;
	}
	
	void swapByte(byte b1, byte b2) {
		byte hold = b1;
		b1 = b2;
		b2 = hold;
	}
	
	byte[] copyByteArray(byte[] in) {
		byte[] out = new byte[in.length];
		for(int i = 0; i<in.length; i++) {
			out[i] = in[i];
		}
		
		return out;
	}
	
	static void swapByteArray(byte[] ar1, byte[] ar2) {
		byte hold;
		for(int i = 0; i<ar1.length; i++) {
			hold = ar1[i];
			ar1[i] = ar2[i];
			ar2[i] = hold;
		}
	}
	
	byte[][] deepCopyPixels(byte[][] source) {
		byte[][] copy = new byte[source.length][source[0].length];
		for(int i = 0; i< source.length; i++) {
			for(int q = 0; q < source[0].length; q++) {
				copy[i][q] = source[i][q];
			}
		}
		return copy;
	}
}


class Cluster {
	BitMap map;
	int[] selected;
	
	Cluster(BitMap map, int[] selected) {
		this.map = map;
		this.selected = selected;
	}
}


//Holds a selection, and a pointer to the BitMap it came from
//This pointer will keep an otherwise deleted BitMap alive in memory
//When deleting an instance of bitmap, search through cluster instances as well.


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

//Takes a selection as input, and does a per pixel function across the selection
class Demon {
	Selector sel;
	
	Demon(Selector target) {
		this.sel = target;
	}

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
				int[] hold = sel.selection.get(s).selected;
				
				for(int i = 0; i<hold.length; i++) {
					if(Math.random() > .5) {
						if(i-1 > 0) {
							BitMap.swapByteArray(sel.target.pixels[hold[i]],sel.target.pixels[hold[i-1]]);
						}
					} else {
						if(i+1 < sel.target.pixels.length) {
							BitMap.swapByteArray(sel.target.pixels[hold[i]],sel.target.pixels[hold[i+1]]);
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

/*	Brain storming console function
 * 
 * 	Quick overview of framework...
 * 
 * 		BitMap <- Selector <- Demon
 * 
 * 		BitMap holds all data about a loaded image
 * 		Selector holds an int array, refering to the placement of pixels and a pointer to what BitMap it came from
 * 			
 * 
 * Ex:
 * 
 * 	COMMANDS					| RESULTS
 * 
 * 	**
 * 
 * 	BitMap new map from input   | Creates a new BitMap named "map" and reads input.bmp into it
 * 	Selector new sel  			| Creates a new selector
 *	Demon new belial   			| Creates a new demon called belial
 *
 *	sel targets map 			| Sets sel to target map instance of BitMap
 *	belial targets sel			| Sets belial to target lain's selections
 *
 *	**
 *	
 *	Because the demon needs a path to the bitmap,
 *	the (BitMap <- Selector <- Demon) chain needs to be created to accomplish anything. Maybe make a shorthand function for it to be called with each new instance of console.
 *	Something like:
 *	
 *	chain map lain belial from input | chain <BitMap name> <Selector name> <Demon name> from <file name>
 *	
 *	When a new name is taken, check all HashMaps for matching names, not just the type of object it is
 *	Prevent overwrites of names by default
 *	Allow instances of these classes to be deleted
 *
 *	Next we use these instances to call functions
 *	
 *Ex:
 *	
 *	sel all 1 <option string> | Should call  sel.all(1,<option string>);
 *
 *	General form:
 *
 *		<Selector name> <Selector method> <Cluster instance name> <Options String>
 *
 *		The hardest part of getting it to work right will be matching required parameters to the correct places
 *		It might be easier just to simplify all functions to use required options instead of required parameters
 *
 *		because of the way the functions are written, a "bash like command" should be easy to create a general case of.  lain all 1 -rev -u, calls the selector lain, which call the func all with options reverse and undoable
 *		The options come all at the end for now, because you can simply read the trailing characters as a string and pass it on without any heavy editing
 *		A more refined version might look like "lain all -r -u 1", but thats harder to do.
 *
 *		I would eventually like for options to be able to convey numerical values like -s.5 for "Start 50% through the index"
 *		That would be built function side though
 *
 *		Demon function calls would be similarly syntax'd.
 * 
 */


class Console {
	
	//Data Structures
	
	HashMap<String,BitMap> maps = new HashMap<String,BitMap>();
	HashMap<String,Demon> demons = new HashMap<String,Demon>();
	Selector sel = new Selector();
	Scanner input = new Scanner(System.in);
	
	//primitive data members
	
	boolean exit = false;
	String usrInput = "";
	String words[];
	
	
	
	
	//Overload for automated input
	void in(String input) {
		this.usrInput = input;
		words = this.usrInput.toLowerCase().split(" ");
		System.out.println("Read as words");
		for(int i = 0; i< words.length; i++) {
			System.out.print(words[i] + " | ");
		}
		System.out.println("\n");
	}
	
	//Version for default usr input
	void in() {
		this.usrInput = input.nextLine();
		words = this.usrInput.toLowerCase().split(" ");
		System.out.println("Read as words");
		for(int i = 0; i< words.length; i++) {
			System.out.print(words[i] + " | ");
		}
		System.out.println("\n");
	}
	
	static void start() {
		Console c = new Console();
		
		//Auto add input as bitmap
		c.in("bitmap add input");
		c.D0_Root();
		c.in("sel all 1");
		c.D0_Root();
		c.in("sel all 2");
		c.D0_Root();
		
		/*
		c.in("bitmap remove input");
		c.D0_Root();
		c.in("bitmap add input");
		c.D0_Root();
		*/
		
		while(!c.exit) {
			System.out.print("\n>");
			c.in();
			c.D0_Root();
		}
	}
	
	void SelectorTarget(String name) {
		if(maps.containsKey(name)) {
			System.out.println("targeting " + name + "...");
			sel.target(maps.get(name));
		} else {
			System.out.println("Could not find BitMap " + name);
			
		}
	}
	
	void removeBitMap(String name) {
		if(maps.containsKey(name)) {
			for(Iterator<Entry<String, Cluster>> it = sel.selection.entrySet().iterator(); it.hasNext();) {
				
				Map.Entry<String,Cluster> e = it.next();
				if((maps.get(name)) == (e.getValue().map)) {
					System.out.println("Removing instance of " + name + " found in selection");
					it.remove();
				}
			}
			maps.remove(name);
			System.out.println("Deleted " + name + " from map pool");
			
		} else {
			System.out.println("Bitmap " + name + " not found");
		}
	}
	
	void addDemon(String name) {
		if(!demons.containsKey(name)) {
			demons.put(name, new Demon(sel));
			System.out.println("Demon " + name + " added");
		} else {
			System.out.println("Demon " + name + " already exists.");
		}
	}
	
	void removeDemon(String name) {
		if(demons.containsKey(name)) {
			demons.remove(name);
			System.out.println("RIP DEVIL");
		} else {
			System.out.println("Demon " + name + " not found");
		}
	}
	
	void addBitMap(String name) {
		if(!maps.containsKey(name)) {
			maps.put(name, new BitMap(name,1));
			sel.target = maps.get(name);
			System.out.println(maps.get(name).toString());
		} else {
			System.out.println("BitMap " + name + " already exists.");
		}
	}
	
	void D0_Root() {
		
		if(words.length < 1) {System.out.println("In D0_Root : expected more words, did you just put a space or something?"); return;}
		
		switch(this.words[0]) {
			case("bitmap"):
				D1_BitMap();
				break;
				
			case("sel"):
				D1_Selector();
				break;
			
			case("target"):
				SelectorTarget(this.usrInput.split(" ",2)[1]);
				break;
			
			case("demon"):
				D1_Demon();
				break;
				
			case("quit"):
				System.out.println("Exiting...");
				this.exit = true; 
				break;
				
			case("help"):
				System.out.println("cmds: \nbitmap ..\nsel ..\ntarget ..\ndemon ..\nquit ..\nlegacy");
				break;
			
			case("legacy"):
				D1_legacy();
			default:
				System.out.println("In D0_Root : did not find cmd \"" + words[0] + "\"");
			
		}
	}
	
	void D1_legacy() {
		//TODO: 7 commits in and we have legacy functions to re-implement
	}
	
	
	void D1_BitMap() {
		
		if(words[1] == "help") {
			System.out.println("BitMap cmds: \nadd <name>\ndel <name>\nundo\nredo");
		}
		
		if(!(words.length > 2)) {
			System.out.println("In D1_BitMap : expecting more words");
			return;
		}
		
		switch(words[1]) {
			case("add"):
				addBitMap(words[2]);
				break;
			
			case("del"):
			case("remove"):
				removeBitMap(words[2]);
				break;
			
			case("undo"):
				maps.get(words[2]).histUndo();
				break;
				
			case("redo"):
				maps.get(words[2]).histRedo();
				break;
			default:
				System.out.println("In D1_BitMap : did not find cmd \"" + words[1] + "\"");
		}
	}
	
	void D1_Demon() {
		
		if(!(words.length > 1)) {
			System.out.println("In D1_Demon: expecting more words");
			return;
		}
		
		switch(words[1]) {
		
			case("add"):
				addDemon(words[2]);
				break;
			
			case("remove"):
				removeDemon(words[2]);
				break;
			case("help"):
				System.out.println("Demon cmds: \nadd <name>\nremove <name>");
				break;
			
			default:
				System.out.println("In D1_Demon : did not find cmd \"" + words[1] + "\"");
			
		}
	}
	
	void D1_Selector() {
		
		if(!(words.length > 1)) {
			System.out.println("In D1_Selector: expecting more words");
			return;
		}
		
		switch(this.words[1]) {
			case("all"):
				if(words.length <= 3) {
					sel.all(this.usrInput.split(" ",3)[2]);
				} else {
					sel.all(this.usrInput.split(" ",3)[2],this.usrInput.split(" ",3)[3]);
				}
				
				System.out.println("Selection add to " + this.usrInput.split(" ",3)[2]);
			
				break;
			case("remove"):
				if(sel.selection.containsKey(this.words[2])) {
					sel.selection.remove(words[2]);
					System.out.println("Removed " + words[2]);
				} else {
					System.out.println("Did not find " + words[2]);
				}
			
				break;
			
			case("show"):
				System.out.println(sel.selection.keySet());
				break;
			case("help"):
				System.out.println("Selector cmds: \nall <name> <options>\nshow \nremove <name>");
				break;
			default:
				System.out.println("In D1_Selector : did not find cmd \"" + words[1] + "\"");
					
		}
	}
}
