
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Map;





public class MaizuoLogUtil {
	/**
	 * дlog <br/>
	 * linuxĬ�ϵ�ַ : /data/logs/maizuo.log  <br/> 
	 * windowsĬ�ϵ�ַ��F:/data/logs/maizuo.log <br/>
	 * 
	 *  1	֧������	ͳһ֧��ϵͳ <br/>
	 * 	2	��������	ͳһ����ϵͳ <br/>
	 *	3	��������	ͳһ����ϵͳ <br/>
	 *	4	����Ʊ����	ͳһ����Ʊ����ϵͳ <br/>
	 *	5	��Ӱ������	ͳһ��Ӱ��ϵͳ <br/>
	 *	6	�ֻ���תserver	�����ֻ���ҵ��ӿ� <br/>
	 *	7	�����Ż�ϵͳ	���������ŻݵĽӿ� <br/>
	 *	8	��Ʒϵͳ	��Ʒϵͳ <br/>
	 *	9	��������	���ŵ��·� <br/>
	 *	10	ǰ��web	������ǰ����־ <br/>
	 *	11	�û�server	�û���Ϣserver<br/>
	 *	12	��������̨	��������̨<br/>
	 *	13	�ֽ�ϵͳ	�ֽ�ϵͳ<br/>
	 *	14	Feed server	Feed server<br/>
	 *  15     ��������
	 *
	 * @param fromtype �����豸����
	 * @param totype �������豸����
	 * @param param ҵ����ò���
	 * @param interfaceUri ҵ����ýӿ�����
	 * @param result ҵ�񷵻ؽ��
	 * @param other ҵ��������Ϣ
	 */
	public static void writeLog(String fromtype,String totype,String param,
			String interfaceUri,String result,String other,long processTime,int alarmId){
		
		/**
		 * ��ʽ
		 * {"@timestamp":"2012-10-31T15:52:54+08:00",
		 * 	"@source":"192.168.1.203",
		 *  "@fields":{
		 *  		"fromtype":1,
		 *  		"totype":1,
		 *  		"param":"userid=12&cinemaId=152",
		 *  		"interface":"http://www.maizuo.com/pay.htm",
		 *  		"result":"ok",
		 *  		"other":"www.maizuo.com"
		 *  }}
		 */
		
		if(param != null){
			param = param.replaceAll("\"", "\'");	
			param = param.replaceAll("\n", " ");
		}
		if(interfaceUri != null){
			interfaceUri = interfaceUri.replaceAll("\"", "\'");
			interfaceUri = interfaceUri.replaceAll("\n", " ");
		}
		if(result != null){
			result = result.replaceAll("\"", "\'");	
			result = result.replaceAll("\n", " ");
		}
		if(other != null){
			other = other.replaceAll("\"", "\'");	
			other = other.replaceAll("\n", " ");
		}
		
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","+
						 "\"@source\":\"" + getRealIp() + "\","+
						 "\"@fields\":{"+
						 "\"fromtype\":\"" + fromtype + "\","+
						 "\"totype\":\"" + totype + "\","+
						 "\"param\":\"" + param + "\","+
						 "\"interface\":\"" + interfaceUri + "\","+
						 "\"result\":\"" + result + "\","+
						 "\"processTime\":\"" + processTime + "\","+
						 "\"alarmId\":\"" + alarmId + "\","+
						 "\"other\":\"" + other + "\"" + "}}\n";
		writeFile(jsonLog);
		
		
	}
	
