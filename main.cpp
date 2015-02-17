#include <iostream>
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
	Mat image;
	int* image_buffer;	
	int i;
	int j;
	double inverse_gamma = 1.0 / 5;
	
	image = imread(IMAGE_PATH,1);
	uchar *buffer_image=image.ptr();
    //image dimension data
    int nrows = image.rows;
    int ncol = image.cols;
    int channels = image.channels();
    //total size of image buffer array
   
	
	
	for(i=0;i < ncol*nrows*channels;i++){
		*buffer_image = gammaCorrectPixel(*buffer_image,inverse_gamma);		
		buffer_image++;
		
	}
	

	
		
	
		
	//Mat image2 = correctGamma(image,2.0);

	
	//Mat gray_image;
	//cvtColor( image, gray_image, CV_BGR2GRAY );

	imwrite( "./mod_image.jpg", image );
}
