package gov.nasa.impact;

import gov.nasa.jpf.regression.ast.ASTLoader;
import gov.nasa.jpf.regression.ast.MethodASTInfo;
import gov.nasa.jpf.regression.ast.MethodSignature;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cse.unl.edu.ast.ASTDiffer;

public class RunASTDiff {
	
	//this is the string/character astro uses as a delimiter in the string of method info
	//TODO: synchronize with ASTro.jar (should eventually be removed from all classes except ASTro)
	//private final String astroDelimiter = ">>";
	private final String astroDelimiter = "#";
	
	private AnalysisResults results = new AnalysisResults();

	private boolean verbose = true;
	private Set<String> ignores = new HashSet<String>();
	private boolean ignoreTests = false;
	
	public String getAstroDelimiter(){
		return astroDelimiter;
	}
	
	private boolean isIgnored(String fName){
		boolean ignored = false;
		Iterator<String> it = ignores.iterator();
		boolean done = false;
		if (ignoreTests && fName.toLowerCase().contains("test")){
			done = true;
			ignored = true;
		}
		while (it.hasNext() && !done){
			String ignoredPkg = it.next();
			if (fName.contains(ignoredPkg)){
				done = true;
				ignored = true;
			}
		}
		return ignored;
	}

	/*
	 * This is the entry point to the class
	 * It first runs the text diff and then if there 
	 * are differences, refines them by comparing the ASTs.
	 * The results are stored back into the AnalysisResults Object
	 */
	public boolean computeDeltas(String origSrcPath, String modSrcPath,
			String origClsPath, String modClsPath, String diffDir){
		//Check text diff first to filter out unchanged files
		RunTextDiff td = new RunTextDiff();
		File old = new File(origSrcPath);
		File mod = new File(modSrcPath);
		List<String> deltaSet = new ArrayList<String>();
		List<String> sameSet = new ArrayList<String>();
		boolean same = td.sameText(old, mod, deltaSet,sameSet);
		//add results to the AnalysisResults Object
		Iterator<String> itO = deltaSet.iterator();
		while (itO.hasNext()){
			String diff = itO.next();
			int indx = diff.indexOf(">>");
			String file1 = diff.substring(0,indx);
			String file2 = diff.substring(indx+2);
			if (file1.equalsIgnoreCase("none") && !isIgnored(file2))
				results.addModUnmatchedFile(file2);
			else if (file2.equalsIgnoreCase("none") && !isIgnored(file1))
				results.addOldUnmatchedFile(file1);
			else if (!isIgnored(file1) && !isIgnored(file2))
				results.addMatchedAndChangedFile(diff);
		}
		Iterator<String> it1 = sameSet.iterator();
		while (it1.hasNext()){
			String diff = it1.next();
			results.addMatchedAndUnchangedFile(diff);
		}
		//Use the AST diff to compare all matched but changed files
		if (!same){
			same = true;
			if (verbose)
				System.out.println("Computing AST diffs...");
			same = computeASTDiffs(origClsPath,modClsPath,diffDir);
		}else{
			if (verbose)
				System.out.println("No syntactic differences in original and modified files");
		}
		if (verbose)
			System.out.println("Finished computing Deltas \n " +
					"*****************************");
		return same;
	}

