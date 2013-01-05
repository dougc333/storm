package test4.demo.spouts;

import java.io.Serializable;

public class TransactionMetadata implements Serializable {
	private static final long serialVersionUID = 1L;

	public long from = 1108L;
	public int quantity = 1;

	public TransactionMetadata(long from, int quantity) {
		this.from = from;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "TMD (f:" + from + ", q:" + quantity + ")";
	}
}
