#include <iostream>
#include <mpi.h>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

//#define number_values_per_process 614400

using namespace cv;
using namespace std;



uchar gammaCorrectPixel(uchar pixel,double inverse_gamma){	
	return (uchar)( pow( (double) pixel / 255, inverse_gamma ) * 255.0 );
}



int main( int argc, char** argv ){
	Mat image;
	int* image_buffer;	
	double inverse_gamma = 1.0 / 5;
	int rank, number_processes;
	int i;
    
   
	
	//Getting buffer from image
	image = imread(IMAGE_PATH,1);
	uchar *buffer_image=image.ptr();
	cout << "size of int" << sizeof(int)<< " uchar" <<  sizeof(uchar);


    //image dimension data
    int nrows = image.rows;
    int ncol = image.cols;
    int channels = image.channels();
    //total size of image buffer array
    int buffer_size = ncol*nrows*channels;
    cout << "Buffer size: "<<buffer_size <<endl;

    
	
	MPI_Init(&argc, &argv);	/* starts MPI */
	MPI_Comm_rank (MPI_COMM_WORLD, &rank);	/* get current process id */
	MPI_Comm_size (MPI_COMM_WORLD, &number_processes);
	cout << "Number of processes : "<< number_processes <<endl;
	int number_values_per_process = (int)buffer_size/number_processes;
	cout << "values per  processes : "<< number_values_per_process <<endl;


	uchar *gamma_corrected_buffer = (uchar *)malloc((sizeof(uchar))*number_values_per_process);

    
   
    MPI_Scatter(buffer_image, number_values_per_process, MPI_BYTE, gamma_corrected_buffer,
            number_values_per_process, MPI_BYTE, 0, MPI_COMM_WORLD);
    
	 printf("Hello from processor %d of %d received %d \n", rank, number_processes, gamma_corrected_buffer[0]);


	for(i=0;i < number_values_per_process;i++){
		gamma_corrected_buffer[i] = gammaCorrectPixel(gamma_corrected_buffer[i],inverse_gamma);		
		
		
	}
	
	MPI_Gather(gamma_corrected_buffer, number_values_per_process, MPI_BYTE, buffer_image, number_values_per_process, MPI_BYTE, 0,MPI_COMM_WORLD);
 
	
	if(rank == 0){
		imwrite( "./mod_image.jpg", image );
	
	}
	MPI_Finalize();
    return 0;
	
		
	
		
	//Mat image2 = correctGamma(image,2.0);

	
	//Mat gray_image;
	//cvtColor( image, gray_image, CV_BGR2GRAY );

	//imwrite( "./mod_image.jpg", image );
}