	//computes the AST diff and stores the results in the AnalysisResults object
	//returns true if there are no difference based on the AST diff
	private boolean computeASTDiffs(String origClsPath, String modClsPath,
			String diffDir){
		boolean same = true;
		// Compare the files based on AST.
		ASTDiffer astDiffer = new ASTDiffer();
		astDiffer.setHeuristics(true);
		Iterator<String> it = results.getMatchedAndChangedFiles().iterator();
		while (it.hasNext()){
			System.out.print(".");
			String pair = it.next();
			int indx = pair.indexOf(">>");
			String file1 = pair.substring(0,indx);
			String file2 = pair.substring(indx+2);
			String shortName = file1.substring(file1.lastIndexOf(File.separator)+1);
			shortName = shortName.substring(0,shortName.lastIndexOf("."));
			File sourceFile1 = new File(file1);
			File sourceFile2 = new File(file2);
			if (sourceFile1.exists() && sourceFile2.exists()) {
				boolean equal = astDiffer.sameAST(sourceFile1, sourceFile2, 
						origClsPath, modClsPath, "", "");
				if (equal){
					if (verbose)
						System.out.println(file1 + " is the same as " + file2);
				}else{
					same = false;
					if (verbose)
						System.out.println(file1 + " is not the same as " + file2);
					// Write the output XML file.
					if(diffDir != ""){
						//String xmlFile = shortName + ".xml";
						String tmp = astDiffer.getLongName();
						tmp = tmp.replace('.', '_');
						String xmlFile = tmp + ".xml";
						astDiffer.writeBlockSummary(diffDir, xmlFile);
						results.addASTFile(diffDir + File.separator + xmlFile);
						storeResults(file1,file2,diffDir,xmlFile,results);
					}else
						System.out.println("RunASTDiff: Cannot write XML file - no directory specified");
				}

			}else
				System.out.println("RunASTDiff: One of the source files does not exist.");
		}
		System.out.println();
		//compute AST diff for unchanged files too if they do not exist
		Iterator<String> it1 = results.getMatchedAndUnchangedFiles().iterator();
		while (it1.hasNext()){
			String pair = it1.next();
			String pkgName = pair.substring(1,pair.indexOf(')'));
			pair = pair.substring(pair.indexOf(')') + 1);
			int indx = pair.indexOf(">>");
			String file1 = pair.substring(0,indx);
			String file2 = pair.substring(indx+2);
			String shortName = file1.substring(file1.lastIndexOf(File.separator)+1);
			shortName = shortName.substring(0,shortName.lastIndexOf("."));
			File sourceFile1 = new File(file1);
			File sourceFile2 = new File(file2);
			String xmlFile = shortName + ".xml";
			if (pkgName.length()>0)
				xmlFile = pkgName + "_" + xmlFile;
			String longXmlFile = diffDir + File.separator + xmlFile;
			File xFile = new File(longXmlFile);
			if (sourceFile1.exists() && sourceFile2.exists()){
				if (!xFile.exists()) { //no XML for this pair of files
					boolean equal = astDiffer.sameAST(sourceFile1, sourceFile2, 
							origClsPath, modClsPath, "", "");
					if (!equal)
						System.out.println("WARNING>>>files are in unchanged list but ASTro indicates " + 
								file1 + " is NOT the same as " + file2);
					if(diffDir != ""){
						astDiffer.writeBlockSummary(diffDir, xmlFile);
						results.addASTFile(diffDir + File.separator + xmlFile);
						storeResults(file1,file2,diffDir,xmlFile,results);
					}else
						System.out.println("RunASTDiff: Cannot write XML file - no directory specified");
				}else{
					results.addASTFile(diffDir + File.separator + xmlFile);
				}

			}else{
				if (!sourceFile1.exists() || !sourceFile2.exists())
					System.out.println("RunASTDiff: One of the source files does not exist.");
				//if (xFile.exists())
				//	System.out.println("Decaf: the XML file already exists for the unchanged file.");
			}
		}

		//process unmatched files to get list of removed/added methods
		same = false;
		Iterator<String> it3 = results.getOldUnmatchedFiles().iterator();
		while (it3.hasNext()){
			same = false;
			String file1 = it3.next();
			List<String> methods = astDiffer.getMethodList(new File(file1),
					origClsPath, "");
			Iterator<String> it4 = methods.iterator();
			while (it4.hasNext()){
				String mName = it4.next();
				String fString = file1 + astroDelimiter + mName;
				MethodInfo mInfo = parseMethodInfo(fString, false);
				mInfo.setRemovedFile(true);
				mInfo.setIsModVersion(false);
				results.addRemovedMethodInfo(fString, mInfo);
			}
		}
		Iterator<String> it5 = results.getModUnmatchedFiles().iterator();
		while (it5.hasNext()){
			same = false;
			String file2 = it5.next();
			List<String> methods = astDiffer.getMethodList(new File(file2),
					modClsPath, "");
			Iterator<String> itM = methods.iterator();
			while (itM.hasNext()){
				String mName = itM.next();
				String fString = file2 + astroDelimiter + mName;
				MethodInfo mInfo = parseMethodInfo(fString, true);
				mInfo.setAddedFile(true);
				mInfo.setIsModVersion(true);
				results.addAddedMethodInfo(fString, mInfo);
			}
		}
		return same;
	}

