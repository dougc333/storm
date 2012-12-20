import redis.clients.jedis.Jedis;

public class TestJedis {
	private static final String NEXT_READ = "NEXT_READ";
	private static final String NEXT_WRITE = "NEXT_WRITE";

	// usage testNext
	public static void testNext() {
		Jedis jedis = new Jedis("localhost");
		jedis.connect();
		// need a write for this to work; not complete
		String sNextRead = jedis.get(NEXT_READ);
		System.out.println("next read:" + sNextRead);

		String nextWrite = jedis.get(NEXT_WRITE);
		System.out.println("next write:" + nextWrite);

	}

	// usage: testConnection(). tests read/write
	public static void testConnection() {
		Jedis jedis = new Jedis("localhost");
		// odd connect() is optional
		// jedis.connect();
		jedis.set("black", "asdf");
		String v = jedis.get("black");
		System.out.println("v:" + v);

	}

	public static void createData() {
		Jedis jedis = new Jedis("localhost");
		for (int i = 0; i < 1000; i++) {
			jedis.set(Integer.toString(i), Integer.toString(i));
		}
		System.out.println("10:" + jedis.get("10"));
	}

	public static void main(String[] args) {
		try {
			// testConnection();
			createData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
