package test4.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class TestDataDelete {
	private static Logger LOG = Logger.getLogger(TestDataDelete.class);
	
	static class DataDeleteSpout extends BaseRichSpout{
		SpoutOutputCollector collector;
		private static JedisPool pool = new JedisPool(new JedisPoolConfig(),ServerAddressPort.HOST);
		//private static Jedis jedis;
		private static Connection conn;
		private String GROUPID;
		//private static JedisPool pool; 
		
		public DataDeleteSpout(String g){
			this.GROUPID=g;
			LOG.info("DataDeleteSpout CTOR:"+GROUPID);
			//jedis=pool.getResource();
			//jedis = new Jedis("localhost");
		}
		
		public void setGroupID(String g){
			LOG.info("SETTING GROUPID:"+g);
			this.GROUPID=g;
		}
		
		@Override
		public void nextTuple() {
			Jedis jedis = pool.getResource();
		    LOG.info("TEST DATADELETE SPOUT NEXT TUPLE!!!");
		    LOG.info("SPOUT GROUPID:"+GROUPID);
			String groupID;
			List<String> list;
			while(true){
//				LOG.info("groupID"+GROUPID);
				if(jedis==null){
					LOG.info("JEDIS NULL!!!!!!!!!!!!!!!!!!");
				}
				if(jedis.exists("groupID"+GROUPID)){
					groupID=jedis.get("groupID"+GROUPID);
					LOG.info("groupID"+GROUPID);
					System.out.println("groupID:"+groupID);
					list = jedis.lrange(GROUPID, 0, jedis.llen(GROUPID));
					collector.emit(new  Values(list.toString()));
					jedis.del("groupID"+GROUPID);  
					break;
				}
			}
						
			for(String st:list){
				LOG.info("DDSpout ESN:"+st);
			}
			LOG.info("DDSpout list size:"+list.size());
			pool.returnResource(jedis);
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2){
			this.collector = arg2;			
		    LOG.info("OPEN TEST DATADELETE SPOUT!!!");			
		    //jedis = pool.getResource(); //replace w/storm0 or local ip when nto at starbucks
			//jedis = new Jedis("localhost");
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			arg0.declare(new Fields("spoutoutput"));
		}
		
	}
	
	static class DataDeleteBolt extends BaseRichBolt{
		Connection conn;
		Map conf;
		TopologyContext context;
		OutputCollector collector;
		boolean connectionPresent;
		
		@Override
		public void execute(Tuple arg0) {
			LOG.info("BOLT GOT  MESSAGE!!!!!"+arg0.getString(0));
			StringTokenizer st = new StringTokenizer(arg0.getString(0),",");
			while(st.hasMoreTokens()){
				String ESN = st.nextToken().replace('[', ' ').trim().replace(']', ' ').trim();
				LOG.info("ESN:"+ESN);
				try{
					Statement stmt = conn.createStatement();
					String sql = "insert into [DeviceLock].[BulkRequestQueue_DC] values('"+ESN+"')";
					LOG.info("SQL:"+sql);
					int result = stmt.executeUpdate(sql);
					LOG.info(result);
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			
		}

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2){
			this.conf = arg0;
			this.context= arg1;
			this.collector = arg2;
					
			try{
				Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
				conn=DriverManager.getConnection("jdbc:jtds:sqlserver://DV2CORP2/CCDATA;domain=Absolute;","dchang","Passw0rd2");
				if(conn==null){
					LOG.info("BOLT NO CONNECTION");
				}else{
					LOG.info("BOLT CONNECTION!!!!!!");
					String c = "use ccdata;";
					Statement stmt = conn.createStatement();
					int resultUseDB = stmt.executeUpdate(c);
					LOG.info("resultDB:"+resultUseDB);
					LOG.info("BOLT AAA");	
				}
								
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			arg0.declare(new Fields("boltoutput"));
		}		
	}


	
	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			DataDeleteSpout ds = new DataDeleteSpout("555");
			DataDeleteSpout ds1 = new DataDeleteSpout("60");

			builder.setSpout("ddspout", ds,1);
			builder.setSpout("ddspout1", ds1,1);

			builder.setBolt("ddbolt", new DataDeleteBolt(), 1).shuffleGrouping("ddspout");		
			builder.setBolt("ddbolt1", new DataDeleteBolt(), 1).shuffleGrouping("ddspout1");
		
			
			Config conf = new Config();
			StormSubmitter.submitTopology("TestDataDelete", conf, builder.createTopology());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
