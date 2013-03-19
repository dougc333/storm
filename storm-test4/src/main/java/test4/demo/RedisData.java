package test4.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import redis.clients.jedis.Jedis;


public class RedisData {
	private static List<String> headerNames = new ArrayList<String>();
	private static List<String> dataList = new ArrayList<String>();
	
	private static void processData(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/home/dc/data"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/dc/dataclean"));
			String fileLine = null;
			while((fileLine=br.readLine())!=null){
				//System.out.println(fileLine);
				//System.out.println(fileLine.replaceAll("<tr>", "").replaceAll("<td>","").replaceAll("</td>","\t"));
				bw.write(fileLine.replaceAll("<tr>", "").replaceAll("<td>","").replaceAll("</td>","\t"));
				bw.newLine();
				dataList.add(fileLine.replaceAll("<tr>", "").replaceAll("<td>","").replaceAll("</td>","\t"));
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void processHeader(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/home/dc/header.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/dc/headerclean.txt"));
			String fileLine = null;
			while((fileLine=br.readLine())!=null){
				//System.out.println(fileLine);
				//System.out.println(fileLine.replaceAll("<th>", "").replaceAll("</th>",""));
				headerNames.add(fileLine.replaceAll("<th>", "").replaceAll("</th>",""));
				bw.write(fileLine.replaceAll("<th>", "").replaceAll("</th>",""));
				bw.newLine();
			}
			bw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void main(String []args){
		try{
			Jedis jedis = new Jedis("localhost");
			processHeader();			
			processData();
			Integer dataNum=1;
			System.out.println("dataList size:"+dataList.size());
			System.out.println("headerNames size:"+headerNames.size());
			for (String d : dataList){
				System.out.println("processing:"+d);
				StringTokenizer st = new StringTokenizer(d);
				System.out.println("numtokens"+st.countTokens());
				Map<String,String> m = new TreeMap<String,String>();
				int numCol=0;
				while(st.hasMoreTokens()&& numCol<st.countTokens()){
					String headerName=headerNames.get(numCol);
					String fieldValue=st.nextToken();
					System.out.println("dataNum:"+dataNum+" fieldName:"+headerName+" fieldValue:"+fieldValue);
					m.put(headerName,fieldValue);
					//System.out.println(jedis.hset(dataNum.toString(), headerName, fieldValue));
					//System.out.println("hget:"+jedis.hget(dataNum.toString(), headerName));
					numCol++;
				}
				String s = jedis.hmset(dataNum.toString(), m);
				System.out.println("s:"+s);
				dataNum++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
