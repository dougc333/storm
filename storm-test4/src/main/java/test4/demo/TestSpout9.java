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
//try thrift query 
//
public class TestSpout9 {
		static Logger LOG = Logger.getLogger(TestStorm8.class);
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
				if(next <1000){
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
		
		static class OneTupleBolt extends BaseRichBolt{
			OutputCollector collector;
			TopologyContext context;
			Map conf;
			
			@Override
			public void execute(Tuple tuple) {
				LOG.info("OneTupleBolt EMIT ONE!!!!!!");
				collector.emit(new Values(tuple.getInteger(0)));
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
				OneTupleSpout oneTupleSpout = new OneTupleSpout();
				builder.setSpout("spout", oneTupleSpout,4);
				builder.setBolt("bolt",new OneTupleBolt(),4).shuffleGrouping("spout");
				
				Config conf = new Config();
				//conf.setDebug(true);
//				conf.setNumWorkers(10);
				
				LocalCluster cluster  = new LocalCluster();
				//cluster.submitTopology("TestStorm9", conf, builder.createTopology());
				
				if(LOCAL){
					ClusterSummary localClusterSummary = cluster.getClusterInfo();
					
					List<TopologySummary> localTopos = localClusterSummary.get_topologies();
					for(TopologySummary ts:localTopos){
						LOG.info("Topology Name:"+ts.get_name());
						LOG.info("Topology id:"+ts.get_id());
						LOG.info("Topology numexecutors:"+ts.get_num_executors());
						LOG.info("Topology numtasks:"+ts.get_num_tasks());
						LOG.info("Topology numworkers:"+ts.get_num_workers());
						LOG.info("Topology status:"+ts.get_status());
						LOG.info("Topology uptime:"+ts.get_uptime_secs());
					}
					
					List<SupervisorSummary> localListSups = localClusterSummary.get_supervisors();
					for(SupervisorSummary s: localListSups){
						LOG.info("supervisor numworkers:"+s.get_num_workers());
						LOG.info("supervisor host:"+s.get_host());
						LOG.info("supervisor num used workers:"+s.get_num_used_workers());
						LOG.info("supervisor uptime seconds:"+s.get_uptime_secs());
					
					}
					LOG.info("nimbus uptime:"+localClusterSummary.get_nimbus_uptime_secs());
					localClusterSummary.get_supervisors_size();
					
					
					
					Utils.sleep(10000);
					cluster.deactivate("TestStorm9");
					cluster.shutdown();
				
				}else{
				StormTopology st = builder.createTopology();
				StormSubmitter.submitTopology("TestSpout9",conf,st);

				
				//NimbusClient("YOUR_IP").getClient().getTopologyInfo("YOUR_TOPOLOGY_ID").get_executors().get(0).get_stats().get_emitted().get("600").get("__ack_ack"); 
				
				NimbusClient nc = new NimbusClient("localhost",6627);
				Nimbus.Client client = nc.getClient();
				ClusterSummary cs = client.getClusterInfo();
				
				java.util.List<SupervisorSummary> supLis = cs.get_supervisors();
				LOG.info("-----------------------------------------------");
				LOG.info("Supervisory Summary");
				LOG.info("----------------------------------------------");
				for(SupervisorSummary sum:supLis){
					LOG.info("numUsedWorkers:"+sum.get_num_used_workers());
					LOG.info("host:"+sum.get_host());
					LOG.info("numWorkers:"+sum.get_num_workers());
					LOG.info("uptime secs:"+sum.get_uptime_secs());
					LOG.info("numUsedWorkers"+sum.get_num_used_workers());
					
				}
				java.util.List<TopologySummary> topSum = cs.get_topologies();
				
				LOG.info("--------------------------------------------------");
				LOG.info("Topology Summary");
				LOG.info("--------------------------------------------------");
				String topologyId = null;
				for(TopologySummary sum : topSum){
					LOG.info("topology id:"+sum.get_id());
					topologyId = sum.get_id();
					LOG.info("topology name:"+sum.get_name());
					LOG.info("topology num executors:"+sum.get_num_executors());
					LOG.info("topology num tasks:"+sum.get_num_tasks());
					LOG.info("topology num workers:"+sum.get_num_workers());
					LOG.info("topology id:"+sum.get_status());
					LOG.info("topology uptime:"+sum.get_uptime_secs());
				}
				
				//from Thrift server, this doesnt exist in local mode which has no thrift server
				TopologyInfo ti = client.getTopologyInfo(topologyId);
				LOG.info("topology id:"+topologyId);
				LOG.info("Topology info Name:"+ti.get_name());
				LOG.info("Topology info executors size:"+ti.get_executors_size());
				LOG.info("Topology info status:"+ti.get_status());
				LOG.info("Topology uptime:"+ti.get_uptime_secs());
				LOG.info("Topology executors size:"+ti.get_executors().size());
				
				java.util.List<ExecutorSummary> execSum = ti.get_executors();
	//			ExecutorSummary es = execSum.get(0);
//				LOG.info("asdfasdf:"+es.get_stats().get_emitted().get("600").get("__ack_ack"));
				LOG.info("+++++++++++++++++++++++++++++++");
				for(ExecutorSummary e : execSum){
					LOG.info("Executor component_id:"+e.get_component_id());
					LOG.info("Executor host:"+e.get_host());
					LOG.info("Executor port:"+e.get_port());
					LOG.info("Executor uptime:"+e.get_uptime_secs());
					ExecutorInfo ei = e.get_executor_info();
					LOG.info("ExecutorInfo taskStart:"+ei.get_task_start());
					LOG.info("ExecutorInfo taskEnd:"+ei.get_task_end());
					
					//careful there are 2 imports here... one for generated one for the daemon dont know the difference
					
					backtype.storm.generated.ExecutorStats es1 = e.get_stats();
					if(es1!=null){
					LOG.info("ExecutorStats emittedSize:"+es1.get_emitted_size());
					LOG.info("ExecutorStats transferredSize:"+es1.get_transferred_size());
					//what is other map for? 
					
					Map<String, Map<String,Long>> stats = es1.get_transferred();
					Set<String> statKeys = stats.keySet();
					for(String s:statKeys){
						Map<String,Long> map = stats.get(s);
						LOG.info("statKey:"+s+" size more stats:"+map.size() );
						Set<String> statK = map.keySet();
						for(String s1:statK){
							LOG.info("	statK:"+statK+" value:"+map.get(s1));
						}
					}
					}else{
						LOG.info("STATS NULL!!!!!");
					}
//					LOG.info("ExecutorStats emittedSize:"+es.);

				}
				//print out bolts and spouts from st. 
				Map<String,Bolt> mapBolts = st.get_bolts();
				Set<String> keys = mapBolts.keySet();
				for(String s:keys){
					LOG.info("bolt:"+s);
					Bolt b = mapBolts.get(s);
					
				}
				
				//try teh thrift interface directly. 
				TProtocol input = client.getInputProtocol();
				TProtocol output = client.getOutputProtocol();
				}
				
				//System.out.println();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


