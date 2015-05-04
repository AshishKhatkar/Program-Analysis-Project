// Storing texual difference in structure 
// mapping byte code generated information ( line number and method name ) to the data structure

package datastructure;

import net.n3.nanoxml.*;

import java.util.*;
//import parser.Xmlparser;

class BlockInfo {

    String blockId;
    String version;
    String matchingBlock;
    String changeType;
    String start;
    String end;
    String parentBlock;
    String attributes;
}

class MethodSummary {

    String methodName;
    String modMethodReturnType;
    String origMethodReturnType;
    String defaultConstructorName;
    String matched;
    String equivalent;
    ArrayList<String> origModifiersArrayList = new ArrayList<>();
    ArrayList<String> origMethodParametersArrayList = new ArrayList<>();
    ArrayList<String> origMethodParamTypesArrayList = new ArrayList<>();
    ArrayList<String> modModifiersArrayList = new ArrayList<>();
    ArrayList<String> modMethodParametersArrayList = new ArrayList<>();
    ArrayList<String> modMethodParamTypesArrayList = new ArrayList<>();
    ArrayList<BlockInfo> blockInfo = new ArrayList<>();
}

class ClassSummary {

    String className;
    String computerMetaData;
    String origModifiers;
    String modModifiers;
    ArrayList<MethodSummary> methodSummary = new ArrayList<>();
}

class BlockSummary {

    String originalFile;
    String modifiedFile;
    ClassSummary classSummary;
}

public class MappingTextDiffToStruct {

    private int numTabs = 1;
    BlockSummary blockSummary = new BlockSummary();

    void mapByteCodeToSourceCode(String lineNumber, String methodName) {
        //BlockSummary is Global
        ClassSummary cs = blockSummary.classSummary;
        ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
        for (MethodSummary methodObject : methodSummaryList) {
            if (methodObject.methodName.equalsIgnoreCase(methodName)) {
                ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                for (BlockInfo blockInfoObject : blockInfoList) {
                    if (blockInfoObject.start.equals(lineNumber) && blockInfoObject.version.equalsIgnoreCase("original")) {
                        System.out.println("Mapped!!!!!!!!!!");
                        System.out.println("methodName: " + methodName);
                        System.out.println("Version: " + "Original");
                        System.out.println("lineNumber: " + lineNumber);
                    }
                }
            }
        }

    }

    void storeblockSummary(String childElement, String content) {
        switch (childElement) {
            case "originalFile":
                blockSummary.originalFile = content;
                break;
            case "modifiedFile":
                blockSummary.modifiedFile = content;
                break;
            case "classSummary":
                blockSummary.classSummary = new ClassSummary();
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: " + childElement);
        }

    }

    void storeClassSummary(ClassSummary classSummary, String childElement, String content) {
        switch (childElement) {
            case "className":
                classSummary.className = content;
                break;
            case "computeMetaData":
                classSummary.computerMetaData = content;
                break;
            case "origModifiers":
                classSummary.origModifiers = content;
                break;
            case "modModifiers":
                classSummary.modModifiers = content;
                break;
            case "methodSummary":
                blockSummary.classSummary.methodSummary.add(new MethodSummary());
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: " + childElement);
        }
    }

    public void storeMethodSummary(MethodSummary methodSummary, String childElement, String content) {
        switch (childElement) {
            case "methodName":
                methodSummary.methodName = content;
                break;
            case "origMethodReturnType":
                methodSummary.origMethodReturnType = content;
                break;
            case "modMethodReturnType":
                methodSummary.modMethodReturnType = content;
                break;
            case "defaultConstructorName":
                methodSummary.defaultConstructorName = content;
                break;
            case "origModifiers":
                methodSummary.origModifiersArrayList.add(content);
                break;
            case "origMethodParameters":
                methodSummary.origMethodParametersArrayList.add(content);
                break;
            case "origMethodParamTypes":
                methodSummary.origMethodParamTypesArrayList.add(content);
                break;
            case "modModifiers":
                methodSummary.modModifiersArrayList.add(content);
                break;
            case "modMethodParameters":
                methodSummary.modMethodParametersArrayList.add(content);
                break;
            case "modMethodParamTypes":
                methodSummary.modMethodParamTypesArrayList.add(content);
                break;

            case "matched":
                methodSummary.defaultConstructorName = content;
                break;
            case "equivalent":
                methodSummary.equivalent = content;
                break;
            case "blockInfo":
                blockSummary.classSummary.methodSummary.get(blockSummary.classSummary.methodSummary.size() - 1).blockInfo.add(new BlockInfo());
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: " + childElement);
        }
    }

