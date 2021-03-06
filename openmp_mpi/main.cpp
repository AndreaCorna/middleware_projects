#include <iostream>
#include <mpi.h>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

using namespace cv;
using namespace std;


/*Code for gamma correction for a single pixel*/
int gammaCorrectPixel(int pixel,double inverse_gamma){	
	return (int)( pow( (double) pixel / 255, inverse_gamma ) * 255.0 );
}


int main( int argc, char** argv ){
	
	int rank, number_processes;
	
    MPI_Init(&argc, &argv);	/* starts MPI */
	MPI_Comm_rank (MPI_COMM_WORLD, &rank);	/* get current process id */
	MPI_Comm_size (MPI_COMM_WORLD, &number_processes);
   
	Mat image;
	double inverse_gamma = 1.0 / 5;
	int i;
	uchar *buffer_image;
	int nrows,ncol,channels,buffer_size,item_for_process;
	
	if(argc<2){
		printf("Insert the number of threads");
		exit(0);
	}
	int num_threads = atoi(argv[1]);
	
	if(rank == 0){
		image = imread(IMAGE_PATH,1);
		buffer_image=image.ptr();
		nrows = image.rows;
		ncol = image.cols;
		channels = image.channels();
		buffer_size = ncol*nrows*channels;
		item_for_process = (int) buffer_size/number_processes;
		cout<<"i've receive "<<item_for_process<<endl;
	}
	//Broadcast of number of pixel to be processed by every single machine
	MPI_Bcast(&item_for_process,1,MPI_INT,0,MPI_COMM_WORLD);

    
	cout<<"out "<<item_for_process<<endl;

	//buffer for elaboration
	uchar *gamma_corrected_buffer = (uchar *)malloc((sizeof(uchar))*item_for_process);
	//Scatter of the data 
    MPI_Scatter(buffer_image, item_for_process, MPI_UNSIGNED_CHAR, gamma_corrected_buffer,item_for_process, MPI_UNSIGNED_CHAR, 0, MPI_COMM_WORLD);
    
	/*gamma correction with omp elaboration*/
	omp_set_num_threads(num_threads);
	#pragma omp parallel for shared(gamma_corrected_buffer)
		for(i=0;i < item_for_process;i++){
			gamma_corrected_buffer[i] = gammaCorrectPixel(gamma_corrected_buffer[i],inverse_gamma);		
			
		}
	
	uchar* dest = 0;
	if(rank == 0){
			dest = (uchar*)malloc((sizeof(uchar))*buffer_size);
	}
	//collect all the data
	MPI_Gather(gamma_corrected_buffer, item_for_process, MPI_UNSIGNED_CHAR, dest, item_for_process, MPI_UNSIGNED_CHAR, 0,MPI_COMM_WORLD);
 

	if(rank == 0){
		image.data = dest;
		imwrite( "./mod_image.jpg", image );
	}

	
	MPI_Finalize();
	
}
