package test4.demo;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

// jedis i/f set to write k/v into db
// get to read key out of db.
// http://code.google.com/p/jedis/
public class TestJedis {
	static Logger LOG = Logger.getLogger(FailTest.class);

	public static void main(String[] args) {
		try {
			LOG.info("TestJedis start");

			Jedis jedis = new Jedis("localhost");
			jedis.connect();
			// test if key exists

			String keyExist = jedis.get("testjedis");
			if (keyExist == null) {
				LOG.info("TestJedis populating data first time");
				jedis.set("testjedis", "test data");
				jedis.set("testjedis1", "1");
				jedis.set("testjedis2", "2");
				jedis.set("testjedis3", "3");
				jedis.set("testjedis4", "4");
				jedis.set("testjedis5", "5");
			} else {
				System.out.println("redis populated with test data already");
				LOG.info("jedis data for testjedis:" + jedis.get("testjedis"));
				LOG.info("jedis data for testjedis1:" + jedis.get("testjedis1"));
				LOG.info("jedis data for testjedis2:" + jedis.get("testjedis2"));
				LOG.info("jedis data for testjedis3:" + jedis.get("testjedis3"));
				LOG.info("jedis data for testjedis4:" + jedis.get("testjedis4"));
				LOG.info("jedis data for testjedis5:" + jedis.get("testjedis5"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
