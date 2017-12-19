package stp.message;

import stp.core.STPObject;

public class Payload extends STPObject {
	
	private long position;
	private int length;
	private long totalLength;
	private byte[] content;
	
	public Payload() {
		
	}
	
	public Payload(final long position, final int contentLength, final long totalLength, final byte[] content) {
		this.position = position;
		this.length = contentLength;
		this.totalLength = totalLength;
		this.content = content;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(final byte[] content) {
		this.content = content;
	}
	
	public long getPosition() {
		return position;
	}
	
	public void setPosition(final long position) {
		this.position = position;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(final int length) {
		this.length = length;
	}
	
	public long getTotalLength() {
		return totalLength;
	}
	
	public void setTotalLength(final long totalLength) {
		this.totalLength = totalLength;
	}
}
