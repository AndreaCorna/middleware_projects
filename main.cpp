#include <iostream>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

using namespace cv;

int main( int argc, char** argv ){
	Mat image;
	image = imread(IMAGE_PATH,1);
	
	Mat gray_image;
	cvtColor( image, gray_image, CV_BGR2GRAY );

	imwrite( "./Gray_Image.jpg", gray_image );
}
