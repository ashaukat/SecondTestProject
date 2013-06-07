package com.mypackage.audio_record_test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AudioRecordTest extends Activity {

	MediaRecorder recorder;
	MediaPlayer mplayer;
	File audiofile = null;
	String mFileName;
	Button startButton, stopButton, startPlay, saveEditAudio, playEditAudio,
			forward, backward;
	File sampleDir;
	FileInputStream is;
	ContentValues values;
	int currentSongIndex = 0, currentPosition;

	public void startRecording() throws IOException {
		sampleDir = Environment.getExternalStorageDirectory();
		try {
			audiofile = File.createTempFile("MysoundFile", ".3gp", sampleDir);
		} catch (IOException e) {
			Log.e("RecordTesting", "sdcard access error");
			return;
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audiofile.getAbsolutePath());
		recorder.prepare();
		recorder.start();
	}

	public void startRecordingForEidtion() throws IOException {
		sampleDir = Environment.getExternalStorageDirectory();
		try {
			audiofile = File.createTempFile("EditedFile", ".3gp", sampleDir);
		} catch (IOException e) {
			Log.e("RecordTesting", "sdcard access error");
			return;
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audiofile.getAbsolutePath());
		recorder.prepare();
		recorder.start();
	}

	public void addEditedfileInLibrary() {
		values = new ContentValues(4);
		long current = System.currentTimeMillis();
		values.put(MediaStore.Audio.Media.TITLE, "" + audiofile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
		values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
		Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
	}

	public void stopRecording(Button b) {
		recorder.stop();
		recorder.release();
		addRecordingToMediaLibrary();
	}

	protected void addRecordingToMediaLibrary() {
		values = new ContentValues(4);
		long current = System.currentTimeMillis();
		values.put(MediaStore.Audio.Media.TITLE, "" + audiofile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
		values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
		Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
	}

	private void startPlaying() throws IOException {
		is = new FileInputStream(audiofile);
		mplayer = new MediaPlayer();

		mplayer.reset();
		try {
			mplayer.setDataSource(is.getFD());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mplayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mplayer.start();
	}

	private void stopPlaying() {
		mplayer.release();
		mplayer = null;
	}

	public void playEditedAudio() {
		try {
			is = new FileInputStream(audiofile);
			mplayer = new MediaPlayer();
			mplayer.reset();
			try {
				mplayer.setDataSource(is.getFD());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mplayer.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mplayer.start();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void forward_mPlayer() {

		currentPosition = mplayer.getCurrentPosition();
		if (currentPosition + 5000 < mplayer.getDuration()) {
			mplayer.seekTo(currentPosition + 5000);
			Toast.makeText(AudioRecordTest.this, "Media player moving forward",
					1000).show();
		} else {

			Toast.makeText(AudioRecordTest.this, "media player ending", 1000)
					.show();
		}

	}

	public void backward_mPlayer() {

		currentPosition = mplayer.getCurrentPosition();
		if (currentPosition - 5000 > 0) {
			mplayer.seekTo(currentPosition - 5000);
			Toast.makeText(AudioRecordTest.this,
					"Media player moving backward", 1000).show();
		} else {
			Toast.makeText(AudioRecordTest.this, "Media player starting", 1000)
					.show();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startButton = (Button) findViewById(R.id.start);
		stopButton = (Button) findViewById(R.id.stop);
		startPlay = (Button) findViewById(R.id.start_play);
		saveEditAudio = (Button) findViewById(R.id.save_edited_audio);
		playEditAudio = (Button) findViewById(R.id.play_edited_audio);
		forward = (Button) findViewById(R.id.forward);
		backward = (Button) findViewById(R.id.backward);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		startPlay.setEnabled(false);
		saveEditAudio.setEnabled(false);
		playEditAudio.setEnabled(false);
		forward.setEnabled(false);
		backward.setEnabled(false);

		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				startPlay.setEnabled(false);
				saveEditAudio.setEnabled(false);
				playEditAudio.setEnabled(false);
				try {
					startRecording();
				} catch (IOException e) {
					Log.e("AudioRecordingTesting", "prepare() failed");
				}

			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				startPlay.setEnabled(true);
				saveEditAudio.setEnabled(false);
				playEditAudio.setEnabled(false);
				stopRecording(stopButton);
				addRecordingToMediaLibrary();
				recorder.release();
			}
		});
		startPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				startPlay.setEnabled(false);
				saveEditAudio.setEnabled(false);
				playEditAudio.setEnabled(false);
				forward.setEnabled(true);
				backward.setEnabled(true);
				try {
					startPlaying();
				} catch (IOException e) {

					e.printStackTrace();
				}
				/*
				 * try {
				 * 
				 * startRecordingForEidtion(); } catch (IOException e) {
				 * 
				 * e.printStackTrace(); }
				 */

			}
		});
		saveEditAudio.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				startPlay.setEnabled(false);
				saveEditAudio.setEnabled(false);
				playEditAudio.setEnabled(true);
				addEditedfileInLibrary();
				recorder.release();

			}
		});
		playEditAudio.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				startPlay.setEnabled(false);
				saveEditAudio.setEnabled(false);
				playEditAudio.setEnabled(false);
				playEditedAudio();
			}
		});
		forward.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mplayer.isPlaying()) {
					forward_mPlayer();
				} else {
					Toast.makeText(AudioRecordTest.this,
							"media player is not playing", 1000).show();
				}

			}
		});
		backward.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mplayer.isPlaying()) {
					backward_mPlayer();
				} else {
					Toast.makeText(AudioRecordTest.this,
							"media player is not playing", 1000).show();
				}

			}
		});

	}
}