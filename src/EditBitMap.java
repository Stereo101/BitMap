import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Byte;


public class EditBitMap {
	public static void main(String[] args) throws IOException {
		
		BitMap lain = new BitMap("input",1);
		Selector choice = new Selector(lain);
		Demon belial = new Demon(choice);
		choice.all(1,"-r");
		choice.all(2);
		belial.swap(1,2);
		
		lain.genNewBitMap("output");
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
	int histCount = 0;;
	int redos = 0;
	
	
	
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
		
		
		System.out.print("\nSignature: ");
		longFromRange(1,2,data);
		System.out.print("FileSize: ");
		FileSize = longFromRange(3,4,data);
		System.out.print("reserved: ");
		reserved = longFromRange(7,4,data);
		System.out.print("DataOffset: ");
		DataOffset = longFromRange(11,4,data);
		
		System.out.print("Size: ");
		Size = longFromRange(15,4,data);
		
		System.out.print("Width: ");
		Width = longFromRange(19,4,data);
		
		System.out.print("Height: ");
		Height = longFromRange(23,4,data);
		
		System.out.print("Planes: ");
		Planes = longFromRange(27,2,data);
		
		System.out.print("BitCount: ");
		BitCount = longFromRange(29,2,data);
		
		System.out.print("Number of Colors: ");
		switch((int)BitCount) {
		case(1): NumberOfColors=1; break;
		case(4): NumberOfColors=16; break;
		case(8): NumberOfColors=256; break;
		case(16): NumberOfColors=65536; break;
		case(24): NumberOfColors=16777216; break;
		}
		System.out.println(NumberOfColors);
		
		
		
		System.out.print("Compression: ");
		Compression = longFromRange(31,4,data);
		
		System.out.print("ImageSize(After Compression, 0 if uncompressed): ");
		ImageSize = longFromRange(35,4,data);
		
		System.out.print("XpixelsPerM: ");
		XpixelsPerM = longFromRange(39,4,data);
		
		System.out.print("YpixelsPerM: ");
		YpixelsPerM = longFromRange(43,4,data);
		
		System.out.print("ColorsUsed: ");
		ColorsUsed = longFromRange(47,4,data);
		
		System.out.print("Colors important: ");
		ColorsImportant = longFromRange(51,4,data);
		
		SizeColorTable = BitCount >=8 ? 0 : 4*BitCount;
		System.out.println("SizeColorTable: " + SizeColorTable);
		
		
		
		Header = new byte[(int)DataOffset];
		for(int i = 0; i<(int)DataOffset; i++) {
			Header[i] = data[i];
			if(i%8 == 0) { System.out.println();}
			System.out.printf("%5s" ,(int)Header[i]);
		}
		
		
		pixels = new byte[(int)((FileSize - DataOffset)/(BitCount/8))][(int)BitCount/8];
		for(int i = 0, t = 0; i<(int)((FileSize-DataOffset)/(BitCount/8)); i++) {
			for(int q = 0; q<(int)(BitCount/8); q++,t++) {
				pixels[i][q] = data[(int)((double)t*slide)%(int)(FileSize-DataOffset)];
			}
		}
		
		pixelsHistory[0] = deepCopyPixels(pixels);
		
		System.out.println("\n\n\n");
		System.out.println("There are "  + pixels.length + " pixels in this image");
	}
	
	
	
	//Creates a new bitmap in local directly
	//Combines header and pixelArray back together into a valid bitmap
	void genNewBitMap(String name) throws IOException {
		byte[] out = new byte[(int)FileSize];
		for(int i = 0; i<(int)Header.length; i++) {
			out[i] = Header[i];
		}
		
		for(int i = Header.length; i<(int) (FileSize - DataOffset - 1); i++) {
			out[i] = pixels[i/(int)(BitCount/8)][i%(int)(BitCount/8)];
		}
		
		FileOutputStream fos = new FileOutputStream(name + ".bmp");
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
		System.out.println(shiftbuild);
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

//Takes BitMap as input, and create an array of selected pixels, in the order they would be selected
//Hold selections in a 
class Selector {
	BitMap map;
	
	int selectedCount;
	HashMap<Integer,int[]> selection = new HashMap<Integer,int[]>();
	
	Selector(BitMap target) {
		this.map = target;
	}
	
	int all(int spot){return all(spot,"");}
	int all(int spot, String options){
		boolean reverse = false;
		if(options.contains("-r")) {reverse = true;}
		
		selection.put(spot, new int[map.pixels.length]);
		int[] hold = selection.get(spot);
		for(int i = 0; i< map.pixels.length; i++) {
			if(!reverse) {
				hold[i] = i;
			} else {
				hold[i] = map.pixels.length - (i+1);
			}
		}
		return spot;
	}
	
	//TODO
	int vert(int spot, double start, double end) {
		selection.put(spot, new int[map.pixels.length]);
		int[] hold = selection.get(spot);
		
		
		return spot;
	}
	
	//TODO
	int horz(int spot, double start, double end) {
		selection.put(spot, new int[map.pixels.length]);
		int[] hold = selection.get(spot);
		
		
		return spot;
	}
}

//Takes a selection as input, and does a per pixel function across the selection
class Demon {
	Selector sel;
	
	Demon(Selector target) {
		this.sel = target;
	}
	
	//Overload redirectors
	void fuzz(int selIndex) {fuzz(new int[]{selIndex},"");}
	void fuzz(int selIndex, String options) {fuzz(new int[]{selIndex},options);}
	
	void fuzz(int[] selIndex, String options) {
		for(int s : selIndex) {
			boolean undoable = options.contains("-u");
			
			
			if(undoable) {
				sel.map.histAdd();
			}
			
			if(sel.selection.containsKey(s)) {
				int[] hold = sel.selection.get(s);
				
				for(int i = 0; i<hold.length; i++) {
					if(Math.random() > .5) {
						if(i-1 > 0) {
							BitMap.swapByteArray(sel.map.pixels[hold[i]],sel.map.pixels[hold[i-1]]);
						}
					} else {
						if(i+1 < sel.map.pixels.length) {
							BitMap.swapByteArray(sel.map.pixels[hold[i]],sel.map.pixels[hold[i+1]]);
						}
					}
				}
			}
		}
	}
	
	//Overload redirectors
	void swap(int selIndex1, int selIndex2) {swap(new int[]{selIndex1},new int[]{selIndex2}, "");}
	void swap(int selIndex1, int selIndex2, String options) {swap(new int[]{selIndex1},new int[]{selIndex2}, options);}
	void swap(int[] selIndex1, int[] selIndex2) {swap(selIndex1,selIndex2, "");}
	
	void swap(int[] selIndex1,int[] selIndex2, String options) {
		for(int s = 0; s<selIndex1.length && s<selIndex2.length; s++) {
			if(sel.selection.containsKey(selIndex1[s]) && sel.selection.containsKey(selIndex2[s])) {
				int[] hold1 = sel.selection.get(selIndex1[s]);
				int[] hold2 = sel.selection.get(selIndex2[s]);
				
				for(int i = 0; i<hold1.length && i<hold2.length; i++) {
					BitMap.swapByteArray(sel.map.pixels[hold1[i]],sel.map.pixels[hold2[i]]);
				}
			} else {
				System.out.println("ERROR, COULD NOT FIND SWAP PAIR (" + selIndex1[s] + "," + selIndex2[s] + ")");
			}
		}
	}
	
}

//TODO
class Loader {
	
}

