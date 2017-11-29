package stp.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import stp.gateway.Peer;
import stp.gateway.Server;
import stp.message.Message;
import stp.system.STPException;
import stp.system.STPObject;

public class ParserManager extends STPObject {
	
	private static ParserManager instance;
	protected final Map<String, Parser> parsers = new Hashtable<>();
	protected final List<ParserPeerKnown> parsersPeerKnown = new ArrayList<>();
	protected final List<ParserServerKnown> parsersServerKnown = new ArrayList<>();
	
	protected ParserManager() {
		
	}
	
	public static ParserManager getInstance() {
		if (instance == null) {
			instance = new ParserManager();
		}
		
		return instance;
	}
	
	public Map<String, Parser> getAll() {
		return parsers;
	}
	
	public boolean add(final Parser parser) {
		if (!contains(parser.getType())) {
			parsers.put(parser.getType(), parser);
			
			if (parser instanceof ParserPeerKnown) {
				parsersPeerKnown.add((ParserPeerKnown) parser);
			}
			
			if (parser instanceof ParserServerKnown) {
				parsersServerKnown.add((ParserServerKnown) parser);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void remove(final Parser parser) {
		final Parser removedParser = parsers.remove(parser.getType());
		
		if (removedParser != null) {
			if (removedParser instanceof ParserPeerKnown) {
				parsersPeerKnown.remove(removedParser);
			}
			
			if (removedParser instanceof ParserServerKnown) {
				parsersServerKnown.remove(removedParser);
			}
		}
	}
	
	public void removeAll() {
		parsers.clear();
		parsersPeerKnown.clear();
		parsersServerKnown.clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Parser> T get(final Class<T> parserClass) {
		for (final Map.Entry<String, Parser> entry : parsers.entrySet()) {
			if (entry.getValue().getClass().equals(parserClass)) {
				return (T) entry.getValue();
			}
		}
		
		return null;
	}
	
	public Parser get(final String type) {
		return parsers.get(type);
	}
	
	public boolean contains(final String type) {
		return parsers.containsKey(type);
	}
	
	public void onPeerStart(final Peer peer) {
		for (ParserPeerKnown parserPeerKnown : parsersPeerKnown) {
			parserPeerKnown.onPeerStart(peer);
		}
	}
	
	public void onPeerEnd(final Peer peer) {
		for (ParserPeerKnown parserPeerKnown : parsersPeerKnown) {
			parserPeerKnown.onPeerEnd(peer);
		}
	}
	
	public void onServerStart(final Server server) {
		for (ParserServerKnown parserServerKnown : parsersServerKnown) {
			parserServerKnown.onServerStart(server);
		}
	}
	
	public void onServerEnd(final Server server) {
		for (ParserServerKnown parserServerKnown : parsersServerKnown) {
			parserServerKnown.onServerEnd(server);
		}
	}
	
	public void onPeerStart(final Server server, final Peer peer) {
		for (ParserServerKnown parserServerKnown : parsersServerKnown) {
			parserServerKnown.onPeerStart(server, peer);
		}
	}
	
	public void onPeerEnd(final Server server, final Peer peer) {
		for (ParserServerKnown parserServerKnown : parsersServerKnown) {
			parserServerKnown.onPeerEnd(server, peer);
		}
	}
	
	public void read(final Peer peer, final Message message) throws STPException {
		if (contains(message.getType())) {
			get(message.getType()).read(peer, message);
		} else {
			throw new STPException(STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE, message.getType());
		}
	}
	
	public void written(final Peer peer, final Message message) throws STPException {
		if (contains(message.getType())) {
			get(message.getType()).written(peer, message);
		} else {
			throw new STPException(STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE, message.getType());
		}
	}
	
	public Message prepareReading(final Message message) throws STPException {
		if (contains(message.getType())) {
			return get(message.getType()).willRead(message);
		} else {
			throw new STPException(STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE, message.getType());
		}
	}
	
	public Message prepareWriting(final Message message) throws STPException {
		if (contains(message.getType())) {
			return get(message.getType()).willWrite(message);
		} else {
			throw new STPException(STPException.EXCEPTION_CODE_PARSER_UNKNOWN_TYPE, message.getType());
		}
	}
}
