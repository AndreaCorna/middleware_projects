#include <iostream>
#include "omp.h"
#include <cv.h>
#include <highgui.h>
#define IMAGE_PATH "./test.jpg"

using namespace cv;

int main( int argc, char** argv ){
	Mat image;
	int i;
	int j;
	image = imread(IMAGE_PATH,1);
	
	for(i=0;i<image.rows;i++){
		
		int* row = image.ptr<int>(i);
		for(j=0;j<image.cols;j++){
			cout << row[j];
		}
	}
	
	
	//Mat gray_image;
	//cvtColor( image, gray_image, CV_BGR2GRAY );

	//imwrite( "./Gray_Image.jpg", gray_image );
}
