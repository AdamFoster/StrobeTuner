package net.adamfoster.android.strobe;

import android.media.MediaRecorder;

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
	public static final String PREF_MIC_INPUT = "PREF_MIC_INPUT";
	public static final String PREF_KEYBOARD = "PREF_KEYBORD";
	
	public static final int PREF_KEYBOARD_PIANO = 0;
	public static final int PREF_KEYBOARD_GUITAR = 1;
	public static final int PREF_KEYBOARD_BASS_GUITAR = 2;
  public static final String[] PREF_KEYBOARD_NAMES = {"Piano", "Guitar", "Bass Guitar"};
    
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
	public static final int DEFAULT_GUITAR_OCTAVE = 2;
	
	public static final int DEFAULT_MIC_INPUT = MediaRecorder.AudioSource.DEFAULT;
  public static final int DEFAULT_MIC_INPUT_OTHER = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
	
	public static final String SHARED_PREFERENCE_NAME = "StrobePreferences";
	
	public static final String KEY_PACKAGE = "net.adamfoster.android.strobe.key";
	
    private static final double QUARTER_ROOT_5 = Math.pow(5, 0.25);
    private static final double ROOT_5 = Math.sqrt(5);
    private static final double ROOT_2 = Math.sqrt(2);
    
    private static final double COMMA = 531441.0/524288.0;
    private static final double COMMA_HALF = Math.sqrt(COMMA);
    private static final double COMMA_QUARTER = Math.sqrt(COMMA_HALF);
    private static final double COMMA_3QUARTER = COMMA_HALF*COMMA_QUARTER;
    private static final double COMMA_SIXTH = Math.pow(COMMA, 1.0/6.0);
    private static final double COMMA_THIRD = Math.pow(COMMA, 1.0/3.0);
    private static final double COMMA_5SIXTH = Math.pow(COMMA, 5.0/6.0);
    private static final double COMMA_2THIRD = Math.pow(COMMA, 2.0/3.0);
    private static final double COMMA_11TWELFTH = Math.pow(COMMA, 11.0/12.0);
    
    
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
            new Scale("Pythagorean (Augmented 4th)", false, new double[]
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
                    "Pyth 4+"),
            new Scale("Pythagorean (Diminished 5th)", false, new double[]
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
                    "Pyth 5-"),
            new Scale("5-limit (symmetric 1)", false, new double[]
                    {
                        1.0, 16.0/15.0, 9.0/8.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 16.0/9.0, 15.0/8.0
                    },
                    new String[]
                    {
                        "1", "16/15", "9/8",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "16/9", "15/8"
                    },
                    "5-limit sym 1"),
            new Scale("5-limit (symmetric 2)", false, new double[]
                    {
                        1.0, 16.0/15.0, 10.0/9.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 9.0/5.0, 15.0/8.0
                    },
                    new String[]
                    {
                        "1", "16/15", "10/9",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "9/5", "15/8"
                    },
                    "5-limit sym-2)"),
            new Scale("5-limit (asymmetric)", false, new double[]
                    {
                        1.0, 16.0/15.0, 9.0/8.0,
                        6.0/5.0, 5.0/4.0, 4.0/3.0,
                        45.0/32.0, 3.0/2.0, 8.0/5.0,
                        5.0/3.0, 9.0/5.0, 15.0/8.0
                    },
                    new String[]
                    {
                        "1", "16/15", "9/8",
                        "6/5", "5/4", "4/3",
                        "45/32", "3/2", "8/5",
                        "5/3", "9/5", "15/8"
                    },
                    "5-limit (asym)"),
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
            new Scale("Kirnberger II", false, new double[]
                    {
                        1.0,                                        Math.pow(3,7)/Math.pow(2,7)/16.0/COMMA, 
                        Math.pow(3,2)/Math.pow(2,2)/2.0,            Math.pow(3,9)/Math.pow(2,9)/32.0/COMMA, 
                        Math.pow(3,4)/Math.pow(2,4)/4.0/COMMA, 
                        Math.pow(3,11)/Math.pow(2,11)/64.0/COMMA,   Math.pow(3,6)/Math.pow(2,6)/8.0/COMMA, 
                        Math.pow(3,1)/Math.pow(2,1)/1.0,            Math.pow(3,8)/Math.pow(2,8)/16.0/COMMA,
                        Math.pow(3,3)/Math.pow(2,3)/2.0/COMMA_HALF, Math.pow(3,10)/Math.pow(2,10)/32.0/COMMA, 
                        Math.pow(3,5)/Math.pow(2,5)/4.0/COMMA, //7
                    },
                    new String[]
                    {
                        "0", "-1", "0", //unison
                        "-1", "-1", "-1", //minor 3
                        "-1", "0", "-1", //aug 4
                        "-1/2", "-1", "-1" //maj 6
                    },
                    "Kirnberger II"),
            new Scale("Kirnberger III", false, new double[]
                    {
                        1.0,                                        Math.pow(3,7)/Math.pow(2,7)/16.0/COMMA, 
                        Math.pow(3,2)/Math.pow(2,2)/2.0/COMMA_HALF,            Math.pow(3,9)/Math.pow(2,9)/32.0/COMMA, 
                        Math.pow(3,4)/Math.pow(2,4)/4.0/COMMA, 
                        Math.pow(3,11)/Math.pow(2,11)/64.0/COMMA,   Math.pow(3,6)/Math.pow(2,6)/8.0/COMMA, 
                        Math.pow(3,1)/Math.pow(2,1)/1.0/COMMA_QUARTER,            Math.pow(3,8)/Math.pow(2,8)/16.0/COMMA,
                        Math.pow(3,3)/Math.pow(2,3)/2.0/COMMA_3QUARTER, Math.pow(3,10)/Math.pow(2,10)/32.0/COMMA, 
                        Math.pow(3,5)/Math.pow(2,5)/4.0/COMMA, //7
                    },
                    new String[]
                    {
                        "0", "-1/2", "-1", //unison
                        "-1", "-1", "-1", //minor 3
                        "-1", "-1/4", "-1", //aug 4
                        "-3/4", "-1", "-1" //maj 6
                    },
                    "Kirnberger III"),
            new Scale("Werckmeister III", false, new double[]
                    {
                        1.0, 256.0/243.0, 64.0/81.0*ROOT_2,
                        32.0/27.0, 256.0/243.0*Math.pow(2, 0.25), 4.0/3.0,
                        1024.0/729.0, 8.0/9.0*Math.pow(8, 0.24), 128.0/81.0,
                        1024.0/729.0*Math.pow(2, 0.25), 16.0/9.0, 128.0/81.0*Math.pow(2,  0.25)
                    },
                    new String[]
                    {
                        "0", "90", "192", //unison
                        "294", "390", "498", //minor 3
                        "588", "696", "792", //aug 4
                        "888", "996", "1092" //maj 6
                    },
                    "Werckmeister III"),
            new Scale("Werckmeister IV", false, new double[]
                    {
                        1.0, 16384.0/19683.0*Math.pow(2, 1.0/3.0), 8.0/9.0*Math.pow(2, 1.0/3.0),
                        32.0/27.0, 64.0/81.0*Math.pow(4, 1.0/3.0), 4.0/3.0,
                        1024.0/729.0, 32.0/27.0*Math.pow(2, 1.0/3.0), 8192.0/6561.0*Math.pow(2, 1.0/3.0),
                        256.0/243.0*Math.pow(4, 1.0/3.0), 9.0/4.0/Math.pow(2, 1.0/3.0), 4096.0/2187.0
                    },
                    new String[]
                    {
                        "0", "82", "192", //unison
                        "294", "392", "498", //minor 3
                        "588", "694", "784", //aug 4
                        "890", "1004", "1086" //maj 6
                    },
                    "Werckmeister IV"),
            new Scale("Werckmeister V", false, new double[]
                    {
                        1.0, 8.0/9.0*Math.pow(2, 0.25), 9.0/8.0,
                        Math.pow(2,  0.25), 8.0/9.0*ROOT_2, 9.0/8.0*Math.pow(2, 0.25),
                        ROOT_2, 3.0/2.0, 128.0/81.0, 
                        Math.pow(8, 0.25), 3.0/Math.pow(8, 0.25), 4.0/3.0*ROOT_2 
                    },
                    new String[]
                    {
                        "0", "96", "204", //unison
                        "300", "396", "504", //minor 3
                        "600", "702", "792", //aug 4
                        "900", "1002", "1098" //maj 6
                    },
                    "Werckmeister V"),
            new Scale("Werckmeister VI", false, new double[]
                    {
                        1.0, 98.0/93.0, 28.0/25.0,
                        196.0/165.0, 49.0/39.0, 4.0/3.0,
                        196.0/139.0, 196.0/131.0, 49.0/31.0,
                        196.0/117.0, 98.0/55.0, 49.0/26.0
                    },
                    new String[]
                    {
                        "0", "91", "196", //unison
                        "298", "395", "498", //minor 3
                        "595", "698", "793", //aug 4
                        "893", "1000", "1097" //maj 6
                    },
            "Werckmeister VI"),
            new Scale("Young II", false, new double[]
                    {
                        1.0,                                           Math.pow(3,7)/Math.pow(2,7)/16.0/COMMA, 
                        Math.pow(3,2)/Math.pow(2,2)/2.0/COMMA_THIRD,    Math.pow(3,9)/Math.pow(2,9)/32.0/COMMA, 
                        Math.pow(3,4)/Math.pow(2,4)/4.0/COMMA_2THIRD, 
                        Math.pow(3,11)/Math.pow(2,11)/64.0/COMMA,      Math.pow(3,6)/Math.pow(2,6)/8.0/COMMA, 
                        Math.pow(3,1)/Math.pow(2,1)/1.0/COMMA_SIXTH, Math.pow(3,8)/Math.pow(2,8)/16.0/COMMA,
                        Math.pow(3,3)/Math.pow(2,3)/2.0/COMMA_HALF,    Math.pow(3,10)/Math.pow(2,10)/32.0/COMMA, 
                        Math.pow(3,5)/Math.pow(2,5)/4.0/COMMA_5SIXTH, //7
                    },
                    new String[]
                    {
                        "0", "-1", 
                        "-2/6", "-1", 
                        "-4/6", 
                        "-1", "-1", 
                        "-1/6", "-1", 
                        "-3/6", "-1", 
                        "-5/6" 
                    },
                    "Young II"),
            new Scale("Bach/Lehman", false, new double[]
                {
                    1.0,                                           Math.pow(3,7)/Math.pow(2,7)/16.0/COMMA_2THIRD, 
                    Math.pow(3,2)/Math.pow(2,2)/2.0/COMMA_THIRD,    Math.pow(3,9)/Math.pow(2,9)/32.0/COMMA_5SIXTH, 
                    Math.pow(3,4)/Math.pow(2,4)/4.0/COMMA_2THIRD, 
                    Math.pow(3,11)/Math.pow(2,11)/64.0/COMMA_5SIXTH,      Math.pow(3,6)/Math.pow(2,6)/8.0/COMMA_2THIRD, 
                    Math.pow(3,1)/Math.pow(2,1)/1.0/COMMA_SIXTH, Math.pow(3,8)/Math.pow(2,8)/16.0/COMMA_3QUARTER,
                    Math.pow(3,3)/Math.pow(2,3)/2.0/COMMA_HALF,    Math.pow(3,10)/Math.pow(2,10)/32.0/COMMA_11TWELFTH, 
                    Math.pow(3,5)/Math.pow(2,5)/4.0/COMMA_2THIRD, //7
                },
                new String[]
                {
                    "0", "-1", 
                    "-2/6", "-1", 
                    "-4/6", 
                    "-1", "-1", 
                    "-1/6", "-1", 
                    "-3/6", "-1", 
                    "-5/6" 
                },
                "Bach/Lehman"),
            new Scale("Vallotti", false, new double[]
                {
                    1.0,                                           Math.pow(3,7)/Math.pow(2,7)/16.0/COMMA_5SIXTH, 
                    Math.pow(3,2)/Math.pow(2,2)/2.0/COMMA_THIRD,    Math.pow(3,9)/Math.pow(2,9)/32.0/COMMA_5SIXTH, 
                    Math.pow(3,4)/Math.pow(2,4)/4.0/COMMA_2THIRD, 
                    Math.pow(3,11)/Math.pow(2,11)/64.0/COMMA_5SIXTH,      Math.pow(3,6)/Math.pow(2,6)/8.0/COMMA_5SIXTH, 
                    Math.pow(3,1)/Math.pow(2,1)/1.0/COMMA_SIXTH, Math.pow(3,8)/Math.pow(2,8)/16.0/COMMA_5SIXTH,
                    Math.pow(3,3)/Math.pow(2,3)/2.0/COMMA_HALF,    Math.pow(3,10)/Math.pow(2,10)/32.0/COMMA_5SIXTH, 
                    Math.pow(3,5)/Math.pow(2,5)/4.0/COMMA_5SIXTH, //7
                },
                new String[]
                {
                    "0", "-1", 
                    "-2/6", "-1", 
                    "-4/6", 
                    "-1", "-1", 
                    "-1/6", "-1", 
                    "-3/6", "-1", 
                    "-5/6" 
                },
                "Vallotti"),
        };
    public static final int SCALE_INDEX_EQUAL = 0;
    public static final int SCALE_INDEX_JUST_PYTH_AUG = 1;
    public static final int SCALE_INDEX_JUST_PYTH_DIM = 2;
    public static final int SCALE_INDEX_JUST_5_SYM1 = 3;
    public static final int SCALE_INDEX_JUST_5_SYM2 = 4;
    public static final int SCALE_INDEX_JUST_5_ASYM = 5;
    public static final int SCALE_INDEX_QUARTER_MEANTONE_AUG_4 = 6;
    public static final int SCALE_INDEX_QUARTER_MEANTONE_DIM_5 = 7;
    public static final int SCALE_INDEX_KIRNBERGER_II = 8;
    public static final int SCALE_INDEX_KIRNBERGER_III = 9;
    public static final int SCALE_INDEX_WERCKMEISTER_III = 10;
    public static final int SCALE_INDEX_WERCKMEISTER_IV = 11;
    public static final int SCALE_INDEX_WERCKMEISTER_V = 12;
    public static final int SCALE_INDEX_WERCKMEISTER_VI = 13;
    public static final int SCALE_INDEX_YOUNG_II = 14;
    public static final int SCALE_INDEX_BACH_LEHMAN = 15;
    public static final int SCALE_INDEX_VALLOTTI = 16;
    
    
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
