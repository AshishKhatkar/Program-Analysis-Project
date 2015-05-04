/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;


/**
 * @author Masood
 */


public class MVICFG {

    class Edge {

        String dest;
        ArrayList<Integer> versions;

        Edge(String dest, ArrayList<Integer> versions) {
            this.dest = dest;
            this.versions = versions;
        }

        public void addVersion(int v) {
            versions.add(v);
        }
    }

    private HashMap<String, ArrayList<Edge>> graph;
    private HashMap<String , Set<String>> mapping;
    
    public MVICFG() {
        this.graph = new HashMap<String, ArrayList<Edge>>();
        this.graph.put("-1", new ArrayList<Edge>());
        this.mapping = new HashMap<String, Set<String>>();
    }

    public HashMap<String, ArrayList<Edge>> getGraph() {
        return graph;
    }

    public void addEdge(int src, int dest, int ver, int srcmblock, int destmblock, int sourceMatchBlockLine, int destMatchBlockLine) {

        if (ver == 1) { // first version
            String sr = "";
            sr += src;
            sr += " ver ";
            sr += (ver);

            String des = "";
            des += dest;
            des += " ver ";
            des += (ver);
            // dest into src
            if (dest == -1) {
                des = "-1";
                Edge edge = new Edge(des, new ArrayList<Integer>());
                edge.addVersion(ver);
                ArrayList<Edge> arrayListEdge = new ArrayList<Edge>();
                arrayListEdge.add(edge);
                graph.put(sr, arrayListEdge);
                mapping.put(sr, new HashSet<String>());
            } else {
                Edge edge = new Edge(des, new ArrayList<Integer>());
                edge.addVersion(ver);
                ArrayList<Edge> arrayListEdge = new ArrayList<Edge>();
                arrayListEdge.add(edge);
                graph.put(sr, arrayListEdge);
                
                mapping.put(sr, new HashSet<String>());
            }
            // sr into dest
            if (dest == -1) {
                Edge edge = new Edge(sr, new ArrayList<Integer>());
                edge.addVersion(ver);
                graph.get("-1").add(edge);
            } else {
                // Create a new edge with src and add it to the adj of dest
                Edge e = new Edge(sr, new ArrayList<Integer>());
                e.addVersion(ver);
                graph.get(des).add(e);
            }
        } // if (ver == 1)
        else { // if( ver != 1 )

            if (dest == -1) {
                if (srcmblock == -1) {
                    String sr = "";
                    sr += src;
                    sr += " ver ";
                    sr += (ver);

                    String des = "-1";
                    ArrayList<Edge> arrayListEdge = new ArrayList<Edge>();

                    Edge edge = new Edge(des, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    arrayListEdge.add(edge);
                    graph.put(sr, arrayListEdge);

                    edge = new Edge(sr, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    graph.get(des).add(edge);
                    mapping.put(sr, new HashSet<String>());

                } else {
                    // if(srcmblock != -1)
                    // get the line number of matching block in previous version
                    int sourceMatchLine = sourceMatchBlockLine; // will it work if previous version's block is a
                    // match to its previous version ?
                    String sr = "";
                    sr += sourceMatchLine;
                    sr += " ver ";
                    sr += (ver - 1);

                    String des = "-1";

                    // updating edge lable  from source to destination
                    for (Edge e : graph.get(sr)) {
                        if (e.dest.equals(des)) {
                            e.addVersion(ver);
                            break;
                        }
                    }

                    // updating edge lable from destination to source
                    for (Edge e : graph.get(des)) {
                        if (e.dest.equals(sr)) {
                            e.addVersion(ver);
                            break;
                        }
                    }
                    String map_src = "";
                    map_src += src;
                    map_src += " ver ";
                    map_src += ver;
                    mapping.get(sr).add(map_src);
                } // else ( dest != -1 )
            } // if (dest == -1)
            else if (destmblock == -1) { // i.e dest is of v2  
                //case 1
                if (srcmblock == -1) { //i.e soource is of v2
                    // if source does not have a match
                    // add an edge b/w source and destination
                    String sr = "";
                    sr += src;
                    sr += " ver ";
                    sr += (ver);

                    String des = "";
                    des += dest;
                    des += " ver ";
                    des += (ver);

                    ArrayList<Edge> arrayListEdge = new ArrayList<Edge>();

                    Edge edge = new Edge(des, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    arrayListEdge.add(edge);
                    graph.put(sr, arrayListEdge);

                    Edge e = new Edge(sr, new ArrayList<Integer>());
                    e.addVersion(ver);
                    graph.get(des).add(e);
                    mapping.put(sr, new HashSet<String>());
                } else { // case 2
                    // if(srcmblock != -1) source is of v1
                    // matchSrcNode = matchingBlock(src, ver, mblock); make ver -1
                    // add edge b/w matchnode and destination

                    int sourceMatchLine = sourceMatchBlockLine;//getMatchingBlockLine(src, ver, destmblock);

                    String sr = "";
                    sr += sourceMatchBlockLine;
                    sr += " ver ";
                    sr += (ver - 1);

                    String des = "";
                    des += dest;
                    des += " ver ";
                    des += (ver);
                    
                    // finding key that has sr as mapping
                    boolean found = false;
                    String key_map = sr;
                    for(String k : mapping.keySet())
                    {
//                        System.out.print(k + "  :    ");
                        for(String mpp : mapping.get(k))
                        {
                            if(mpp.equals(sr))
                            {
                                found = true;
                                key_map = k;
                                break;
                            }
                        }
                        if(found)
                            break;
//                            System.out.print(mpp + "    ");
//                        System.out.println();
                    }
                    
                    Edge edge = new Edge(des, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    graph.get(key_map).add(edge);

                    edge = new Edge(key_map, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    graph.get(des).add(edge);
                    
                    String map_src = "";
                    map_src += src;
                    map_src += " ver ";
                    map_src += ver;
                    mapping.get(key_map).add(map_src);
                }
            } else if (destmblock != -1) {//i.e dest is of v1
                // case 3
                if (srcmblock == -1) { // source is of v2
                    // matchDestNode = matchingBlock(dest, ver, mblock) make ver -1
                    // add an edge b/w src and destnode

                    int destMatchLine = destMatchBlockLine;//getMatchingBlockLine(dest, ver, destmblock);

                    String sr = "";
                    sr += src;
                    sr += " ver ";
                    sr += (ver);

                    String des = "";
                    des += destMatchLine;
                    des += " ver ";
                    des += (ver - 1);

                    // finding key that has des as mapping
                    boolean found = false;
                    String key_map = des;
                    for(String k : mapping.keySet())
                    {
//                        System.out.print(k + "  :    ");
                        for(String mpp : mapping.get(k))
                        {
                            if(mpp.equals(des))
                            {
                                found = true;
                                key_map = k;
                                break;
                            }
                        }
                        if(found)
                            break;
//                            System.out.print(mpp + "    ");
//                        System.out.println();
                    }    
                    
                    ArrayList<Edge> arrayListEdge = new ArrayList<Edge>();

                    Edge edge = new Edge(key_map, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    arrayListEdge.add(edge);
                    graph.put(sr, arrayListEdge);

                    edge = new Edge(sr, new ArrayList<Integer>());
                    edge.addVersion(ver);
                    graph.get(key_map).add(edge);
                    String match_des = "";
                    match_des += dest;
                    match_des += " ver ";
                    match_des += ver;
                    mapping.get(key_map).add(match_des);
                    mapping.put(sr, new HashSet<String>());
                } else { // case 4
                    // matchSrcNode = matchingBlock( src, ver, srcmblock)
                    // matchDestNode = matchingBlock( dest, ver, destmblock)
                    // add an edge b/w src and destnode

                    int sourceMatchLine = sourceMatchBlockLine;//getMatchingBlockLine(src, ver, srcmblock);
                    int destMatchLine = destMatchBlockLine;//getMatchingBlockLine(dest, ver, destmblock);

                    String sr = "";
                    sr += sourceMatchBlockLine;
                    sr += " ver ";
                    sr += (ver - 1);

                    String des = "";
                    des += destMatchLine;
                    des += " ver ";
                    des += (ver - 1);

                    // finding key that has sr as mapping
                    boolean found = false;
                    String key_map_src = sr;
                    for(String k : mapping.keySet())
                    {
//                        System.out.print(k + "  :    ");
                        for(String mpp : mapping.get(k))
                        {
                            if(mpp.equals(sr))
                            {
                                found = true;
                                key_map_src = k;
                                break;
                            }
                        }
                        if(found)
                            break;
//                            System.out.print(mpp + "    ");
//                        System.out.println();
                    }
                    
                    // finding key that has des as mapping
                    found = false;
                    String key_map_dest = des;
                    for(String k : mapping.keySet())
                    {
//                        System.out.print(k + "  :    ");
                        for(String mpp : mapping.get(k))
                        {
                            if(mpp.equals(des))
                            {
                                found = true;
                                key_map_dest = k;
                                break;
                            }
                        }
                        if(found)
                            break;
//                            System.out.print(mpp + "    ");
//                        System.out.println();
                    }
                    
                    for (Edge e : graph.get(key_map_src)) {
                        if (e.dest.equals(key_map_dest)) {
                            e.addVersion(ver);
                            break;
                        }
                    }

                    for (Edge e : graph.get(key_map_dest)) {
                        if (e.dest.equals(key_map_src)) {
                            e.addVersion(ver);
                            break;
                        }
                    }
                    String match_des = "";
                    match_des += dest;
                    match_des += " ver ";
                    match_des += ver;
                    mapping.get(key_map_dest).add(match_des);
                    String map_src = "";
                    map_src += src;
                    map_src += " ver ";
                    map_src += ver;
                    mapping.get(key_map_src).add(map_src);
                }
            } // if (destmblock == -1)
        } // else if (ver != 1 )

    } //addEdge

    public void printGraph(HashMap<String, ArrayList<Edge>> graph) {
        Set<String> key = graph.keySet();
        for (String src : key) {
            System.out.print(src + " : ");
            ArrayList<Edge> ar = graph.get(src);
            for (Edge e : ar) {
                System.out.print(e.dest + "<" + e.versions + ">" + " : ");
            }
            System.out.println();
        }
    }

    // destination line number within same version
    public String getDestination(MappingTextDiffToStruct textDiff, String className, String methodName, String version, String blockId) {
        if (blockId.equals("-1")) {
            return "-1";
        } else {
            ClassSummary cs = textDiff.blockSummary.classSummary;
            ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
            for (MethodSummary methodObject : methodSummaryList) {
                if (methodObject.methodName.equalsIgnoreCase(methodName)) {
                    ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                    for (BlockInfo blockInfoObject : blockInfoList) {
                        if (blockInfoObject.version.equals(version) && blockInfoObject.blockId.equals(blockId)) { // for original
                            return blockInfoObject.start;
                        }
                    }
                }
            }
        }
        return "-1";
    }

    // go to parent, return destination matching block
    public String getDestinationMatchingBlockTag(MappingTextDiffToStruct textDiff, String className, String methodName, String version, String blockId) {
        if (blockId.equals("-1")) {
            return "-1";
        } else {
            ClassSummary cs = textDiff.blockSummary.classSummary;
            ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
            for (MethodSummary methodObject : methodSummaryList) {
                if (methodObject.methodName.equals(methodName)) {
                    ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                    for (BlockInfo blockInfoObject : blockInfoList) {
                        if (blockInfoObject.version.equals(version) && blockInfoObject.blockId.equals(blockId)) { // for original
                            return blockInfoObject.matchingBlock;
                        }
                    }
                }
            }
        }
        return "-1";

    }

    // returns line no. of matching block or say node
    public String getMatchingBlockLine(MappingTextDiffToStruct textDiff, String className, String methodName, String version, String blockId) {
        // go to block ( = mblock) of previous version
        // retrive start 
        // convert to int and return

        ClassSummary cs = textDiff.blockSummary.classSummary;
        ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
        for (MethodSummary methodObject : methodSummaryList) {
            if (methodObject.methodName.equals(methodName)) {
                ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                for (BlockInfo blockInfoObject : blockInfoList) {
                    if (blockInfoObject.version.equals(version) && blockInfoObject.blockId.equals(blockId)) { // for original
                        return blockInfoObject.start;
                    }
                }
            }
        }
        return "-2";
    }

    public void matchingNode(String node){
        boolean found = false;
        String key_map = node;
        for(String k : mapping.keySet())
        {
            for(String mpp : mapping.get(k))
            {
                if(mpp.equals(node))
                {
                    found = true;
                    key_map = k;
                    break;
                }
            }
            if(found)
                break;
        }
        System.out.println(node);
        for (Edge e : graph.get(key_map)) 
        {
            System.out.println(e.dest + " " + e.versions);
        }
    }
    public void createGraphOriginal(MappingTextDiffToStruct textDiff) {
        ClassSummary cs = textDiff.blockSummary.classSummary;
        String className = cs.className;
        ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
        for (MethodSummary methodObject : methodSummaryList) {
            String methodName = methodObject.methodName;
            if (methodName.equals("func")) {
                ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                for (BlockInfo blockInfoObject : blockInfoList) {
                    if (blockInfoObject.version.equals("original")) {

                        int source = Integer.parseInt(blockInfoObject.start);
                        int dest = Integer.parseInt(getDestination(textDiff, className, methodName, "original", blockInfoObject.parentBlock));
                        int version = 1;
                        Integer sourceMatchingBlock = Integer.parseInt(blockInfoObject.matchingBlock);
                        Integer destMatchingBlock = Integer.parseInt(getDestinationMatchingBlockTag(textDiff, className, methodName, "original", blockInfoObject.parentBlock));
                        int sourceMatchBlockLine = Integer.parseInt(getMatchingBlockLine(textDiff, className, methodName, "modified", sourceMatchingBlock.toString()));
                        int destMatchBlockLine = Integer.parseInt(getMatchingBlockLine(textDiff, className, methodName, "modified", destMatchingBlock.toString()));

//                        System.out.println(source + ":" + dest + ":" + version + ":" + sourceMatchingBlock + ":" + destMatchingBlock + ":" + sourceMatchBlockLine + ":" + destMatchBlockLine);
                        addEdge(source, dest, version, sourceMatchingBlock, destMatchingBlock, sourceMatchBlockLine, destMatchBlockLine);

                    }
                }
            }

        }
    }

    public void createGraphModified(MappingTextDiffToStruct textDiff, int ver) {
        ClassSummary cs = textDiff.blockSummary.classSummary;
        String className = cs.className;
        ArrayList<MethodSummary> methodSummaryList = cs.methodSummary;
        for (MethodSummary methodObject : methodSummaryList) {
            String methodName = methodObject.methodName;
            if (methodName.equals("func")) {
                ArrayList<BlockInfo> blockInfoList = methodObject.blockInfo;
                for (BlockInfo blockInfoObject : blockInfoList) {
                    if (blockInfoObject.version.equals("modified")) {

                        int source = Integer.parseInt(blockInfoObject.start);
                        int dest = Integer.parseInt(getDestination(textDiff, className, methodName, "modified", blockInfoObject.parentBlock));
                        int version = ver;
                        Integer sourceMatchingBlock = Integer.parseInt(blockInfoObject.matchingBlock);
                        Integer destMatchingBlock = Integer.parseInt(getDestinationMatchingBlockTag(textDiff, className, methodName, "modified", blockInfoObject.parentBlock));
                        int sourceMatchBlockLine = Integer.parseInt(getMatchingBlockLine(textDiff, className, methodName, "original", sourceMatchingBlock.toString()));
                        int destMatchBlockLine = Integer.parseInt(getMatchingBlockLine(textDiff, className, methodName, "original", destMatchingBlock.toString()));

//                        System.out.println(source + ":" + dest + ":" + version + ":" + sourceMatchingBlock + ":" + destMatchingBlock + ":" + sourceMatchBlockLine + ":" + destMatchBlockLine);
                        addEdge(source, dest, version, sourceMatchingBlock, destMatchingBlock, sourceMatchBlockLine, destMatchBlockLine);

                    }
                }
            }

        }
    }

    public void runTextDiff() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, XMLException {
        IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
        IXMLReader reader = StdXMLReader.fileReader("testingSoftware_v1_Test1.xml");
        parser.setReader(reader);
        IXMLElement xml = (IXMLElement) parser.parse();
        System.out.println(xml.getFullName());
    
        //Class instance
        MappingTextDiffToStruct textDiff = new MappingTextDiffToStruct();
        textDiff.readFile(xml);
        
        //create cfg for v1
        createGraphOriginal(textDiff);
        //create mvicfg v_1-2
        createGraphModified(textDiff, 2);
//        System.out.println("******* integrating version 3 *********");
//        //create mvicfg v_1-2-3 v2_Test1
//        reader = StdXMLReader.fileReader("v2_Test1.xml");
//        parser.setReader(reader);
//        xml = (IXMLElement) parser.parse();
//        textDiff.readFile(xml);
//        createGraphModified(textDiff, 3);
        
    }
    
    public void print_mapping()
    {
        for(String k : mapping.keySet())
        {
            System.out.print(k + "  :    ");
            for(String mpp : mapping.get(k))
                System.out.print(mpp + "    ");
            System.out.println();
        }
    }
            
    
    public static void main(String[] args) throws Exception {

        MVICFG mvicfg = new MVICFG();
        mvicfg.runTextDiff();
        mvicfg.matchingNode("13 ver 1");
        mvicfg.print_mapping();
//         Add 1st version
//         addEdge(src, dest, version, srcmblock, destmblock)
//         provide srcmblock and destmblock from text diff
//        mvicfg.addEdge(13, -1, 1, 2, -1);
//        mvicfg.addEdge(14, 13, 1, 3, 2);
//        mvicfg.addEdge(16, 13, 1, 1, 2);
//
//        // Add 2nd version
//        mvicfg.addEdge(13, -1, 2, -1, -1);
//        mvicfg.addEdge(14, 13, 2, 2, -1);
//        mvicfg.addEdge(15, 13, 2, 0, -1);
//        mvicfg.addEdge(16, 15, 2, 1, 0);
//        mvicfg.addEdge(18, 15, 2, -1, 0);

        /*
         13:-1:1:2:-1:15:-2
        14:13:1:3:2:16:15
        16:13:1:1:2:14:15
        
        13:-1:2:-1:-1:-2:-2
        14:13:2:2:-1:16:-2
        15:13:2:0:-1:13:-2
        16:15:2:1:0:14:13
        18:15:2:-1:0:-2:13
         */
        mvicfg.printGraph(mvicfg.getGraph());
    }
 }
