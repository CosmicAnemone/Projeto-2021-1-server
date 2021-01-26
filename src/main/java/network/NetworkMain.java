package network;

import ratpack.server.RatpackServer;
import utils.EnvUtils;

public class NetworkMain {
	private static RatpackServer server;
	
	public static void create() throws Exception {
		if(server == null){
			server = RatpackServer.start(s->s
					.serverConfig(server->server
							.port(EnvUtils.PORT))
					.handlers(chain->chain
							.get("registro", RequestHandler::handleGet)
							.post("registro", RequestHandler::handlePost))
			);
		}
	}
	
	public static void awaitTillClosed() throws Exception {
		server.stop();
	}
}
