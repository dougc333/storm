package test4.demo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PerfObject implements KryoSerializable {
	Integer dummyValue;
	long timestamp;

	public Integer getDummyValue() {
		return dummyValue;
	}

	public void setDummyValue(Integer dummyValue) {
		this.dummyValue = dummyValue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public void read(Kryo arg0, Input arg1) {
		// TODO Auto-generated method stub
		this.dummyValue = arg1.readInt();
		this.timestamp = arg1.readLong();
	}

	@Override
	public void write(Kryo arg0, Output arg1) {
		// TODO Auto-generated method stub
		arg1.writeInt(dummyValue);
		arg1.writeLong(timestamp);
		arg1.flush();
	}

}
