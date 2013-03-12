import java.util.Map;

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


public class datadelete1 {
	
	static class datadeleteSpout extends BaseRichSpout{

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			System.out.println("spout called");
			//update to mongo
			
			
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2) {
			// TODO Auto-generated method stub
			
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	static class datadeletebolt extends BaseRichBolt{

		Map conf;
		TopologyContext context;
		OutputCollector collector;
		
		
		@Override
		public void execute(Tuple arg0) {
			// TODO Auto-generated method stub
			//do a mongo access here?, this is scheduled like a spout? No. 
			System.out.println("bolt called");
			collector.emit(new Values(1));
		}

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
			// TODO Auto-generated method stub
			this.conf = arg0;
			this.context= arg1;
			this.collector = arg2;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			arg0.declare(new Fields("boltoutput"));
		}
		
		
	}
	
	public static void main(String []args){
		
		try{
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("dd1spout", new datadeleteSpout(),3);
			builder.setBolt("dd1bolt", new datadeletebolt(), 3);
			
			Config conf = new Config();
			StormSubmitter.submitTopology("datadelete1", conf, builder.createTopology());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
