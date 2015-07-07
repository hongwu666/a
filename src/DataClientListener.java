


import java.util.Map;


import org.apache.zookeeper.KeeperException;


public interface DataClientListener {
	
	void creatNode(String node,String value,Map<String,Object> nodeList,String path,String rootPath,String common);
	
	void removeNode(String node,Map<String,Object> nodeList,String path,String rootPath,String common);
	
	void updateNode(String node,String value,Map<String,Object> nodeList,String path,String rootPath,String common);
	
	void getAllNodes(String path,ZooKeeperOperator zk, Map<String,Object> m) throws KeeperException, InterruptedException;
	
	String getCreateNode(String path,ZooKeeperOperator zk,String rootPath) throws KeeperException, InterruptedException;
	
	void getRootNodes(String path,ZooKeeperOperator zk) throws KeeperException, InterruptedException;
}
