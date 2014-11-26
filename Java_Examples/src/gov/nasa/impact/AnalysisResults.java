package gov.nasa.impact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * A container for the analysis results.
 * May need to extend this container for the particular
 * client analysis
 */
public class AnalysisResults {
	
	//The following three data structures store the results of
	//the text-based diff
	private List<String> oldUnmatchedFiles = new ArrayList<String>(); //removed files
	private List<String> modUnmatchedFiles = new ArrayList<String>(); //added files
	private List<String> matchedAndChangedFiles = new ArrayList<String>();//changed (matched)
	//matched but same - string is prepended with (<package name>)
	private List<String> matchedAndSameFiles = new ArrayList<String>(); //unchanged
	
	private List<String> ASTFiles = new ArrayList<String>();
	
	//results of AST diff
	//key=file>>modified method id (original method infor is referenced in the mod method info)
	private Map<String,MethodInfo> changedMethodInfos = new HashMap<String,MethodInfo>();
	private Map<String,MethodInfo> unChangedMethodInfos = new HashMap<String,MethodInfo>();

	//format: file>>mName -> mInfo
	private HashMap<String,MethodInfo> addedMethodInfos = new HashMap<String,MethodInfo>();
	private HashMap<String,MethodInfo> removedMethodInfos = new HashMap<String,MethodInfo>();

	
	public Map<String,MethodInfo> getChangedMethodInfos(){
		return this.changedMethodInfos;
	}
	
	public Map<String,MethodInfo> getUnChangedMethodInfos(){
		return this.unChangedMethodInfos;
	}
	
	public List<String> getOldUnmatchedFiles() {
		return oldUnmatchedFiles;
	}
	public void addOldUnmatchedFile(String oldUnmatchedFiles) {
		this.oldUnmatchedFiles.add(oldUnmatchedFiles);
	}
	public List<String> getModUnmatchedFiles() {
		return modUnmatchedFiles;
	}
	public void addModUnmatchedFile(String modUnmatchedFiles) {
		this.modUnmatchedFiles.add(modUnmatchedFiles);
	}
	public List<String> getMatchedAndChangedFiles() {
		return matchedAndChangedFiles;
	}
	public void addMatchedAndChangedFile(String matchedFile) {
		this.matchedAndChangedFiles.add(matchedFile);
	}
	public List<String> getMatchedAndUnchangedFiles() {
		return matchedAndSameFiles;
	}
	public void addMatchedAndUnchangedFile(String unchangedFile) {
		this.matchedAndSameFiles.add(unchangedFile);
	}
	public void addRemovedMethod(String removed){
		this.removedMethodInfos.put(removed, null);
	}
	public List<String> getRemovedMethods() {
		return new ArrayList<String>(removedMethodInfos.keySet());
	}
	public Map<String,MethodInfo> getRemovedMethodInfos(){
		return removedMethodInfos;
	}
	public void addRemovedMethodInfo(String mName, MethodInfo mInfo) {
		this.removedMethodInfos.put(mName, mInfo);
	}
	//TODO: remove this method
	public void addAddedMethod(String added){
		this.addedMethodInfos.put(added, null);
	}
	public List<String> getAddedMethods() {
		return new ArrayList<String>(addedMethodInfos.keySet());
//		return (List<String>) addedMethodInfos.keySet();
	}
	public void addAddedMethodInfo(String mName, MethodInfo mInfo) {
		this.addedMethodInfos.put(mName, mInfo);
	}
	public Map<String,MethodInfo> getAddedMethodInfos(){
		return addedMethodInfos;
	}
	public List<String> getASTFiles() {
		return ASTFiles;
	}
	public void addASTFile(String aSTFile) {
		ASTFiles.add(aSTFile);
	}
}
