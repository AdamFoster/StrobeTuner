package net.adamfoster.android.strobe;

public class C
{
	public static final String PREF_A4_FREQ = "PREF_A4_FREQ";
	public static final String PREF_FLASH_THRESHOLD = "PREF_FLASH_THRESHOLD";
	public static final String PREF_MASK_OPEN = "PREF_MASK_OPEN";
	public static final String PREF_SAVE = "PREF_SAVE";
	public static final String PREF_SAVE_NOTE = "PREF_SAVE_NOTE";
	public static final String PREF_SAVE_OCTAVE = "PREF_SAVE_OCTAVE";
	public static final String PREF_SAVE_SCALE = "PREF_SAVE_SCALE";
	public static final String PREF_NOTE = "PREF_NOTE";
	public static final String PREF_OCTAVE = "PREF_OCTAVE";
	public static final String PREF_FIRST_RUN = "PREF_FIRST_RUN";
	public static final String PREF_LAST_RUN_VERSION = "PREF_LAST_RUN_VERSION";
	public static final String PREF_RESTORED_TRANSACTIONS = "PREF_RESTORED_TRANSACTIONS";
	public static final String PREF_CALIBRATION_FACTOR = "PREF_CALIBRATION_FACTOR";
	public static final String PREF_PLUS = "PREF_PLUS";
	public static final String PREF_COLOR = "PREF_COLOR";
	public static final String PREF_SCALE = "PREF_SCALE";
	public static final String PREF_SCALE_START_NOTE = "PREF_SCALE_START_NOTE";
    
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
	public static final boolean DEFAULT_SAVE_SCALE = false;
	public static final int DEFAULT_NOTE = Recorder.NOTE_A;
	public static final int DEFAULT_OCTAVE = 4;
	
	public static final String SHARED_PREFERENCE_NAME = "StrobePreferences";
	
	public static final String KEY_PACKAGE = "net.adamfoster.android.strobe.key";
	
    private static final double QUARTER_ROOT_5 = Math.pow(5, 0.25);
    private static final double ROOT_5 = Math.sqrt(5);
    
