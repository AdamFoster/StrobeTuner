package net.adamfoster.android.strobe;

import java.text.DecimalFormat;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;

public class StrobePreferences extends Activity implements OnClickListener
{
	private static String TAG = "StrobePreferences";
	
	private double mA4Freq;
	private int mFlashThreshold;
	private int mAperture;
	private int mCalibrationFactor;
	private boolean mSaveNote;
	private boolean mSaveOctave;
	private boolean mPlus;
	
	private DecimalFormat mDecimalFormat;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		mDecimalFormat = new DecimalFormat("0.0##");
		
		findViewById(R.id.scrollView).setScrollbarFadingEnabled(false);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mA4Freq = extras.getDouble(C.PREF_A4_FREQ, C.DEFAULT_A4_FREQ);
			mFlashThreshold = extras.getInt(C.PREF_FLASH_THRESHOLD, C.DEFAULT_FLASH_THRESHOLD);
			mCalibrationFactor = extras.getInt(C.PREF_CALIBRATION_FACTOR, C.DEFAULT_CALIBRATION_FACTOR);
			mAperture = extras.getInt(C.PREF_MASK_OPEN, C.DEFAULT_MASK_OPEN);
			mSaveNote = extras.getBoolean(C.PREF_SAVE_NOTE, C.DEFAULT_SAVE_NOTE);
			mSaveOctave = extras.getBoolean(C.PREF_SAVE_OCTAVE, C.DEFAULT_SAVE_OCTAVE);
			mPlus = extras.getBoolean(C.PREF_PLUS, false);
			
			if (! mPlus)
	        {
	        	AdView adView = (AdView) findViewById(R.id.adView);
	            adView.loadAd(new AdRequest());
	        }
			
			EditText a4View = (EditText) findViewById(R.id.prefA4value);
			a4View.setText("" + mDecimalFormat.format(mA4Freq));
			
			EditText flashView = (EditText) findViewById(R.id.prefNoiseValue);
			flashView.setText("" + mFlashThreshold);
			
			EditText apertureView = (EditText) findViewById(R.id.prefMaskValue);
			apertureView.setText("" + mAperture);
			
			EditText calibrationView = (EditText) findViewById(R.id.prefCalibrationValue);
			calibrationView.setText("" + mCalibrationFactor);
			
			CheckBox saveNoteBox = (CheckBox) findViewById(R.id.prefSaveNoteBox);
			saveNoteBox.setChecked(mSaveNote);

