

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;


public class DataClientListenerImpl implements DataClientListener {
	
	private  Map<String,String> serverPath = new HashMap<String, String>();

	public void creatNode(String node,String value,Map<String,Object> nodeList,String nodePath,String rootPath,String common) {
		if(nodeList!=null){
			if(common==null || (nodePath.startsWith(common) && !serverPath.containsKey(rootPath+"/"+node)) || nodePath.startsWith(rootPath)){
				nodeList.put(node, value);
			}
		}
		serverPath.put(nodePath, node);
	}

	public void removeNode(String node, Map<String, Object> nodeList,String nodePath,String rootPath,String common) {
		if (nodeList != null) {
			for (String s : nodeList.keySet()) {
				if (s.equals(node)) {
					if(common==null || (nodePath.startsWith(rootPath) && !serverPath.containsKey(common+"/"+node)) || (nodePath.startsWith(common) && !serverPath.containsKey(rootPath+"/"+node))){
						nodeList.remove(s);
					}
					if(common!=null && serverPath.containsKey(common+"/"+node) && serverPath.containsKey(rootPath+"/"+node)){
						if(nodePath.startsWith(rootPath)){
							String value = serverPath.get(common+"/"+node);
							nodeList.put(node, value);
						}
					}
					break;
				}
			}
		}
		serverPath.remove(nodePath);
	}
	
	public void getAllNodes(String path, ZooKeeperOperator zk,
			Map<String, Object> m) throws KeeperException, InterruptedException{
		List<String> data = null;
		Stat stat = null;
		stat = zk.getZooKeeper().exists(path, false);
		if (stat != null) {
			data = zk.getZooKeeper().getChildren(path, zk, stat);
		}
		if (data != null && data.size() > 0) {
			for (String s : data) {
				stat = zk.getZooKeeper().exists(path + "/" + s, true);
				if (stat != null) {
					byte[] sb = zk.getZooKeeper().getData(path + "/" + s, zk,
							stat);
					m.put(s, String.valueOf(sb));
					serverPath.put(path + "/" + s, String.valueOf(sb));
				}

			}
		}
	}

	public void updateNode(String node, String value,Map<String, Object> nodeList,String nodePath,String rootPath,String common) {
		if(nodeList!=null){
			if(common==null || nodePath.startsWith(rootPath)){
				nodeList.put(node, value);
			}
		}
	}

	public String getCreateNode(String path, ZooKeeperOperator zk,
			String rootPath) throws KeeperException, InterruptedException {
		List<String> data = null;
		String childNode = null;
		data = zk.getZooKeeper().getChildren(path, zk);
		if (data != null && data.size() > 0) {
			if(path.equals("/")){
				path="";
			}
			for (String s : data) {
				if (!serverPath.containsKey(path + "/" + s)) {
					Stat stat = zk.getZooKeeper().exists(path + "/" + s, true);
					if (rootPath.equals("/")) {
						zk.getZooKeeper().getChildren(path + "/" + s, zk);
					}
					childNode = s;
					byte[] sb = zk.getZooKeeper().getData(path + "/" + s, null,stat);
					serverPath.put(path + "/" + s, String.valueOf(sb));
					break;
				}
			}
			return childNode;
		}
		return null;
	}

	public void getRootNodes(String path, ZooKeeperOperator zk) throws KeeperException, InterruptedException{
		List<String> data = null;
		Stat stat = null;
		stat = zk.getZooKeeper().exists(path, true);
		if (stat != null) {
			data = zk.getZooKeeper().getChildren(path, zk, stat);
			byte[] sb = zk.getZooKeeper().getData(path, null, stat);
			serverPath.put(path, String.valueOf(sb));
		}
		if (data != null && data.size() > 0) {
			if (path.equals("/")) {
				path = "";
			}
			for (String s : data) {
				getRootNodes(path + "/" + s, zk);
			}
		}
	}
	
}
