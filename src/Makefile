JAR_FILE = /home/khalid/Downloads/jnetpcap-1.4.r1300/jnetpcap.jar
FLAG = -cp
JC = sudo javac
COMPILE_FLAG = -Xlint:unchecked
JVM = sudo java
FILE = com/net/capturePackages.java  com/net/networkScan.java
POINT = .:
PACKAGE_NAME = com.net.capturePackages

compile_sheck:
	$(JC) $(FLAG) $(JAR_FILE) $(COMPILE_FLAG) $(FILE)
compile:
	$(JC) $(FLAG) $(JAR_FILE) $(FILE) 
run:
	$(JVM) $(FLAG) $(POINT)$(JAR_FILE) $(PACKAGE_NAME)

clean:
	rm -rf com/net/*.class
all: clean compile run
