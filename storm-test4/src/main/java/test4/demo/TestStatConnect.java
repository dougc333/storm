package test4.demo;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.utils.NimbusClient;

public class TestStatConnect {

	public static void main(String []args){
		try{
			NimbusClient nc = new NimbusClient("localhost",6627);
			Nimbus.Client client = nc.getClient();
			ClusterSummary cs = client.getClusterInfo();
			System.out.println("cs:"+cs.toString());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
