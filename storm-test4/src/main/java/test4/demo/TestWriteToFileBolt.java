package test4.demo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
//import test4.demo.bolts.*;
import test4.demo.spouts.*;

public class TestWriteToFileBolt {
	static Logger log = Logger.getLogger(TestWriteToFileBolt.class);
	static BufferedWriter bw; 
	static final String FILE="/home/dc/storm/testboltfileoutput.txt";
	
	static class FileBolt extends BaseRichBolt{
		Map conf;
		OutputCollector  collector;
		TopologyContext context;
		
		@Override
		public void execute(Tuple arg0) {
			// TODO Auto-generated method stub
			try{
				Fields f = arg0.getFields();
				java.util.List list = f.toList();
				bw.write("tuple:");
				for(int i=0;i<f.size();i++){
					bw.write(list.get(i).toString());
				}
				bw.newLine();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
			// TODO Auto-generated method stub
			this.conf = arg0;
			this.context = arg1;
			this.collector = arg2;
			try{
				bw = new BufferedWriter(new FileWriter(FILE));
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public void cleanup(){
			try{
				bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
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
