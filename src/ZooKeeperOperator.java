
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.apache.zookeeper.ZooKeeper;

import com.hyx.zookeeper.EquNode;

public class ZooKeeperOperator implements Watcher {

	private static final Map<String, Object> nodeList = new HashMap<String, Object>();// �������ڵ��б�

	private DataClientListener listener = new DataClientListenerImpl();

	private static ZooKeeper zooKeeper;// zookeeper���Ӷ���

	private static ZooKeeper copyZooKeeper;// �������Ӷ���

	private String serverPath;// ָ��������Ŀ¼
	private final static String rootPath = "/config";

	private String common;// ָ��������Ŀ¼

	private String hosts;// zookeeper��������ַ�ַ�

	private int SESSION_TIME = 30000;// ��ʱʱ��

	private String digest;// ������Ȩ���

	private int reconnectTimes;// ��������

	private static ZooKeeperOperator zooKeeperOperator;

	private CountDownLatch connectedLatch = new CountDownLatch(1);

	public static ZooKeeperOperator getInstance() {
		if (zooKeeperOperator == null) {

			zooKeeperOperator = new ZooKeeperOperator();
		}
		return zooKeeperOperator;
	}

	/* ����zookeeper��������ͬʱ��ȡ��Ӧ�Ľڵ��б� */
	private void connect() {
		try {
			if (zooKeeper == null) {
				zooKeeper = new ZooKeeper(this.hosts, this.SESSION_TIME, this);

				try {
					connectedLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// zooKeeper = copyZooKeeper;
		} catch (IOException e) {
			System.out.println("zookeeper client connection error..................");
			MaizuoLogUtil.writeLog("52", "52", "", "connect/connectLoss", "zookeeper����ʧ��", "", 0, 1);
			// reconnect();
			return;
		}
		zooKeeper.addAuthInfo("digest", this.digest.getBytes());
		System.out.println("zookeeper client connection succ..................");
		reconnectTimes = 0;
		//
		// if(rootPath!=null && !rootPath.equals("/")){
		// if(common!=null && !rootPath.equals("/")){
		// try {
		// listener.getAllNodes(common, this, nodeList);
		// }catch (KeeperException e) {
		// MaizuoLogUtil.writeLog("52", "52", "", "connect/readCommonPathError",
		// "��ȡ����������Ϣʧ��", "", 0, 1);
		// reconnect();
		// return;
		// }catch (InterruptedException e) {
		// MaizuoLogUtil.writeLog("52", "52", "",
		// "connect/readCommonPathError/connectLoss", "��������Ͽ�����", "", 0,
		// 1);
		// reconnect();
		// return;
		// }
		// }
		// try {
		// listener.getAllNodes(rootPath, this, nodeList);
		// }catch (KeeperException e) {
		// MaizuoLogUtil.writeLog("52", "52", "", "connect/readServerPathError",
		// "��ȡϵͳ������Ϣʧ��", "", 0, 1);
		// reconnect();
		// return;
		// }catch (InterruptedException e) {
		// MaizuoLogUtil.writeLog("52", "52", "",
		// "connect/readServerPathError/connectLoss", "��������Ͽ�����", "", 0,
		// 1);
		// reconnect();
		// return;
		// }
		// reconnectTimes = 0;
		// }else if(rootPath!=null&&rootPath.equals("/")){
		// try {
		// listener.getRootNodes(rootPath, this);
		// } catch (KeeperException e) {
		// MaizuoLogUtil.writeLog("52", "52", "",
		// "connect/getRootNodes/readAllNodes", "��ȡ������������Ϣʧ��", "", 0,
		// 1);
		// reconnect();
		// return;
		// } catch (InterruptedException e) {
		// MaizuoLogUtil.writeLog("52", "52", "",
		// "connect/getRootNodes/connectLoss", "��������Ͽ�����", "", 0, 1);
		// reconnect();
		// return;
		// }
		// reconnectTimes = 0;
		// }
	}

	public void close() {
		try {
			zooKeeper.close();
			zooKeeper = null;
		} catch (InterruptedException e) {
			System.out.println("connection have closed");
		}
	}

	public void reconnect() {
		if (!judgeConnection()) {
			reconnectTimes++;
			if (reconnectTimes <= 3) {
				connect();
			} else {
				MaizuoLogUtil.writeLog("52", "52", "", "reconnect_overtime", "�����Ѵ�����,����ʧ�ܣ����ܷ������ѹ���", "", 0, 1);
				close();
				reconnectTimes = 0;
			}
		} else {
			MaizuoLogUtil.writeLog("52", "52", "", "reconnect_Innerexception", "�������������,�ڲ��߼�����", "", 0, 1);
		}
	}

	/* ����ZooKeeperOperator����ͬʱ��ʼ�����Ӽ���ȡ�ڵ㵽����nodeList */
	public ZooKeeperOperator() {

		// if(!StringUtils.isBlank(serverPath)){
		// serverPath = "/"+serverPath;
		// }
		// if(common!=null&&!common.startsWith("/")){
		// common = "/"+common;
		// }
		// if(rootPath==null){
		// MaizuoLogUtil.writeLog("52", "52", "",
		// "ZooKeeperOperator/wrongConnectParams", "��������Ӳ���", "", 0, 1);
		// return;
		// }
		// this.serverPath = serverPath;
		// this.common = common;
		System.out.println("init........................");
		initConfig();
		this.connect();
	}

	private void initConfig() {
		ServerConfig sc = null;
		try {
			sc = ServerConfig.getInstance();
		} catch (ConfigurationException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "initConfig", "serverConfig.properties", "", 0, 1);
		}
		this.hosts = sc.config.getString("hosts");
		this.SESSION_TIME = sc.config.getInt("session_time");
		this.digest = sc.config.getString("digest");
	}

	public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}

