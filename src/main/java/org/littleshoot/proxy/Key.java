package org.littleshoot.proxy;
public class Key implements de.uniba.wiai.lspi.chord.service.Key {
	private String theString;

	public Key(String theString) {
		this.theString = theString;
	}

	public byte[] getBytes() {
		return this.theString.getBytes();
	}

	public int hashCode() {
		return this.theString.hashCode();
	}

	public boolean equals(Object o) {
		if (o instanceof Key) {
			return ((Key) o).theString.equals(this.theString);
		}
		return false;
	}

}