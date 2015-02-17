all: main

main: main.o
	g++ main.o  `pkg-config --libs opencv` -o main
	
main.o: main.cpp
	g++ -c main.cpp -fopenmp  `pkg-config opencv --cflags`  
	
clean:
	rm *o main
