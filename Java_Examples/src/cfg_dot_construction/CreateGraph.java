// Create Control Flow Graph
// Identify source code line number and method name

package com.masood.bcel;
/*
 * @author: Anindya Srivastava, Masood Pathan
 */

import java.io.IOException;
import java.util.*;

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
import org.apache.bcel.verifier.structurals.InstructionContext;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;

public class CreateGraph {

    static HashSet<InstructionContext> visited = new HashSet<>();

    public static void printGraph(ControlFlowGraph cfg, InstructionList il) {
        InstructionHandle[] instr_handles = il.getInstructionHandles();
        int v = 0;

        for (v = 0; v < instr_handles.length; v++) {
            System.out.println(cfg.contextOf(instr_handles[v]).toString());
            InstructionContext ic = cfg.contextOf(instr_handles[v]);
            InstructionContext[] succ = ic.getSuccessors();
            for (InstructionContext succ_ic : succ) {
                System.out.println("\t\t" + succ_ic.getInstruction().toString(true));

            }
        }

    }
    
    public static void identifySourceLineNumberAndMehtodName(Method method){
        Code code = method.getCode();
            String methodName = method.getName();
            System.out.println("Method Name: " + methodName);
            LineNumberTable lnt = code.getLineNumberTable();
            LineNumber[] lineNumbers = lnt.getLineNumberTable();
            for (LineNumber lineNumber : lineNumbers) {
                int lineNumberValue = lineNumber.getLineNumber();
                System.out.println("Line Number: " + lineNumberValue);
            }
    }

    public static void main(String[] args) throws ClassFormatException, IOException {
        ClassParser parser = new ClassParser("E:\\ScholarlyWork\\Program-Analysis-Project\\Java_Examples\\bin\\v1\\Test1.class");
        JavaClass javaClass = parser.parse();
        ClassGen classGen = new ClassGen(javaClass);
        ConstantPoolGen constantPool = new ConstantPoolGen(javaClass.getConstantPool());
        //get methods in a class
        Method[] methods = javaClass.getMethods();

        int i = 0;
        //for each method
        for (i = 0; i < methods.length; i++) {
            
            //*** line number and method name
            identifySourceLineNumberAndMehtodName(methods[i]);

            MethodGen methodGen = new MethodGen(methods[i], javaClass.getClassName(), constantPool);
            InstructionList il = methodGen.getInstructionList();
            Instruction[] instructions = il.getInstructions();
            int v = 0;
            System.out.println("CFG FOR : " + methods[i].getName() + "\n");
            ControlFlowGraph cfg = new ControlFlowGraph(methodGen);
            InstructionContext[] instructionContexts = cfg.getInstructionContexts();
            int j = 0;
            for (j = 0; j < instructionContexts.length; j++) {
                InstructionContext ic = instructionContexts[j];
                InstructionHandle ih = ic.getInstruction();
                Instruction ins = ih.getInstruction();
                InstructionContext[] succ = ic.getSuccessors();
                int k = 0;
                for (k = 0; k < succ.length; k++) {
                    InstructionContext ics = succ[k];
                    InstructionHandle ihs = ics.getInstruction();
                    Instruction inss = ihs.getInstruction();
                }
            }
            printGraph(cfg, il);
            System.out.println();
        }
    }
}
