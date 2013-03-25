import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class TestRedishmset {

	public static void main(String []args){
		Jedis jedis = new Jedis(ServerAddressPort.HOST);
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		
		System.out.println(jedis.dbSize());
		HashMap<String, String> hash = new HashMap<String,String>(){
			{
				put("a","aaaa");
				put("b","bbbb");
				
			}
		};
		jedis.hmset("testkey", hash);
		Set<String> set = jedis.hkeys("testkey");
		System.out.println("do you see all the test keys, a, b? ");
		for(String s:set){
			System.out.println(s);
		}
		System.out.println("      ");
		System.out.println("test getting map back");
		Map<String,String> testThere = jedis.hgetAll("testkey");
		Set<String> ks = testThere.keySet();
		for(String s:ks){
			System.out.println("key:"+s+" value:"+testThere.get(s));
		}
		
		//jedis.hset("testkey", "key", "value");
		//print out single key... verify...
		
	}
}