    public void storeBlockInfo(BlockInfo blockInfo, String childElement, String content) {
        switch (childElement) {
            case "blockId":
                blockInfo.blockId = content;
                break;
            case "version":
                blockInfo.version = content;
                break;
            case "matchingBlock":
                blockInfo.matchingBlock = content;
                break;
            case "changeType":
                blockInfo.changeType = content;
                break;
            case "start":
                blockInfo.start = content;
                break;
            case "end":
                blockInfo.end = content;
                break;
            case "attributes":
                blockInfo.attributes = content;
                break;
            case "parentBlock":
                blockInfo.parentBlock = content;
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: " + childElement);

        }
    }

    public void store(String... args) {
//        String childElementParam, String contentParam, String parentElement
        String childElementParam = args[0];
        String contentParam = args[1];
        String parentElement = args[2];

        switch (parentElement) {
            case "blockSummary":
//                    storeblockSummary(blockSummary, childElementParam, contentParam);
                storeblockSummary(childElementParam, contentParam);
                break;
            case "classSummary":
                storeClassSummary(blockSummary.classSummary, childElementParam, contentParam);
                break;
            case "methodSummary":
                storeMethodSummary(blockSummary.classSummary.methodSummary.get(blockSummary.classSummary.methodSummary.size() - 1), childElementParam, contentParam);
                break;
            case "blockInfo":
                storeBlockInfo(blockSummary.classSummary.methodSummary.get(blockSummary.classSummary.methodSummary.size() - 1).blockInfo.get(blockSummary.classSummary.methodSummary.get(blockSummary.classSummary.methodSummary.size() - 1).blockInfo.size() - 1), childElementParam, contentParam);
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter: " + parentElement);

        }
    }

    public void readFile(IXMLElement element) {
        //System.out.println(Element.getFullName());
        Enumeration e = element.enumerateChildren();
        IXMLElement childElement = null;

        while (e.hasMoreElements()) {
//            System.out.println("element: " + element.getFullName());
            childElement = (IXMLElement) e.nextElement();

            String childElementParam = childElement.getFullName();
            String contentParam = childElement.getContent();
            String parentElement = element.getFullName();

            // Element has Children
            if (childElement.hasChildren()) {
                for (int i = numTabs; i > 0; i--) {
                    System.out.print("\t");
                }
                //ElementName
                System.out.println(childElement.getFullName());

                // store in a structure
                store(childElementParam, contentParam, parentElement);

                //If Element has attribute
                if (childElement.getAttributeCount() != 0) {
                    System.out.println("Attribute:  " + childElement.getAttributes());
                }
                numTabs++;
                readFile(childElement);
                numTabs--;
            } else {
                // Element has no more children
                for (int i = numTabs; i > 0; i--) {
                    System.out.print("\t");
                }
                System.out.println(childElement.getFullName() + ":  " + childElement.getContent());
//                blockSummary.childElement.getFullName() = childElement.getContent();

                // store in a structure
                store(childElementParam, contentParam, parentElement);

                if (childElement.getAttributeCount() != 0) {
                    System.out.println("Attribute:  " + childElement.getAttributes());
                }
                //System.out.println(childElement.getAttributes());
            }

        }

    }//EOMeth

    public static void main(String[] args) throws Exception {
        IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
        IXMLReader reader = StdXMLReader.fileReader("v1_Test1.xml");
        parser.setReader(reader);
        IXMLElement xml = (IXMLElement) parser.parse();
        System.out.println(xml.getFullName());

        MappingTextDiffToStruct textDiff = new MappingTextDiffToStruct();
        //reading xml file and storing in a structure
        textDiff.readFile(xml);

        // passign linenumber and method name
        textDiff.mapByteCodeToSourceCode("13", "func");
    }
}
