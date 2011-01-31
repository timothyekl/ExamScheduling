all:
	javac -d bin/ src/*.java

clean:
	rm -rf bin/*.class
