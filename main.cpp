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

int main( int argc, char** argv ){
	Mat image;
	int i;
	int j;
	double inverse_gamma = 1.0 / 2.2;
	image = imread(IMAGE_PATH,1);
	#pragma omp parallel for shared(image)
		for(i=0;i<image.rows;i++){
			
			int* row = image.ptr<int>(i);
			#pragma omp parallel for shared(row)
				for(j=0;j<image.cols;j++){
					
				
					row[j] = (int)( pow( (double) row[j] / 255, inverse_gamma ) * 255.0 );
				
				
				}
			
			
			
		}
	
		
	//Mat image2 = correctGamma(image,2.0);

	
	//Mat gray_image;
	//cvtColor( image, gray_image, CV_BGR2GRAY );

	imwrite( "./mod_image.jpg", image );
}
