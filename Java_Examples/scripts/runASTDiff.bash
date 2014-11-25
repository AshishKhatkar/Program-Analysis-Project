#!/bin/bash

# To run the AST Diff process set a path to the library folder
   export testHome=/Users/sperson/Documents/RahulsProject/Program-Analysis-Project/

# -dir specifies the directory to write the results
# -file specifies the name of the xml file
# (NOTE: if you run the tool with no options, you will get a usage message)

java -cp $testHome/lib/astro.jar:$testHome/lib/tools.jar cse.unl.edu.ast.ASTDiffer -original ../src/v1/Test1.java -modified ../src/v2/Test1.java -heu -xml -file Test1_V1_V2 -dir ../diffDir
