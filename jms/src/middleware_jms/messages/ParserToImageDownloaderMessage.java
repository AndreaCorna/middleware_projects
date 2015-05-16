package middleware_jms.messages;

import java.io.Serializable;



public class ParserToImageDownloaderMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String base64Encode;
	private String listImageUrl;
	private String urlSite;
	
	public ParserToImageDownloaderMessage(String base64Encode, String listImageUrl,String urlSite){
		this.base64Encode = base64Encode;
		this.listImageUrl = listImageUrl;
		this.urlSite = urlSite;
	}
	
	public String getBase64Encode(){
		return this.base64Encode;
	}
	
	public String getListImageUrl(){
		return this.listImageUrl;
	}
	
	public String getUrlSite(){
		return this.urlSite;
	}

	
}
