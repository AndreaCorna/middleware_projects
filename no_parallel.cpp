#include <iostream>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

using namespace cv;
using namespace std;

/*
int gammaCorrectPixel(int pixel,double inverse_gamma){	
	return (int)( pow( (double) pixel / 255, inverse_gamma ) * 255.0 );
}
* */

uchar gammaCorrectPixel(uchar pixel,double inverse_gamma){	
		
	return (uchar)( pow( (double) pixel / 255, inverse_gamma ) * 255.0 );

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
	double inverse_gamma = 1.0 / 0.2;
	
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
