import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.apache.bcel.verifier.structurals.InstructionContext;

public class CFGtoDOT {
	private static void myFoo(String verPath, ClassGen cgen, Method method) throws IOException {
		
		ConstantPoolGen pgen = cgen.getConstantPool();
		String cname = cgen.getClassName();
		MethodGen methgen = new MethodGen(method, cname, pgen);
		ControlFlowGraph c = new ControlFlowGraph (methgen);
		System.out.println(c.toString());
		
		/* Now writing the graph to dot file. */
		
		String fileName = verPath + "dot/" + cgen.getClassName() + "-" + method.getName() + ".dot";
		File file = new File(fileName);
		File parent_directory = file.getParentFile();
		if (null != parent_directory){
		    parent_directory.mkdirs();
		}
	    FileWriter out = new FileWriter(file);
	    out.write("digraph v1 {\n\tnode [shape=box]\n");
		
	    int pos;
		String instr;
		InstructionContext[] ictxt = c.getInstructionContexts();
		for (int i=0; i< ictxt.length; ++i){ 
			System.out.println(ictxt[i].getInstruction().toString());
			pos = ictxt[i].getInstruction().getPosition();
			instr = ictxt[i].getInstruction().toString();
			System.out.println("\tPosition: "+ pos);
			System.out.println("\tInstruction: "+instr);
			out.write("\n\t" + pos + " [label=\"" + instr + "\"];");	
			System.out.println("\tName: "+ictxt[i].getInstruction().getInstruction().getName());
			System.out.println("\tOpCode: "+ictxt[i].getInstruction().getInstruction().getOpcode());
			InstructionContext[] succ = ictxt[i].getSuccessors();
			int succpos; 
			for (int j=0; j<succ.length; ++j){
				succpos = succ[j].getInstruction().getPosition();
				System.out.println("\tSuccessor: "+succpos);
				out.write("\n\t"+ pos + " -> "+succpos+";");
			}
		}
		out.write("\n}");
		out.close();
	} 

	public static void main(String[] args) {
		System.out.println("Running CFGtoDOT");
		
		try {
			String dirPath="";
			String verCount="";
			String className="";
			String funcName="";
			
			if (args.length < 4){
				/* Might have to make it work for all the classes (in one Project) 
				 * and all the functions (in one Class) in the future. */
				System.out.print("USAGE: java CFGtoDOT" + 
						" -dirPath </path/to/artifact/directory>" +  
						" -verCount <version_count> " +
						" -className <name_of_class>" +
						" -funcName <name_of_function>");
				System.exit(0);
			}
			else{
				for (int i=0; i<args.length; i++){
					if (args[i].startsWith("-dirPath")) {
						dirPath = args[i+1];
						if (false == dirPath.endsWith("/"))
							dirPath = dirPath+"/";
						i++;
					}else if (args[i].startsWith("-verCount")) {
						verCount = args[i+1];
						i++;
					}else if (args[i].startsWith("-className")) {
						className = args[i+1];
						i++;
					}else if (args[i].startsWith("-funcName")) {
						funcName = args[i+1];
						i++;
					}else
						System.out.println("Unknown option: " + args[i]);
				}
				
				/* The command looks fine, let's proceed. */
				int vCount = Integer.parseInt(verCount);
				for (int i=1; i<=vCount; ++i){
					String ver = "Version";
					ver=ver+i;
					String verPath = dirPath+ver+"/";
					/* For every version, generate DOT files. */
					String classPath = verPath+"bin/";
					JavaClass jclas = new ClassParser(classPath+className+".class").parse();
					ClassGen cgen = new ClassGen(jclas);
					Method[] methods = jclas.getMethods();
					int index;
					
					for (index = 0; index < methods.length; index++) {
						String methName = methods[index].getName().toString();
						if (methName.equals(funcName) && (index < methods.length)) {
							myFoo (verPath, cgen, methods[index]);
							System.out.println("DOT file generated succesfully for "+className+"/"+methName);
							break;
						}
					}
					if (index == methods.length){
						System.err.println("Method " + funcName + " not found in " + className);
					}
				} // End of outer for loop
			} // End of else block
		} // End of try block
		catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
    }// End of main
} // End of class
