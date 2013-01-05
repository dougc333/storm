package test4.demo.bolts;

import java.util.Map;

import org.apache.log4j.Logger;

import test4.demo.FailTest;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class PacketSplitterBolt implements IBasicBolt {
	static Logger LOG = Logger.getLogger(PacketSplitterBolt.class);

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPLITTERBOLT declareOutputFields()");
		declarer.declareStream("packets", new Fields("txid", "packetid",
				"packet"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPLITTERBOLT getComponentConfiguration()");
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPLITTERBOLT prepare()");
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPLITTER execute()");
		// from PacketSpoutEmitter, emits tx, "" + packetId, message
		String x = input.getStringByField("packet_id");
		String y = input.getStringByField("packet");

		LOG.info("PACKET SPLITTER BOLT x:" + x + " y:" + y);
		Fields fields = input.getFields();
		java.util.Iterator<String> it = fields.iterator();
		while (it.hasNext()) {
			String fieldName = it.next();
			LOG.info("PACKETSPLITTERBOLT fieldNames:" + fieldName);

		}
		// LOG.info("PACKETSPLITTERBOLT:");

		// print out the input first, verify we are getting the input stream

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPLITTERBOLT cleanup()");
	}

}
