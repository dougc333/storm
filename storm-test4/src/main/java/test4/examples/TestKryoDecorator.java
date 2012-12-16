package test4.examples;

import test4.demo.spouts.TestSerObject;
import backtype.storm.serialization.IKryoDecorator;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TestKryoDecorator implements IKryoDecorator {

    public void decorate(Kryo k) {
        k.register(TestSerObject.class);
    }
}
