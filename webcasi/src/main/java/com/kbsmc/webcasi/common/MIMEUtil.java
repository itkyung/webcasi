package com.kbsmc.webcasi.common;

import java.util.HashMap;
import java.util.Map;

public class MIMEUtil {
	public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
	private static final Map<String, String> MIME_MAPPINGS = new HashMap<String, String>(256);
	private static final String MIME_MAPPINGS_ARRAY[]= {
		"7z", "application/x-7z-compressed",
		"avi", "video/x-msvideo",
		"bmp", "image/bmp",
		"css", "text/css",
		"csv", "text/comma-separated-values",
		"doc", "application/msword",
		"docx", "application/msword",
		"gif", "images/gif",
		"gz", "application/x-gzip",
		"htc", "text/x-component",
		"htm", "text/html",
		"html", "text/html",
		"jpg", "images/jpeg",
		"jpe", "image/jpeg",
		"jpeg", "image/jpeg",
		"js", "application/x-javascript",
		"json", "application/json",
		"mid", "audio/mid",
		"mp3", "audio/mpeg",
		"mov", "video/quicktime",
		"mpg", "video/mpeg",
		"mpeg", "video/mpeg",
		"pdf", "application/pdf",
		"png", "images/png",
		"ppt", "application/vnd.ms-powerpoint",
		"pptx", "application/vnd.ms-powerpoint",
		"ra", "audio/x-pn-realaudio",
		"ram", "audio/x-pn-realaudio",
		"svg", "image/svg+xml",
		"swf", "application/x-shockwave-flash",
		"tar", "application/x-tar",
		"tif", "image/tiff",
		"tiff", "image/tiff",
		"txt", "text/plain",
		"wav", "audio/x-wav",
		"xls", "application/vnd.ms-excel",
		"xlsx", "application/vnd.ms-excel",
		"xml", "text/xml",
		"zip", "application/zip"
	};
	
	static {
		try {
			for (int i = 0; i < MIME_MAPPINGS_ARRAY.length; i+= 2) {
				MIME_MAPPINGS.put(MIME_MAPPINGS_ARRAY[i], MIME_MAPPINGS_ARRAY[i+1]);
			}
		} catch (Throwable t) {
			t.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * extension 에 해당되는 Mime type 을 구함
	 * @param fileName
	 * @return extension 에 해당되는 Mime type
	*/
	public static final String getMimeType(String fileName) {
		int idx = fileName.lastIndexOf('.');
		String result = DEFAULT_MIME_TYPE;
		if (idx != -1) {
			result = MIME_MAPPINGS.get(fileName.substring(idx+1).toLowerCase());
	    }
		return result == null ? DEFAULT_MIME_TYPE : result;
	}
}
