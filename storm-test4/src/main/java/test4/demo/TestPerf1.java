package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
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
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;

//shows increasing parallelism and increase in performance. not really linear in local mode

////00:00  INFO: elapsed time:101 ms

//00:00  INFO: elapsed time:190 ms
//00:00  INFO: elapsed time:219 ms
//00:00  INFO: elapsed time:237 ms

// 
//00:00  INFO: elapsed time:181 ms
//00:00  INFO: elapsed time:212 ms
//00:00  INFO: elapsed time:212 ms
//00:00  INFO: elapsed time:315 ms
//00:00  INFO: elapsed time:393 ms
//00:00  INFO: elapsed time:422 ms

public class TestPerf1 {
	static Logger LOG = Logger.getLogger(TestPerf1.class);
	static long startTime;
	
	
	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Integer next=0;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if(next<=1000){
			if(next==0){
				startTime = System.currentTimeMillis();
			}
			collector.emit(new Values(next));
			next++;
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("perf"));
		}

	}

	static class TestBolt extends BaseRichBolt {

		OutputCollector collector;
		TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub			
	//		Log.info("num:"+input.getInteger(0).toString());
			if(input.getInteger(0)==1000){
				long stopTime = System.currentTimeMillis();
				LOG.info("elapsed time:"+(stopTime-startTime)+" ms");
			}
			collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("field1"));
		}

	}

	public static void main(String[] args) {

		try {
		
			TopologyBuilder builder = new TopologyBuilder();
			//the total is 12 for the parallelism hint, add the spout and bolts together. 
			//2 worker processes, 12/2 = 6 threads. Each worker process w/6 threads. 
			// 16/2=8 threads  
			builder.setSpout("spout1", new TestSpout(), 20);
			builder.setSpout("spout2", new TestSpout(), 20);
			builder.setSpout("spout3", new TestSpout(), 20);
			builder.setSpout("spout4", new TestSpout(), 20);
			builder.setSpout("spout5", new TestSpout(), 20);
			builder.setSpout("spout6", new TestSpout(), 20);
			builder.setSpout("spout7", new TestSpout(), 20);
			
			builder.setBolt("bolt1", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout1");
			builder.setBolt("bolt2", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout2");
			builder.setBolt("bolt3", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout3");
			builder.setBolt("bolt4", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout4");
			builder.setBolt("bolt5", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout5");
			builder.setBolt("bolt6", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout6");
			builder.setBolt("bolt7", new TestBolt(), 20).setNumTasks(1).shuffleGrouping("spout7");

						
			Config conf = new Config();
//			conf.setNumWorkers(40);
//			LocalCluster cluster = new LocalCluster();
			conf.setNumAckers(0);
			//conf.setDebug(true);
			//conf.setNumWorkers(6);
			//conf.setStatsSampleRate(.001);
			StormSubmitter.submitTopology("TestPerf1", conf, builder.createTopology());
//			cluster.submitTopology("TestPerf1", conf, builder.createTopology());
//			Utils.sleep(10000);
//			cluster.killTopology("TestPerf1");
//			cluster.shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
