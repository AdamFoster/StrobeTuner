package net.adamfoster.android.strobe;

public class C
{
	public static final String PREF_A4_FREQ = "PREF_A4_FREQ";
	public static final String PREF_FLASH_THRESHOLD = "PREF_FLASH_THRESHOLD";
	public static final String PREF_MASK_OPEN = "PREF_MASK_OPEN";
	public static final String PREF_SAVE = "PREF_SAVE";
	public static final String PREF_SAVE_NOTE = "PREF_SAVE_NOTE";
	public static final String PREF_SAVE_OCTAVE = "PREF_SAVE_OCTAVE";
	public static final String PREF_NOTE = "PREF_NOTE";
	public static final String PREF_OCTAVE = "PREF_OCTAVE";
	public static final String PREF_FIRST_RUN = "PREF_FIRST_RUN";
	public static final String PREF_RESTORED_TRANSACTIONS = "PREF_RESTORED_TRANSACTIONS";
	public static final String PREF_CALIBRATION_FACTOR = "PREF_CALIBRATION_FACTOR";
	public static final String PREF_PLUS = "PREF_PLUS";

	public static final float DEFAULT_A4_FREQ = 440;
	public static final float DEFAULT_A4_FREQ_MAX = 2000;
	public static final float DEFAULT_A4_FREQ_MIN = 10;
	
	public static final int DEFAULT_CALIBRATION_FACTOR = 0;
	public static final int DEFAULT_CALIBRATION_FACTOR_MAX = 1000;
	public static final int DEFAULT_CALIBRATION_FACTOR_MIN = -1000;

	public static final int DEFAULT_FLASH_THRESHOLD = 500;
	public static final int DEFAULT_FLASH_THRESHOLD_MIN = 0;
	public static final int DEFAULT_FLASH_THRESHOLD_MAX = (int) (Short.MAX_VALUE * 0.8);

	public static final int DEFAULT_MASK_OPEN = 45;
	public static final int DEFAULT_MASK_OPEN_MAX = 55;
	public static final int DEFAULT_MASK_OPEN_MIN = 5;

	public static final boolean DEFAULT_SAVE_NOTE = false;
	public static final boolean DEFAULT_SAVE_OCTAVE = false;
	public static final int DEFAULT_NOTE = Recorder.NOTE_A;
	public static final int DEFAULT_OCTAVE = 4;
	
	public static final String SHARED_PREFERENCE_NAME = "StrobePreferences";
	
	public static final String KEY_PACKAGE = "net.adamfoster.android.strobe.key";
}
