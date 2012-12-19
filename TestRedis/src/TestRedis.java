import java.io.*;
import java.util.*;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TestRedis {

	public static void main(String[] args) {
		try {
			Jedis jedis = new Jedis("localhost");
			BufferedReader br = new BufferedReader(new FileReader(
					"/home/dc/workspace/TestRedis/src/tweets"));

			String fileLine = null;
			while ((fileLine = br.readLine()) != null) {
				System.out.println(fileLine);
				String nextTweet = jedis.get("NEXT_WRITE");
				if (nextTweet == null)
					nextTweet = "0";
				jedis.watch("NEXT_WRITE");
				Transaction transaction = jedis.multi();
				transaction.set(nextTweet, fileLine);
				transaction.incr("NEXT_WRITE");
				transaction.exec();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
