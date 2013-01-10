package test4.demo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.*;

public class PerfObject implements KryoSerializable {
	Integer msgnum;
	long timestamp;
	ArrayList<Object> list;

	public Integer getMsgNum() {
		return msgnum;
	}

	public ArrayList<Object> getList() {
		return list;
	}

	public void setList(ArrayList<Object> list) {
		this.list = list;
	}

	public void setMsgNum(Integer dummyValue) {
		this.msgnum = dummyValue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	//
	@Override
	public void read(Kryo arg0, Input arg1) {
		// TODO Auto-generated method stub
		this.msgnum = arg1.readInt();
		this.timestamp = arg1.readLong();
	}

	@Override
	public void write(Kryo arg0, Output arg1) {
		// TODO Auto-generated method stub
		arg1.writeInt(msgnum);
		arg1.writeLong(timestamp);
		arg1.flush();
	}

}
