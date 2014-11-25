package cfg_dot_construction;
/*
 * @author: Ashish Khatkar, Pulkit Manocha, Anindya Srivastava, Masood Pathan
 */
import java.io.IOException;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.apache.bcel.verifier.structurals.Frame;
import org.apache.bcel.verifier.structurals.InstructionContext;
import java.util.*;
public class CreateGraph 
{
	static HashSet<InstructionContext> visited=new HashSet<>();
	public static void printGraph(ControlFlowGraph cfg, InstructionList il)
	{
		InstructionHandle[] instr_handles=il.getInstructionHandles();
		int v=0;
		for(v=0;v<instr_handles.length;v++)
		{
			System.out.println(cfg.contextOf(instr_handles[v]).toString());
			InstructionContext ic=cfg.contextOf(instr_handles[v]);
			InstructionContext[] succ=ic.getSuccessors();
			for(InstructionContext succ_ic: succ)
			{
				System.out.println("\t\t"+succ_ic.getInstruction().toString(true));
			}
 		}
		
	}
	public static void main(String[] args)throws ClassFormatException, IOException
	{
		ClassParser parser=new ClassParser("/home/pulkit/Program-Analysis-Project/Java_Examples/bin/Version4.class");
		JavaClass javaClass=parser.parse();
		ClassGen classGen=new ClassGen(javaClass);
		ConstantPoolGen constantPool=new ConstantPoolGen(javaClass.getConstantPool());
		Method[] methods=javaClass.getMethods();
		int i=0;
		for(i=0;i<methods.length;i++)
		{
			MethodGen methodGen=new MethodGen(methods[i],javaClass.getClassName(),constantPool);
			InstructionList il = methodGen.getInstructionList();
			Instruction[] instructions=il.getInstructions();
			int v=0;
			System.out.println("CFG FOR : "+methods[i].getName()+"\n");
			ControlFlowGraph cfg = new ControlFlowGraph(methodGen);
			InstructionContext[] instructionContexts=cfg.getInstructionContexts();
			int j=0;
			for(j=0;j<instructionContexts.length;j++)
			{
				InstructionContext ic=instructionContexts[j];
				InstructionHandle ih=ic.getInstruction();
				Instruction ins=ih.getInstruction();
				InstructionContext[] succ = ic.getSuccessors();
				int k=0;
				for(k=0;k<succ.length;k++)
				{
					InstructionContext ics=succ[k];
					InstructionHandle ihs=ics.getInstruction();
					Instruction inss=ihs.getInstruction();
				}
			}
			printGraph(cfg,il);
			System.out.println();
		}
	}
}