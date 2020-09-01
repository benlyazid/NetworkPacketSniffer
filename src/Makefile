JAR_FILE = /home/khalid/Downloads/jnetpcap-1.4.r1300/jnetpcap.jar
FLAG = -cp
JC = sudo javac
JVM = sudo java
FILE = com/net/capturePckages.java
POINT = .:
PACKAGE_NAME = com.net.capturePckages

compile:
	$(JC) $(FLAG) $(JAR_FILE) $(FILE) 
run:
	$(JVM) $(FLAG) $(POINT)$(JAR_FILE) $(PACKAGE_NAME) 

clean: 
	rm -rf com/net/*.class



all: clean compile run
	
	
