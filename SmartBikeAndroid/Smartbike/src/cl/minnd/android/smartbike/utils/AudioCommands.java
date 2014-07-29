package cl.minnd.android.smartbike.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.view.KeyEvent;

public class AudioCommands {

	private Context context = null;
	private AudioManager manager = null;

	public AudioCommands(Context c) {
		this.context = c;
		// initialize audio manager
		manager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * If the music is not playing, will play it
	 */
	public void playMusic() {
		Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));

		context.sendOrderedBroadcast(i, null);

		i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));

		context.sendOrderedBroadcast(i, null);
	}

	/**
	 * If the music is playing, we stop it
	 */
	public void stopMusic() {
		Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_MEDIA_STOP));

		context.sendOrderedBroadcast(i, null);

		i = new Intent(Intent.ACTION_MEDIA_BUTTON);
		i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_MEDIA_STOP));

		context.sendOrderedBroadcast(i, null);

	}

	/**
	 * A method to determine if the default music player is currently playing or
	 * not
	 * 
	 * @return true if the music is playing, false otherwise
	 */
	public boolean getMusicStatus() {
		return manager.isMusicActive();
	}

	/**
	 * If the music is playing, it will pause it. If the music is paused, it
	 * will play it
	 */
	public void toggleMusic() {
		Intent i = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
		if (manager.isMusicActive()) {
			i.putExtra("command", "pause");
		} else {
			i.putExtra("command", "play");
		}
		context.sendBroadcast(i);
	}

	/**
	 * Adjusts the music volume
	 * 
	 * @param up
	 *            true if the user wants to raise the volume, false to lower the
	 *            music's volume
	 */
	public void adjustVolume(boolean up) {
		if (up) {
			manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
		} else {
			manager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
		}
	}

	/**
	 * Gets the current media volume
	 * 
	 * @return the current media volume
	 */
	public int getCurrentMediaVolume() {
		return manager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * Changes the song played on the default player
	 * 
	 * @param next
	 *            true if the user wants to listen to the next track, false if
	 *            the user wants to listen to the previous track
	 */
	public void changeMusic(boolean next) {

		if (manager.isMusicActive()) {
			Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
			if (next) {
				// next song
				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
				context.sendOrderedBroadcast(i, null);

				i = new Intent(Intent.ACTION_MEDIA_BUTTON);
				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
						KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
				context.sendOrderedBroadcast(i, null);
			} else {
				// previous song
				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
				context.sendOrderedBroadcast(i, null);

				i = new Intent(Intent.ACTION_MEDIA_BUTTON);
				i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
						KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
				context.sendOrderedBroadcast(i, null);
			}
		} else {
			playMusic();
		}
	}

}