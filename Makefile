all: main

main: main.o
	mpic++ main.o  -fopenmp `pkg-config --libs opencv` -o main
	
main.o: main.cpp
	mpic++ -c main.cpp -fopenmp  `pkg-config opencv --cflags`  
	
clean:
	rm *o main
