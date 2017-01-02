package stp.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import stp.system.STPConstants;
import stp.system.STPObject;

public class Message extends STPObject {
	
	private String tag;
	private String id;
	private String type;
	private Payload payload;
	
	public Message() {
		this(null, null, null);
	}
	
	public Message(final String type, final String id, final Payload payload) {
		tag = STPConstants.MSG_TAG;
		this.id = id == null ? generateId() : id;
		this.type = type;
		this.payload = payload;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(final String type) {
		this.type = type;
	}
	
	public Payload getPayload() {
		return payload;
	}
	
	public void setPayload(final Payload payload) {
		this.payload = payload;
	}
	
	public String getTag() {
		return tag;
	}
	
	private String generateId() {
		return UUID.randomUUID().toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getHeader());
		stringBuilder.append(new String(payload.getContent()));
		stringBuilder.append(new String(STPConstants.STP_TRAILER));
		
		return stringBuilder.toString();
	}
	
	public byte[] toBytes() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(STPConstants.MSG_MAX_SIZE);
		byteArrayOutputStream.write(getHeader().getBytes());
		byteArrayOutputStream.write(getPayload().getContent());
		byteArrayOutputStream.write(STPConstants.STP_TRAILER);
		
		return byteArrayOutputStream.toByteArray();
	}
	
	public String getHeader() {
		final StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(tag);
		stringBuilder.append(" ");
		stringBuilder.append(type);
		stringBuilder.append(" ");
		stringBuilder.append(id);
		stringBuilder.append(" ");
		stringBuilder.append(payload.getPosition());
		stringBuilder.append(" ");
		stringBuilder.append(payload.getLength());
		stringBuilder.append(" ");
		stringBuilder.append(payload.getTotalLength());
		stringBuilder.append("\r\n");
		
		return stringBuilder.toString();
	}
}