	//need to load the XML file to get the details we need to store in the 
	// AnalysisObject (the astDiffer i/f does not store all of the info)
	private void storeResults(String file1, String file2, String diffDir, 
			String xmlFile, AnalysisResults results){

		ASTLoader loader = new ASTLoader();
		Map<String,MethodASTInfo> methodASTInfo =
			new HashMap<String,MethodASTInfo>();
		methodASTInfo = loader.loadAST(diffDir + File.separator + xmlFile);
		Iterator<Map.Entry<String,MethodASTInfo>> it1 = 
			methodASTInfo.entrySet().iterator();
	    while (it1.hasNext()) {
	        Map.Entry<String,MethodASTInfo> pairs = it1.next();
	        MethodASTInfo m = pairs.getValue();
	        
	        if (m.getMatched() && !m.getEquivalent()){ //changed
		        MethodInfo modMInfo = new MethodInfo(m.getClassName(),
		        		m.getMethodName(),m.getModModifiers(),m.getParameters(), 
		        		m.getParamTypes(),m.getReturnType(),file2);
		        modMInfo.setChgMethod(true);
		        modMInfo.setIsModVersion(true);
		        modMInfo.setRefFileName(file2);
		        MethodInfo origMInfo = new MethodInfo(m.getClassName(),
		        		m.getMethodName(),m.getOrigModifiers(),m.getOrigParameters(), 
		        		m.getOrigParamTypes(),m.getOrigReturnType(),file1);
		        origMInfo.setRefFileName(file2);
		        origMInfo.setChgMethod(true);
		        modMInfo.setOrigInfo(origMInfo);
	        	String id = modMInfo.getUniqueID();
	        	results.getChangedMethodInfos().put(id, modMInfo);
	        	
	        	if (m.getAssertLinesMod().size()>0)
	        		modMInfo.getAssertLocs().addAll(m.getAssertLinesMod());
	        	if (m.getAssertLinesOrig().size()>0)
	        		origMInfo.getAssertLocs().addAll(m.getAssertLinesOrig());	
	        	if (m.getChangedLinesMod().size()>0)
	        		modMInfo.getChgdLocs().addAll(m.getChangedLinesMod());
	        	if (m.getChangedLinesOrig().size()>0)
	        		origMInfo.getChgdLocs().addAll(m.getChangedLinesOrig());
	        	if (m.getAddedLines().size()>0)
	        		modMInfo.getAddedLocs().addAll(m.getAddedLines());
	        	if (m.getRemovedLines().size()>0)
	        		modMInfo.getRemovedLocs().addAll(m.getRemovedLines());
	        	if (m.getModModifiers().size()>0)
	        		modMInfo.getModifiers().addAll(m.getModModifiers());
	        	if (m.getOrigModifiers().size()>0)
	        		origMInfo.getModifiers().addAll(m.getOrigModifiers());
	        	if (m.getModClassModifiers().size()>0){
	        		origMInfo.getModClassModifiers().addAll(m.getModClassModifiers());
	        		modMInfo.getModClassModifiers().addAll(m.getModClassModifiers());
	        	}
	        	if (m.getOrigClassModifiers().size()>0){
	        		origMInfo.getOrigClassModifiers().addAll(m.getOrigClassModifiers());
	        		modMInfo.getOrigClassModifiers().addAll(m.getOrigClassModifiers());
	        	}
	        		
	        }else{ //not matched 
	        	//TODO: this format may differ from added/removed file format above?!!
	        	//TODO: can we store these as methodInfos too? vs. strings
	    	   if (m.getAdded()){
	    		   MethodInfo mInfo = new MethodInfo(m.getClassName(),
			        		m.getMethodName(),m.getModModifiers(),m.getParameters(), 
			        		m.getParamTypes(),m.getReturnType(),file2);
	    		   mInfo.setRefFileName(file2);
	    		   mInfo.setNewMethod(true);
	    		   if (m.getAssertLinesMod().size()>0)
		        		mInfo.getAssertLocs().addAll(m.getAssertLinesMod());
	    		   if (m.getModModifiers().size()>0)
		        		mInfo.getModifiers().addAll(m.getModModifiers());
	    		   if (m.getModClassModifiers().size()>0)
		        		mInfo.getModClassModifiers().addAll(m.getModClassModifiers());
	    		   String id = mInfo.getUniqueID();
	    		   
	    		   TreeSet<Integer> addedSrc = new TreeSet<Integer>(m.getChangedLinesMod());
	    		   mInfo.setAddedSrcLocs(addedSrc.first(), addedSrc.last());
	    		   mInfo.setRemovedSrcLocs(addedSrc.first(), addedSrc.last());
	    		   
	    		   id = formatString(id, mInfo);
 		           results.addAddedMethodInfo(id, mInfo);
	    	   }else if (m.getRemoved()){ 
	    		   MethodInfo mInfo = new MethodInfo(m.getClassName(),
			        		m.getMethodName(),m.getOrigModifiers(),m.getOrigParameters(), 
			        		m.getOrigParamTypes(),m.getOrigReturnType(),file1);
	    		   mInfo.setRefFileName(file2);
	    		   mInfo.setDelMethod(true);
	    		   if (m.getOrigModifiers().size()>0)
		        		mInfo.getModifiers().addAll(m.getOrigModifiers());
	    		   if (m.getOrigClassModifiers().size()>0)
		        		mInfo.getOrigClassModifiers().addAll(m.getOrigClassModifiers());
	    		   String id = mInfo.getUniqueID();
	    		   
	    		   TreeSet<Integer> removedSrc = new TreeSet<Integer>(m.getChangedLinesOrig());
	    		   mInfo.setAddedSrcLocs(removedSrc.first(), removedSrc.last());
	    		   mInfo.setRemovedSrcLocs(removedSrc.first(), removedSrc.last());
	    		   
	    		   id = formatString(id, mInfo);
		           results.addRemovedMethodInfo(id, mInfo);
	    	   }
	       }
		}
	}
	
