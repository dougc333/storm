package test4.demo;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;

import test4.demo.TestStorm1a.TestStorm1Bolt;
import test4.demo.TestStorm1a.TestStorm1Spout;
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

//simulate number of clients with different message sizes
//plot memory and cpu usage w/#clients and message size
//
public class TestStorm1Clients {
	private static Integer NUM_CLIENTS=10;
	private static Integer MESSAGE_SIZE=3;
	private static Integer BUFFER_SIZE=10;
	static Logger LOG = Logger.getLogger(TestStorm1Clients.class);
	static final String Id[]={"a","b","c","d"};
	private static AtomicInteger last=new AtomicInteger(0);
	private static Sigar sigar=new Sigar();	
	//for cpu1 only
	private static double maxUserTime;
	private static Vector<Double> avgUserTime= new Vector<Double>();
	
	private static double maxIdleTime;
	private static double avgIdleTime;
	
	
	static class TestStorm1ClientSpout extends BaseRichSpout {
		static final Integer DELAY=0;
		Integer next = 0;
		SpoutOutputCollector collector;
		Vector<String> data=new Vector<String>();
		String id;
		
		long lastMessageSent=0L;
		
		
		private String createMessage(){
			StringBuffer sb = new StringBuffer();
			while(sb.length()<MESSAGE_SIZE){
				sb.append("a");
			}
			LOG.info("SPOUT message:"+sb.toString());
			return sb.toString();
		}
		//represents message queue/buffer for messages
		private void createByteArray(){
			for(int i=0;i<BUFFER_SIZE;i++){
				data.addElement(createMessage());
			}
			System.out.println("buffer size:"+data.size());
			LOG.info("buffer size:"+data.size());
		}
		
		
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			LOG.info("calling getCPUInfo !!!!!!!!!!!!");
			LOG.info("TestStorm1Client spout open()");
			this.collector = collector;
			//synchronized (this){
			//	this.id=Id[last.get()];
			//	LOG.info("LAST::::::::"+last);
			//	last.getAndIncrement();
			//}
			createByteArray();
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			LOG.info("TestStorm1Client in Spout nextTuple()");
			if (next == data.size()-1) {
				next = 0;
			}
			//add an ID have to put in. How does this behave?
			// look at SS code to duplicate
			if(lastMessageSent==0L || (lastMessageSent+DELAY) <= System.currentTimeMillis()){
				LOG.info("EMITTING "+data.get(next));
				collector.emit(new Values("bbb"));
				next++;
//				LOG.info("TestStorm1a SPOUT EMITTING:" + data.get(next) + " !!!!!!! time:"+System.currentTimeMillis());
//				LOG.info("TestStorm1a SPOUT next:" + next);
				lastMessageSent=System.currentTimeMillis();

			}else{
				LOG.info("delay not set, SPOUT not EMITTING!!!!");
			}
			//printCPU();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("message"));
		}

	}

	
	static class TestStorm1ClientBolt extends BaseRichBolt {
		OutputCollector collector;
		TopologyContext context;
				
		
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
			this.context=context;
		}
		
		
		@Override
		public void execute(Tuple input) {
			LOG.info("TEST BOLT EXECUTE!!!!!!!!  :"+input.getString(0));
			collector.emit(input,new Values(input.getString(0)));
			collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("boltoutput"));
		}

	}

	
	private static void printCPU(){
		try{
		CpuInfo []cpuInfo = sigar.getCpuInfoList();
		LOG.info("num cpus:"+cpuInfo.length);
		for(int i=0;i<cpuInfo.length;i++){
		//	System.out.println("vendor:"+cpuInfo[i].getVendor());
		//	System.out.println("cores/socket:"+cpuInfo[i].getCoresPerSocket());
		//	System.out.println("cachesize:"+cpuInfo[i].getCacheSize());
		//	System.out.println("model:"+cpuInfo[i].getModel());
		//	System.out.println("total cores:"+cpuInfo[i].getTotalCores());
		}
		
		CpuPerc[] cpus = sigar.getCpuPercList();
		for(int i=0;i<cpus.length;i++){
			System.out.println("UserTime:"+CpuPerc.format(cpus[i].getUser()));
			//System.out.println("SysTime:"+CpuPerc.format(cpus[i].getSys()));
			System.out.println("IdleTime:"+CpuPerc.format(cpus[i].getIdle()));
			//System.out.println("WaitTime:"+CpuPerc.format(cpus[i].getWait()));
			//System.out.println("NiceTime:"+CpuPerc.format(cpus[i].getNice()));
			//System.out.println("CombinedTime:"+CpuPerc.format(cpus[i].getCombined()));

			//find maxUserTime
			if(i==0){
				if(cpus[i].getUser() > maxUserTime){
					maxUserTime=cpus[i].getUser();
				}
				
				//average UserTime
				if(avgUserTime.size()<=100){
//					avgUserTime.add(cpus[i].getUser());
				}else{
					//compute avg
					double total=0;
					for(Double testMe:avgUserTime){
						total = total+testMe;
					}
					System.out.println("avg user time:"+total/avgUserTime.size());
					avgUserTime.clear();
				}
				//find maxIdleTime
				if(cpus[i].getIdle() > maxIdleTime){
					maxIdleTime=cpus[i].getIdle();
				}
				
			}
		}
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();				
			builder.setSpout("la", new TestStorm1ClientSpout(), 1);
			//builder.setSpout("secondlettera", new TestStorm1ClientSpout(), 5);
			//builder.setSpout("thirdlettera", new TestStorm1ClientSpout(), 5);
			
			builder.setBolt("id1", new TestStorm1ClientBolt(), 1).setNumTasks(5).shuffleGrouping(
					"la");
			//builder.setBolt("id2a", new TestStorm1ClientBolt(), 10).shuffleGrouping(
			//		"secondlettera");
			//builder.setBolt("id3a", new TestStorm1ClientBolt(), 10).shuffleGrouping(
			//		"thirdlettera");
			//builder.setBolt("id4", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"secondletter");
			//builder.setBolt("id5", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"thirdletter");
			//builder.setBolt("id6", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"thirdletter");

			Config conf = new Config();
			conf.setDebug(true);
//			conf.setNumWorkers(6);
//			conf.setNumAckers(10);
//			conf.setMaxSpoutPending(10000);
			
			StormSubmitter.submitTopology("TestStorm1Clients", conf, builder.createTopology());

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
}
