package main;

import java.util.List;
import java.util.Map;

import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorInfo;
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.utils.NimbusClient;

//run this when topology loaded or write separate program to load the topology
//and run this
public class Test {

	public static void main(String[] args) {
		System.out.println("asfdasdf");
		try{
			NimbusClient nc = new NimbusClient("localhost",6627);
			Nimbus.Client client = nc.getClient();
			
			System.out.println("client:"+client.toString());
			
			ClusterSummary cs = client.getClusterInfo();
			System.out.println("cs:"+cs.toString());
			
			
			List<TopologySummary> localTopos = cs.get_topologies();
			for(TopologySummary ts:localTopos){
				System.out.println("Topology Name:"+ts.get_name());
				System.out.println("Topology id:"+ts.get_id());
				System.out.println("Topology numexecutors:"+ts.get_num_executors());
				System.out.println("Topology numtasks:"+ts.get_num_tasks());
				System.out.println("Topology numworkers:"+ts.get_num_workers());
				System.out.println("Topology status:"+ts.get_status());
				System.out.println("Topology uptime:"+ts.get_uptime_secs());
			}
			
			List<SupervisorSummary> localListSups = cs.get_supervisors();
			for(SupervisorSummary s: localListSups){
				System.out.println("supervisor numworkers:"+s.get_num_workers());
				System.out.println("supervisor host:"+s.get_host());
				System.out.println("supervisor num used workers:"+s.get_num_used_workers());
				System.out.println("supervisor uptime seconds:"+s.get_uptime_secs());
			
			}
			System.out.println("nimbus uptime:"+cs.get_nimbus_uptime_secs());

			java.util.List<TopologySummary> topSum = cs.get_topologies();
			
			System.out.println("--------------------------------------------------");
			System.out.println("Topology Summary");
			System.out.println("--------------------------------------------------");
			String topologyId = null;
			for(TopologySummary sum : topSum){
				System.out.println("topology id:"+sum.get_id());
				topologyId = sum.get_id();
				System.out.println("topology name:"+sum.get_name());
				System.out.println("topology num executors:"+sum.get_num_executors());
				System.out.println("topology num tasks:"+sum.get_num_tasks());
				System.out.println("topology num workers:"+sum.get_num_workers());
				System.out.println("topology status:"+sum.get_status());
				System.out.println("topology uptime:"+sum.get_uptime_secs());
			}
			
			//from Thrift server, this doesnt exist in local mode which has no thrift server
			TopologyInfo ti = client.getTopologyInfo(topologyId);
			System.out.println("topology id:"+topologyId);
			System.out.println("Topology Name:"+ti.get_name());
			System.out.println("Topology executors size:"+ti.get_executors_size());
			System.out.println("Topology status:"+ti.get_status());
			System.out.println("Topology uptime:"+ti.get_uptime_secs());
			System.out.println("Topology executors size:"+ti.get_executors().size());
			
			java.util.List<ExecutorSummary> execSum = ti.get_executors();
//				ExecutorSummary es = execSum.get(0);
//			System.out.println("asdfasdf:"+es.get_stats().get_emitted().get("600").get("__ack_ack"));
			System.out.println("+++++++++++++++++++++++++++++++");
			
			System.out.println("+++++++++++++++++++++++++++++++");
			for(ExecutorSummary e : execSum){
				System.out.println("ExecutorSummary component_id:"+e.get_component_id());
				System.out.println("ExecutorSummary host:"+e.get_host());
				System.out.println("ExecutorSummary port:"+e.get_port());
				System.out.println("ExecutorSummary uptime:"+e.get_uptime_secs());
				
				ExecutorInfo ei = e.get_executor_info();
				System.out.println("ExecutorInfo taskStart:"+ei.get_task_start());
				System.out.println("ExecutorInfo taskEnd:"+ei.get_task_end());
				
				//careful there are 2 imports here... one for generated one for the daemon dont know the difference
				backtype.storm.generated.ExecutorStats es1 = e.get_stats();
				if(es1!=null){
				System.out.println("ExecutorStats emittedSize:"+es1.get_emitted_size());
				System.out.println("ExecutorStats transferredSize:"+es1.get_transferred_size());
			
			
				Map<String, Map<String,Long>> emittedStats = es1.get_emitted();
				for(String procEmit: emittedStats.keySet()){
					System.out.println("Emitted Stats key:"+procEmit);
					Map<String,Long> m = emittedStats.get(procEmit);
					for(String emitS : m.keySet()){
						System.out.println("EmittedStats key:"+emitS+" value:"+m.get(emitS));
					}
				}
				
				
				for(String s:es1.get_transferred().keySet()){
					for(String s1:es1.get_transferred().keySet()){
						System.out.println("	statK:"+s1+" value:"+es1.get_transferred());
					}
				}
				
				ExecutorSpecificStats ess = es1.get_specific();
				SpoutStats spoutStats = ess.get_spout();
				System.out.println("spout stats:"+spoutStats.toString());
				System.out.println("spoutStats ackedSize:"+spoutStats.get_acked_size());
				System.out.println("spoutStats complete_ms_avg_size:"+spoutStats.get_complete_ms_avg_size());
				System.out.println("spoutStats failedSize:"+spoutStats.get_failed_size());
				
				//add rest in maps
				
				
				BoltStats boltStats = ess.get_bolt();
				System.out.println("boltStats ackedSize:"+boltStats.get_acked_size());
				System.out.println("boltStats execute ms avg size:"+boltStats.get_execute_ms_avg_size());
				System.out.println("boltStats executed size:"+boltStats.get_executed_size());
				System.out.println("boltStats failed size:"+boltStats.get_failed_size());
				System.out.println("boltStats ms avg size:"+boltStats.get_process_ms_avg_size());
				System.out.println("boltStats:"+boltStats.toString());
				
				
				
				}else{
					System.out.println("STATS NULL!!!!!");
				}
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}


	}

}
