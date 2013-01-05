package test4.demo;

import org.apache.log4j.Logger;

import test4.demo.spouts.TransactionsSpouts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class FailTest {
	static Logger LOG = Logger.getLogger(FailTest.class);

	public static void main(String[] args) throws InterruptedException {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("transactions-spout",
				new test4.demo.spouts.TransactionsSpouts());

//		builder.setBolt("random-failure-bolt",
//				new test4.demo.bolts.RandomFailureBolt()).shuffleGrouping(
//				"transactions-spout");

		LocalCluster cluster = new LocalCluster();
		Config conf = new Config();
		conf.setDebug(true);
		cluster.submitTopology("transactions-test", conf,
				builder.createTopology());
		LOG.info("BEFORE WHILE LOOP");
		while (true) {
			LOG.info("IN WHILE LOOP");
			// Will wait for a fail
			Thread.sleep(1000);
		}
		// LOG.debug("DONE WHILE LOOP");
	}
}
