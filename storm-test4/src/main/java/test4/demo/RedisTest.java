package test4.demo;

import test4.demo.spouts.RQ;

public class RedisTest {

	public static void main(String[] args) {

		RQ redis = new RQ();

		// tricky, first was 1000, then 0, but when you rerun, is 0 from start
		// System.out.println("next read:" + redis.getNextRead());
		// redis.setNextRead(0L);
		// System.out.println("next read:" + redis.getNextRead());

		java.util.List<String> l = redis.getMessages(1108, 18);
		for (String s : l) {
			System.out.println("data:" + s);
		}

		// how does getavailtoread work? this is the number of messages/values
		// after the given read key not related to concurrency
		System.out.println("avail to read:" + redis.getAvailableToRead(0L));

		System.out.println("avail to read:" + redis.getAvailableToRead(200L));
		System.out.println("avail to read:" + redis.getAvailableToRead(1108));

	}
}
