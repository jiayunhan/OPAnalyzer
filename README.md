# OPAnalyzer
Working repository for the EuroS&amp;P 17 paper: Open door for Bob and Mallory. 

Java Analyzer:
The Java layer analysis is built based on the [Amandroid](https://github.com/sireum/amandroid)  Project. Thanks to the authors of Amandroid. 
OPAnalyzer adds the open port specific taint tracking into the code base, together with the constraints analysis. 
Our analysis was built based on old version of Amandroid, and latest version can be found [here](https://github.com/arguslab/Argus-SAF).
Please follow the instruction on Amandroid [website](http://amandroid.sireum.org/docs/) to setup the environment and run the analysis. 

The Native code analysis uses the IDA Pro as backend, and runs inter-procedure taint analysis to find control-flow jumps from native code layer to Java layer. It takes .so files extracted from APKs as input, and outpute the taint paths from open port to system() calls

The ThreadController is a Java-based multi-thread controller that aims at parallelizing the download/decode/install/analysis of tens of thousands of APK files. 

The runtime-analysis automates the process of downloading APK files from our internal data store, install on the test device, and extract the runtime open port information from proc file, which provides us more insights into the potential vulnerabilities. 

This is just an unofficial release, and more documents and instructions will be added later. 
