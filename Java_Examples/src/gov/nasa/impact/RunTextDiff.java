package gov.nasa.impact;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class RunTextDiff {
	
	//private Vector<String> origFiles = new Vector<String>();
	//private Vector<String> modFiles = new Vector<String>();
	private boolean verbose = false;
	
	private String longName = "";
	
	private String removeComments(File f){
	
		InputStream is = null;
		//Read the contents of the files
		try{
			is = new FileInputStream(f);
		}catch (Exception e){
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader (new InputStreamReader(is));

        //each line read in
        String line="";

        //resulting string
        String outString="";
        
        String tmp = "";

        try{
        	boolean inComment = false;
        	int count = 1;
        	while((line=br.readLine())!=null){
        		//System.out.println("parsing line " + count + ": " + line);
        		if (inComment){ //ignore everything until end of comment
        			if (line.contains("*/")){
            			tmp = line.substring(line.indexOf("*/")+1);
            			outString = outString.concat(tmp + "\n");
            			inComment = false;
            		}
        		}else{
        			//check for comments
            		if (line.contains("//")){
            			if (line.indexOf("//") != 0 ){
            				tmp = line.substring(0, line.indexOf("//")-1);
            				outString= outString.concat(tmp + "\n");
            			}
            		}else if (line.contains("/*")){
            			if (line.indexOf("/*") != 0 ){
            				tmp = line.substring(0, line.indexOf("/*")-1);
            				outString = outString.concat(tmp + "\n");
            			}
            			String restOfLine = line.substring(line.indexOf("/*"));
            			if (!restOfLine.endsWith("*/"))
            				inComment = true;
            		}else
        				outString = outString.concat(line + "\n");
        		}
        		count++;
        	}
        	is.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
        return outString;
	}
	
	//utility method for debugging
	private String getFileAsString(File f){
		
		InputStream is = null;
		//Read the contents of the files
		try{
			is = new FileInputStream(f);
		}catch (Exception e){
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader (new InputStreamReader(is));

        //each line read in
        String line="";

        //resulting string
        String outString="";

        try{
        while((line=br.readLine())!=null){
        	outString = outString.concat(line);
        }	
        	is.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
        return outString;
	}
	
  static private void validateDirectory (File aDirectory) throws FileNotFoundException {
    if (aDirectory == null)
      throw new IllegalArgumentException("Directory should not be null.");
    if (!aDirectory.exists())
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    if (!aDirectory.isDirectory()) 
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    if (!aDirectory.canRead()) 
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
  }
	  
  static private List<File> getFileListingNoSort(File aStartingDir)
	  		throws FileNotFoundException {
	  List<File> result = new ArrayList<File>();
	  File[] filesAndDirs = aStartingDir.listFiles();
	  List<File> filesDirs = Arrays.asList(filesAndDirs);
	  for(File file : filesDirs) {
		  result.add(file); //always add, even if directory
		  if ( ! file.isFile() ) {
			  //must be a directory
			  //recursive call!
			  List<File> deeperList = getFileListingNoSort(file);
			  result.addAll(deeperList);
		  }
	  }
	  return result;
  }
  
  static final Comparator<File> FILE_NAME = 
		  new Comparator<File>() {
	  public int compare(File f1, File f2) {
		  return f2.getName().compareTo(f1.getName());
	  }
  };
	
	static private List<File> getFileListing(File aStartingDir)
		throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result, FILE_NAME);
		return result;
	}
	
	/*
	* This is the entry point to the the textual comparison
	* No need to call if performing an AST diff - it will get called
	* automatically from there
	* Input:two files (or directories) to be compared
	* Returns true if the files (or all the files in the dirs) are the same
	* Ignores whitespace and Java comments
	*/
	public boolean sameText(File file1, File file2, 
			List<String> deltaSet,	List<String> sameSet){
		if (verbose){
			System.out.println("*****************************");
			System.out.println("Starting Text Diff...");
		}
		Vector<String> origFiles = new Vector<String>();
		Vector<String> modFiles = new Vector<String>();
		boolean same = false;
		try{
	        if (file1.isDirectory() && file2.isDirectory()){
	    		List<File> oldFiles = getFileListing(file1);
	    		Iterator<File> itO= oldFiles.iterator();
	    		while (itO.hasNext()){
	    			File f = (File)itO.next();
	    			String fName = f.getAbsolutePath();
	    			//if (!fName.contains("EnvDriver"))
	    				origFiles.add(fName);
	    		}
	    		List<File> newFiles = getFileListing(file2);
	    		Iterator<File> itN= newFiles.iterator();
	    		while (itN.hasNext()){
	    			File f = (File)itN.next();
	    			String fName = f.getAbsolutePath();
	    		//	if (!fName.contains("EnvDriver"))
	    				modFiles.add(fName);
	    		}        		
	    	}else if (file1.isFile() && file2.isFile()){
	    		origFiles.add(file1.getAbsolutePath());
	    		modFiles.add(file2.getAbsolutePath());
	    	}else{
	    		System.out.print("Enter two java source file names or");
	    		System.out.println(" two package names");
	    		System.out.println("....Exiting from the program");
	            System.exit(0);
	    	}	
			same = textDiff(origFiles, modFiles, deltaSet, sameSet);
			if (verbose){
				System.out.println("End of Text Diff");
				System.out.println("*****************************");
			}
			return same;
		}catch(Exception e){
			e.printStackTrace();
			if (verbose){
				System.out.println("End of Text Diff");
				System.out.println("*****************************");
			}
			return same;
		}

	}
	
	//returns true if the files are "different"
	private boolean compareFiles(File f1, File f2){
		
		if (verbose)
			System.out.println("Text Diff comparing " + f1.getAbsolutePath() +
				" with " + f2.getAbsolutePath());

		String file1 = removeComments(f1);
		String file2 = removeComments(f2);
		
//		String file1 = getFileAsString(f1);
//		String file2 = getFileAsString(f2);
		
		if (file1.contains("package")){
			int index = file1.indexOf("package");
			longName = file1.substring(index + 8, file1.indexOf(";",index + 8));
		}else
			longName = "";

		//now tokenize to omit whitespace
		String [] file1Tokens = file1.split("\\s+");
		String [] file2Tokens = file2.split("\\s+");
    
		if (file1Tokens.length != file2Tokens.length)
			return true;
        
        //now compare the files; exit on first difference
        //TODO: may want to look ahead/behind on the tokens in the case
    	//where the tokenizing is slightly different but the files are
        //still textually the same
        boolean diff = false;
        int count = 0;
        while (count < file1Tokens.length && !diff){
        	if(!file1Tokens[count].equals(file2Tokens[count])){
        		diff = true;
        	}
        	count++;
        }
        return diff;
	}
	
	//The deltaSet contains matched and unmatched files
	//The sameSet contains matched and unchanged files
	private boolean textDiff(Vector<String> origFiles, Vector<String> modFiles,
			List<String> deltaSet, List<String> sameSet){
		boolean same = true;
		
		int size1 = origFiles.size();
		int size2 = modFiles.size();
		
		if (verbose)
			System.out.println("Text Diff: Started with " + size1 + 
				" file(s) in original version and " + size2 + " file(s) in modified version");
		try{
			if (verbose)
				System.out.println("Processing original file(s) for text matches");
			
			//First filter out non-Java class files
			List<String> origJava = new ArrayList<String>();
			Iterator<String> itOrig = origFiles.iterator();
			while (itOrig.hasNext()){
				String name = (String)itOrig.next();
				if (name.endsWith(".java"))
					origJava.add(name);
			}
			List<String> modJava = new ArrayList<String>();
			Iterator<String> itMod = modFiles.iterator();
			while (itMod.hasNext()){
				String name = (String)itMod.next();
				if (name.endsWith(".java"))
					modJava.add(name);
			}
			//at this point, only Java files remain to be compared
			//first match on class/file name
			Iterator<String> it = origJava.iterator();
    		while (it.hasNext()){		
    			String file1 = (String)it.next();
    			//strip off the path so names can be matched
    			String short1 = file1.substring(file1.lastIndexOf(File.separator)+1);
    			boolean found = false;
    			int size = modJava.size();
    			int count = 0;
    			while (!found && count<size){
    				String file2 = (String)modJava.get(count);
    				String short2 = file2.substring(file2.lastIndexOf(File.separator)+1);
    				if (short1.equalsIgnoreCase(short2)){
    					found = true;
    					File origFile = new File(file1);
    					File modFile = new File(file2);
    					boolean diff = compareFiles(origFile, modFile);
    					if (same) //once same is not true, don't change it again
    						same = !diff;
    					if (diff){ //but matched
    						deltaSet.add(file1 + ">>" + file2);
    						if (verbose)
    							System.out.println(file1 + " is not the same in both versions" +  
    							" (based on textual diff)");
    					}else{ //prepend (<package name>)
    						sameSet.add("(" + longName + ")" + file1 + ">>" + file2);
    					}
    					modJava.remove(file2);
    				}else
    					count++;
    			}
    			if (!found) //no match for file1
    				deltaSet.add(file1 + ">>" + "none");
    		}
    		
			//any files remaining in modJava files have no match
    		if (modJava.size()>0)
    			same = false;
			Iterator<String> it2 = modJava.iterator();
			while (it2.hasNext()){
				String file2 = (String)it2.next();
				deltaSet.add("none" + ">>" + file2);
			}
    		return same;
		}catch(Exception e){
			e.printStackTrace();
			return same;
		}
	}

	/*
	 * Inputs are the names of the files to be compared & ignorePkgName (for
	 * the case where a package was simply renamed)
	 */
	public static void main(String[] args)	{

		try{
			String file1 = "";
			String file2 = "";

			if (args.length < 2){
				System.out.print("USAGE: java TextDiffer -original <file>.java | <dir>" +
						" -modified <file>.java | <dir> ");
	            System.exit(0);
			}else{
				for (int i=0; i<args.length; i++){
					if (args[i].startsWith("-o")) {
						file1 = args[i+1];
						i++;
					}else if (args[i].startsWith("-m")) {
						file2 = args[i+1];
						i++;
					}else
						System.out.println("Unknown option: " + args[i]);
				}
			}
			
			File sourceFile1 = new File(file1);
			File sourceFile2 = new File(file2);
			List<String> deltaOld = new ArrayList<String>();
			List<String> deltaMod = new ArrayList<String>();
			if (sourceFile1.exists() && sourceFile2.exists()) {
				RunTextDiff textDiffer = new RunTextDiff();
				textDiffer.sameText(sourceFile1, sourceFile2, deltaOld, deltaMod);
			}else{
				System.out.println("TextDiffer: One of the specified files does not exist...");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
