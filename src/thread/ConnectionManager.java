package thread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ThreadLocal类为每一个线程都维护了自己独有的变量拷贝。每个线程都拥有了自己独立的一个变量，竞争条件被彻底消除了，
 * 那就没有任何必要对这些线程进行同步
 * ，它们也能最大限度的由CPU调度，并发执行。并且由于每个线程在访问该变量时，读取和修改的，都是自己独有的那一份变量拷贝，变量被彻底封闭在每个访问的线程中
 * ，并发错误出现的可能也完全消除了。对比前一种方案，这是一种以空间来换取线程安全性的策略
 * 
 * @author Administrator
 * 
 */
public class ConnectionManager {

	private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>() {
		@Override
		protected Connection initialValue() {
			Connection conn = null;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "username", "password");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return conn;
		}
	};

	public static Connection getConnection() {
		return connectionHolder.get();
	}

	public static void setConnection(Connection conn) {
		connectionHolder.set(conn);
	}
}
