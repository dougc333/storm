package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//import redis.clients.jedis.Jedis;

import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
//infinite loop, test storm ui
//test jedis i/f
//test design patterns https://github.com/nathanmarz/storm/wiki/Common-patterns
import org.apache.log4j.*;

public class TestStorm1 {
	static Logger LOG = Logger.getLogger(TestStorm1.class);
	static String data[] = { "a", "b", "c", "d", "e", "f", "g", "h","i","j","k","l","m","n","o","p","q","r","s","t" };
	
	
	static class TestStorm1Spout extends BaseRichSpout {
		Integer next = 0;
		SpoutOutputCollector collector;
		TopologyContext context;
		Map conf;
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			this.collector = collector;
			this.context=context;
			this.conf = conf;
		}

		@Override
		public void nextTuple() {
			if (next == 20) {
				next = 0;
			}
			LOG.info("TestStorm1 SPOUT EMITTING:" + data[next] + " !!!!!!!");
<<<<<<< HEAD
=======
			LOG.info("TestStorm1 SPOUT next:" + next);
			System.out.println("spout this.componentID:"+context.getThisComponentId());
			System.out.println("spout this.taskID:"+context.getThisTaskId());
			Set<String> compIDs = context.getComponentIds();
			System.out.println("spout compoentIDs for this topology:"+compIDs);
			System.out.println("current thead id:"+Thread.currentThread().getId());
			System.out.println("current thead name:"+Thread.currentThread().getName());
			
			//add an ID have to put in. How does this behave?
			// look at SS code to duplicate
>>>>>>> 844a26cf249bf0ba0c9ec4210b4eaaa18041eb24
			collector.emit(new Values(data[next]));
			next++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("letter"));
		}

	}

	static class TestStorm1Bolt extends BaseRichBolt {
		OutputCollector collector;
		TopologyContext context;
<<<<<<< HEAD
=======
		Map conf;
		
>>>>>>> 844a26cf249bf0ba0c9ec4210b4eaaa18041eb24
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
<<<<<<< HEAD
			this.context=context;
=======
			this.context = context;
			this.conf = conf;
			Set<String> compIds = context.getComponentIds();
			System.out.println("component ids in topology:"+compIds);
			Map<Integer,String> mapTaskIDCompID = context.getTaskToComponent();
			for(Integer taskID:mapTaskIDCompID.keySet()){
				System.out.println("taskID:"+taskID+" componentID:"+mapTaskIDCompID.get(taskID));
			}
			
>>>>>>> 844a26cf249bf0ba0c9ec4210b4eaaa18041eb24
		}
		
		private static int fib(int first){
			if(first<2){
				return first;
			}else{
				return fib(first-1)+fib(first-2);
			}
			
		}
		
		@Override
		public void execute(Tuple input) {
<<<<<<< HEAD
			long startTime = System.currentTimeMillis();
			LOG.info("TEST BOLT EXECUTE!!!!!!!!");
			//lets try a delay does this show up in our elapsed time?
			//yes for 100 ms
//			Utils.sleep(100);
			//no for Utils.sleep(1000), this is larger than the time slice 
			//so you never see the elapsed time
			
			//lets try a fibonacci sequence and see if it will complete
			System.out.println("fib:"+fib(40));
			
=======
			// TODO Auto-generated method stub
			System.out.println("spout this.componentID:"+context.getThisComponentId());
			System.out.println("spout tasks in this task, this.taskID:"+context.getThisTaskId());
			System.out.println("spout workers in this task, this.taskID:"+context.getThisWorkerTasks());
			Set<String> compIDs = context.getComponentIds();
			System.out.println("spout compoentIDs for this topology:"+compIDs);
			System.out.println("source task:"+input.getSourceTask());
			System.out.println("source component:"+input.getSourceComponent());
			ExecutorService executor = context.getSharedExecutor();
			
			LOG.info("TEST BOLT EXECUTE!!!!!!!!");
>>>>>>> 844a26cf249bf0ba0c9ec4210b4eaaa18041eb24
			collector.emit(input,new Values(input.getString(0)));
			collector.ack(input);
			System.out.println("Bolt execute:elapsedTime: "+(System.currentTimeMillis()-startTime)/1000.0+" component:"+context.getThisComponentId());
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("bolt1output"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

<<<<<<< HEAD
			builder.setSpout("letter", new TestStorm1Spout(), 5);
			builder.setSpout("secondletter", new TestStorm1Spout(), 5);
			builder.setSpout("thirdletter", new TestStorm1Spout(), 5);
			
			builder.setBolt("id1", new TestStorm1Bolt(), 10).setNumTasks(5).shuffleGrouping(
=======
			builder.setSpout("letter", new TestStorm1Spout(), 4).setNumTasks(10);
			//builder.setSpout("secondletter", new TestStorm1Spout(), 1);
			//builder.setSpout("thirdletter", new TestStorm1Spout(), 1);
			
			builder.setBolt("id1", new TestStorm1Bolt(), 4).shuffleGrouping(
>>>>>>> 844a26cf249bf0ba0c9ec4210b4eaaa18041eb24
					"letter");
			builder.setBolt("id2", new TestStorm1Bolt(), 10).shuffleGrouping(
					"secondletter");
			builder.setBolt("id3", new TestStorm1Bolt(), 10).shuffleGrouping(
					"thirdletter");
		
			Config conf = new Config();
			System.out.println("STORM CLUSTER MODE:"+conf.STORM_CLUSTER_MODE);
			System.out.println("STORM SCHEDULER:"+conf.STORM_SCHEDULER);
			

			
			conf.setDebug(true);
			conf.setNumWorkers(6);
//			conf.setNumAckers(10);
//			conf.setMaxSpoutPending(10000);
			
			StormSubmitter.submitTopology("TestStorm1", conf, builder.createTopology());

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
