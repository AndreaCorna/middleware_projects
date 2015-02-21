package middleware_jms.messages;

import java.io.Serializable;



public class ParserToImageDownloaderMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String base64Encode;
	private String imageAbsoluteUrl;
	
	public ParserToImageDownloaderMessage(String base64Encode, String imageAbsoluteUrl){
		this.base64Encode = base64Encode;
		this.imageAbsoluteUrl = imageAbsoluteUrl;
	}
	
	public String getBase64Encode(){
		return this.base64Encode;
	}
	
	public String getAbsoluteImageUrl(){
		return this.imageAbsoluteUrl;
	}

	
}
