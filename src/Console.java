import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

class Console {
	
	//Data Structures
	
	HashMap<String,BitMap> maps = new HashMap<String,BitMap>();
	HashMap<String,Demon> demons = new HashMap<String,Demon>();
	Selector sel = new Selector();
	Demon de = new Demon(sel);
	
	Scanner input = new Scanner(System.in);
	
	//primitive data members
	
	boolean exit = false;
	String usrInput = "";
	String words[];
	
	
	
	
	//Overload for automated input
	void in(String input) {
		this.usrInput = input.toLowerCase().trim().replaceAll(" +"," ");
		words = this.usrInput.split(" ");
		System.out.println("Read as words");
		for(int i = 0; i< words.length; i++) {
			System.out.print(words[i] + " | ");
		}
		System.out.println("\n");
	}
	
	//Version for default usr input
	void in() {
		this.usrInput = input.nextLine().toLowerCase().trim().replaceAll(" +"," ");
		words = this.usrInput.split(" ");
		System.out.println("Read as words");
		for(int i = 0; i< words.length; i++) {
			System.out.print(words[i] + " | ");
		}
		System.out.println("\n");
	}
	
	boolean wordCheck(int required, String message) {
		if(this.words.length < required) {
			System.out.println(message + " expected more words.");
			return true;
		}
		return false;
	}
	
	//SHIT UNIT TEST TO CHECK FOR OBVIOUS ISSUES
	static void unitTest() {
		Console c = new Console();
		
		String[] op1 = {"bitmap", "sel", "demon","help","target","legacy", " ","input", "garbage", "1","42","add","del","undo","help","show","remove","garbage"};
		
		System.out.println("Unit test started, entering " + (int)(Math.pow(op1.length,5)*2) + " commands total");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i = 0; i < 2; i++) {
			for(String s : op1) {
				for(String d : op1) {
					for (String f : op1) {
						for(String w : op1) {
							for (String e : op1) {
								c.in(s + " " + d + " " + f + " " + w + " " + e);
								c.D0_Root();
							}
						}
					}
				}
			}
		}
		
	}
	
	//Begins the console. To be called in Main
	static void start() {
		Console c = new Console();
		
		//Auto add input as bitmap
		c.in("bitmap add input");
		c.D0_Root();
		c.in("sel all 1");
		c.D0_Root();
		c.in("demon add de");
		c.D0_Root();
		
		
		c.sel.circle("circle", "nooption");
		c.in("demon black circle");
		c.D0_Root();
		
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
			BitMap newMap = new BitMap(name,1);
			if(newMap.ImageSize != 0) {
				maps.put(name, new BitMap(name,1));
				SelectorTarget(name);
				System.out.println(maps.get(name).toString());
			} else {
				System.out.println("Failed to load BitMap");
			}
			
		} else {
			System.out.println("BitMap " + name + " already exists.");
		}
	}
	
	void D0_Root() {
		
		if(wordCheck(1,"In D0_Root :" + Thread.currentThread().getName())) {return;}
		
		switch(this.words[0]) {
			case("bitmap"):
				D1_BitMap();
				break;
				
			case("sel"):
				D1_Selector();
				break;
			
			case("target"):
				if(wordCheck(2,"In D0_Root_target : ")){return;}
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
		if(wordCheck(2,"In D1_BitMap : ")){return;}
		
		if(words[1] == "help") {
			System.out.println("BitMap cmds: \nadd <name>\ndel <name>\nundo\nredo");
		}
		
		if(words.length < 2) {
			System.out.println("In D1_BitMap : expecting more words");
			return;
		}
		
		switch(words[1]) {
			case("add"):
				if(wordCheck(3,"In D1_BitMap_add : ")){return;}
				addBitMap(words[2]);
				break;
			
			case("del"):
			case("remove"):
				if(wordCheck(3,"In D1_BitMap_remove : ")){return;}
				removeBitMap(words[2]);
				break;
			
			case("undo"):
				if(wordCheck(3,"In D1_BitMap_undo : ")){return;}
			
				if(maps.containsKey(words[2])) {
					maps.get(words[2]).histUndo();
					maps.get(words[2]).genNewBitMap();
				} else {
					System.out.println("Did not find BitMap " + words[2]);
				}
				
				break;
				
			case("redo"):		
				if(maps.containsKey(words[2])) {
					maps.get(words[2]).histRedo();
					maps.get(words[2]).genNewBitMap();
				} else {
					System.out.println("Did not find BitMap " + words[2]);
				}
				break;
			default:
				System.out.println("In D1_BitMap : did not find cmd \"" + words[1] + "\"");
		}
	}
	
	void D1_Demon() {

		if(wordCheck(2,"In D1_Demon :")){return;}
		
		switch(words[1]) {
			/*
			case("add"):
				if(wordCheck(3,"In D1_Demon_add : ")){return;}
				addDemon(words[2]);
				break;
			case("remove"):
				if(wordCheck(3,"In D1_Demon_remove : ")){return;}
				removeDemon(words[2]);
				break;
				
			*/
		
			case("black"):
				if(wordCheck(3,"In D1_Demon_black : ")){return;}
				
				if(words.length == 3) {
					de.black(words[2]);
					sel.genFromCluster(words[2]);
					
				} else {
					if(wordCheck(4,"In D1_Demon_black : ")) {return;}
					de.black(words[2],this.usrInput.split(" ",4)[3]);
					sel.genFromCluster(words[2]);
				}
				break;
				
			case("help"):
				System.out.println("Demon cmds: \nadd <name>\nremove <name>");
				break;
			case("fuzz"):
				if(wordCheck(3,"In D1_Demon_fuzz : ")){return;}
				if(words.length == 3) {
					de.fuzz(words[2]);
					sel.genFromCluster(words[2]);
					
				} else {
					if(wordCheck(4,"In D1_Demon_fuzz : ")) {return;}
					de.fuzz(words[2],this.usrInput.split(" ",4)[3]);
					sel.genFromCluster(words[2]);
				}
				break;
			default:
				System.out.println("In D1_Demon : did not find cmd \"" + words[1] + "\"");
				break;
			
		}
	}
	
	void D1_Selector() {
		if(wordCheck(2,"In D1_Selector : ")){return;}
		
		switch(this.words[1]) {
			case("all"):
				if(words.length <= 3) {
					sel.all(words[2]);
				} else {
					sel.all(words[2],usrInput.toLowerCase().trim().replaceAll(" +", " ").split(" ",4)[3]);
				}
				
				System.out.println("Selection add to " + words[2]);
			
				break;
			case("remove"):
				if(wordCheck(3,"In D1_Selector_remove : ")){return;}
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
