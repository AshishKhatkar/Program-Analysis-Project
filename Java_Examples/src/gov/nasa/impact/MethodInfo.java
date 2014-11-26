package gov.nasa.impact;

import gov.nasa.jpf.regression.ast.MethodSignature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodInfo {
	
	private boolean isModVersion = false; //true if matched and has ref to orig
	private boolean inAddedFile = false;
	private boolean inRemovedFiles = false;
	private boolean isNewMethod = false;
	private boolean isDelMethod = false;
	private boolean isChgMethod = false;
	private MethodInfo minfoOrig;
	
	private MethodSignature mSig;
	private String uniqueID = ""; 
	private String fileName = "";
	private String refFileName = ""; //for showing all changes w.r.t. modified version

	private Set<Integer> chgdLocs = new HashSet<Integer>();
	private Set<Integer> addedLocs = new HashSet<Integer>(); //empty in orig
	private Set<Integer> removedLocs = new HashSet<Integer>();//empty in orig
	private List<String> modifiers = new ArrayList<String>();
	private Set<Integer> assertLocs = new HashSet<Integer>();
	private Set<String> origClassModifiers = new HashSet<String>();
	private Set<String> modClassModifiers = new HashSet<String>();
	
	private Integer addedSrcStart, addedSrcEnd, removedSrcStart, removedSrcEnd;
	
	public MethodInfo(String cName, String mName, List<String> mods,
			List<String> argNames,List<String> argTypes,String retType, String f){
		this.fileName = f;
		mSig = new MethodSignature(cName, mods, mName, argNames, argTypes, retType);
		uniqueID = cName + ": " + retType + " " + mName + "(";
        if (argTypes.size()>0){
        	for (int i=0;i<argTypes.size();i++){
        		uniqueID = uniqueID + argTypes.get(i);
        		if (i+1 < argTypes.size())
        			uniqueID = uniqueID + ",";
        	}
        }
        uniqueID = uniqueID + ")";
	}
	
	public boolean isInAddedFile(){
		return inAddedFile;
	}
	
	public void setAddedFile(boolean a){
		inAddedFile = a;
	}
	
	public boolean isInRemovedFile(){
		return inRemovedFiles;
	}
	
	public void setRemovedFile(boolean r){
		inRemovedFiles = r;
	}
	
	public void setDelMethod(boolean d){
		this.isDelMethod = d;
	}
	public boolean isDelMethod(){
		return this.isDelMethod;
	}
	public void setNewMethod(boolean m){
		this.isNewMethod = m;
	}
	public boolean isNewMethod(){
		return this.isNewMethod;
	}
	public void setChgMethod(boolean c){
		this.isChgMethod = c;
	}
	public boolean isChgMethod(){
		return this.isChgMethod;
	}
	
	public void setIsModVersion(boolean mod){
		this.isModVersion = mod;
	}
	
	public MethodInfo getOrigMInfo(){
		assert(isModVersion);
		return this.minfoOrig;
	}
	
	public void setOrigInfo(MethodInfo mi){
		isModVersion = true;
		minfoOrig = mi;
	}
	
	public MethodSignature getSignature() {
		return mSig;
	}
	
	public void setRefFileName(String f){
		this.refFileName = f;
	}
	
	public String getRefFileName(){
		return this.refFileName;
	}

	public String getUniqueID() {
		return uniqueID;
	}
	
	public void addChgdLoc(Integer loc){
		chgdLocs.add(loc);
	}
	
	public Set<Integer> getChgdLocs(){
		return this.chgdLocs;
	}
	
	public void addAddedLoc(Integer loc){
		addedLocs.add(loc);
	}
	
	public Set<Integer> getAddedLocs(){
		return this.addedLocs;
	}
	
	public void addRemovedLoc(Integer loc){
		removedLocs.add(loc);
	}
	
	public Set<Integer> getRemovedLocs(){
		return this.removedLocs;
	}
	
	public void addModifier(String mod){
		modifiers.add(mod);
	}
	
	public List<String> getModifiers(){
		return this.modifiers;
	}
	
	public void addAssertLocs(Integer loc){
		assertLocs.add(loc);
	}
	
	public Set<Integer> getAssertLocs(){
		return this.assertLocs;
	}
	
	public String getFileName(){
		return this.fileName;
	}
	
	public Set<String> getModClassModifiers(){
		return modClassModifiers;
	}
	
	public void setModClassModifiers(Set<String> mods) {
		this.modClassModifiers = mods;
	}
	
	public Set<String> getOrigClassModifiers(){
		return origClassModifiers;
	}
	
	public void setOrigClassModifiers(Set<String> mods) {
		this.origClassModifiers = mods;
	}
	
	public void setAddedSrcLocs(Integer start, Integer end) {
		this.addedSrcStart = start;
		this.addedSrcEnd = end;
	}
	public void setRemovedSrcLocs(Integer start, Integer end) {
		this.removedSrcStart = start;
		this.removedSrcEnd = end;
	}
	public Integer getAddedSrcStart() {
		return this.addedSrcStart;
	}
	public Integer getAddedSrcEnd() {
		return this.addedSrcEnd;
	}
	public Integer getRemovedSrcStart() {
		return this.removedSrcStart;
	}
	public Integer getRemovedSrcEnd() {
		return this.removedSrcEnd;
	}
	public boolean isModVersion() {
		return this.isModVersion;
	}
	
	public String toString(){
		String tmp = mSig.toString() + " : changed locs: " + chgdLocs.toString();
		tmp = tmp + ", removedLocs: " + removedLocs.toString();
		tmp = tmp + ", addedLocs: " + addedLocs.toString();
		return tmp;
	}
}
