package test4.demo;

public class TestSpinWait {

	public static void main(String []args){
		long EXPECTED_ELAPSED_TIME=1000;
		long startTime = System.currentTimeMillis();
		long elapsed = 0;   
		  while(elapsed < EXPECTED_ELAPSED_TIME){
		      elapsed= System.currentTimeMillis()-startTime;
		  }
	}
}
