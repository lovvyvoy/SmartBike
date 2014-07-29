package cl.minnd.android.smartbike.exceptions;

/**
 * Thrown when the connection to the Arduino has been lost or disconnected.
 */
public class ConnectionLostException extends Exception {
	private static final long serialVersionUID = 7422862446246046772L;

	public ConnectionLostException(Exception e) {
		super(e);
	}

	public ConnectionLostException() {
		super("Connection lost");
	}
}