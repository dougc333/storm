package test4.demo;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.transactional.*;
import test4.demo.spouts.FileSpoutTransactionalSpout;
import test4.demo.bolts.*;
import test4.demo.spouts.*;

//test 1 packetsplitter bolt
public class TestTrans {

	static Logger LOG = Logger.getLogger(TestTrans.class);

	public static void main(String[] args) {
		try {
			TransactionalTopologyBuilder tt = new TransactionalTopologyBuilder(
					"TestTrans", "spout", new PacketTransSpout());

			PacketSplitterBolt ps = new PacketSplitterBolt();
			tt.setBolt("packetspliltter", ps,1).shuffleGrouping("spout");
			LocalCluster cluster = new LocalCluster();
			Config config = new Config();
			config.setDebug(true);
			// config.setMaxSpoutPending(1);
			// config.setMaxTaskParallelism(1);
			cluster.submitTopology("TestTransTopology", config,
					tt.buildTopology());
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
