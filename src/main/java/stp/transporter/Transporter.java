package stp.transporter;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import stp.gateway.Peer;
import stp.message.Message;
import stp.message.Payload;
import stp.parser.ParserManager;
import stp.system.STPConstants;
import stp.system.STPException;
import stp.system.STPLogger;
import stp.system.STPObject;

public class Transporter extends STPObject {
	
	private Peer peer;
	private Socket socket;
	private BlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<>();
	private Message sendingMessage;
	private Message receivingMessage;
	private Thread receiveThread;
	private Thread sendThread;
	
	public Transporter(final Peer peer, final Socket socket) {
		this.peer = peer;
		this.socket = socket;
	}
	
	public Queue<Message> getPendingMessages() {
		final Queue<Message> pendingMessages = new LinkedList<Message>();
		
		if (sendingMessage != null) {
			pendingMessages.add(sendingMessage);
		}
		
		pendingMessages.addAll(sendMessageQueue);
		
		return pendingMessages;
	}
	
	public Message getSendingMessage() {
		return sendingMessage;
	}
	
	public Message getReceivingMessage() {
		return receivingMessage;
	}
	
	public void start() {
		receiveThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						receive();
					} catch (final STPException exception) {
						STPLogger.exception(exception);
						
						break;
					}
				}
			}
		};
		
		receiveThread.start();
		
		sendThread = new Thread() {
			@Override
			public void run() {
				try {
					send();
				} catch (final STPException exception) {
					STPLogger.exception(exception);
				}
			}
		};
		
		sendThread.start();
	}
	
	public void receive() throws STPException {
		try {
			STPLogger.info(STPConstants.TRANSPORT_PREPARING_TO_READ_MESSAGE);
			
			final String[] header = receiveHeader();
			
			final String type = header[STPConstants.STP_HEADER_INDEX_TYPE];
			final String id = header[STPConstants.STP_HEADER_INDEX_ID];
			final long payloadPosition = Long.parseLong(header[STPConstants.STP_HEADER_INDEX_PAYLOAD_POSITION]);
			final int payloadContentLength = Integer.parseInt(header[STPConstants.STP_HEADER_INDEX_PAYLOAD_CONTENT_LENGTH]);
			final long payloadTotalLength = Long.parseLong(header[STPConstants.STP_HEADER_INDEX_PAYLOAD_TOTAL_LENGTH]);
			
			final byte[] payloadContent = receiveContent(payloadContentLength);
			
			receiveTrailer();
			
			final Payload payload = new Payload(payloadPosition, payloadContentLength, payloadTotalLength, payloadContent);
			Message message = new Message(type, id, payload);
			
			message = ParserManager.getInstance().prepareReading(message);
			
			if (message != null) {
				receivingMessage = message;
				
				validate(message);
				
				STPLogger.info(STPConstants.TRANSPORT_MESSAGE_READ);
				
				ParserManager.getInstance().read(peer, message);
				
				receivingMessage = null;
			} else {
				STPLogger.info(STPConstants.TRANSPORT_MESSAGE_IGNORED);
			}
		} catch (final STPException exception) {
			switch (exception.getCode()) {
				case STPConstants.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION:
				
				case STPConstants.EXCEPTION_CODE_HEADER_INVALID_FORMAT:
				case STPConstants.EXCEPTION_CODE_TRAILER_INVALID_CONTENT:
				
				case STPConstants.EXCEPTION_CODE_OBJECT_NULL_MESSAGE:
				case STPConstants.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD:
				case STPConstants.EXCEPTION_CODE_MESSAGE_INVALID_ID:
				case STPConstants.EXCEPTION_CODE_MESSAGE_NULL_TYPE:
				case STPConstants.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE:
				
				case STPConstants.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_OVERLOADED:
				
				case STPConstants.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE:
				
				default: {
					STPLogger.exception(exception);
					
					peer.end();
					
					throw new STPException(STPConstants.EXCEPTION_CODE_PEER_CONNECTION_CLOSED, STPConstants.EXCEPTION_STR_PEER_CONNECTION_CLOSED);
				}
			}
		} catch (final Exception exception) {
			peer.end();
			
			throw new STPException(exception);
		}
	}
	
	public void sendAsync(final Message message) throws STPException {
		try {
			sendMessageQueue.put(message);
		} catch (final InterruptedException exception) {
			
		}
	}
	
	public synchronized void sendSync(final Message message) throws STPException {
		try {
			STPLogger.info(STPConstants.TRANSPORT_PREPARING_TO_SEND_MESSAGE);
			
			final Message newMessage = ParserManager.getInstance().prepareWriting(message);
			
			if (newMessage != null) {
				sendingMessage = newMessage;
				
				validate(newMessage);
				
				write(newMessage.toBytes());
				
				STPLogger.info(STPConstants.TRANSPORT_MESSAGE_SENT);
				
				ParserManager.getInstance().written(peer, newMessage);
				
				sendingMessage = null;
			} else {
				STPLogger.info(STPConstants.TRANSPORT_MESSAGE_IGNORED);
			}
		} catch (final STPException exception) {
			switch (exception.getCode()) {
				case STPConstants.EXCEPTION_CODE_HEADER_INVALID_FORMAT:
				
				case STPConstants.EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION:
				
				case STPConstants.EXCEPTION_CODE_OBJECT_NULL_MESSAGE:
				case STPConstants.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD:
				case STPConstants.EXCEPTION_CODE_MESSAGE_INVALID_ID:
				case STPConstants.EXCEPTION_CODE_MESSAGE_NULL_TYPE:
				case STPConstants.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE:
				
				case STPConstants.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH:
				case STPConstants.EXCEPTION_CODE_PAYLOAD_OVERLOADED:
				
				case STPConstants.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE:
				
				default: {
					STPLogger.exception(exception);
					
					peer.end();
					
					throw new STPException(STPConstants.EXCEPTION_CODE_PEER_CONNECTION_CLOSED, STPConstants.EXCEPTION_STR_PEER_CONNECTION_CLOSED);
				}
			}
		} catch (final Exception exception) {
			peer.end();
			
			throw new STPException(STPConstants.EXCEPTION_CODE_GENERAL_EXCEPTION, exception.getMessage());
		}
	}
	
	private void send() throws STPException {
		Message message;
		
		try {
			while ((message = sendMessageQueue.take()) != null) {
				sendSync(message);
			}
		} catch (final InterruptedException exception) {
			
		}
	}
	
	private int read() throws STPException {
		try {
			return socket.getInputStream().read();
		} catch (final IOException exception) {
			throw new STPException(STPConstants.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION, exception.getMessage());
		}
	}
	
	private int read(byte[] bytes, int off, int len) throws STPException {
		try {
			return socket.getInputStream().read(bytes, off, len);
		} catch (final IOException exception) {
			throw new STPException(STPConstants.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION, exception.getMessage());
		}
	}
	
	private void write(byte[] bytes) throws STPException {
		try {
			socket.getOutputStream().write(bytes);
		} catch (final IOException exception) {
			throw new STPException(STPConstants.EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION, exception.getMessage());
		}
	}
	
	private String[] receiveHeader() throws STPException {
		String header = "";
		
		byte lastReadByte = 0;
		byte readByte = 0;
		
		do {
			lastReadByte = readByte;
			readByte = (byte) read();
			
			if (readByte == -1) {
				throw new STPException(STPConstants.EXCEPTION_CODE_PEER_CONNECTION_CLOSED, STPConstants.EXCEPTION_STR_PEER_CONNECTION_CLOSED);
			}
			
			header += (char) readByte;
		} while (lastReadByte != '\r' && readByte != '\n');
		
		return splitHeader(header.substring(0, header.length() - 2));
	}
	
	private byte[] receiveContent(final int length) throws STPException {
		final byte[] content = new byte[length];
		
		int receive = 0, total = 0;
		
		do {
			receive = read(content, total, (length - total));
			total += receive;
			
			if (receive == -1) {
				throw new STPException(STPConstants.EXCEPTION_CODE_PEER_CONNECTION_CLOSED, STPConstants.EXCEPTION_STR_PEER_CONNECTION_CLOSED);
			}
		} while (receive > 0);
		
		return content;
	}
	
	private void receiveTrailer() throws STPException {
		final byte[] trailer = new byte[STPConstants.STP_TRAILER.length];
		
		final int readSize = read(trailer, 0, STPConstants.STP_TRAILER.length);
		
		if (readSize == -1) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PEER_CONNECTION_CLOSED, STPConstants.EXCEPTION_STR_PEER_CONNECTION_CLOSED);
		}
		
		for (int i = 0; i < STPConstants.STP_TRAILER.length; i++) {
			if (trailer[i] != STPConstants.STP_TRAILER[i]) {
				throw new STPException(STPConstants.EXCEPTION_CODE_TRAILER_INVALID_CONTENT, STPConstants.EXCEPTION_STR_TRAILER_INVALID_CONTENT);
			}
		}
	}
	
	private String[] splitHeader(final String header) throws STPException {
		validateHeader(header);
		
		return header.split(" ");
	}
	
	public static void validate(final Message message) throws STPException {
		validateMessage(message);
		
		validateHeader(message.getHeader());
		
		validatePayload(message.getPayload());
	}
	
	private static void validateMessage(final Message message) throws STPException {
		if (message == null) {
			throw new STPException(STPConstants.EXCEPTION_CODE_OBJECT_NULL_MESSAGE, STPConstants.EXCEPTION_STR_OBJECT_NULL_MESSAGE);
		}
		
		if (message.getPayload() == null) {
			throw new STPException(STPConstants.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD, STPConstants.EXCEPTION_STR_MESSAGE_NULL_PAYLOAD);
		}
		
		if (message.getId().length() != STPConstants.MSG_ID_LENGTH) {
			throw new STPException(STPConstants.EXCEPTION_CODE_MESSAGE_INVALID_ID, STPConstants.EXCEPTION_STR_MESSAGE_INVALID_ID);
		}
		
		if (message.getType() == null) {
			throw new STPException(STPConstants.EXCEPTION_CODE_MESSAGE_NULL_TYPE, STPConstants.EXCEPTION_STR_MESSAGE_NULL_TYPE);
		}
		
		if (message.getType().isEmpty()) {
			throw new STPException(STPConstants.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE, STPConstants.EXCEPTION_STR_MESSAGE_EMPTY_TYPE);
		}
	}
	
	private static void validatePayload(final Payload payload) throws STPException {
		if (payload == null) {
			throw new STPException(STPConstants.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD, STPConstants.EXCEPTION_STR_OBJECT_NULL_PAYLOAD);
		}
		
		if (payload.getContent() == null) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT, STPConstants.EXCEPTION_STR_PAYLOAD_NULL_CONTENT);
		}
		
		if (payload.getPosition() < 0) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION, STPConstants.EXCEPTION_STR_PAYLOAD_INVALID_POSITION);
		}
		
		if (payload.getLength() < 0 || payload.getLength() > STPConstants.PAYLOAD_MAX_SIZE || payload.getTotalLength() > STPConstants.PAYLOAD_MAX_SIZE) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH, STPConstants.EXCEPTION_STR_PAYLOAD_INVALID_LENGTH);
		}
		
		if (payload.getLength() != payload.getContent().length) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH, STPConstants.EXCEPTION_STR_PAYLOAD_INVALID_LENGTH_MATCH);
		}
		
		if (payload.getPosition() + payload.getLength() > payload.getTotalLength()) {
			throw new STPException(STPConstants.EXCEPTION_CODE_PAYLOAD_OVERLOADED, STPConstants.EXCEPTION_STR_PAYLOAD_OVERLOADED);
		}
	}
	
	private static void validateHeader(final String header) throws STPException {
		final String[] headerPieces = header.split(" ");
		
		int piecesQuantity = 0;
		
		for (final String headerPiece : headerPieces) {
			if (!headerPiece.isEmpty()) {
				piecesQuantity++;
			}
		}
		
		if (headerPieces.length != piecesQuantity || piecesQuantity != STPConstants.STP_HEADER_PIECES_LENGTH) {
			throw new STPException(STPConstants.EXCEPTION_CODE_HEADER_INVALID_FORMAT, STPConstants.EXCEPTION_STR_HEADER_INVALID_FORMAT);
		}
	}
}
