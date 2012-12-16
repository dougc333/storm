package test4.examples;

import java.io.Serializable;

public interface AckFailDelegate extends Serializable {
    public void ack(Object id);
    public void fail(Object id);
}
