package middleware_jms.messages;

import java.io.Serializable;



public class ParserToImageDownloaderMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String base64Encode;
	private String listImageUrl;
	
	public ParserToImageDownloaderMessage(String base64Encode, String listImageUrl){
		this.base64Encode = base64Encode;
		this.listImageUrl = listImageUrl;
	}
	
	public String getBase64Encode(){
		return this.base64Encode;
	}
	
	public String getListImageUrl(){
		return this.listImageUrl;
	}

	
}
