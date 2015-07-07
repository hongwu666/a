
import java.beans.IntrospectionException;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;

/**
 * ���л�����ΪJSON��ʽ ��ѭJSON��֯������׼
 */
public class JsonUtil {

	/**
	 * @param obj
	 *            �������
	 * @return String
	 */

	private static Gson gson = new Gson();

	private static String object2Json(Object obj) {

		String json = gson.toJson(obj);

		return json;
	}

	/**
	 * @param bean
	 *            bean����
	 * @return String
	 */
	private static String bean2Json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2Json(props[i].getName());
					String value = object2Json(props[i].getReadMethod().invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list����
	 * @return String
	 */
	private static String list2Json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param array
	 *            ��������
	 * @return String
	 */
	private static String array2Json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map
	 *            map����
	 * @return String
	 */
	public static String map2Json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2Json(key));
				json.append(":");
				json.append(object2Json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set
	 *            ���϶���
	 * @return String
	 */
	private static String set2Json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s
	 *            ����
	 * @return String
	 */
	private static String string2Json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * ת����json
	 */
	public static String toJSON(Object toJsonObject) {
		if (toJsonObject == null) {
			return "[]";
		}
		String json = object2Json(toJsonObject);
		return json;
	}

	/**
	 * ��json�ַ�ת����list,��ҪGson��֧��
	 * 
	 * @param <T>
	 * @param json
	 * @param object
	 * @return
	 */
	public static <T> T json2List(String json, Type typeOfT) {
		return (T) gson.fromJson(json, typeOfT);
	}

	/**
	 * ��json�ַ�ת����java����,��ҪGson��֧�� ʾ��:PreBook
	 * preBook=JsonUtil.json2Bean(str,PreBook.class);
	 * 
	 * @param <T>
	 * @param json
	 * @param classOfT
	 * @return
	 * 
	 */
	public static <T> T json2Bean(String json, Class<T> classOfT) {

		return gson.fromJson(json, classOfT);
	}

	public static Map<String, String> bean2Map(Object bean) {

		Map<String, String> map = new HashMap<String, String>();

		if (bean == null) {
			return map;
		}
		String json = gson.toJson(bean);
		map = gson.fromJson(json, Map.class);
		return map;
	}

	public static void main(String[] args) {
		Object obj = null;

		String json = gson.toJson(obj);
		System.out.println(json);
		Map<String, String> map = new HashMap<String, String>();
		System.out.println(map.size());
		System.out.println(json.equals("null"));
		System.out.println(map2Json(map));
	}

	public static Map<String, String> json2Map(String json) {

		Map<String, String> map = gson.fromJson(json, Map.class);
		return map;
	}

	public static <T> T map2Bean(Map<?, ?> map, Class<T> classOfT) {

		String jsonStr = map2Json(map);
		return (T) gson.fromJson(jsonStr, classOfT);
	}

	private static enum ParseState {
		NORMAL, ESCAPE, UNICODE_ESCAPE
	}

	/**
	 * ��unicode ת��utf8
	 * 
	 * @param s
	 * @return
	 */
	public static String convertUnicodeEscape(String s) {
		if (null == s || "".equals(s)) {
			return null;
		}
		char[] out = new char[s.length()];

		ParseState state = ParseState.NORMAL;
		int j = 0, k = 0, unicode = 0;
		char c = ' ';
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (state == ParseState.ESCAPE) {
				if (c == 'u') {
					state = ParseState.UNICODE_ESCAPE;
					unicode = 0;
				} else { // we don't care about other escapes
					out[j++] = '\\';
					out[j++] = c;
					state = ParseState.NORMAL;
				}
			} else if (state == ParseState.UNICODE_ESCAPE) {
				if ((c >= '0') && (c <= '9')) {
					unicode = (unicode << 4) + c - '0';
				} else if ((c >= 'a') && (c <= 'f')) {
					unicode = (unicode << 4) + 10 + c - 'a';
				} else if ((c >= 'A') && (c <= 'F')) {
					unicode = (unicode << 4) + 10 + c - 'A';
				} else {
					throw new IllegalArgumentException("Malformed unicode escape");
				}
				k++;

				if (k == 4) {
					out[j++] = (char) unicode;
					k = 0;
					state = ParseState.NORMAL;
				}
			} else if (c == '\\') {
				state = ParseState.ESCAPE;
			} else {
				out[j++] = c;
			}
		}

		if (state == ParseState.ESCAPE) {
			out[j++] = c;
		}

		return new String(out, 0, j);
	}

	/**
	 * ����תΪָ����ʱ��
	 * 
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static String date2Str(Date d, String pattern) {
		if (null == d) {
			return null;
		}
		DateFormat format = new SimpleDateFormat(pattern);
		String str = format.format(d);
		return str;
	}

	public static String delHTMLTag(String str) {
		String regEx_html = "<[^>]+>";
		Pattern p_style = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(str);
		str = m_style.replaceAll("");
		return str;
	}

	/**
	 * ����url����
	 * 
	 * by Table
	 * 
	 * 2011-08-17 15:11:45
	 * 
	 * @param url
	 * @return
	 */
	public static String sendGetRequest(String url) {

		HttpURLConnection conn = null;
		StringBuffer sb = new StringBuffer();

		String msg = "";

		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.connect();

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String temp = "";
				while (null != (temp = br.readLine())) {
					sb.append(temp);
				}
				br.close();
			}
			msg = sb.toString();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}

		return msg;
	}

	/**
	 * ����url����(POST)
	 * 
	 * by Table
	 * 
	 * 2011-08-17 15:11:45
	 * 
	 * @param url
	 * @return
	 */
	public static String sendPostRequest(String url, String Parameter) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		String msg = "";
		// Post�����url����get��ͬ���ǲ���Ҫ�����
		URL postUrl;
		try {
			postUrl = new URL(url);
			// ������
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());

			out.writeBytes(Parameter);
			out.flush();
			out.close();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				String temp = "";
				while (null != (temp = br.readLine())) {
					sb.append(temp);
				}
				br.close();
				msg = sb.toString();
				System.out.println("�ӿڷ���:" + msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return msg;
	}

}