	public static void writeLog(String fromtype,String totype,String param,
			String interfaceUri,String result,String other,String alarmID){
		
		/**
		 * ��ʽ
		 * {"@timestamp":"2012-10-31T15:52:54+08:00",
		 * 	"@source":"192.168.1.203",
		 *  "@fields":{
		 *  		"fromtype":1,
		 *  		"totype":1,
		 *  		"param":"userid=12&cinemaId=152",
		 *  		"interface":"http://www.maizuo.com/pay.htm",
		 *  		"result":"ok",
		 *  		"other":"www.maizuo.com"
		 *  }}
		 */
		
		if(param != null){
			param = param.replaceAll("\"", "\'");	
			param = param.replaceAll("\n", " ");
		}
		if(interfaceUri != null){
			interfaceUri = interfaceUri.replaceAll("\"", "\'");
			interfaceUri = interfaceUri.replaceAll("\n", " ");
		}
		if(result != null){
			result = result.replaceAll("\"", "\'");	
			result = result.replaceAll("\n", " ");
		}
		if(other != null){
			other = other.replaceAll("\"", "\'");	
			other = other.replaceAll("\n", " ");
		}
		
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","+
						 "\"@source\":\"" + getRealIp() + "\","+
						 "\"@fields\":{"+
						 "\"fromtype\":\"" + fromtype + "\","+
						 "\"totype\":\"" + totype + "\","+
						 "\"param\":\"" + param + "\","+
						 "\"interface\":\"" + interfaceUri + "\","+
						 "\"result\":\"" + result + "\","+
						 "\"alarmID\":\"" + alarmID + "\","+
						 "\"other\":\"" + other + "\"" + "}}\n";
		writeFile(jsonLog);
		
		
	}
	
	private static void writeFile(String jsonLog){
		
		String fs = System.getProperties().getProperty("file.separator");
		if (fs.equals("/")) {
			
			FileWriter fw = null;
			try {
				File file_linux = new File("/data/logs/");
				if (!file_linux.exists()) {
					file_linux.mkdirs();
				}
				File filelogs_linux = new File("/data/logs/maizuo.log");
				if (!filelogs_linux.exists()) {
					filelogs_linux.createNewFile();
				}

				fw = new FileWriter(filelogs_linux, true);
				fw.write(jsonLog);
				fw.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			// windows

			FileWriter fw = null;
			try {
				File file_wp = new File("F:/data/");
				if (!file_wp.exists()) {
					boolean b = file_wp.mkdirs();
					if (b == false)
						return;
				}
				file_wp = new File("F:/data/logs/");
				if (!file_wp.exists()) {
					file_wp.mkdirs();
				}
				File filelogs_linux = new File("F:/data/logs/maizuo.log");
				if (!filelogs_linux.exists()) {
					filelogs_linux.createNewFile();
				}

				fw = new FileWriter(filelogs_linux, true);
				fw.write(jsonLog);
				fw.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
	
	public static void writeLog(Map<String, Object> map) {
	
		//String mapJson = com.hyx.tool.JsonConver.mapConverJson(map);
		String mapJson = JsonUtil.map2Json(map);
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":"+ mapJson+"}\n";
		
		writeFile(jsonLog);
		
	}
	
	/**
	 * ��ȡ��ǰʱ��
	 * @return
	 */
	public static String getCurTime(){
		Calendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return  format.format(date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'");
		return  format.format(date);
	}
	
	public static String getClientIp(){
		
		return null;
	}
	
	/**
	 * ��ȡ����ip
	 * @return 
	 */
	public static String getRealIp(){
		String localip = null;// ����IP�����û����������IP�򷵻���
		String netip = null;// ����IP

		try {
			Enumeration<NetworkInterface> netInterfaces = 
				NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// �Ƿ��ҵ�����IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() 
							&& !ip.isLoopbackAddress() 
							&& ip.getHostAddress().indexOf(":") == -1) {// ����IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() 
							&& !ip.isLoopbackAddress() 
							&& ip.getHostAddress().indexOf(":") == -1) {// ����IP
						localip = ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}
	
	
public static void main(String[] args) {
		
		//���÷���
		//MaizuoLogUtil.writeLog("1","2", "userId=1&cinemaId=2 \n kfjdlsf ", "http://www.maizuo.com/pay.htm", "{\"goodsId\":2}", null);
		
//		getCurTime();
		
	}
}
