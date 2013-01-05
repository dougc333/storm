package test4.demo;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.*;
//import test4.demo.bolts.*;
import test4.demo.spouts.*;

public class TestWriteToFileBolt {
	static Logger log = Logger.getLogger(TestWriteToFileBolt.class);

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new FileSpout(), 1);
//		builder.setBolt("bolt", new WriteToFileBolt());
//
		Config conf = new Config();
		conf.setDebug(true);
		LocalCluster cluster = new LocalCluster();

//		cluster.submitTopology(arg0, arg1, arg2);

	}
}
