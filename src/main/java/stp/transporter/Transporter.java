package stp.transporter;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import stp.gateway.Peer;
import stp.message.Message;
import stp.message.Payload;
import stp.parser.ParserManager;
import stp.system.STPException;
import stp.system.STPObject;

public class Transporter extends STPObject {
	
	private static final Logger logger = Logger.getLogger(Transporter.class);
	
	public static final int PAYLOAD_MAX_SIZE = 4096;
	public static final int MSG_ID_LENGTH = 36;
	public static final int STP_HEADER_PIECES_LENGTH = 6;
	public static final int STP_HEADER_INDEX_TAG = 0;
	public static final int STP_HEADER_INDEX_TYPE = 1;
	public static final int STP_HEADER_INDEX_ID = 2;
	public static final int STP_HEADER_INDEX_PAYLOAD_POSITION = 3;
	public static final int STP_HEADER_INDEX_PAYLOAD_CONTENT_LENGTH = 4;
	public static final int STP_HEADER_INDEX_PAYLOAD_TOTAL_LENGTH = 5;
	public static final byte[] STP_TRAILER = { 'E', 'N', 'D', '\r', '\n' };
	public static final String TRANSPORT_PREPARING_TO_SEND_MESSAGE = "Transport - preparing to send message";
	public static final String TRANSPORT_MESSAGE_SENT = "Transport - sent message";
	public static final String TRANSPORT_PREPARING_TO_READ_MESSAGE = "Transport - preparing to read message";
	public static final String TRANSPORT_MESSAGE_READ = "Transport - message read";
	public static final String TRANSPORT_MESSAGE_IGNORED = "Transport - message ignored";
	
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
						logger.error(exception);
						
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
					logger.error(exception);
				}
			}
		};
		
		sendThread.start();
	}
	
	public void receive() throws STPException {
		try {
			logger.info(TRANSPORT_PREPARING_TO_READ_MESSAGE);
			
			final String[] header = receiveHeader();
			
			final String type = header[STP_HEADER_INDEX_TYPE];
			final String id = header[STP_HEADER_INDEX_ID];
			final long payloadPosition = Long.parseLong(header[STP_HEADER_INDEX_PAYLOAD_POSITION]);
			final int payloadContentLength = Integer.parseInt(header[STP_HEADER_INDEX_PAYLOAD_CONTENT_LENGTH]);
			final long payloadTotalLength = Long.parseLong(header[STP_HEADER_INDEX_PAYLOAD_TOTAL_LENGTH]);
			
			final byte[] payloadContent = receiveContent(payloadContentLength);
			
			receiveTrailer();
			
			final Payload payload = new Payload(payloadPosition, payloadContentLength, payloadTotalLength, payloadContent);
			Message message = new Message(type, id, payload);
			
			message = ParserManager.getInstance().prepareReading(message);
			
			if (message != null) {
				receivingMessage = message;
				
				validate(message);
				
				logger.info(TRANSPORT_MESSAGE_READ);
				
				ParserManager.getInstance().read(peer, message);
				
				receivingMessage = null;
			} else {
				logger.info(TRANSPORT_MESSAGE_IGNORED);
			}
		} catch (final STPException exception) {
			switch (exception.getCode()) {
				case STPException.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION:
				
				case STPException.EXCEPTION_CODE_HEADER_INVALID_FORMAT:
				case STPException.EXCEPTION_CODE_TRAILER_INVALID_CONTENT:
				
				case STPException.EXCEPTION_CODE_OBJECT_NULL_MESSAGE:
				case STPException.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD:
				case STPException.EXCEPTION_CODE_MESSAGE_INVALID_ID:
				case STPException.EXCEPTION_CODE_MESSAGE_NULL_TYPE:
				case STPException.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE:
				
				case STPException.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD:
				case STPException.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH:
				case STPException.EXCEPTION_CODE_PAYLOAD_OVERLOADED:
				
				case STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE:
				
				default: {
					logger.error(exception);
					
					peer.end();
					
					throw new STPException(STPException.EXCEPTION_CODE_PEER_CONNECTION_CLOSED);
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
			logger.error(exception);
			
			Thread.currentThread().interrupt();
		}
	}
	
	public synchronized void sendSync(final Message message) throws STPException {
		try {
			logger.info(TRANSPORT_PREPARING_TO_SEND_MESSAGE);
			
			final Message newMessage = ParserManager.getInstance().prepareWriting(message);
			
			if (newMessage != null) {
				sendingMessage = newMessage;
				
				validate(newMessage);
				
				write(newMessage.toBytes());
				
				logger.info(TRANSPORT_MESSAGE_SENT);
				
				ParserManager.getInstance().written(peer, newMessage);
				
				sendingMessage = null;
			} else {
				logger.info(TRANSPORT_MESSAGE_IGNORED);
			}
		} catch (final STPException exception) {
			switch (exception.getCode()) {
				case STPException.EXCEPTION_CODE_HEADER_INVALID_FORMAT:
				
				case STPException.EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION:
				
				case STPException.EXCEPTION_CODE_OBJECT_NULL_MESSAGE:
				case STPException.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD:
				case STPException.EXCEPTION_CODE_MESSAGE_INVALID_ID:
				case STPException.EXCEPTION_CODE_MESSAGE_NULL_TYPE:
				case STPException.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE:
				
				case STPException.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD:
				case STPException.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH:
				case STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH:
				case STPException.EXCEPTION_CODE_PAYLOAD_OVERLOADED:
				
				case STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE:
				
				default: {
					logger.error(exception);
					
					peer.end();
					
					throw new STPException(STPException.EXCEPTION_CODE_PEER_CONNECTION_CLOSED);
				}
			}
		} catch (final Exception exception) {
			peer.end();
			
			throw new STPException(exception);
		}
	}
	
	private void send() throws STPException {
		Message message;
		
		try {
			while ((message = sendMessageQueue.take()) != null) {
				sendSync(message);
			}
		} catch (final InterruptedException exception) {
			logger.error(exception);
			
			Thread.currentThread().interrupt();
		}
	}
	
	private int read() throws STPException {
		try {
			return socket.getInputStream().read();
		} catch (final IOException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION, exception);
		}
	}
	
	private int read(byte[] bytes, int off, int len) throws STPException {
		try {
			return socket.getInputStream().read(bytes, off, len);
		} catch (final IOException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_INPUTSTREAM_EXCEPTION, exception);
		}
	}
	
	private void write(byte[] bytes) throws STPException {
		try {
			socket.getOutputStream().write(bytes);
		} catch (final IOException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_OUTPUTSTREAM_EXCEPTION, exception);
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
				throw new STPException(STPException.EXCEPTION_CODE_PEER_CONNECTION_CLOSED);
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
				throw new STPException(STPException.EXCEPTION_CODE_PEER_CONNECTION_CLOSED);
			}
		} while (receive > 0);
		
		return content;
	}
	
	private void receiveTrailer() throws STPException {
		final byte[] trailer = new byte[STP_TRAILER.length];
		
		final int readSize = read(trailer, 0, STP_TRAILER.length);
		
		if (readSize == -1) {
			throw new STPException(STPException.EXCEPTION_CODE_PEER_CONNECTION_CLOSED);
		}
		
		for (int i = 0; i < STP_TRAILER.length; i++) {
			if (trailer[i] != STP_TRAILER[i]) {
				throw new STPException(STPException.EXCEPTION_CODE_TRAILER_INVALID_CONTENT);
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
			throw new STPException(STPException.EXCEPTION_CODE_OBJECT_NULL_MESSAGE);
		}
		
		if (message.getPayload() == null) {
			throw new STPException(STPException.EXCEPTION_CODE_MESSAGE_NULL_PAYLOAD);
		}
		
		if (message.getId().length() != Message.MSG_ID_LENGTH) {
			throw new STPException(STPException.EXCEPTION_CODE_MESSAGE_INVALID_ID);
		}
		
		if (message.getType() == null) {
			throw new STPException(STPException.EXCEPTION_CODE_MESSAGE_NULL_TYPE);
		}
		
		if (message.getType().isEmpty()) {
			throw new STPException(STPException.EXCEPTION_CODE_MESSAGE_EMPTY_TYPE);
		}
	}
	
	private static void validatePayload(final Payload payload) throws STPException {
		if (payload == null) {
			throw new STPException(STPException.EXCEPTION_CODE_OBJECT_NULL_PAYLOAD);
		}
		
		if (payload.getContent() == null) {
			throw new STPException(STPException.EXCEPTION_CODE_PAYLOAD_NULL_CONTENT);
		}
		
		if (payload.getPosition() < 0) {
			throw new STPException(STPException.EXCEPTION_CODE_PAYLOAD_INVALID_POSITION);
		}
		
		if (payload.getLength() < 0 || payload.getLength() > PAYLOAD_MAX_SIZE || payload.getTotalLength() > PAYLOAD_MAX_SIZE) {
			throw new STPException(STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH);
		}
		
		if (payload.getLength() != payload.getContent().length) {
			throw new STPException(STPException.EXCEPTION_CODE_PAYLOAD_INVALID_LENGTH_MATCH);
		}
		
		if (payload.getPosition() + payload.getLength() > payload.getTotalLength()) {
			throw new STPException(STPException.EXCEPTION_CODE_PAYLOAD_OVERLOADED);
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
		
		if (headerPieces.length != piecesQuantity || piecesQuantity != STP_HEADER_PIECES_LENGTH) {
			throw new STPException(STPException.EXCEPTION_CODE_HEADER_INVALID_FORMAT);
		}
	}
}