    public static final Scale[] SCALES = 
        {
            new Scale("Equal", true, new double[]
                    {
                        1.0, Math.pow(2, 1.0/12.0), Math.pow(2, 2.0/12.0),
                        Math.pow(2, 3.0/12.0), Math.pow(2, 4.0/12.0), Math.pow(2, 5.0/12.0),
                        Math.pow(2, 6.0/12.0), Math.pow(2, 7.0/12.0), Math.pow(2, 8.0/12.0),
                        Math.pow(2, 9.0/12.0), Math.pow(2, 10.0/12.0), Math.pow(2, 11.0/12.0)
                    },
                    new String[]
                    {
                        "1", "2^1/12", "2^2/12",
                        "2^3/12", "2^4/12", "2^5/12",
                        "2^6/12", "2^7/12", "2^8/12",
                        "2^9/12", "2^1/12", "2^11/12",
                    },
                    "Equal"), 
            new Scale("Just (Pythagorean Augmented 4th)", false, new double[]
                    {
                        1.0, 256.0/243.0, 9.0/8.0,
                        32.0/27, 81.0/64.0, 4.0/3.0,
                        729.0/512.0, 3.0/2.0, 128.0/81.0,
                        27.0/16.0, 16.0/9.0, 243.0/128.0
                    },
                    new String[]
                    {
                        "1", "256/243", "9/8",
                        "32/27", "81/64", "4/3",
                        "729/512", "3/2", "128/81",
                        "27/16", "16/9", "243/128"
                    },
                    "Just (Pyth Aug 4)"),
            new Scale("Just (Pythagorean Diminished 5th)", false, new double[]
                    {
                        1.0, 256.0/243.0, 9.0/8.0,
                        32.0/27, 81.0/64.0, 4.0/3.0,
                        1024.0/729.0, 3.0/2.0, 128.0/81.0,
                        27.0/16.0, 16.0/9.0, 243.0/128.0
                    },
                    new String[]
                    {
                        "1", "256/243", "9/8",
                        "32/27", "81/64", "4/3",
                        "1024/729", "3/2", "128/81",
                        "27/16", "16/9", "243/128"
                    },
                    "Just (Pyth Dim 5)"),
            new Scale("Just (5-limit symmetric 1)", false, new double[]
                    {
                        1.0, 16.0/15.0, 9.0/8.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 16.0/9.0, 6.0/5.0
                    },
                    new String[]
                    {
                        "1", "16/15", "9/8",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "16/9", "6/5"
                    },
                    "Just (5 sym 1)"),
            new Scale("Just (5-limit symmetric 2)", false, new double[]
                    {
                        1.0, 16.0/15.0, 10.0/9.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 9.0/5.0, 6.0/5.0
                    },
                    new String[]
                    {
                        "1", "16/15", "10/9",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "9/5", "6/5"
                    },
                    "Just (5 sym-2)"),
            new Scale("Just (5-limit asymmetric)", false, new double[]
                    {
                        1.0, 16.0/15.0, 9.0/8.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 9.0/5.0, 6.0/5.0
                    },
                    new String[]
                    {
                        "1", "16/15", "9/8",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "9/5", "6/5"
                    },
                    "Just (5 asym)"),
            new Scale("Quarter-comma meantone (Aug 4th)", false, new double[]
                    {
                        1.0, 8.0*ROOT_5*QUARTER_ROOT_5/25.0, ROOT_5/2.0,
                        4.0*QUARTER_ROOT_5/5.0, 5.0/4.0, 2.0*ROOT_5*QUARTER_ROOT_5/5.0,
                        5.0*ROOT_5/8.0, QUARTER_ROOT_5, 8.0/5.0,
                        ROOT_5*QUARTER_ROOT_5/2.0, 4.0*ROOT_5/5.0, 5.0*QUARTER_ROOT_5/4.0
                    },
                    new String[]
                    {
                        "1", "8r5q5/25", "r5/2", //unison
                        "4q5/5", "5/4", "2r5q5/5", //minor 3
                        "5r5/8", "q5", "8/5", //aug 4
                        "r5q5/2", "4r5/5", "5q5/4" //maj 6
                    },
                    "1/4-comma meantone (4+)"),
            new Scale("Quarter-comma meantone (Dim 5th)", false, new double[]
                    {
                        1.0, 8.0*ROOT_5*QUARTER_ROOT_5/25.0, ROOT_5/2.0,
                        4.0*QUARTER_ROOT_5/5.0, 5.0/4.0, 2.0*ROOT_5*QUARTER_ROOT_5/5.0,
                        15.0*ROOT_5/25.0, QUARTER_ROOT_5, 8.0/5.0,
                        ROOT_5*QUARTER_ROOT_5/2.0, 4.0*ROOT_5/5.0, 5.0*QUARTER_ROOT_5/4.0
                    },
                    new String[]
                    {
                        "1", "8r5q5/25", "r5/2", //unison
                        "4q5/5", "5/4", "2r5q5/5", //minor 3
                        "16r5/25", "q5", "8/5", //aug 4
                        "r5q5/2", "4r5/5", "5q5/4" //maj 6
                    },
                    "1/4-comma meantone (5-)"),
        };
    public static final int SCALE_INDEX_EQUAL = 0;
    public static final int SCALE_INDEX_JUST_PYTH_AUG = 1;
    public static final int SCALE_INDEX_JUST_PYTH_DIM = 2;
    public static final int SCALE_INDEX_JUST_5_SYM1 = 3;
    public static final int SCALE_INDEX_JUST_5_SYM2 = 4;
    public static final int SCALE_INDEX_JUST_5_ASYM = 5;
    public static final int SCALE_INDEX_QUARTER_MEANTONE_AUG_4 = 6;
    public static final int SCALE_INDEX_QUARTER_MEANTONE_DIM_5 = 7;
    
    
    public static class Scale
    {
        public String description;
        public boolean isEqual;
        public double[] factors;
        public String[] display;
        public String shortDescription;
        
        public Scale(String d, boolean e, double[] f, String[] s, String shortDescription)
        {
            description = d;
            isEqual = e;
            factors = f;
            display = s;
            this.shortDescription = shortDescription;
        }
    }

}
