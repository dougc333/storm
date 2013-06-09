package test4.demo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import test4.demo.TestBoltScheduling.TestBolt;

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

public class TestSpoutScheduling {
	private static Logger LOG= Logger.getLogger("TestSpoutScheduling");
	
	
	static class TestSpout extends BaseRichSpout{
		private Map conf;
		private TopologyContext context;
		private SpoutOutputCollector collector;
		private static AtomicInteger num;
		private static AtomicLong elapsedTime;
		
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.conf = conf;
			this.context = context;
			this.collector = collector;
			num.set(0);
			LOG.info("TestSpoutScheduling OPEN---------------");
		}

		@Override
		public void nextTuple() {
			if(elapsedTime==null){
				System.out.println("Spout Starting no elapsed time yet!!!!!");
				elapsedTime = new AtomicLong();
				elapsedTime.set(System.currentTimeMillis());
			}else{
				System.out.println("Elapsed Time from Last Spout nextTuple() call:"+(System.currentTimeMillis()-elapsedTime.get())/1000.0+" secs");
				elapsedTime.set(System.currentTimeMillis());
			}
			
			LOG.info("nextTuple----------------------"+num.get());
			collector.emit(new Values(num));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}
		
	}
	
	
	static class TestBolt extends BaseRichBolt{
		Map conf;
		TopologyContext context;
		OutputCollector collector;
		AtomicInteger num;
		
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			conf = stormConf;
			this.context = context;
			this.collector = collector;
			num.set(0);
			LOG.info("TestBoltScheduling prepare");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			// there will never be any input here. So we will never see this trigger. 
			LOG.info("bolt execute!!!!!!!!!!!");
			num.addAndGet(1);
			collector.emit(new Values(1));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("boltfield"));
		}
		
		
	}

	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			TestSpout ts = new TestSpout();
			builder.setSpout("spout",ts,3);
			builder.setBolt("bolt", new TestBolt(),3).shuffleGrouping("spout");
			
			Config conf = new Config();
			StormSubmitter.submitTopology("TestSpoutScheduling",conf,builder.createTopology());

			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