			CheckBox saveOctaveBox = (CheckBox) findViewById(R.id.prefSaveOctaveBox);
			saveOctaveBox.setChecked(mSaveOctave);
}
		else
		{
			Log.e(TAG, "No extras found!");
		}
	}

	public void onClick(View v)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		switch (v.getId())
		{
			case R.id.prefOk:
				if (! validate())
				{
					break;
				}
				
				bundle.putBoolean(C.PREF_SAVE, true);
				
				try
				{
					EditText a4View = (EditText) findViewById(R.id.prefA4value);
					double a4 = Double.parseDouble(a4View.getText().toString());
					bundle.putDouble(C.PREF_A4_FREQ, a4);
				}
				catch (Exception e)	{}
				
				try
				{
					EditText flashView = (EditText) findViewById(R.id.prefNoiseValue);
					int flash = Integer.parseInt(flashView.getText().toString());
					bundle.putInt(C.PREF_FLASH_THRESHOLD, flash);
				}
				catch (Exception e)	{}

				try
				{
					EditText apertureView = (EditText) findViewById(R.id.prefMaskValue);
					int aperture = Integer.parseInt(apertureView.getText().toString());
					bundle.putInt(C.PREF_MASK_OPEN, aperture);
				}
				catch (Exception e)	{}
				
				try
				{
					EditText calibrationView = (EditText) findViewById(R.id.prefCalibrationValue);
					int calibrationFactor = Integer.parseInt(calibrationView.getText().toString());
					bundle.putInt(C.PREF_CALIBRATION_FACTOR, calibrationFactor);
				}
				catch (Exception e)	{}
				
				try
				{
					CheckBox saveNoteBox = (CheckBox) findViewById(R.id.prefSaveNoteBox);
					bundle.putBoolean(C.PREF_SAVE_NOTE, saveNoteBox.isChecked());
				}
				catch (Exception e)	{}
					
				try
				{
					CheckBox saveOctaveBox = (CheckBox) findViewById(R.id.prefSaveOctaveBox);
					bundle.putBoolean(C.PREF_SAVE_OCTAVE, saveOctaveBox.isChecked());
				}
				catch (Exception e)	{}
				
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
				break;
				
			case R.id.prefCancel:
				bundle.putBoolean(C.PREF_SAVE, false);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
				break;
				
			case R.id.prefReset:
				EditText a4View = (EditText) findViewById(R.id.prefA4value);
				a4View.setText("" + mDecimalFormat.format(C.DEFAULT_A4_FREQ));
				
				EditText flashView = (EditText) findViewById(R.id.prefNoiseValue);
				flashView.setText("" + C.DEFAULT_FLASH_THRESHOLD);
				
				EditText apertureView = (EditText) findViewById(R.id.prefMaskValue);
				apertureView.setText("" + C.DEFAULT_MASK_OPEN);
				
				EditText calibrationView = (EditText) findViewById(R.id.prefCalibrationValue);
				calibrationView.setText("" + C.DEFAULT_CALIBRATION_FACTOR);
				
				CheckBox saveNoteBox = (CheckBox) findViewById(R.id.prefSaveNoteBox);
				saveNoteBox.setChecked(C.DEFAULT_SAVE_NOTE);

				CheckBox saveOctaveBox = (CheckBox) findViewById(R.id.prefSaveOctaveBox);
				saveOctaveBox.setChecked(C.DEFAULT_SAVE_OCTAVE);
				
				break;
			case R.id.prefFreqTextView:
				Toast.makeText(this, "The frequency of A4, can be a decimal.\n(" + 
							C.DEFAULT_A4_FREQ_MIN + " to " + 
							C.DEFAULT_A4_FREQ_MAX + ", default: " +
							C.DEFAULT_A4_FREQ + ")" , Toast.LENGTH_LONG).show();
				break;
			case R.id.prefApatureTextView:
				Toast.makeText(this, "The percentage of the mask wheel that is open. Small numbers give larger dark bands which may be helpful if the whole wheel appears red. Whole numbers only.\n(" + 
							C.DEFAULT_MASK_OPEN_MIN + " to " + 
							C.DEFAULT_MASK_OPEN_MAX + ", default: " +
							C.DEFAULT_MASK_OPEN + ")" , Toast.LENGTH_LONG).show();
				break;
			case R.id.prefCalibrationTextView:
				Toast.makeText(this, "The calibration factor. Changes the app's internal tuning by 0.1 cent increments to take into account device dependent timings or to calibrate to a specific instrument.\n(" + 
							C.DEFAULT_CALIBRATION_FACTOR_MIN + " to " + 
							C.DEFAULT_CALIBRATION_FACTOR_MAX + ", default: " +
							C.DEFAULT_CALIBRATION_FACTOR + ")" , Toast.LENGTH_LONG).show();
				break;
			case R.id.prefNoiseTextView:
				Toast.makeText(this, "How much noise to ignore. Higher numbers mean ignore more, but will give less sound for analysis. Quiet background conversation is about 500.\n(" + 
							C.DEFAULT_FLASH_THRESHOLD_MIN + " to " + 
							C.DEFAULT_FLASH_THRESHOLD_MAX + ", default: " +
							C.DEFAULT_FLASH_THRESHOLD + ")" , Toast.LENGTH_LONG).show();
				break;
			case R.id.prefSaveNoteTextView:
				Toast.makeText(this, "Should the current note be saved on exit?\n(default: no)" , Toast.LENGTH_LONG).show();
				break;
			case R.id.prefSaveOctaveTextView:
				Toast.makeText(this, "Should the current octave be saved on exit?\n(default: no)" , Toast.LENGTH_LONG).show();
				break;

			default:
				Log.e(TAG, "Unknown button click");
				break;
		}
	}
	
	private boolean validate()
	{
		boolean retVal = true;
		
		EditText a4View = null;
		EditText flashView = null;
		EditText apertureView = null;
		EditText calibrationView = null;
		
		try
		{
			a4View = (EditText) findViewById(R.id.prefA4value);
			double a4 = Double.parseDouble(a4View.getText().toString());
			if (a4 > C.DEFAULT_A4_FREQ_MAX + 1)
			{
				Toast.makeText(this, "A4 Frequency must be between " + C.DEFAULT_A4_FREQ_MIN + " and " + C.DEFAULT_A4_FREQ_MAX, Toast.LENGTH_LONG).show();
				a4View.setText("" + mDecimalFormat.format(C.DEFAULT_A4_FREQ_MAX));
				retVal = false;
			}
			else if (a4 < C.DEFAULT_A4_FREQ_MIN - 1)
			{
				Toast.makeText(this, "A4 Frequency must be between " + C.DEFAULT_A4_FREQ_MIN + " and " + C.DEFAULT_A4_FREQ_MAX, Toast.LENGTH_LONG).show();
				a4View.setText("" + mDecimalFormat.format(C.DEFAULT_A4_FREQ_MIN));
				retVal = false;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "A4 Frequency must be a decimal number (e.g. '440.0')", Toast.LENGTH_LONG).show();
			a4View.setText("" + mDecimalFormat.format(C.DEFAULT_A4_FREQ));
			retVal = false;
		}
		
		try
		{
			flashView = (EditText) findViewById(R.id.prefNoiseValue);
			int flash = Integer.parseInt(flashView.getText().toString());
			if (flash > C.DEFAULT_FLASH_THRESHOLD_MAX + 1)
			{
				Toast.makeText(this, "Noise threshold must be between " + C.DEFAULT_FLASH_THRESHOLD_MIN + " and " + C.DEFAULT_FLASH_THRESHOLD_MAX, Toast.LENGTH_LONG).show();
				flashView.setText("" + C.DEFAULT_FLASH_THRESHOLD_MAX);
				retVal = false;
			}
			else if (flash < C.DEFAULT_FLASH_THRESHOLD_MIN - 1)
			{
				Toast.makeText(this, "Noise threshold must be between " + C.DEFAULT_FLASH_THRESHOLD_MIN + " and " + C.DEFAULT_FLASH_THRESHOLD_MAX, Toast.LENGTH_LONG).show();
				flashView.setText("" + C.DEFAULT_FLASH_THRESHOLD_MIN);
				retVal = false;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Noise threshold must be an integer (e.g. '500')", Toast.LENGTH_LONG).show();
			flashView.setText("" + C.DEFAULT_FLASH_THRESHOLD);
			retVal = false;
		}
		
		try
		{
			apertureView = (EditText) findViewById(R.id.prefMaskValue);
			int aperture = Integer.parseInt(apertureView.getText().toString());
			if (aperture > C.DEFAULT_MASK_OPEN_MAX + 1)
			{
				Toast.makeText(this, "Mask aperture size must be between " + C.DEFAULT_MASK_OPEN_MIN + " and " + C.DEFAULT_MASK_OPEN_MAX, Toast.LENGTH_LONG).show();
				apertureView.setText("" + C.DEFAULT_MASK_OPEN_MAX);
				retVal = false;
			}
			else if (aperture < C.DEFAULT_MASK_OPEN_MIN - 1)
			{
				Toast.makeText(this, "Mask aperture size must be between " + C.DEFAULT_MASK_OPEN_MIN + " and " + C.DEFAULT_MASK_OPEN_MAX, Toast.LENGTH_LONG).show();
				apertureView.setText("" + C.DEFAULT_MASK_OPEN_MIN);
				retVal = false;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Mask aperture size size be an integer (e.g. '45')", Toast.LENGTH_LONG).show();
			apertureView.setText("" + C.DEFAULT_MASK_OPEN);
			retVal = false;
		}
		
		try
		{
			calibrationView = (EditText) findViewById(R.id.prefCalibrationValue);
			int calibration = Integer.parseInt(calibrationView.getText().toString());
			if (calibration > C.DEFAULT_CALIBRATION_FACTOR_MAX + 1)
			{
				Toast.makeText(this, "Calibration factor must be between " + C.DEFAULT_CALIBRATION_FACTOR_MIN + " and " + C.DEFAULT_CALIBRATION_FACTOR_MAX, Toast.LENGTH_LONG).show();
				apertureView.setText("" + C.DEFAULT_CALIBRATION_FACTOR_MAX);
				retVal = false;
			}
			else if (calibration < C.DEFAULT_CALIBRATION_FACTOR_MIN - 1)
			{
				Toast.makeText(this, "Calibration factor must be between " + C.DEFAULT_CALIBRATION_FACTOR_MIN + " and " + C.DEFAULT_CALIBRATION_FACTOR_MAX, Toast.LENGTH_LONG).show();
				apertureView.setText("" + C.DEFAULT_CALIBRATION_FACTOR_MIN);
				retVal = false;
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Calibration factor must be an integer (e.g. '-4 or 25')", Toast.LENGTH_LONG).show();
			apertureView.setText("" + C.DEFAULT_CALIBRATION_FACTOR);
			retVal = false;
		}
		
		return retVal;
	}
}

