package thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 定时任务调度
 * @author Administrator
 *
 */
public class ScheduledUtils {
	private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(20);

	public static void schelduleAtFixed(Runnable runnable, TimeUnit time, long times) {
		SERVICE.scheduleAtFixedRate(runnable, times, times, time);
		
	}
	
	public static void main(String[] args) {
		schelduleAtFixed(new Runnable() {
			// 每隔一段时间就触发异常
			@Override
			public void run() {
				// throw new RuntimeException();
				System.out.println("================");
			}
		}, TimeUnit.MILLISECONDS, 1000);
		schelduleAtFixed(new Runnable() {
			// 每隔一段时间打印系统时间，证明两者是互不影响的
			@Override
			public void run() {
				System.out.println(System.nanoTime());
			}
		}, TimeUnit.MILLISECONDS, 1000);
	}
}