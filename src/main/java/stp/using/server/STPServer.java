/**
 *
 * @author daviarimateia93
 * 
 * Simple Transfer Protocol - STP
 * 
 * MSG FORMAT:
 *              MSG TYPE 0123456789012345678901234567890123456789 0 405 405\r\n
 *              ...
 *              ...
 *              ...
 *              END\r\n
 * 
 * Terminal test:   (each enter is equivalent to \r\n on terminal)
 *                  telnet localhost 8888
 *                  MSG REPEATER 012345678901234567890123456789012345 0 3 3(press enter)
 *                  A(press enter)
 *                  END(press enter)
 * 
 * Created by:  Davi de Sousa Arimateia
 *              daviarimateia93@gmail.com
 */

package stp.using.server;

import stp.gateway.Server;
import stp.parser.ParserManager;
import stp.system.STPException;
import stp.system.STPLogger;

public class STPServer {
	
	public static final int DEFAULT_PORT = 8888;
	
	public static void main(final String[] args) {
		int port = DEFAULT_PORT;
		
		if (args != null) {
			if (args.length == 1) {
				port = Integer.valueOf(args[0]);
			}
		}
		
		STPLogger.welcomeSTP();
		
		try {
			final Server server = new Server();
			
			ParserManager.getInstance().add(new ServerRepeaterParser());
			ParserManager.getInstance().add(new ServerCalculatorParser());
			
			server.start(port);
		} catch (final STPException exception) {
			STPLogger.exception(exception);
		}
	}
}
