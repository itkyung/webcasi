package com.kbsmc.webcasi.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

	private static final Pattern USERNAME_REGEXP = Pattern.compile("^\\w{6,}$");
	private static final Pattern EMAIL_REGEXP = Pattern.compile("^([\\w-]+){1,}@[\\w-]+(\\.[\\w-]+){1,}$");
	private static final Pattern IMAGE_REGEXP = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif))$)");
	
	private static final Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
	private static final Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
	private static final Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
	private static final Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
	private static final Pattern WHITESPACE = Pattern.compile("\\s\\s+");

	private static final Pattern INNER_BODY_REGEXP = Pattern.compile(".*<body[^>]*>(.*)</body>.*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	
	public static boolean isUsernameValid(String username) {
		return USERNAME_REGEXP.matcher(username).matches();
	}

	public static boolean isEmailValid(String email) {
		return EMAIL_REGEXP.matcher(email).matches();
	}

	public static boolean isImageFilename(String filename) {
		return IMAGE_REGEXP.matcher(filename).matches();
	}
	
	public static String innerBody(String html) {
		html = html.replaceAll("\r\n|\r|\n", " ");
		Matcher matcher = INNER_BODY_REGEXP.matcher(html);
		if(matcher.matches()) {
			return matcher.group(1);
		}

		return "";
	}
	
	public static String removeHTML(String originString) {
		
		if(StringUtils.isBlank(originString)) {
			return originString;
		}
		
		originString = SCRIPTS.matcher(originString).replaceAll("");
		originString = STYLE.matcher(originString).replaceAll("");
		originString = TAGS.matcher(originString).replaceAll("");
		originString = ENTITY_REFS.matcher(originString).replaceAll("");
		originString = WHITESPACE.matcher(originString).replaceAll(" ");
		return originString;
	}
	
	public static String escapeScriptString(String org){
		return org.replaceAll("'", "");
	}
	
}