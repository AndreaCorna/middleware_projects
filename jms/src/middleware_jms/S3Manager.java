package middleware_jms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Class to manage upload and get operations of file from S3 amazon service
 * @author andrea
 *
 */
public class S3Manager {
	
	private String bucketName = "middlewarebucket";
	
	/**
	 * Method to upload a file on S3 service
	 * @param file - file to upload
	 * @param fileName - name of the file used as key 
	 * @param directory - sub directory where upload the file
	 */
	public void uploadFile(File file,String fileName, String directory){

		AmazonS3 s3client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            s3client.putObject(new PutObjectRequest(
            		                 bucketName, directory+"/"+fileName, file));
            file.delete();

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	/**
	 * Get a file from S3 service
	 * @param directory - sub directory 
	 * @param id - id of file
	 * @return
	 */
	public File getFile(String directory, String id){
		File file = new File(id);
		AmazonS3 s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());        
		S3Object object = s3Client.getObject(
		                  new GetObjectRequest(bucketName, directory+"/"+id));
		InputStream reader = new BufferedInputStream(
				   object.getObjectContent());
		   
		try {
			OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			int read = -1;

			while ( ( read = reader.read() ) != -1 ) {
			    writer.write(read);
			}

			writer.flush();
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		return file;
	}
		

}