	public static Map<String, Object> getNodelist() {
		return nodeList;
	}

	public DataClientListener getListener() {
		return listener;
	}

	public void setListener(DataClientListener listener) {
		this.listener = listener;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public void setNodeWatcher(String path) {
		try {
			Stat stat = zooKeeper.exists(path, true);
			if (stat != null) {
				zooKeeper.getChildren(path, true);
			}
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "setNodeWatcher", "����������" + path + "�����쳣", "", 0, 1);
			reconnect();
			return;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "setNodeWatcher/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return;
		}
	}

	public String setChildWatcher(String path) {
		String childNode = null;
		try {
			childNode = listener.getCreateNode(path, this, rootPath);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "setChildWatcher/getCreateNode", "��ȡ�½�������Ϣʧ��", "", 0, 1);
			reconnect();
			return null;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "setChildWatcher/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return null;
		}
		return childNode;
	}

	public boolean judgeConnection() {
		if (zooKeeper.getState().isAlive() && zooKeeper.getState().isConnected()) {
			return true;
		}
		return false;
	}

	private void createDefaultNode() {
		String nodeName = "/config";
		try {
			Stat stat = zooKeeper.exists(nodeName, false);
			if (stat == null) {
				this.create("", "/config", "��������");
			} else {
				System.out.println("/config is exists!!!!!!!!!!!");
			}

			nodeName = "/config/common";
			stat = zooKeeper.exists(nodeName, false);
			if (stat == null) {
				this.create("common", "/config", "��������·��");
			}

		} catch (KeeperException e) {
			String msg = e.getMessage();
			System.out.println("exception msg===========" + msg);
			MaizuoLogUtil.writeLog("52", "52", "", "maizuoConfig", msg, "", 0, 1);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void process(WatchedEvent event) {

		System.out.println("event.getState============" + event.getState());

		if (event.getState() == KeeperState.SyncConnected) {
			connectedLatch.countDown();
			System.out.println("client connection succ---------");
			this.createDefaultNode();
			// String nodeName = "/config/common";
			// try {
			// Stat stat = zooKeeper.exists(nodeName, false);
			// if(stat == null){
			// System.out.println("create common path.....");
			// this.create("/common", "/config", "��������·��");
			// }else{
			// System.out.println("/config/common is exists!!");
			// }
			// } catch (KeeperException e) {
			// e.printStackTrace();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// try {
			// List<String> data = zooKeeper.getChildren("/", null);
			// for(String str : data){
			// System.out.println("data ========"+str);
			// }
			// } catch (KeeperException e) {
			// e.printStackTrace();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			//
		}

		if (event.getState() == KeeperState.Expired) {
			System.out.println("client timeout............");
		}
		String nodeName = null;
		if (event.getType() == Event.EventType.NodeDataChanged) {
			nodeName = getNodeName(event.getPath());

			System.out.println(nodeName + " is changed.......");
			System.out.println("��Ҫִ���޸ģ���������Ӷ���� update����");
			setNodeWatcher(event.getPath());// ��������������¼�
			listener.updateNode(nodeName, getData(event.getPath()), nodeList, event.getPath(), this.rootPath, this.common);// ���ڿͻ���ά���޸ı�������б?��
		}
		if (event.getType() == Event.EventType.NodeDeleted) {
			// event.
			nodeName = getNodeName(event.getPath());
			System.out.println(nodeName + " is deleted.......");
			System.out.println("��Ҫִ��ɾ����������Ӷ����remove����");
			listener.removeNode(nodeName, nodeList, event.getPath(), this.rootPath, this.common);// ���ڿͻ���ά��ɾ�������б���ݷ���
		}

		if (event.getType() == Event.EventType.NodeChildrenChanged) {
			String childPath = setChildWatcher(event.getPath());// ��������������¼�
			if (childPath != null) {
				System.out.println(childPath + " is created.......");
				System.out.println("��Ҫ����������������Ӷ���� create����");
				String path = "/" + childPath;
				if (!event.getPath().equals("/")) {
					path = event.getPath() + path;
				}
				listener.creatNode(childPath, getData(path), nodeList, path, this.rootPath, this.common);// ���ڿͻ���ά�����?������б���ݷ���
			}
		}
		/*
		 * if(event.getState()==Event.KeeperState.Disconnected){
		 * System.out.println("the server is closed,will reconnect");
		 * reconnect(); }
		 */
	}

	private String getNodeName(String path) {
		if (path.equals("/")) {
			return "/";
		}
		String[] as = path.split("/");
		return as[as.length - 1];
	}

	private String getData(String str) {
		Stat stat = null;
		try {
			stat = zooKeeper.exists(str, false);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/exists", "������" + str + "��Ϣ������", "", 0, 1);
			reconnect();
			return null;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return null;
		}
		if (stat == null) {
			return null;
		}
		try {
			byte[] val = zooKeeper.getData(str, true, stat);
			if (val == null)
				return null;
			return new String(val);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/getData", "��ȡ������" + str + "����ʧ��", "", 0, 1);
			reconnect();
			return null;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/last/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return null;
		}
	}

	public String getData(String serverName, String parentName) {
		if (serverName == null || serverName.equals("")) {
			MaizuoLogUtil.writeLog("52", "52", "", "create/params", "serverName won't be null or ''", "", 0, 1);
			return null;
		}
		String path = null;
		if (parentName == null || parentName.equals("")) {
			path = "/" + serverName;
		} else {
			path = "/" + parentName + "/" + serverName;
		}
		Stat stat = null;
		try {
			stat = zooKeeper.exists(path, false);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/exists", "������" + serverName + "��Ϣ������", "", 0, 1);
			reconnect();
			return null;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return null;
		}
		if (stat == null) {
			return null;
		}
		try {
			return new String(zooKeeper.getData(path, true, stat));
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/getData", "��ȡ������" + serverName + "����ʧ��", "", 0, 1);
			reconnect();
			return null;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "getData/last/connectLoss", "��������Ͽ�����", "", 0, 1);
			reconnect();
			return null;
		}
	}

	public boolean create(String nodeName, String parentName, String data) {

		// if(nodeName==null||nodeName.equals("")){
		// MaizuoLogUtil.writeLog("52", "52", "", "create/params",
		// "serverName won't be null or ''", "", 0, 1);
		// return false;
		// }
		List<ACL> acls = new ArrayList<ACL>(1);
		Id id = null;
		try {

			// ��ӹ���ԱȨ��
			id = new Id("digest", DigestAuthenticationProvider.generateDigest(this.digest));
			ACL acl1 = new ACL(ZooDefs.Perms.ALL, id);
			acls.add(acl1);
		} catch (NoSuchAlgorithmException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "create/ACL", "ʵ�����Ա�û�Ȩ��ʧ��", "", 0, 1);
			return false;
		}

		String path = null;
		if (parentName == null || parentName.equals("")) {
			parentName = rootPath;
		} else {
			if (parentName.charAt(0) != '/')
				parentName = "/" + parentName;

		}
		if (!StringUtils.isBlank(nodeName)) {
			if (nodeName.charAt(0) != '/')
				nodeName = "/" + nodeName;
		}

		path = parentName + nodeName;
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		System.out.println("create node :" + path);
		try {
			// Stat stat = zooKeeper.exists(path,false);
			// if(stat != null) return

			if (data == null) {
				data = "";
			}
			byte[] da = null;
			if (data != null)
				da = data.getBytes("utf-8");
			zooKeeper.create(path, da, acls, CreateMode.PERSISTENT);
			// zooKeeper.create(path,da, null, CreateMode.PERSISTENT);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "create/create", "���������" + nodeName + "ʧ��", "", 0, 1);
			e.printStackTrace();
			// reconnect();
			return false;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "create/connectionLoss", "������������ж�", "", 0, 1);
			reconnect();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean remove(String serverName, String parentName) {
		if (serverName == null || serverName.equals("")) {
			MaizuoLogUtil.writeLog("52", "52", "", "remove/params", "serverName won't be null or ''", "", 0, 1);
			return false;
		}
		Stat stat = null;
		String path = null;
		if (parentName == null || parentName.equals("")) {
			path = "/" + serverName;
		} else {
			path = "/" + parentName + "/" + serverName;
		}
		try {
			// ����Ƿ���ڱ��ڵ�
			stat = zooKeeper.exists(path, true);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "remove/exists", "��·�����������", "", 0, 1);
			reconnect();
			return false;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "remove/connectionLoss", "������������ж�", "", 0, 1);
			reconnect();
			return false;
		}
		if (stat != null) {
			try {
				zooKeeper.delete(path, -1);
			} catch (Exception e) {
				if (judgeConnection()) {
					MaizuoLogUtil.writeLog("52", "52", "", "remove/delete", "ɾ��������" + serverName + "ʧ��", "", 0, 1);
				} else {
					MaizuoLogUtil.writeLog("52", "52", "", "delete/connectionLoss", "������������ж�", "", 0, 1);
				}
				reconnect();
				return false;
			}
		}
		return true;
	}

	public boolean update(String serverName, String parentName, Object data) {
		if (serverName == null || serverName.equals("")) {
			MaizuoLogUtil.writeLog("52", "52", "", "update/params", "serverName won't be null or ''", "", 0, 1);
			return false;
		}
		Stat stat = null;
		String path = null;
		if (parentName == null || parentName.equals("")) {
			path = "/" + serverName;
		} else {
			path = "/" + parentName + "/" + serverName;
		}
		try {
			// ����Ƿ���ڱ��ڵ�
			stat = zooKeeper.exists(path, true);
		} catch (KeeperException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "update/exists", "�����������", "", 0, 1);
			reconnect();
			return false;
		} catch (InterruptedException e) {
			MaizuoLogUtil.writeLog("52", "52", "", "update/connectionLoss", "������������ж�", "", 0, 1);
			reconnect();
			return false;
		}
		if (stat != null) {
			try {
				if (data == null) {
					data = "";
				}
				byte[] da = null;
				if (data instanceof String) {
					da = ((String) data).getBytes();
				}
				zooKeeper.setData(path, da, -1);
			} catch (Exception e) {
				if (judgeConnection()) {
					MaizuoLogUtil.writeLog("52", "52", "", "update/setData", "����������" + serverName + "��ݴ���", "", 0, 1);
				} else {
					MaizuoLogUtil.writeLog("52", "52", "", "setData/connectionLoss", "������������ж�", "", 0, 1);
				}
				reconnect();
				return false;
			}
		}
		return true;
	}

	public List getChildConfigs(String path) {
		List<String> data = null;
		List<EquNode> m = new ArrayList<EquNode>();
		if (StringUtils.isBlank(path)) {
			return null;
		} else {
			if ("/".equals(path))
				path = rootPath;
			else if (path.charAt(0) != '/')
				path = "/" + path;
		}
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		EquNode e;
		String[] parents = path.split("/");
		try {
			data = zooKeeper.getChildren(path, null);

			if (data != null && data.size() > 0) {
				if (path.equals("/")) {
					path = "";
				}
				for (String s : data) {

					if (!"common".equals(s)) {// ȥ������
						String val = this.getData(path + "/" + s);

						e = new EquNode(s, val, path + "/" + s, parents[parents.length - 1]);

						m.add(e);
					}
				}
			}
		} catch (KeeperException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// System.out.println(data.size());

		return m;
	}

	@SuppressWarnings("rawtypes")
	public List getChildConfigs(String serverName, String parentName) {

		// if(zooKeeper.getState())
		// if(serverName==null||serverName.equals("")){
		// MaizuoLogUtil.writeLog("52", "52", "", "getchildConfigs/params",
		// "serverName won't be null or ''", "", 0, 1);
		// return null;
		// }
		List<String> data = null;
		List<EquNode> m = new ArrayList<EquNode>();
		String path = null;

		if (StringUtils.isBlank(parentName)) {
			parentName = rootPath;
		} else {
			if (parentName.charAt(0) != '/')
				parentName = "/" + parentName;
		}

		if (StringUtils.isBlank(serverName)) {
			path = parentName;
		} else {
			if (serverName.charAt(0) != '/')
				serverName = "/" + serverName;
			path = parentName + serverName;
		}
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		// System.out.println("========path=========="+path);
		try {
			data = zooKeeper.getChildren(path, null);
			if (data != null && data.size() > 0) {
				if (path.equals("/")) {
					path = "";
				}
				for (String s : data) {
					String val = this.getData(path + "/" + s);
					// String parentValue = new
					// String(zooKeeper.getData(path+"/"+s, null, new Stat()));
					// System.out.println("parentValue===="+val);
					EquNode e = null;
					e = new EquNode(s, val, path + "/" + s, serverName);
					m.add(e);
				}
			}
		} catch (KeeperException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// System.out.println(data.size());

		return m;
	}

	public boolean deleteRootConfig(String serverName, String parentName) {
		if (serverName == null || serverName.equals("")) {
			MaizuoLogUtil.writeLog("52", "52", "", "deleteRootConfig/params", "serverName won't be null or ''", "", 0, 1);
			return false;
		}
		String path = null;
		if (parentName == null || parentName.equals("")) {
			path = "/" + serverName;
		} else {
			path = "/" + parentName + "/" + serverName;
		}
		try {
			List<String> data = null;
			data = zooKeeper.getChildren(path, null);
			if (data != null && data.size() > 0) {
				for (String s : data) {
					zooKeeper.delete(path + "/" + s, -1);
				}
			}
			zooKeeper.delete(path, -1);
		} catch (Exception e) {
			if (judgeConnection()) {
				MaizuoLogUtil.writeLog("52", "52", "", "deleteRootConfig", "ɾ����������" + path + "ʧ��", "", 0, 1);
			} else {
				MaizuoLogUtil.writeLog("52", "52", "", "deleteRootConfig/connectionLoss", "������������ж�", "", 0, 1);
			}
			reconnect();
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public List getConfigsByName(String serverName, String parentName) {
		if (parentName == null || parentName.equals("")) {
			MaizuoLogUtil.writeLog("52", "52", "", "getConfigsByName/params", "parentName won't be null or ''", "", 0, 1);
			return null;
		}
		List<EquNode> m = new ArrayList<EquNode>();
		try {
			List<String> data = null;
			String path = null;
			path = parentName;
			data = zooKeeper.getChildren(path, null);

			if (data != null && data.size() > 0) {
				for (String s : data) {
					if (serverName != null) {
						if (s.contains(serverName)) {
							EquNode e = null;
							String value = new String(zooKeeper.getData(path + "/" + s, null, new Stat()));
							e = new EquNode(s, value, path + "/" + s, parentName);
							m.add(e);
						}
					} else {
						EquNode e = null;
						String value = new String(zooKeeper.getData(path + "/" + s, null, new Stat()));
						e = new EquNode(s, value, path + "/" + s, parentName);
						m.add(e);
					}
				}
			}
		} catch (Exception e1) {
			if (judgeConnection()) {
				MaizuoLogUtil.writeLog("52", "52", "", "getConfigsByName", "�����ѯ������ʧ��", "", 0, 1);
			} else {
				MaizuoLogUtil.writeLog("52", "52", "", "getConfigsByName/connectionloss", "����������ж�", "", 0, 1);
			}
			reconnect();
			return null;
		}
		return m;
	}

	/** junit���ԣ����Ըı������һ�ͻ����Ƿ��յ�֪ͨ�¼� **/
	public static void main(String[] args) {
		ZooKeeperOperator zk = new ZooKeeperOperator();

		try {
			Stat stat = zk.getZooKeeper().exists("/config", true);
			Stat stat1 = zk.getZooKeeper().exists("/test4", true);
			zk.getZooKeeper().create("/config", "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			// Stat stat = zk.getZooKeeper().setData("/test/child2",
			// "child3".getBytes(), -1);
			// System.out.println(stat);
			// zk.getZooKeeper().delete("/common/child2",-1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public List<EquNode> getCommonInfo(String path) {
		List<String> data = null;
		List<EquNode> m = new ArrayList<EquNode>();
		if (StringUtils.isBlank(path)) {
			return null;
		} else {
			if ("/".equals(path))
				path = rootPath;
			else if (path.charAt(0) != '/')
				path = "/" + path;
		}
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		EquNode e;
		String[] parents = path.split("/");
		try {
			data = zooKeeper.getChildren(path, null);

			if (data != null && data.size() > 0) {
				if (path.equals("/")) {
					path = "";
				}
				for (String s : data) {

					if ("common".equals(s)) {// ��������
						String val = this.getData(path + "/" + s);

						e = new EquNode(s, val, path + "/" + s, parents[parents.length - 1]);

						m.add(e);
					}
				}
			}
		} catch (KeeperException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// System.out.println(data.size());

		return m;
	}
}
