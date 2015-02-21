package middleware_jms.messages;

import java.io.Serializable;



public class DownloadToParserMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String base64Encode;
	private String htmlFileName;
	
	public DownloadToParserMessage(String base64Encode, String htmlFileName){
		this.base64Encode = base64Encode;
		this.htmlFileName = htmlFileName;
	}
	
	public String getBase64Encode(){
		return this.base64Encode;
	}
	
	public String getHtmlFileName(){
		return this.htmlFileName;
	}

}
