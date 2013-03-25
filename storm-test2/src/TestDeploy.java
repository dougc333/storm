

import backtype.storm.Config;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.TopologyInitialStatus;
import backtype.storm.utils.NimbusClient;
import backtype.storm.generated.StormTopology;

public class TestDeploy {

	public static void main(String []args){
		try{
			NimbusClient nc = new NimbusClient("localhost",6627);
			
			Nimbus.Client client = nc.getClient();
			
			client.beginFileUpload();
			client.finishFileUpload("/home/dc/storm/storm-test4/target/stormtest4-0.0.1-jar-with-dependencies.jar");
			//TopologyInitialStatus ts;
			//ts.findByValue(2);
			
			System.out.println("client:"+client.toString());
		
			ClusterSummary cs = client.getClusterInfo();
			System.out.println("cs:"+cs.toString());
			Config conf = new Config();
			
			StormTopology st = new StormTopology();
			
			//how to specify the topology name?
			//client.submitTopology("test4.demo.TestStorm1", "","" , st);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
