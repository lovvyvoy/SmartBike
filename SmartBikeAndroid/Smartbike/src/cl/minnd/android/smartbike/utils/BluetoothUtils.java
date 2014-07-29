package cl.minnd.android.smartbike.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import cl.minnd.android.smartbike.exceptions.ConnectionLostException;
import cl.minnd.android.smartbike.utils.BluetoothUtils.BluetoothConnectionListener.Status;

/**
 * Singleton class to manage Bluetooth connections
 * 
 * @author Alberto Maluje
 * 
 */
public class BluetoothUtils {
	private static final String TAG = "BluetoothUtils";
	public static final int BLUETOOTH_REQUEST_CODE = 1234;

	public static BluetoothUtils instance;
	private Context context;

	private String name_ = "";
	private String address_ = "";
	private BluetoothDevice mmDevice;
	private BluetoothSocket mmSocket;
	private OutputStream mmOutputStream;
	private InputStream mmInputStream;
	private Thread workerThread;
	private byte[] readBuffer;
	private int readBufferPosition;
	private volatile boolean disconnect_;

	private BluetoothConnectionListener bluetoothListener;

	public static BluetoothUtils getInstance() {
		return instance == null ? new BluetoothUtils() : instance;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public interface BluetoothConnectionListener {
		public enum Status {
			CONNECTING, CONNECTED, DISCONNECTED, FAILED, LISTENING, DATA_SENT;
		}

		public void onBluetoothStatusChanged(Status newstatus);

		public void onRead(String data);
	}

	public void setBluetoothConnectionListener(BluetoothConnectionListener l) {
		this.bluetoothListener = l;
	}

	/**
	 * Searches for a given Bluetooth connection
	 * 
	 * @param name
	 *            : the Bluetooth device to search
	 * @return true if it has already being paired, false otherwise
	 */
	@SuppressLint("NewApi")
	public void findBluetooth(String name) throws IOException,
			ConnectionLostException {
		changeListenerStatus(Status.CONNECTING);

		BluetoothAdapter mBluetoothAdapter;

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		} else {
			final BluetoothManager bluetoothManager = (BluetoothManager) context
					.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}

		if (mBluetoothAdapter == null) {
			changeListenerStatus(Status.FAILED);
			throw new NullPointerException(
					"Bluetooth is not supported on this hardware platform");
		}

		// if the user hasn't enabled Bluetooth in their device, we prompt a
		// dialog
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBluetooth = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) context).startActivityForResult(enableBluetooth,
					BLUETOOTH_REQUEST_CODE);
		} else {
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();
			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					if (device.getName().equals(name)) {
						mmDevice = device;

						name_ = mmDevice.getName();
						address_ = mmDevice.getAddress();

						changeListenerStatus(Status.CONNECTING);

						openBluetooth();
						return;
					}
				}
			}
			changeListenerStatus(Status.FAILED);
		}
	}

	private void changeListenerStatus(Status s) {
		if (bluetoothListener != null)
			bluetoothListener.onBluetoothStatusChanged(s);
	}

	public void openBluetooth() throws IOException, ConnectionLostException {
		// Standard Standard ID
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

		synchronized (this) {
			if (disconnect_) {
				changeListenerStatus(Status.FAILED);
				throw new ConnectionLostException();
			}
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
					// We're trying to create an insecure socket, which is only
					// supported in API 10 and up. Otherwise, we try a secure
					// socket which is in API 7 and up.
					mmSocket = mmDevice
							.createInsecureRfcommSocketToServiceRecord(uuid);
				} else {
					mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
				}
			} catch (IOException e) {
				changeListenerStatus(Status.FAILED);
				throw new ConnectionLostException(e);
			}
		}
		// keep trying to connect as long as we're not aborting
		while (true) {
			try {
				Log.v(TAG, "Attempting to connect to Bluetooth device: "
						+ name_);

				// here we link the user with the device
				mmOutputStream = mmSocket.getOutputStream();
				mmInputStream = mmSocket.getInputStream();

				mmSocket.connect();

				Log.v(TAG, "Established connection to device " + name_
						+ " address: " + address_);

				changeListenerStatus(Status.CONNECTED);

				break; // if we got here, we're connected
			} catch (Exception e) {
				e.printStackTrace();
				if (disconnect_) {
					changeListenerStatus(Status.FAILED);
					throw new ConnectionLostException(e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					changeListenerStatus(Status.FAILED);
				}
			}
		}
		// Success! Wrap the streams with a properly sized buffers.
		// mmInputStream = new FixedReadBufferedInputStream(mmInputStream, 64);
		mmOutputStream = new BufferedOutputStream(mmOutputStream, 1024);

		beginListenForData();
	}

	private void beginListenForData() {
		// changeListenerStatus(Status.LISTENING);
		Log.v(TAG, "Start listening");

		final Handler handler = new Handler();
		// This is the ASCII code for a newline character
		final byte delimiter = 10;

		disconnect_ = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];

		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted() && !disconnect_) {
					try {
						int bytesAvailable = mmInputStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);

							for (int i = 0; i < bytesAvailable; i++) {

								byte b = packetBytes[i];
								if (b == delimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0,
											encodedBytes, 0,
											encodedBytes.length);
									final String data = new String(
											encodedBytes, "US-ASCII")
											.replaceAll("\n", "");

									readBufferPosition = 0;

									handler.post(new Runnable() {
										@Override
										public void run() {
											if (bluetoothListener != null)
												bluetoothListener.onRead(data);
										}
									});
								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					} catch (IOException ex) {
						disconnect_ = true;
					}
				}
			}
		});
		workerThread.start();
	}

	public void sendData(String message) throws IOException {
		message += "\n";

		if (mmOutputStream != null) {
			Log.v(TAG, "Sending: " + message);
			mmOutputStream.write(message.getBytes());
			mmOutputStream.flush();
			if (bluetoothListener != null)
				bluetoothListener.onBluetoothStatusChanged(Status.DATA_SENT);
		}
	}

	public synchronized void disconnect() {
		if (disconnect_) {
			changeListenerStatus(Status.DISCONNECTED);
			return;
		}
		Log.v(TAG, "Client initiated disconnect");
		disconnect_ = true;
		if (mmSocket != null) {
			try {
				mmOutputStream.close();
				mmInputStream.close();
				mmSocket.close();
				changeListenerStatus(Status.DISCONNECTED);
			} catch (IOException e) {
			}
		}
	}

}
