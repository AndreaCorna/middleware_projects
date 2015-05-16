package middleware_jms.messages;

import java.io.Serializable;

public class ImageDownloaderToHtmlModifierMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String base64Encode;
	private String imageListName;
	
	public ImageDownloaderToHtmlModifierMessage(String base64Encode, String fileImageName){
		this.base64Encode = base64Encode;
		this.imageListName = fileImageName;
	}
	
	public String getBase64Encode(){
		return this.base64Encode;
	}
	
	public String getFileImageName(){
		return this.imageListName;
	}

}
