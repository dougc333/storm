package test4.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

//numbers off, starts at 1108? 
public class LoadPackets {
	public static void main(String[] args) {
		try {
			Jedis jedis = new Jedis("localhost");
			BufferedReader br = new BufferedReader(new FileReader(
					"/home/dc/packets.txt"));

			String fileLine = null;
			String next = "1000";
			System.out.println("next write;" + jedis.get("NEXT_WRITE"));

			// jedis.set("NEXT_WRITE", "1000");
			while ((fileLine = br.readLine()) != null) {
				System.out.println(fileLine);
				next = jedis.get("NEXT_WRITE");
				// if (next == null)
				jedis.watch("NEXT_WRITE");
				Transaction transaction = jedis.multi();
				transaction.set(next, fileLine);
				transaction.incr("NEXT_WRITE");
				transaction.exec();
				System.out.println("next_write;" + jedis.get("NEXT_WRITE"));
			}
			jedis.set("NEXT_READ", "1000");
			System.out.println(jedis.get("NEXT_READ"));

			jedis.set("NEXT_READ", "1001");
			List<String> l = jedis.mget("1001");
			System.out.println(l.size());
			System.out.println("list:" + l.get(0));

			jedis.set("NEXT_READ", "1002");
			System.out.println(jedis.get("NEXT_READ"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
