package test4.demo;

import java.io.*;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
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
import backtype.storm.utils.Utils;

public class TestHttpClient {
	static Logger LOG = Logger.getLogger(TestHttpClient.class);

	static class HttpSpout extends BaseRichSpout {
		HttpClient httpClient;
		StringBuilder sb;
		SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			try {
				httpClient = new DefaultHttpClient();
				HttpGet getRequest = new HttpGet("http://www.google.com");
				HttpResponse response = httpClient.execute(getRequest);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				sb = new StringBuilder();
				String fileLine = null;

				while ((fileLine = br.readLine()) != null) {
					sb.append(fileLine);
				}
				this.collector = collector;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			collector.emit(new Values(sb.toString()));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("urloutput"));
		}

	}

	static class TestBolt extends BaseRichBolt {
		OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;

		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			collector.emit("output", new Values(input.getString(0) + "!!!!!"));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("urloutput", new HttpSpout());
			builder.setBolt("bolt", new TestBolt(), 1);

			Config conf = new Config();
			conf.setDebug(true);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("HttpClientTopology", conf,
					builder.createTopology());
			Utils.sleep(100000);
			cluster.deactivate("HttpClientTopology");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
