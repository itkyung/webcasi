package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.common.ResourceUtils;



/**
 * HttpServletResponse 관련 utility.
 * 예: JSON data 로 message 보내기 및 modified check 같은 것. 
 * @author rhichang
 *
 */
abstract public class ViewUtils {
	static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	static final String LAST_MODIFIED = "Last-Modified";

	static public final String APPLICATION_JSON = "application/json";
	static public final String TEXT_HTML = "text/html";
	private static final Log log = LogFactory.getLog(ViewUtils.class);

	/**
	 * field 명 (name) 을 label (예 : totalAmount -> total amount) 사용할 수 있게 변경
	 * @param name label로 변경할 field 명
	 * @return name 에 해당되는 label
	 */
	public static String label(String name) {
		if (name.length() > 3) {
			StringBuilder buf = new StringBuilder();
			char []arr = name.toCharArray();
			buf.append(Character.toUpperCase(arr[3]));
			for (int i = 4; i < arr.length; i++) {
				char ch = arr[i];
				if (Character.isUpperCase(ch)) {
					buf.append(' ');
				}
				buf.append(ch);
			}
			return buf.toString();
		}
		return null;
	}

	/**
	 * trim 된 s, 그리고 만약 s 가 empty string 이라면 null
	 * @param s trim/null 처리할 string
	 * @return trim 된 s, 그리고 만약 s 가 empty string 이라면 null
	 */
	public static final String str(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.length() == 0) return null;
		return s;
	}

	/**
	 * s 를 javascript string (즉 " 로 쌓여 있는) 으로 변경함
	 * @param s 변경할 string
	 * @return s 에 대한 javascript string
	 */
	public static final String jsonStr(String s) {
		return CommonUtils.toJavascriptString(s);
	}

	/**
	 * results 를 response 로 보냄
	 * @param response 여기에 보냄
	 * @param results 보낼 data
	 * @param contentType 사용할 content type
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void data(HttpServletResponse response, String results, String contentType) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		PrintWriter out = response.getWriter();
		out.println(results);
		out.flush();
	}

	/**
	 * results 를 response 로 보냄. contentType 은 application/json 으로 사용함.
	 * @param response HttpServletResponse
	 * @param results 보낼 data
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonData(HttpServletResponse response, String results ) throws IOException {
		data(response, results, APPLICATION_JSON);
	}

	/**
	 * results 를 json serialize 하여 (IJSON 사용하여) resposne 로 보냄
	 * @param response 여기로 보냄
	 * @param json json serialize 를 이것으로 함
	 * @param results 보낼 data
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonData(HttpServletResponse response, IJSON json, Object results) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(APPLICATION_JSON);
		PrintWriter out = response.getWriter();
		System.out.println(json.serialize(results));
		json.serialize(results, out);
		out.flush();
	}

	/**
	 * JSON success json object 를 보냄. data 에는 results 값을 넣음.
	 * @param response 보낼 response
	 * @param results data 에 넣을 객체
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(HttpServletResponse response, String results) throws IOException {
		jsonSuccess(response, results, null);
	}
	/**
	 * JSON success json object 를 보냄. message 에는 message 값을 넣음.
	 * @param response 보낼 response
	 * @param message message 에 넣을 메시지
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccessMessage(HttpServletResponse response, String message) throws IOException {
		jsonSuccess(response, (String)null, message);
	}  
	/**
	 * JSON success message 를 보냄. data 에는 results 값을 넣음.
	 * @param response 보낼 response
	 * @param json results 를 이 객체를 사용하여 serialize 함
	 * @param results data 에 넣을 객체
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(HttpServletResponse response, IJSON json, Object results) throws IOException {
		jsonSuccess(response, APPLICATION_JSON, json, results, null);
	}

	/**
	 * json success json object 를 string 으로 return 함
	 * @param results 결과
	 * @param message message
	 * @return json success string 
	 */
	public static final String jsonSuccess(String results, String message) {
		if (message == null) {
			message = "Success";
		}
		message = CommonUtils.toJavascriptString(message);
		return "{\"success\":true,\"message\":" + message + ",\"data\":" + results + "}";
	}

	/**
	 * JSON success message 를 보냄. data 에는 results 값을 넣음.
	 * @param writer data 를 보낼 writer
	 * @param json results 를 이 객체를 사용하여 serialize 함
	 * @param results data 에 넣을 객체
	 * @param message message field 에 넣을 string
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(Writer writer, IJSON json, Object results, String message) throws IOException {
		if (message == null) {
			message = "Success";
		}
		message = CommonUtils.toJavascriptString(message);
		writer.write("{\"success\":true,\"message\":");
		writer.write(message);
		writer.write(",\"data\":");
		json.serialize(results, writer);
		writer.write("}");
	}

	/**
	 * JSON success json object 를 보냄. data 에는 results 값을 넣음.
	 * @param response data 를 보낼 response
	 * @param results data 에 넣을 객체
	 * @param message message field 에 넣을 string
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(HttpServletResponse response, String results, String message ) throws IOException {
		jsonSuccess(response, APPLICATION_JSON, results, message);
	}
	/**
	 * JSON success json object 를 보냄. data 에는 results 값을 넣음.
	 * @param response data 를 보낼 response
	 * @param properties root JSON object 에 넣을 property 들
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess2(HttpServletResponse response, Object ... properties) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(APPLICATION_JSON);
		PrintWriter out = response.getWriter();
		out.append("{\"success\":true,");
		for (int i = 0; i < properties.length; i += 2) {
			out.append(',');
			out.append((String) properties[i]).append(':');
			Object v = properties[i + 1];
			if (v == null) {
				out.append("null");
			} else if (v instanceof String) {
				out.append(jsonStr((String) v));
			} else if (v instanceof Number) {
				out.append(v.toString());
			} else {
				out.append(jsonStr(v.toString()));
			}
		}
		out.append("}");
	}

	/**
	 * JSON success json object 를 보냄. data 에는 results 값을 넣음.
	 * @param response data 를 보낼 response
	 * @param contentType 사용할 content type
	 * @param results data 에 넣을 객체
	 * @param message message field 에 넣을 string
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(HttpServletResponse response,String contentType , String results, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);

		PrintWriter out = response.getWriter();

		out.println(jsonSuccess(results, message));
		out.flush();
	}
	/**
	 * JSON success json object 를 보냄. data 에는 results 값을 넣음.
	 * @param response data 를 보낼 response
	 * @param contentType 사용할 content type
	 * @param json data 에 넣을 때 이 json serialize 를 사용하여 serialize 함
	 * @param results data 에 넣을 객체
	 * @param message message field 에 넣을 string
	 * @throws IOException 보내면서 오류가 나면
	 */
	public static final void jsonSuccess(HttpServletResponse response,String contentType , IJSON json, Object results, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		response.setBufferSize(4096);

		PrintWriter out = response.getWriter();

		jsonSuccess(out, json, results, message);
		out.flush();
	}

	/**
	 * json fail json object 를 보냄
	 * @param response
	 * @param message
	 * @throws IOException
	 */
	public static final void jsonFailure(HttpServletResponse response, String message) throws IOException {
		jsonData(response, jsonFailure(message));
	}
	public static final void jsonFailure(HttpServletResponse response, IJSON json, String message) throws IOException {
		jsonData(response,json, jsonFailure(message));
	}
	/**
	 * json 실패 json object 를 return 함
	 * @param message 실패 message
	 * @return json 실패 json object
	 */
	public static final String jsonFailure(String message) {
		if (message == null) {
			message = "Failure";
		}
		return "{\"success\":false,\"message\":" + CommonUtils.toJavascriptString(message) + "}";
	}

	/**
	 * json failure json object 를 return 함
	 * @param ms message source (errors 에 있는 것이 MessageSourceResolvable 인 경우 사용됨)
	 * @param message 오류 메시지
	 * @param errors 오류들
	 * @return json 오류 json object
	 */
	public static final String jsonFailure(MessageSource ms, 
			String message, List<? extends Object> errors) {
		if (message == null) {
			if (errors != null && errors.size() > 0) {
				StringBuilder buf = new StringBuilder();
				for (Object error : errors) {
					if (buf.length()>0) buf.append(",");
					if (error instanceof MessageSourceResolvable) {
						String msg = ms.getMessage((MessageSourceResolvable) error, null);
						buf.append(msg);
					} else {
						buf.append(error);
					}
					break;
				}
				message = buf.toString();
			} else {
				message = "Unknown failure";
			}
		}


		return "{\"success\":false,\"message\":" + CommonUtils.toJavascriptString(message) + "}";
	}  

	/**
	 * exception 에 대한 json 오류 메시지를 response 에 write 함
	 * @param response write 할 response
	 * @param ex exception
	 * @throws IOException 보내면 오류가 나면
	 */
	public static final void jsonFailure(HttpServletResponse response, Throwable ex) throws IOException {
		jsonFailure(response, APPLICATION_JSON, ex);
	}
	/**
	 * exception 에 대한 json 오류 메시지를 response 에 write 함
	 * @param response write 할 response
	 * @param msg 오류 메시지
	 * @param ex exception
	 * @throws IOException 보내면 오류가 나면
	 */
	public static final void jsonFailure(HttpServletResponse response, Throwable ex, String msg) throws IOException{
		jsonFailure(response, APPLICATION_JSON, ex, msg);
	}
	/**
	 * exception 에 대한 json 오류 메시지를 response 에 write 함
	 * @param response write 할 response
	 * @param contentType content type
	 * @param ex exception
	 * @throws IOException 보내면 오류가 나면
	 */
	public static final void jsonFailure(HttpServletResponse response, String contentType, Throwable ex) throws IOException { 
		jsonFailure(response, contentType, ex, null);
	}

	/**
	 * exception 에 대한 json 오류 메시지를 response 에 write 함
	 * @param response write 할 response
	 * @param contentType content type
	 * @param ex exception
	 * @param msg 오류 메시지
	 * @throws IOException 보내면 오류가 나면
	 */
	public static final void jsonFailure(HttpServletResponse response, String contentType, Throwable ex, String msg) throws IOException {
		if(!CommonUtils.isValid(msg)){
			msg = jsonFailure(ex);
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		PrintWriter out = response.getWriter();
		out.println(msg);
		out.flush();
	}

	/**
	 * exception 에 대한 json 오류 메시지를 return 함
	 * @param ex exception
	 */
	public static final String jsonFailure(Throwable ex) {
		String message;
		String exceptionTypeName = "null";
		if (ex == null) {
			message = "No message";
		} else {
			if (ex instanceof InvocationTargetException) {
				ex = ((InvocationTargetException) ex).getCause();
			}

			message = ex.getMessage()==null?"\"No message\"":ex.getMessage();
			String n = ex.getClass().getName();
			exceptionTypeName = CommonUtils.toJavascriptString(n.substring(n.indexOf('.')+1));
		}
		message = CommonUtils.toJavascriptString(message);
		return "{\"success\":false,\"message\":" + message + ",\"exceptionType\":" + exceptionTypeName + "}";
	}

	/**
	 * obj의 String field들의 empty String 을 null로 바꿈.
	 * 주로 request로 넘어오는 data를 clear시키기 위해서 사용함.
	 * 이 method는 상단히 느림. Athlon 2.4Ghz 에서 method당 대략 4 microsecond 정도 걸림.
	 * request당 한번 정도로 제한 하는 것이 좋음 (주로 search query 하기 전에 사용함. null이 중요하기 때문)
	 * @param obj
	 * @param trim trim the strings (empty string check하기 전에)
	 */
	public static final void nullEmptyStringFields(Object obj, boolean trim) {
		Method[] methods = obj.getClass().getMethods();
		List<Method> toCheck = new ArrayList<Method>((methods.length-10)/2);
		Map<String,Method> setters = new HashMap<String,Method>(toCheck.size()*3/2);
		for (Method m : methods) {
			String name = m.getName();
			if (name.length() >= 4 && Character.isUpperCase(name.charAt(3))) {
				if (name.startsWith("get") && m.getParameterTypes().length==0 && String.class.equals(m.getReturnType())) {
					toCheck.add(m);
				} else if (name.startsWith("set") && m.getParameterTypes().length==1 &&
						String.class.equals(m.getParameterTypes()[0])) {
					setters.put(name.substring(3), m);
				}
			}
		}
		for (Method m : toCheck) {
			try {
				String retValue = (String) m.invoke(obj);
				if (retValue == null) continue;
				if (trim && retValue.length() > 0) retValue = retValue.trim();
				if (retValue != null && retValue.length() == 0) {
					Method setter = setters.get(m.getName().substring(3));
					if (setter != null) {
						setter.invoke(obj, (String)null);
					}
				}
			} catch (Exception e) {
				log.warn("While checking : " + m + " : " + e.getMessage(), e);
			}
		}
	}

	/**
	 * message source (ms) 에서 key 에 해당되는 message 를 return 함 (할 때 args 를 사용하여 evaluation 함)
	 * @param key 구하고 싶은 message source 에 있는 message key
	 * @param defaultMessage 만약 message source 에 key 에 대한 message 가 없다면 defaultMessage 를 사용함
	 * @param ms message source
	 * @param args message 를 evaluation 할 때 사용할 argument 들
	 * @param locale 어떤 locale 로 message source 에서 message 를 찾는가
	 * @return evaluation 된 message
	 */
	public static String message(String key, String defaultMessage, MessageSource ms, Object []args, Locale locale) {
		String msg = evaluateMessage(key, defaultMessage, ms, args, locale, new HashMap<String, String>());
		return msg;
	}
	private static Pattern pattern = Pattern.compile("\\{(.+?)\\}");
	private static final String evaluateMessage(String msg, String defaultMessage, MessageSource ms, Object []args, Locale locale, Map<String,String> msgKeys) {
		if (msg == null) return msg;
		if (msg.indexOf('{')==-1) {
			return ms.getMessage(msg, args, defaultMessage, locale);
		}
		StringBuffer out = new StringBuffer(256);
		Matcher m = pattern.matcher(msg);
		while (m.find()) {
			String key = m.group(1);
			String value = msgKeys.get(key);
			if (value == null) {
				if (ms != null) {
					value = ms.getMessage(key, null, defaultMessage, locale);
					msgKeys.put(key, value); // to prevent infinite loop
					value = evaluateMessage(value, defaultMessage, ms, args, locale, msgKeys);
					msgKeys.put(key, value);
				} else value = key;
			}
			if (value != null) m.appendReplacement(out, StringUtils.replace(value, "$", "\\$"));  
		}
		m.appendTail(out);
		return out.toString();
	}

	/**
	 * context path relative path 를 absolute path (scheme 포함하여) 로 변경 하여 return 함
	 * @param request 어떤 request 에 대해서 (여기에서 context path, scheme 등을 구함)
	 * @param path 변경 할 context path relative path
	 * @return absolute URL
	 */
	public static final String encodeRedirectURL(HttpServletRequest request, String path) {
		return encodeRedirectURL(request, path, false);
	}

	/**
	 * context path relative path 를 absolute path (scheme 포함하여) 로 변경 하여 return 함
	 * @param request 어떤 request 에 대해서 (여기에서 context path, scheme 등을 구함)
	 * @param path 변경 할 context path relative path
	 * @param forceHTTP request 에 있는 scheme 무시하고 무조건 http 를 사용하게
	 * @return absolute URL
	 */
	public static final String encodeRedirectURL(HttpServletRequest request, String path, boolean forceHTTP) {
		String scheme = forceHTTP?"http":request.getScheme();
		if (request.getServerPort() == 80) {
			return scheme + "://" + request.getServerName() + request.getContextPath() + path;
		}
		return scheme + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + path;
	}

	private static final long START_TIMESTAMP = System.currentTimeMillis();

	/**
	 * Cache 된 것 check 함 (즉 cache flag 를 browser 로 보냄).
	 * 이 method 는 해당 resourc 가 서버가 재시작하기 전까지 안바뀌는 것들에 한해 사용 할 수 있음.
	 * 즉 checkCached(request, response, *서버시작시간*) 과 동일함.
	 * @param request
	 * @param response
	 * @return 만약 false 이면 data 를 보내야함. true 이면 cache 를 사용하게 해도 된다는 의미
	 */
	public static boolean checkCached(HttpServletRequest request, HttpServletResponse response) {
		return checkCached(request, response, START_TIMESTAMP);
	}
	/**
	 * Cache 된 것 check 함 (즉 cache flag 를 browser 로 보냄).
	 * 이 method 는 해당 resourc 가 서버가 재시작하기 전까지 안바뀌는 것들에 한해 사용 할 수 있음.
	 * 즉 checkCached(request, response, *서버시작시간*) 과 동일함.
	 * @param request
	 * @param response
	 * @param 마지막 수정된 timestamp (1970.1.1 이후 경과된 millisecond)
	 * @return 만약 false 이면 data 를 보내야함. true 이면 cache 를 사용하게 해도 된다는 의미
	 */
	public static boolean checkCached(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		if (lastModified <= 0) return false;
		long ifModifiedSince = -1;
		try {
			ifModifiedSince = ResourceUtils.parseRfc2616Date(request.getHeader(IF_MODIFIED_SINCE));
		} catch (IllegalArgumentException ex) {
		}
		if (ifModifiedSince > -1 && lastModified > 0 && lastModified <= (ifModifiedSince + 999)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		}
		if (lastModified > 0) response.setDateHeader(LAST_MODIFIED, lastModified);
		return false;
	}

	public static String NVL(String str){
		return str==null?"":str;
	}
}