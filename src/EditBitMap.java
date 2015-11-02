import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Byte;


public class EditBitMap {
	public static void main(String[] args) throws IOException {
		File file = new File("temp.txt");
		Path path = Paths.get(file.getAbsolutePath());
		
		byte[] image = null;
		
		try {
			image = Files.readAllBytes(path.resolveSibling("input.bmp"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BitMap lain = new BitMap(image,1);
		lain.reloaderDemon(1,2);
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
	byte[][] pixelArray;
	
	
	
	//Main constructor
	//
	//Reads in the first 54 bytes of a Bitmap and stores them as variables
	//
	//Slide and Div are 2 modifiers that change how the pixelArray is loaded
	//Using slide = div = 1 will load in correctly
	public BitMap(byte[] data, double slide) {
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
		
		
		pixelArray = new byte[(int)((FileSize - DataOffset)/(BitCount/8))][(int)BitCount/8];
		for(int i = 0, t = 0; i<(int)((FileSize-DataOffset)/(BitCount/8)); i++) {
			for(int q = 0; q<(int)(BitCount/8); q++,t++) {
				pixelArray[i][q] = data[(int)((double)t*slide)%(int)(FileSize-DataOffset)];
			}
		}
		
		System.out.println("\n\n\n");
		System.out.println(pixelArray.length);
	}
	
	
	
	//Creates a new bitmap in local directly
	//Combines header and pixelArray back together into a valid bitmap
	void genNewBitMap(String name) throws IOException {
		byte[] out = new byte[(int)FileSize];
		for(int i = 0; i<(int)Header.length; i++) {
			out[i] = Header[i];
		}
		
		for(int i = Header.length; i<(int) (FileSize - DataOffset - 1); i++) {
			out[i] = pixelArray[i/(int)(BitCount/8)][i%(int)(BitCount/8)];
		}
		
		FileOutputStream fos = new FileOutputStream(name + ".bmp");
		fos.write(out);
		fos.close();
	}
	
	
	//For Each Pixel, Flip Coin: Heads:Swap with previous; Tails:Swap with next;
	void fuzzByPixelDemon(int repeat) {
		for(int rep = 0; rep < repeat; rep++) {
			for(int i = 0; i< pixelArray.length;i++) {
				if(Math.random() > .5) {
					if(i-1 > 0) {
						swapByteArray(pixelArray[i],pixelArray[i-1]);
					}
				} else {
					if(i+1 < pixelArray.length) {
						swapByteArray(pixelArray[i],pixelArray[i+1]);
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
								swapByteArray(pixelArray[(int)(i*width + q)%(int)pixelArray.length],pixelArray[(int)((i+1)*width + q)%(int)pixelArray.length]);
							}
						}
					} else {
						if(i-1 > 0){
							for(int q = 0; q < (int)Width - 1; q++) {
								swapByteArray(pixelArray[((int)(i*width) + q)%(int)pixelArray.length],pixelArray[((int)((i-1)*width) + q)%(int)pixelArray.length]);
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
								swapByteArray(pixelArray[i + q*(int)Width],pixelArray[(i+1) + q*(int)Width]);
							}
						}
					} else {
						if(i-1 > 0){
							for(int q = 0; q < (int)Height; q++) {
								swapByteArray(pixelArray[i + q*(int)Width],pixelArray[(i-1) + q*(int)Width]);
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
							swapByteArray(pixelArray[(i + q*(int)((float)Width*1))%pixelArray.length],pixelArray[((i+1) + q*(int)((float)Width*1.001925))%pixelArray.length]);
						}
					}
				} else {
					if(i-1 > 0){
						for(int q = 0; q < (int)Height; q++) {
							swapByteArray(pixelArray[(i + q*(int)((float)Width*1))%pixelArray.length],pixelArray[((i-1) + q*(int)((float)Width*1))%pixelArray.length]);
						}
					}
				}
			}
		}
	}
	
	
	void ColorDemon(int repeat, int shiftAmount, boolean doComplement, boolean randomSwap, int pushAmount,int colorByteToSwap1, int colorByteToSwap2) {
		for(int rep = 0; rep < repeat; rep++) {
			for(int i = 0; i<pixelArray.length; i++) {
				byte hold;
				if(randomSwap){colorByteToSwap1 = (int)(Math.random() * pixelArray[0].length); colorByteToSwap2 = (int)(Math.random() * pixelArray[0].length);}
				hold = pixelArray[i][colorByteToSwap1];
				hold =  (byte)(hold << shiftAmount);
				if(doComplement) {hold = (byte) (~hold);}
				pixelArray[i][colorByteToSwap1] = pixelArray[i][colorByteToSwap2];
				pixelArray[(i+pushAmount)%pixelArray.length][colorByteToSwap2] = hold;
			}
		}
	}
	
	//
	void CarelessSwapDemon(int repeat,int shiftDistance, boolean abs) {
		for(int rep = 0; rep < repeat; rep++) {
			int space = pixelArray.length;
			
			
			if(abs) {
				for(int i = 0; i<space; i++) {
					swapByteArray(pixelArray[Math.abs((-i + (i*(int)Width)))%space],pixelArray[Math.abs((-i + (i*(int)Width)+shiftDistance))%space]);
				}
				
				for(int i = 0; i<space; i++) {
					swapByteArray(pixelArray[Math.abs((i + (i*(int)Width)))%space],pixelArray[Math.abs((i + (i*(int)Width)+shiftDistance))%space]);
				}
			} else {
				for(int i = 0; i<space; i++) {
					swapByteArray(pixelArray[(-i + (i*(int)Width))%space],pixelArray[(-i + (i*(int)Width)+shiftDistance)%space]);
				}
				
				for(int i = 0; i<space; i++) {
					swapByteArray(pixelArray[(i + (i*(int)Width))%space],pixelArray[(i + (i*(int)Width)+shiftDistance)%space]);
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
		for(int i = 0; i<pixelArray.length; i++) {
			double x = i%(int)Width;
			double y = i/(int)Width;
			if(400*Math.cos(x * Math.pow(.995, (double)x))> y) {
				pixelArray[i][0] = pixelArray[i][2] = pixelArray[i][1] = (byte)(pixelArray[i][0]&0x00);
			}
		}
	}
	
	//Loads the pixelArray, from the pixelArray. Makes fractals sometimes
	void reloaderDemon(int repeat, double slide){
		for(int rep = 0; rep < repeat; rep ++) {
			for(int i = 0, t = 0; i<(int)((FileSize-DataOffset)/(BitCount/8)); i++) {
				for(int q = 0; q<(int)(BitCount/8); q++,t++) {
					pixelArray[i][q] = pixelArray[((int)((double)t*slide)%(int)(FileSize-DataOffset))/3][((int)((double)t*slide)%(int)(FileSize-DataOffset))%3];
				}
			}
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
	
	void swapByteArray(byte[] ar1, byte[] ar2) {
		byte hold;
		for(int i = 0; i<ar1.length; i++) {
			hold = ar1[i];
			ar1[i] = ar2[i];
			ar2[i] = hold;
		}
	}
	
}