	//This method servers to format id strings in a similar way to ASTro
	//NOTE: IF ASTRO FORMATTING IS CHANGED THIS METHOD WILL NOT BE IN SYNC
	//TODO: synchronize this method with ASTro formatting
	private String formatString(String id, MethodInfo mInfo) {
		MethodSignature mSig = mInfo.getSignature();
		String retStr = mInfo.getFileName() + astroDelimiter + mSig.getClassName() + astroDelimiter + mSig.getMethodName() + "_";
		List<String> paramTypes = mSig.getParamTypes();
//		for (int i=0; i<mSig.getParamTypes().size(); i++) {
//			retStr += mSig.getParamTypes().get(i);
//			if (i<mSig.getParamTypes().size()-1)
//				retStr += "_";
//		}
		for (String s : paramTypes)
			retStr += s + "_";
		retStr = retStr.substring(0, retStr.length()-1);
		if (!mInfo.isModVersion())
			retStr += "(" + mInfo.getAddedSrcStart().toString() + "," + mInfo.getAddedSrcEnd().toString() + ")";
		else
			retStr += "(" + mInfo.getRemovedSrcStart().toString() + "," + mInfo.getRemovedSrcEnd().toString() + ")";
//		retStr += "()";
		retStr += "{returnType:" + mSig.getReturnType() + "}";
		retStr += "{";
		for (int i=0; i<mSig.getParamTypes().size(); i++) {
			retStr += mSig.getParamTypes().get(i) + " " + mSig.getParamNames().get(i);
			if (i<mSig.getParamTypes().size()-1)
				retStr += ",";
		}

		Set<String> classMods;
		if (mInfo.isModVersion())
			classMods = mInfo.getModClassModifiers();
		else
			classMods = mInfo.getOrigClassModifiers();

		if (classMods.size() > 0) {
			retStr += "}{classModifiers:";
			for (String s : classMods)
				retStr += s + ",";
			retStr = retStr.substring(0, retStr.length()-1);
		}
		else
			retStr += "}{";

		List<String> methodMods = mInfo.getModifiers();

		if (methodMods.size() > 0) {
			retStr += "}{methodModifiers:";
			for (String s : methodMods)
				retStr += s + ",";
			retStr = retStr.substring(0, retStr.length()-1);
			retStr += "}";
		}
		else
			retStr += "}{}";
		
		return retStr;
	}
	
