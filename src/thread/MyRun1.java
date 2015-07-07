package thread;

public class MyRun1 implements Runnable {

	private Test1 t;

	public Test1 getT() {
		return t;
	}

	public void setT(Test1 t) {
		this.t = t;
	}

	@Override
	public void run() {
		synchronized (t) {
			System.out.println(Thread.currentThread().getName() + "_" + t);
			int num = t.getNum();
			t.setNum(num + 1);
		}
	}

}
