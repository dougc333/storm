package storm.test;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;



/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        TopologyBuilder top = new TopologyBuilder();
	
 	Config conf = new Config();
	LocalCluster cluster = new LocalCluster();
	cluster.submitTopology("test",conf,top.createTopology());
	Utils.sleep(1000);
	cluster.shutdown();
    }
}
