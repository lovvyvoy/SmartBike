package cl.minnd.android.smartbike.fragments;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cl.minnd.android.smartbike.R;
import cl.minnd.android.smartbike.exceptions.ConnectionLostException;
import cl.minnd.android.smartbike.utils.AudioCommands;
import cl.minnd.android.smartbike.utils.BluetoothUtils;
import cl.minnd.android.smartbike.utils.BluetoothUtils.BluetoothConnectionListener;

public class PlaceholderFragment extends Fragment {

	private AudioCommands audioCommands;

	private Button button_connect, button_sendData;
	private EditText editText_sendData;
	private TextView textView_read;

	private Context context;
	private BluetoothUtils bu = BluetoothUtils.getInstance();

	private int lastState = TelephonyManager.CALL_STATE_IDLE;
	private Date callStartTime;
	private boolean isIncoming;
	private String savedNumber;

	public PlaceholderFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.context = activity;
		bu.setContext(context);
		audioCommands = new AudioCommands(context);

		// we hide the keyboard
		activity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// register PhoneStateListener
		PhoneStateListener callStateListener = new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				if (lastState == state) {
					// No change, debounce extras
					return;
				}

				String callState = "";

				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					isIncoming = true;
					callStartTime = new Date();
					savedNumber = incomingNumber;
					callState = textView_read.getText().toString() + "\n"
							+ savedNumber + " incoming calling";

					try {

						String name = getContactDisplayNameByNumber(savedNumber);

						if (name.equals(""))
							name = savedNumber.trim();

						bu.sendData("C" + name);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					// Transition of ringing->offhook are pickups of
					// incoming calls.
					// Nothing done on them
					if (lastState != TelephonyManager.CALL_STATE_RINGING) {
						isIncoming = false;
						callStartTime = new Date();

						callState = textView_read.getText().toString() + "\n"
								+ savedNumber + " outgoing calling";
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					// Went to idle- this is the end of a call. What type
					// depends on
					// previous state(s)
					if (lastState == TelephonyManager.CALL_STATE_RINGING) {
						// Ring but no pickup- a miss
						callState = textView_read.getText().toString() + "\n"
								+ savedNumber + " missed call";
					} else if (isIncoming) {
						callState = textView_read.getText().toString() + "\n"
								+ savedNumber + " incoming ended";
					} else {
						callState = textView_read.getText().toString() + "\n"
								+ savedNumber + " outgoing ended";
					}
					break;
				}

				Log.v("Fragment", "Bla: " + callState);
				textView_read.setText(callState);

				lastState = state;
			}
		};
		telephonyManager.listen(callStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	public String getContactDisplayNameByNumber(String number) {
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		String name = "";

		ContentResolver contentResolver = context.getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] {
				BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME },
				null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				name = contactLookup.getString(contactLookup
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}

		return name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

		button_connect = (Button) rootView.findViewById(R.id.button_connect);
		button_sendData = (Button) rootView.findViewById(R.id.button_sendData);
		textView_read = (TextView) rootView.findViewById(R.id.textView_read);
		editText_sendData = (EditText) rootView
				.findViewById(R.id.editText_sendData);

		button_sendData.setEnabled(false);
		editText_sendData.setEnabled(false);

		button_connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bu.setBluetoothConnectionListener(btListener);

				try {
					bu.findBluetooth("linvor");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConnectionLostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		button_sendData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					bu.sendData(editText_sendData.getText().toString());
					editText_sendData.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private BluetoothConnectionListener btListener = new BluetoothConnectionListener() {

		@Override
		public void onRead(String data) {
			// TODO Auto-generated method stub

			if (data.startsWith("M0")) {
				// music 0 command is next song
				audioCommands.changeMusic(true);
			} else if (data.startsWith("M1")) {
				// music 1 command is previous song
				audioCommands.changeMusic(false);
			} else if (data.startsWith("V0")) {
				// request for current volume status
				sendCurrentVolume();
			} else if (data.startsWith("V1")) {
				// volume up
				audioCommands.adjustVolume(true);
				// we send back the current volume
				sendCurrentVolume();
			} else if (data.startsWith("V2")) {
				// volume down
				audioCommands.adjustVolume(false);
				// we send back the current volume
				sendCurrentVolume();
			} else if (data.startsWith("C0")) {
				// accept incoming call
				textView_read.setText("Llamada contestada");

				Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
				context.sendOrderedBroadcast(i, "android.permission.CALL_PRIVILEGED");

			} else if (data.startsWith("C1")) {
				// reject incoming call
				textView_read.setText("Llamada rechazada");

				Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
				buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
				context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
			}

			String bla = textView_read.getText().toString() + "\n" + data;
			textView_read.setText(bla);
		}

		@Override
		public void onBluetoothStatusChanged(Status newstatus) {
			// TODO Auto-generated method stub
			// Log.v("Fragment", "status change: " + newstatus);
			if (newstatus == Status.CONNECTED) {
				try {
					bu.sendData("S0");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				button_connect.setEnabled(false);
				button_sendData.setEnabled(true);
				editText_sendData.setEnabled(true);
			} else if (newstatus == Status.DISCONNECTED) {
				button_connect.setEnabled(true);
				button_sendData.setEnabled(false);
				editText_sendData.setEnabled(false);
			}
		}
	};

	private void sendCurrentVolume() {
		try {
			bu.sendData("V"
					+ String.valueOf(audioCommands.getCurrentMediaVolume()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		bu.disconnect();

		super.onDestroy();
	}
}