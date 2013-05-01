package test4.demo;

import org.apache.log4j.Logger;

import backtype.storm.scheduler.Cluster;
import backtype.storm.scheduler.EvenScheduler;
import backtype.storm.scheduler.ExecutorDetails;
import backtype.storm.scheduler.IScheduler;
import backtype.storm.scheduler.Topologies;
import backtype.storm.scheduler.TopologyDetails;

import java.util.*;


public class TestScheduler implements IScheduler{

	@Override
	public void schedule(Topologies topologies, Cluster cluster) {
		// TODO Auto-generated method stub
		System.out.println("TestScheduler");		
		new EvenScheduler().schedule(topologies,cluster);
	}
	
	public void prepare(Map conf){
		System.out.println("TestScheduler prepare");
	}

}
