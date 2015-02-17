#include <iostream>
#include <mpi.h>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

//#define number_values_per_process 614400

using namespace cv;
using namespace std;

Mat correctGamma( Mat& img, double gamma ) {
	double inverse_gamma = 1.0 / gamma;

	Mat lut_matrix(1, 256, CV_8UC1 );
	uchar * ptr = lut_matrix.ptr();
	for( int i = 0; i < 256; i++ )
	ptr[i] = (int)( pow( (double) i / 255.0, inverse_gamma ) * 255.0 );

	Mat result;
	LUT( img, lut_matrix, result );

	return result;
}

int gammaCorrectPixel(int pixel,double inverse_gamma){	
	return (int)( pow( (double) pixel / 255, inverse_gamma ) * 255.0 );
}

int* linearize_image(Mat image){
	int* image_buffer;	
	image_buffer = (int *) malloc((sizeof(int))*image.rows*image.cols);

	
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
	cout << "sizeop uchar " << sizeof(uchar) <<endl;
	
		cout << "sizeop mpiint " << 	MPI_INT <<endl;

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
