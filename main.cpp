#include <iostream>
#include <mpi.h>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

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
	
	int rank, number_processes;
	
    MPI_Init(&argc, &argv);	/* starts MPI */
	MPI_Comm_rank (MPI_COMM_WORLD, &rank);	/* get current process id */
	MPI_Comm_size (MPI_COMM_WORLD, &number_processes);
   
	Mat image;
	double inverse_gamma = 1.0 / 5;
	int i;
	uchar *buffer_image;
	int nrows,ncol,channels,buffer_size,item_for_process;
	
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

	MPI_Bcast(&item_for_process,1,MPI_INT,0,MPI_COMM_WORLD);

    
	cout<<"out "<<item_for_process<<endl;

	//Getting buffer from image
	
    //image dimension data
    
    
	uchar *gamma_corrected_buffer = (uchar *)malloc((sizeof(uchar))*item_for_process);
	
	
    
   
   MPI_Scatter(buffer_image, item_for_process, MPI_UNSIGNED_CHAR, gamma_corrected_buffer,item_for_process, MPI_UNSIGNED_CHAR, 0, MPI_COMM_WORLD);
    
	
	for(i=0;i < item_for_process;i++){
		*gamma_corrected_buffer = gammaCorrectPixel(*gamma_corrected_buffer,inverse_gamma);		
		gamma_corrected_buffer++;
		
	}
	uchar* dest = 0;
	if(rank == 0){
			dest = (uchar*)malloc((sizeof(uchar))*buffer_size);
	}
	
	//MPI_Gather(gamma_corrected_buffer, item_for_process, MPI_UNSIGNED_CHAR, dest, item_for_process, MPI_UNSIGNED_CHAR, 0,MPI_COMM_WORLD);
 
	//
	if(rank == 0){
		image.data = dest;
		imwrite( "./mod_image.jpg", image );
	
	}

	
	MPI_Finalize();
	
		
	//Mat image2 = correctGamma(image,2.0);

	
	//Mat gray_image;
	//cvtColor( image, gray_image, CV_BGR2GRAY );

	//imwrite( "./mod_image.jpg", image );
}
