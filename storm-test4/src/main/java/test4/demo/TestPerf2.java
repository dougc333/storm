package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import test4.demo.TestPerf1.TestBolt;
import test4.demo.TestPerf1.TestSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
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

//6k
//2552 [Thread-36] INFO  test4.demo.TestPerf2  - size string:20
//2700 [Thread-18] INFO  test4.demo.TestPerf2  - elapsed time:146 ms
//2758 [Thread-38] INFO  test4.demo.TestPerf2  - elapsed time:204 ms
//2828 [Thread-18] INFO  test4.demo.TestPerf2  - elapsed time:274 ms
//2909 [Thread-16] INFO  test4.demo.TestPerf2  - elapsed time:355 ms
//2933 [Thread-18] INFO  test4.demo.TestPerf2  - elapsed time:379 ms
//2996 [Thread-20] INFO  test4.demo.TestPerf2  - elapsed time:442 ms

//6k
//2299 [Thread-34] INFO  test4.demo.TestPerf2  - size string:100
//2482 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:113 ms
//2513 [Thread-24] INFO  test4.demo.TestPerf2  - elapsed time:144 ms
//2555 [Thread-20] INFO  test4.demo.TestPerf2  - elapsed time:186 ms
//2626 [Thread-38] INFO  test4.demo.TestPerf2  - elapsed time:257 ms
//2642 [Thread-24] INFO  test4.demo.TestPerf2  - elapsed time:273 ms
//2706 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:337 ms

//10k bad, more bolts/spouts have dropouts
//3070 [Thread-54] INFO  test4.demo.TestPerf2  - elapsed time:160 ms
//3123 [Thread-54] INFO  test4.demo.TestPerf2  - elapsed time:213 ms
//3251 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:341 ms
//3401 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:491 ms
//3402 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:492 ms
//3421 [Thread-16] INFO  test4.demo.TestPerf2  - elapsed time:511 ms
//3436 [Thread-22] INFO  test4.demo.TestPerf2  - elapsed time:526 ms
//3458 [Thread-16] INFO  test4.demo.TestPerf2  - elapsed time:548 ms



public class TestPerf2 {

	static Logger LOG = Logger.getLogger(TestPerf2.class);
	static long startTime;
	
	
	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Integer next=0;
		String data[]={"aaaaaaaaaa","bbbbbbbbbb"};
		String databig[];
		//STRING_SIZE, 10 means 100 byte strings
		// 100 is 1k byte strings(not 1024)
		static final int STRING_SIZE=10;
		
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
			LOG.info("size string:"+(data[0].getBytes()).length);
			databig = new String[2];
			//
			StringBuilder buffa = new StringBuilder();
			StringBuilder buffb = new StringBuilder();
			for (int i=0;i<STRING_SIZE;i++){
				buffa.append(data[0]);
				buffb.append(data[1]);
			}
			databig[0] = buffa.toString();
			databig[1] = buffb.toString();
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if(next<=1000){
			if(next==0){
				startTime = System.currentTimeMillis();
			}
			
			collector.emit(new Values(databig[next%2],next));
			next++;
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("databig","count"));
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
			if(input.getInteger(1)==1000){
				long stopTime = System.currentTimeMillis();
				LOG.info("elapsed time:"+(stopTime-startTime)+" ms");
			}
			//collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("field1"));
		}

	}

	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TestSpout(), 8).setNumTasks(24);
			builder.setBolt("bolt", new TestBolt(), 8).setNumTasks(24).shuffleGrouping("spout");

			Config conf = new Config();
			LocalCluster cluster = new LocalCluster();
			conf.setNumAckers(0);
//			conf.setStatsSampleRate(.00001);
			cluster.submitTopology("TestPerf2", conf, builder.createTopology());
			Utils.sleep(80000);
			cluster.killTopology("TestPerf2");
			cluster.shutdown();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
