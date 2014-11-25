package cfg_dot_construction;
import java.io.FileOutputStream;
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
	private static void myFoo(ClassGen cgen, Method method) throws IOException {
		
		ConstantPoolGen pgen = cgen.getConstantPool();
		String cname = cgen.getClassName();
		MethodGen methgen = new MethodGen(method, cname, pgen);
		ControlFlowGraph c = new ControlFlowGraph (methgen);
		System.out.println(c.toString());
		
		/* Now writing the graph to dot file. */
		String filename = "dot/" + cgen.getClassName() + "-" + method.getName() + ".dot"; 
	    FileWriter out = new FileWriter(filename);
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
			/*
			System.out.println("\tName: "+ictxt[i].getInstruction().getInstruction().getName());
			System.out.println("\tOpCode: "+ictxt[i].getInstruction().getInstruction().getOpcode());
			*/
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

	public static void main(String[] argv) {
        if (argv.length == 2 && argv[0].endsWith(".class")) {
            try {            
                JavaClass jclas = new ClassParser(argv[0]).parse();
                ClassGen cgen = new ClassGen(jclas);
                Method[] methods = jclas.getMethods();
                int index;
                for (index = 0; index < methods.length; index++) {
                    if (methods[index].getName().equals(argv[1])) {
                        break;
                    }
                }
                if (index < methods.length) {
                	myFoo (cgen, methods[index]);
                } 
                else {
                    System.err.println("Method " + argv[1] + " not found in " + argv[0]);
                }
            } 
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            
        } 
        else {
            System.out.println
                ("Usage: $0 class-file method-name");
        }
    }
}