	//parses information returned from ASTro which characterizes
	//unmatched files; id is filename + methodname
	private MethodInfo parseMethodInfo(String id, boolean added) {
		String fileName, className, methodName, srcStart, srcEnd, returnType;
		List<String> methodMods = new ArrayList<String>();
		List<String> argNames = new ArrayList<String>();
		List<String> argTypes = new ArrayList<String>();
		Set<String> classMods = new HashSet<String>();
		
		//entry hasn't been parsed yet
		String[] items = id.split(astroDelimiter);
		fileName = items[0];
		className = items[1];
		String[] splits = items[2].substring(0, items[2].indexOf("{")).split("_");
		if (splits[0].contains("(")) {
			methodName = splits[0].substring(0, splits[0].indexOf("("));
			srcStart = splits[0].substring(splits[0].indexOf("(")+1, splits[0].indexOf(","));
			srcEnd = splits[0].substring(splits[0].indexOf(",")+1, splits[0].indexOf(")"));
		}
		else {
			methodName = splits[0];
			srcStart = splits[splits.length-1].split("\\(")[1].split(",")[0];
			srcEnd = splits[splits.length-1].split("\\(")[1].split(",")[1].replaceAll("\\)", "");
		}
		
		String[] bracketSplits = items[2].split("\\{");
		returnType = bracketSplits[1].split(":")[1].replaceAll("\\}", "");
		
		//params
		String[] params;
		//Handle generic types - cannot simply split on ','
		if (bracketSplits[2].contains("<")){
			String temp = bracketSplits[2];
			boolean done = false;
			String t="";
			while(!done){
				int start=0;
				if (temp.contains(">")){
					start = temp.indexOf('<')+1;
					t = temp.substring(0,start);
					int end = temp.indexOf('>');
					String type = temp.substring(start,end);
					type = type.replace(',','#');
					t = t + type + ">";
					temp = temp.substring(end+1);
				}else{
					done = true;
					temp = t + temp;
				}
			}
			params = temp.replaceAll("\\}", "").split(",");
			for (String param : params){
				if (param.contains("#"))
					param.replaceAll("#", ",");
			}
		}else
			params = bracketSplits[2].replaceAll("\\}", "").split(",");
		for (String param : params){
			if (!param.equals("") && !param.equals(" ") && !param.equals("\n")) {
				argTypes.add(param.split(" ")[0]);
				System.out.println(methodName + ":" + argNames);
				if (argNames.contains("strPair"))
					System.out.println("here");
				argNames.add(param.split(" ")[1]);
			}
		}
		
		//classMods
		String[] cMods = bracketSplits[3].split(":")[1].split(",");
		for (String mod : cMods)
			classMods.add(mod.replaceAll("}",""));
		
		//methodMods
		String[] mMods = bracketSplits[4].split(":")[1].split(",");
		for (String mod : mMods)
			methodMods.add(mod.replaceAll("}", ""));
		
		
		MethodInfo mInfo = new MethodInfo(className, methodName, methodMods, argNames, argTypes, returnType, fileName);
		Iterator<String> itMod = methodMods.iterator();
		while (itMod.hasNext()){
			mInfo.addModifier(itMod.next());
		}
		
		if (added) {
			mInfo.setModClassModifiers(classMods);
			mInfo.setAddedSrcLocs(Integer.parseInt(srcStart), Integer.parseInt(srcEnd));
		}
		else {
			mInfo.setOrigClassModifiers(classMods);
			mInfo.setRemovedSrcLocs(Integer.parseInt(srcStart), Integer.parseInt(srcEnd));
		}
		return mInfo;
	}
	
	public static void main(String[] args)	{
		System.out.println("Running AST DIff");

		try{
			String origSrc = "";
			String modSrc = "";
			String origCls = "";
			String modCls = "";
			String diffDir = "";

			if (args.length < 5){
				System.out.print("USAGE: java RunASTDiff -origSrc <file>.java | <dir>" +
						" -modSrc <file>.java | <dir> -origCls <file>.class | <dir>" +
						"-modCls <file>.class | <dir> -dDir <results dir>");
	            System.exit(0);
			}else{
				for (int i=0; i<args.length; i++){
					if (args[i].startsWith("-origSrc")) {
						origSrc = args[i+1];
						i++;
					}else if (args[i].startsWith("-modSrc")) {
						modSrc = args[i+1];
						i++;
					}else if (args[i].startsWith("-origCls")) {
						origCls = args[i+1];
						i++;
					}else if (args[i].startsWith("-modCls")) {
						modCls = args[i+1];
						i++;
					}else if (args[i].startsWith("-dDir")) {
						diffDir = args[i+1];
						i++;
					}else
						System.out.println("Unknown option: " + args[i]);
				}
			}		
			
			RunASTDiff astDiffer = new RunASTDiff();
			boolean same = astDiffer.computeDeltas(origSrc, modSrc, origCls, modCls, diffDir);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
	