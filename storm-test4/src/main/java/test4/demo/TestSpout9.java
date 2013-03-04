package test4.demo;


import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.protocol.TProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransport;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.daemon.common.ExecutorStats;
//import backtype.storm.daemon.common.ExecutorStats;
import backtype.storm.generated.Bolt;
import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorInfo;
//import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.scheduler.TopologyDetails;
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



//test single ack in remote mode. never shows up in UI. 
//test 10 tuples, we see this, one for each bolt/spout pair
//add msgid  and ack, verify acks
//
public class TestSpout9 {
		static Logger LOG = Logger.getLogger(TestSpout9.class);
		static Integer next= 0;
		static boolean LOCAL=false;
		
		
		static class OneTupleSpout extends BaseRichSpout{
			SpoutOutputCollector collector;
			TopologyContext context;
		
			TopologyContext getContext(){
				return context;
			}
			
			@Override
			public void nextTuple() {
				// TODO Auto-generated method stub
				if(next<10){
					LOG.info("EMIT ONE!!!!!!!!!!!!!!!!!!!!!!");
					collector.emit(new Values(1));
					next++;
				}
			}

			@Override
			public void open(Map arg0, TopologyContext context,
					SpoutOutputCollector collector) {
				// TODO Auto-generated method stub
				this.collector = collector;
				this.context=context;
			}

			@Override
			public void declareOutputFields(OutputFieldsDeclarer declarer) {
				// TODO Auto-generated method stub
				declarer.declare(new Fields("spoutoutput"));
			}	
			
		}
		
		//baserich bolt acks automatically
		static class OneTupleBolt extends BaseRichBolt{
			OutputCollector collector;
			TopologyContext context;
			Map conf;
			
	
			@Override
			public void execute(Tuple tuple) {
				LOG.info("OneTupleBolt EMIT ONE!!!!!!"+tuple.getString(0));
				collector.emit(new Values(tuple.getString(0)));
				//adding this I get no log entry for the spoutS!!!
				collector.ack(tuple);
			}

			@Override
			public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
				this.conf = conf;
				this.context = context;
				this.collector = collector;
				LOG.info("TESTSTORM9 PREPARE!!!!!!!!!!!!!!!!!!!!!!");
			}

			@Override
			public void declareOutputFields(OutputFieldsDeclarer declarer) {
				declarer.declare(new Fields("boltoutput"));
			}
			
			
		}
		
		public static void main(String []args){
			try{
				
				TopologyBuilder builder = new TopologyBuilder();
//				OneTupleSpout oneTupleSpout = new OneTupleSpout();
//				builder.setSpout("spoutoutput", new OneTupleSpout(),2);
				
				builder.setSpout("spoutoutput", new test4.demo.spouts.ModifiedWordSpout(),4);
				builder.setBolt("bolt",new OneTupleBolt(),4).shuffleGrouping("spoutoutput");
				
				Config conf = new Config();
//				conf.setNumWorkers(10);
				
				if(LOCAL){
					conf.setDebug(true);
					LocalCluster cluster  = new LocalCluster();
					cluster.submitTopology("TestStorm9", conf, builder.createTopology());					
					Utils.sleep(10000);
					cluster.deactivate("TestStorm9");
					cluster.shutdown();				
				}else{
					LOG.info("REMOTE MODE");
					StormTopology st = builder.createTopology();
					conf.setNumAckers(1); //removes reliability
					conf.setNumWorkers(2);
					StormSubmitter.submitTopology("TestSpout9",conf,st);
				
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


