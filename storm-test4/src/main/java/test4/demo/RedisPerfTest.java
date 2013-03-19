package test4.demo;

import redis.clients.jedis.Jedis;

public class RedisPerfTest {
	private static  String longString = "asdfasdfassdfasdfadsfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfsadfadsfasdfasdfasdfadsfasdfasdf";
	
	
	
	public static void main(String []args){
		try{
			
		
			for(int i=0;i<10;i++){
				longString = longString+longString;
			}
			
			Jedis jedis = new Jedis("storm0");
			if(jedis.get("a")==null){
				//do  nothing
			}else{
				//erase
				jedis.del("a","b","c","d","e","f","g","h");
			}
			long startTime = System.currentTimeMillis();
			
			jedis.set("a","1");
			jedis.set("b","2");
			jedis.set("c","3");
			jedis.set("d","4");
			jedis.set("e","5");
			jedis.set("f","6");
			jedis.set("g","7");
			jedis.set("h","8");

			long stopTime = System.currentTimeMillis();
			System.out.println("startTime:"+startTime);
			System.out.println("startTime:"+stopTime);
			
			System.out.println("elapsed time:"+((stopTime-startTime))/8.0);
			
			System.out.println(jedis.get("a"));
			System.out.println(jedis.get("b"));
			System.out.println(jedis.get("c"));
			System.out.println(jedis.get("d"));
			System.out.println(jedis.get("e"));
			System.out.println(jedis.get("f"));
			System.out.println(jedis.get("g"));
			System.out.println(jedis.get("h"));
			
			System.out.println("longString bytes"+longString.getBytes("UTF-8").length);
			long startTimeLongString = System.currentTimeMillis();
			jedis.set("a1",longString);
			jedis.set("a2",longString);
			jedis.set("a3",longString);
			jedis.set("a4",longString);
			jedis.set("a5",longString);
			jedis.set("a6",longString);
			jedis.set("a7",longString);
			jedis.set("a8",longString);
			long stopTimeLongString = System.currentTimeMillis();
			System.out.println("long string ms:"+(stopTimeLongString-startTimeLongString));
			System.out.println("long string ms/8:"+(stopTimeLongString-startTimeLongString)/8);	
//100 bytes 1ms
//			longString bytes123
//			long string ms:1
//			longString bytes3200
//			long string ms:3
//longString bytes102400
//			long string ms:28
			
//			longString bytes200
//			long string ms:1
//			long string ms/8:0
			
//			longString bytes3200
//			long string ms:3
//			long string ms/8:0
			
//			longString bytes102400
//			long string ms:21
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
