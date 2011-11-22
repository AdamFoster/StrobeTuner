package net.adamfoster.android.strobe;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * A stroboscopic tuner
 * 
 * For free version, requires: admob jar AdActivity
 * Internet/Access_network_state permissions AdView
 * 
 * @author Adam Foster
 * 
 */
public class StrobeTunerActivity extends Activity implements OnClickListener, TextWatcher
{
	public static String TAG = "StrobeTuner";

	private Recorder mRecorder;
	private StrobeSurfaceView mStrobeView;
	private int mNote;
	private boolean mPlus;
	private GestureDetector mGestureDetector;

	private static final int MENU_8VA = 0;
	private static final int MENU_8VB = 1;
	// private static final int MENU_FREQUENCY = 2;
	private static final int MENU_SETTINGS = 3;
	private static final int MENU_HELP = 4;
	private static final int MENU_BUY = 5;
	private static final int MENU_CALIBRATE = 7;

	private static final int DIALOG_HELP = 0;
	private static final int DIALOG_MORE_HELP = 1;
	private static final int DIALOG_SETTINGS_HELP = 2;
	private static final int DIALOG_PLUS_HELP = 3;

	private static final int ACTIVITY_PREF = 0;
	
	public static final String AD_UNIT_ID = "a14e5c6a72ea38d";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, "Started Strobe");

		//bodgey copy protection, but whatever
		mPlus = (getPackageManager().checkSignatures(getPackageName(), C.KEY_PACKAGE) == PackageManager.SIGNATURE_MATCH);
		Log.i(TAG, "Strobe unlock key " + (mPlus ? "found" : "NOT found"));

		mStrobeView = (StrobeSurfaceView) findViewById(R.id.surfaceView);
		mRecorder = mStrobeView.getRecorder();
		if (mPlus)
		{
			mGestureDetector = new GestureDetector(this, new MyGestureDetector());
		}

		mNote = R.id.buttonA;
		
		// hide calibration
		LinearLayout calibrationLayout = (LinearLayout) findViewById(R.id.layoutCalibrate);
		calibrationLayout.setVisibility(View.GONE);
		findViewById(R.id.textCalibrate).setVisibility(View.GONE);

		// preferences
		SharedPreferences settings = getSharedPreferences(
				C.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		mRecorder.setA4Freq(settings.getFloat(C.PREF_A4_FREQ, C.DEFAULT_A4_FREQ));
		mRecorder.setFlashThreshold(settings.getInt(C.PREF_FLASH_THRESHOLD, C.DEFAULT_FLASH_THRESHOLD));
		mRecorder.setOpenProportionPercent(settings.getInt(C.PREF_MASK_OPEN, C.DEFAULT_MASK_OPEN));
		mRecorder.setCalibrationFactor(settings.getInt(C.PREF_CALIBRATION_FACTOR, C.DEFAULT_CALIBRATION_FACTOR));
		mRecorder.setSaveNote(settings.getBoolean(C.PREF_SAVE_NOTE, C.DEFAULT_SAVE_NOTE));
		mRecorder.setSaveOctave(settings.getBoolean(C.PREF_SAVE_OCTAVE, C.DEFAULT_SAVE_OCTAVE));
		
		if (mRecorder.getSaveNote())
		{
			mRecorder.setNote(settings.getInt(C.PREF_NOTE, C.DEFAULT_NOTE));
		}
		else if (mPlus)
		{
			mRecorder.setAutoDetect(true);
		}
		if (mRecorder.getSaveOctave())
		{
			mRecorder.setOctave(settings.getInt(C.PREF_OCTAVE, C.DEFAULT_OCTAVE));
		}
		if (settings.getBoolean(C.PREF_FIRST_RUN, true))
		{
			// do first run stuff
			Log.i(TAG, "Welcome to Strobe tuner");
			showDialog(DIALOG_HELP);
		}

		// mRecorder.setOffA4();

		if (! mPlus)
		{
			AdView adView = (AdView) findViewById(R.id.adView);
			adView.loadAd(new AdRequest());
			
		}
		findViewById(R.id.buttonAuto).setVisibility(View.GONE);
		
		
		// set calibration
		EditText calibration = (EditText) findViewById(R.id.editCalibrate);
		calibration.setText("" + mRecorder.getCalibrationFactor());
		calibration.addTextChangedListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}
	
	public void noteUpdated()
	{
		switch (mNote)
		{
			case R.id.buttonC:
				mRecorder.setNote(Recorder.NOTE_C);
				break;
			case R.id.buttonCS:
				mRecorder.setNote(Recorder.NOTE_C_SHARP);
				break;
			case R.id.buttonD:
				mRecorder.setNote(Recorder.NOTE_D);
				break;
			case R.id.buttonDS:
				mRecorder.setNote(Recorder.NOTE_D_SHARP);
				break;
			case R.id.buttonE:
				mRecorder.setNote(Recorder.NOTE_E);
				break;
			case R.id.buttonF:
				mRecorder.setNote(Recorder.NOTE_F);
				break;
			case R.id.buttonFS:
				mRecorder.setNote(Recorder.NOTE_F_SHARP);
				break;
			case R.id.buttonG:
				mRecorder.setNote(Recorder.NOTE_G);
				break;
			case R.id.buttonGS:
				mRecorder.setNote(Recorder.NOTE_G_SHARP);
				break;
			case R.id.buttonA:
				mRecorder.setNote(Recorder.NOTE_A);
				break;
			case R.id.buttonAS:
				mRecorder.setNote(Recorder.NOTE_A_SHARP);
				break;
			case R.id.buttonB:
				mRecorder.setNote(Recorder.NOTE_B);
				break;

			default:
				Log.e(TAG, "Unrecognised note");
		}
		//mRecorder.setOffA4();
	}
	
	private void adjustCalibration(int adjustment)
	{
		mRecorder.adjustCalibrationFactor(adjustment);
		EditText calibration = (EditText) findViewById(R.id.editCalibrate);
		calibration.setText("" + mRecorder.getCalibrationFactor());
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.buttonC:
			case R.id.buttonCS:
			case R.id.buttonD:
			case R.id.buttonDS:
			case R.id.buttonE:
			case R.id.buttonF:
			case R.id.buttonFS:
			case R.id.buttonG:
			case R.id.buttonGS:
			case R.id.buttonA:
			case R.id.buttonAS:
			case R.id.buttonB:
				mNote = v.getId();
				noteUpdated();
				break;
				
			case R.id.buttonCalibrateDownBig:
				adjustCalibration(-10);
				break;
			case R.id.buttonCalibrateDownSmall:
				adjustCalibration(-1);
				break;
			case R.id.buttonCalibrateUpBig:
				adjustCalibration(10);
				break;
			case R.id.buttonCalibrateUpSmall:
				adjustCalibration(1);
				break;
			
			case R.id.buttonAuto:
				mRecorder.setAutoDetect(true);
				break;
				
			default:
				Log.e(TAG, "Unrecognised click");
		}
	}
	
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		if (findViewById(R.id.layoutCalibrate).getVisibility() == View.GONE)
		{
			finish();
		}
		else
		{
			stopCalibration();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		SharedPreferences settings = getSharedPreferences(C.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor e = settings.edit();

		if (mRecorder.getSaveNote() && !mRecorder.getAutoDetect())
		{
			e.putInt(C.PREF_NOTE, mRecorder.getNote());
		}
		if (mRecorder.getSaveOctave() && !mRecorder.getAutoDetect())
		{
			e.putInt(C.PREF_OCTAVE, mRecorder.getOctave());
		}
		e.putInt(C.PREF_CALIBRATION_FACTOR, mRecorder.getCalibrationFactor());
		e.putBoolean(C.PREF_FIRST_RUN, false);
		e.commit();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_8VB, 0, R.string.menu_8vb);
		menu.add(0, MENU_8VA, 0, R.string.menu_8va);
		menu.add(0, MENU_CALIBRATE, 0, R.string.menu_calibrate);
		menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings);
		menu.add(0, MENU_HELP, 0, R.string.menu_help);
		if (! mPlus)
		{
			menu.add(0, MENU_BUY, 0, R.string.menu_buy);
		}
		else
		{
		}
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		
		//switch calibration menu item as required
		MenuItem mi = menu.findItem(MENU_CALIBRATE);
		if (findViewById(R.id.layoutCalibrate).getVisibility() == View.GONE)
		{
			mi.setTitle(R.string.menu_calibrate);
		}
		else
		{
			mi.setTitle(R.string.menu_un_calibrate);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);

		switch (item.getItemId())
		{
			case MENU_8VA:
				mRecorder.upOctave();
				return true;
				
			case MENU_8VB:
				mRecorder.downOctave();
				return true;
				
			case MENU_SETTINGS:
				stopCalibration();
				
				Intent i = new Intent(this, StrobePreferences.class);
				i.putExtra(C.PREF_A4_FREQ, mRecorder.getA4Freq());
				i.putExtra(C.PREF_FLASH_THRESHOLD, mRecorder.getFlashThreshold());
				i.putExtra(C.PREF_CALIBRATION_FACTOR, mRecorder.getCalibrationFactor());
				i.putExtra(C.PREF_MASK_OPEN, mRecorder.getOpenProportionPercent());
				i.putExtra(C.PREF_SAVE_NOTE, mRecorder.getSaveNote());
				i.putExtra(C.PREF_SAVE_OCTAVE, mRecorder.getSaveOctave());
				i.putExtra(C.PREF_PLUS, mPlus);
				startActivityForResult(i, ACTIVITY_PREF);
				return true;
				
			case MENU_HELP:
				showDialog(DIALOG_HELP);
				return true;
				
			case MENU_BUY:
				Log.d(TAG, "Buy clicked");
				
				Toast.makeText(this, "Please restart the Strobe Tuner app after purchasing the key", Toast.LENGTH_SHORT).show(); 
						
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + C.KEY_PACKAGE));
				startActivity(intent);
				
				return true;
				
			case MENU_CALIBRATE:
				LinearLayout calibrationLayout = (LinearLayout) findViewById(R.id.layoutCalibrate);
				if (calibrationLayout.getVisibility() == View.GONE)
				{
					startCalibration();
				}
				else
				{
					stopCalibration();
				}
				
				return true;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch (id)
		{
			case DIALOG_HELP:
				dialog = createHelpDialog();
				break;
			case DIALOG_MORE_HELP:
				dialog = createMoreHelpDialog();
				break;
			case DIALOG_SETTINGS_HELP:
				dialog = createSettingsHelpDialog();
				break;
			case DIALOG_PLUS_HELP:
				dialog = createPlusDialog();
				break;
			default:
				dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_CANCELED)
		{
			return;
		}

		Bundle extras = intent.getExtras();
		switch (requestCode)
		{
			case ACTIVITY_PREF:
				if (extras.getBoolean(C.PREF_SAVE, false)) // only save if the "OK" button was pressed
				{
					mRecorder.setA4Freq(extras.getDouble(C.PREF_A4_FREQ, mRecorder.getA4Freq()));
					mRecorder.setFlashThreshold(extras.getInt(C.PREF_FLASH_THRESHOLD, mRecorder.getFlashThreshold()));
					mRecorder.setOpenProportionPercent(extras.getInt(C.PREF_MASK_OPEN, mRecorder.getOpenProportionPercent()));
					mRecorder.setSaveNote(extras.getBoolean(C.PREF_SAVE_NOTE, mRecorder.getSaveNote()));
					mRecorder.setSaveOctave(extras.getBoolean(C.PREF_SAVE_OCTAVE, mRecorder.getSaveOctave()));
					mRecorder.setCalibrationFactor(extras.getInt(C.PREF_CALIBRATION_FACTOR, mRecorder.getCalibrationFactor()));

					//mRecorder.setOffA4();

					SharedPreferences settings = getSharedPreferences(C.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
					Editor e = settings.edit();
					e.putFloat(C.PREF_A4_FREQ, (float) mRecorder.getA4Freq());
					e.putInt(C.PREF_FLASH_THRESHOLD, mRecorder.getFlashThreshold());
					e.putInt(C.PREF_MASK_OPEN, mRecorder.getOpenProportionPercent());
					e.putInt(C.PREF_CALIBRATION_FACTOR, mRecorder.getCalibrationFactor());
					e.putBoolean(C.PREF_SAVE_NOTE, mRecorder.getSaveNote());
					e.putBoolean(C.PREF_SAVE_OCTAVE, mRecorder.getSaveOctave());
					e.commit();
					
					EditText calibration = (EditText) findViewById(R.id.editCalibrate);
					calibration.setText("" + mRecorder.getCalibrationFactor());
				}

			default:
				break;
		}
	}

	private Dialog createHelpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		try
		{
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			builder.setTitle("Strobe Tuner v" + versionName);
		}
		catch (Exception e)
		{
			builder.setTitle("Strobe Tuner Help");
		}
		builder.setCancelable(true);
		builder.setPositiveButton("Close",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
					}
				});
		builder.setNeutralButton("More", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				StrobeTunerActivity.this.showDialog(DIALOG_MORE_HELP);
				dialog.dismiss();
			}
		});
		builder.setMessage("Strobe tuners work slightly differently to other sorts of tuners. "
				+ "However, they are very powerful and with practice you can get more accurate results than " 
				+ "with a standard electronic tuner and even tune partials above the fundamental.\n\n"
				+ "To start, follow these easy steps:\n"
				+ "1. Select the note you want to tune using the buttons below the disc\n"
				+ "2. Play the note and observe the patterns created on the disc\n"
				+ "3. Tune your note by sharpening if the pattern rotates anti-clockwise and flattening if the pattern rotates clockwise\n"
				+ "4. Your note is in tune if the pattern does not rotate");
		return builder.create();
	}

	private Dialog createMoreHelpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Menu Help");
		builder.setCancelable(true);
		builder.setPositiveButton("Close",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
					}
				});
		builder.setNeutralButton("More", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				StrobeTunerActivity.this.showDialog(DIALOG_SETTINGS_HELP);
				dialog.dismiss();
			}
		});
		String message = "Use the menu key to view advanced options.\n\n"
				+ "8vb and 8va will lower or raise the octave detected. Most of the time you will not need to use "
				+ "these as strobe tuners can detect a wide range of frequencies, but it might be useful when tuning "
				+ "instruments at the exteme ends of the range (eg. Double Bass or Piccolo)\n"
				+ "\n"
				+ "Calibrate will put the screen in calibration mode. Here you can make fine adjustments to the "
				+ "pitch detected. Use it to calibrate the tuner to a set pitch. Press back or select calibrate again to return to normal mode.\n"
				+ "\n"
				+ "Buy will send you to the market to purchase the plus version. Thank you for your support.\n"
				+ "\n"
				+ "Settings will take you to a screen described on the next page\n";

		builder.setMessage(message);

		return builder.create();
	}
	
	private Dialog createSettingsHelpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Settings Help");
		builder.setCancelable(true);
		builder.setPositiveButton("Close",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
					}
				});
		builder.setNeutralButton("More", 
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						StrobeTunerActivity.this.showDialog(DIALOG_PLUS_HELP);
						dialog.dismiss();
					}
				});

		String message = "Settings will allow you to customise the application. Click on any label in the settings screen to get more help on that topic:\n\n"
				+ "A4 FREQUENCY\n(440 Hz by default)\n\n"
				+ "MASK APERTURE SIZE.\n(5%-55%, 45% by default)\nThis will determine how clear the patterns are "
				+ "and how succeptable they are to noise. Smaller apertures are more tolerant, but may not make as effective "
				+ "patterns. Larger values give more effective patterns, but may be swamped with noise. Choose a smaller value if the disc is appearing solid red.\n\n"
				+ "NOISE THRESHOLD.\ndetermines how much background noise is ignored. This is generally dependent on your phone and "
				+ "the environment. The default value is the typical background noise of a sedate but populated office\n\n"
				+ "CALIBRATION FACTOR.\nis used for calibrating the application to a specific note. It adjusts the app's internal tuning by 0.1 of a cent. This can also be changed from the Calibration menu item.\n\n"
				+ "SAVE NOTE and SAVE OCTAVE.\nwill remember the note and/or octave that was selected when the application restarts.\n\n"
				+ "Ok will save changes, Cancel or the back button will cancel changes and Defaults will reset to the application defaults (remember to click ok after setting defaults\n";
		
		builder.setMessage(message);

		return builder.create();
	}
	
	private Dialog createPlusDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Plus features");
		builder.setCancelable(true);
		builder.setPositiveButton("Close",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
					}
				});
		String message = "Purchasing the plus unlock key of this app:\n" +
				"\n" +
				"1. Removes all ads\n\n" +
				"2. Enables the 'Auto Detect' button. This will attempt to automatically detect the " +
				"the note being played and adjust the strobe tuner accordingly. Only the note is " +
				"detected, the tuning mechanism works the same in the regular strobe tuner. You " +
				"can still manually select the note if desired. When auto detection is enabled, " +
				"the word 'Auto' appears beneath the frequency.";
		if (! mPlus)
		{
			message = message
					+ "\n\nIf you like this app, please support me by upgrading to the plus version to remove the ads.";

			builder.setNeutralButton("Buy",
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							Log.d(TAG, "Buy clicked from help");
							
							Toast.makeText(StrobeTunerActivity.this, "Please restart the Strobe Tuner app after purchasing the key", Toast.LENGTH_SHORT).show(); 

							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id=" + C.KEY_PACKAGE));
							startActivity(intent);

							dialog.dismiss();
						}
					});
		}
		else
		{
			message = message
					+ "\n\nThank you for your support!";
		}
		builder.setMessage(message);

		return builder.create();
	}
	
	public void afterTextChanged(Editable s)
	{
		//Log.i(TAG, "Watched '" + s.toString() + "'");
		try
		{
			
			int value = Integer.parseInt(s.toString());
			mRecorder.setCalibrationFactor(value);
		}
		catch (Exception e) {}
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
	}
	
	private void startCalibration()
	{
		// switch to calibration mode
		// hide sharps & naturals
		findViewById(R.id.layoutSharps).setVisibility(View.GONE);
		findViewById(R.id.layoutNaturals).setVisibility(View.GONE);
		// show calibration
		findViewById(R.id.layoutCalibrate).setVisibility(View.VISIBLE);
		findViewById(R.id.textCalibrate).setVisibility(View.VISIBLE);
	}
	private void stopCalibration()
	{
		// switch to normal mode
		// hide calibration
		findViewById(R.id.layoutCalibrate).setVisibility(View.GONE);
		findViewById(R.id.textCalibrate).setVisibility(View.GONE);
		// show sharps & naturals
		findViewById(R.id.layoutSharps).setVisibility(View.VISIBLE);
		findViewById(R.id.layoutNaturals).setVisibility(View.VISIBLE);
	}
	
	class MyGestureDetector extends SimpleOnGestureListener
	{
		private static final float MIN_FLING_VEL = 300;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			if (Math.abs(velocityX) > Math.abs(velocityY))
			{
				// horizontal fling
				if (velocityX > MIN_FLING_VEL)
				{
					// L -> R
					mRecorder.prevNote();
					return true;
				}
				else if (velocityX < -MIN_FLING_VEL)
				{
					// R <- L
					mRecorder.nextNote();
					return true;
				}
			}
			else
			{
				// vertical fling
				if (velocityY > MIN_FLING_VEL)
				{
					// T -> B
					mRecorder.upOctave();
					return true;
				}
				else if (velocityY < -MIN_FLING_VEL)
				{
					// B -> T
					mRecorder.downOctave();
					return true;
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}