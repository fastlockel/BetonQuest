/**
 * 
 */
package pl.betoncraft.betonquest.editor;

import java.util.Map;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * @author co0sh
 *
 */
public class Editor extends NanoHTTPD {

	private static int port = getPortFromConfig();

	public Editor() throws Exception {
		super(port);
	}

	public int getPort() {
		return port;
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();
		System.out.println(method + " '" + uri + "' ");
		String msg = "<html><body><h1>Hello server</h1>\n";
		Map<String, String> parms = session.getParms();
		if (parms.get("username") == null)
			msg += "<form action='?' method='get'>\n" + " <p>Your name: <input type='text' name='username'></p>\n"
					+ "</form>\n";
		else
			msg += "<p>Hello, " + parms.get("username") + "!</p>";
		msg += "</body></html>\n";
		return new NanoHTTPD.Response(msg);
	}

	private static int getPortFromConfig() {
		int port = 8124;
		try {
			port = Integer.valueOf(BetonQuest.getInstance().getConfig().getString("editor.port"));
		} catch (NumberFormatException e) {
			BetonQuest.getInstance().getConfig().set("editor.port", "8124");
			BetonQuest.getInstance().saveConfig();
		}
		return port;
	}

}
