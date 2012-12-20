import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import java.io.*;

public class MakeMoreData {

	// https://github.com/xetorthio/jedis/wiki/AdvancedUsage
	public static void main(String args[]) {
		try {
			Jedis jedis = new Jedis("localhost");
			BufferedReader br = new BufferedReader(new FileReader(
					"/home/dc/storm/storm/TestRedis/src/packetdata"));
			String fileLine = null;
			while ((fileLine = br.readLine()) != null) {
				jedis.watch("PACKETS");
				String packetNum = jedis.get("PACKETS");
				if (packetNum == null) {
					packetNum = "10000";
				}
				Transaction transaction = jedis.multi();
				transaction.set(packetNum, fileLine);
				transaction.incr("PACKETS");
				transaction.exec();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
