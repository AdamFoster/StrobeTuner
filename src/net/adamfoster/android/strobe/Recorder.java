package net.adamfoster.android.strobe;

import java.text.DecimalFormat;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class Recorder
{
	public static String TAG = "StrobeRecorder";
	
	public static int NOTE_C = 0;
	public static int NOTE_C_SHARP = 1;
	public static int NOTE_D = 2;
	public static int NOTE_D_SHARP = 3;
	public static int NOTE_E = 4;
	public static int NOTE_F = 5;
	public static int NOTE_F_SHARP = 6;
	public static int NOTE_G = 7;
	public static int NOTE_G_SHARP = 8;
	public static int NOTE_A = 9;
	public static int NOTE_A_SHARP = 10;
	public static int NOTE_B = 11;
	
	public static String[] NOTES = {"C", "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B"};
	public static boolean[] SHARPS = {false, true, false, true, false, false, true, false, true, false, true, false};
	public static String[] NOTE_NAMES = {"C", "C# / Db", "D", "D# / Eb", "E", "F", "F# / Gb", "G", "G# / Ab", "A", "A# / Bb", "B"};
	
	public static int SAMPLE_FREQUENCY = 44100;
	public static int RINGS = 6;
	public static int BIN_COUNT = 128; // approx 300 bins req'd to get 1 pixel per bin at circumference at 160dip
	public static int BUCKETS_PER_REVOLUTION = 4;
	public static double BUCKETS_PER_HZ = 4; // e.g. buckets per 1/440 th of a second
	public static int BUFFER_SIZE = 32768;
	public static int FRAME_RATE = 30;
	public static double CALIBRATION_FACTOR_DIVISOR = 1.0 / ((Math.pow(2.0, 1.0/12.0) - 1.0)/1000.0);
	
	private static final double DRAW_AVE_RATIO = 0.3;

	private double [][] mBins;
	private int mNote;
	private int mOctave;
	private int mZerosRead; // used to count the number of 0 reads... indicates errors
	private int mFlashBinOffset; // bins need to be offset per reading
	private double mProportion;
	
	private RecorderThread mThread;
	
	private double mA4Freq;
	private double mOpenProportion;
	private int mCalibrationFactor;
	private int mFlashThreshold;
	private boolean mAutoDetect;
    private int mScale;
    private int mScaleStartNote;
	
	private DecimalFormat mDecimalFormat;
	
	private int mWindowSize;
	private double[] mWindow;
	private static final int WINDOW_SIZE = SAMPLE_FREQUENCY/5;

	private boolean mSaveNote;
	private boolean mSaveOctave;
    private boolean mSaveScale;

	private AudioRecord mAudio;
	int mSamplesRead;
	
	private boolean mIsRunning;
	
	//surface stuff
    private int mCanvasHeight = 1;
    private int mCanvasWidth = 1;
    private SurfaceHolder mSurfaceHolder;
    
    private Context mContext;
    private StrobeTunerActivity mStrobeTunerActivity;
    
    private Paint mPaintBackground;
    private Paint mPaintCircle;
    private Paint mPaintBright;
    private Paint mPaintMedium;
    private Paint mPaintDark;
    private Paint mPaintText;
    private Paint mPaintTextSharp;
    private Paint mPaintTextFreq;
	
	public Recorder(SurfaceHolder holder, Context context)
	{
		mSurfaceHolder = holder;
		mContext = context; 
		
		mDecimalFormat = new DecimalFormat("#.0");
		//mTimerFormat = new DecimalFormat("000000,000");
		mZerosRead = 0;
		mFlashBinOffset = 0;
		mProportion = 0;
		
		mThread = new RecorderThread(this);
				
        //Initialise stuff
        mA4Freq = 440.0;
        mOpenProportion = 0.4;
        mCalibrationFactor = 0;
        mFlashThreshold = 500; //500 seems to be office background noise // (int) (Short.MAX_VALUE / 4); // sin(45) ~ 0.707
        mAutoDetect = false;
        
        mBins = new double[RINGS][];
        for (int i=0 ; i<RINGS ; i++)
		{
			mBins[i] = new double[BIN_COUNT];
		}
        mNote = NOTE_A;
        mOctave = 4;
        
        mSaveNote = false;
        mSaveOctave = false;
        mSaveScale = false;
        
        mWindow = new double[WINDOW_SIZE];
        mWindowSize = 0;
        
        //initialise paints
        mPaintBackground = new Paint();
        mPaintBackground.setStyle(Style.FILL); 
        mPaintBackground.setColor(context.getResources().getColor(R.color.Background));
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Style.FILL); 
        mPaintCircle.setColor(context.getResources().getColor(R.color.Circle));
        mPaintBright = new Paint();
        mPaintBright.setStyle(Style.FILL); 
        mPaintBright.setColor(context.getResources().getColor(R.color.Bright));
        mPaintMedium = new Paint();
        mPaintMedium.setStyle(Style.FILL); 
        mPaintMedium.setColor(context.getResources().getColor(R.color.Medium));
        mPaintDark = new Paint();
        mPaintDark.setStyle(Style.FILL); 
        mPaintDark.setColor(context.getResources().getColor(R.color.Dark));
        mPaintText = new Paint();
        mPaintText.setStyle(Style.FILL);
        mPaintText.setColor(context.getResources().getColor(R.color.CentreText));
        mPaintText.setTextSize(30);
        mPaintTextSharp = new Paint();
        mPaintTextSharp.setStyle(Style.FILL);
        mPaintTextSharp.setColor(context.getResources().getColor(R.color.CentreText));
        mPaintTextSharp.setTextSize(mPaintText.getTextSize()/2);
        mPaintTextFreq = new Paint();
        mPaintTextFreq.setStyle(Style.FILL);
        mPaintTextFreq.setColor(context.getResources().getColor(R.color.CentreText));
        mPaintTextFreq.setTextSize(20);
		
        mIsRunning = false;
	}
	
	public void start()
	{
		if (mThread.getState() == Thread.State.TERMINATED)
		{
			mThread = new RecorderThread(this);
			mThread.start();
		}
		else
		{
			mThread.start();
		}
	}
	
	public void join() throws InterruptedException
	{
		mThread.join();
	}
	
	public void run()
	{
        //set up recording
        Log.i(TAG, "getMinBufferSize = " + 
        		AudioRecord.getMinBufferSize(SAMPLE_FREQUENCY, 
        		AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
        mAudio = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_FREQUENCY, 
        		AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        Log.i(TAG, "Audio " + (mAudio.getState() == AudioRecord.STATE_INITIALIZED ? "initialized" : "uninitialized"));
        
        //start recording
        mAudio.startRecording();
		mIsRunning = true;
		
		while (mIsRunning)
		{
			Canvas c = null;
			try
			{
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder)
				{
					//if (DEBUG) Log.v(TAG, "Time Before read: " + mTimerFormat.format((System.nanoTime() - mTime)));
					read();
					//if (DEBUG) Log.v(TAG, "Time Before draw: " + mTimerFormat.format((System.nanoTime() - mTime)));
					doDraw(c);
					//if (DEBUG) Log.v(TAG, "Time After  draw: " + mTimerFormat.format((System.nanoTime() - mTime)));
				}
			}
			finally
			{
				if (c != null)
				{
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
			
		}
		
		mAudio.stop();
		mAudio.release();
	}
	
	public void setActivity(StrobeTunerActivity sta)
	{
		mStrobeTunerActivity = sta;
	}
	
	public void setNote(int note)
	{
		mAutoDetect = false;
		mNote = note;
	}
	public int getNote()
	{
		return mNote;
	}
	public int nextNote()
	{
		mAutoDetect = false;
		mNote = mNote+1;
		if (mNote >= 12)
		{
			mNote -= 12;
			mOctave++;
		}
		return mNote;
	}
	public int prevNote()
	{
		mAutoDetect = false;
		mNote = mNote-1;
		if (mNote < 0)
		{
			mNote += 12;
			mOctave--;
		}
		return mNote;
	}

	public void setOctave(int octave)
	{
		mAutoDetect = false;
		mOctave = octave;
	}
	
	public int getOctave()
	{
		return mOctave;
	}
	
	public int upOctave()
	{
		mAutoDetect = false;
		if (mOctave > 11)
		{
			return mOctave;
		}
		else
		{
			return ++mOctave;
		}
	}
	public int downOctave()
	{
		mAutoDetect = false;
		if (mOctave < 1)
		{
			return mOctave;
		}
		else
		{
			return --mOctave;
		}
	}
	
	public void setRunning(boolean isRunning)
	{	
		mIsRunning = isRunning;
	}
	
	public void setSaveNote(boolean saveNote)
	{
		mSaveNote = saveNote;
	}
	public boolean getSaveNote()
	{
		return mSaveNote;
	}
	
	public void setSaveOctave(boolean saveOctave)
	{
		mSaveOctave = saveOctave;
	}
	public boolean getSaveOctave()
	{
		return mSaveOctave;
	}
	
	public void setSaveScale(boolean saveScale)
    {
        mSaveScale = saveScale;
    }
    public boolean getSaveScale()
    {
        return mSaveScale;
    }
    
    public void setSurfaceSize(int width, int height) 
    {
        synchronized (mSurfaceHolder) 
        {
            mCanvasWidth = width;
            mCanvasHeight = height;
        }
    }
	
	public int getCanvasHeight()
	{
		return mCanvasHeight;
	}
	public int getCanvasWidth()
	{
		return mCanvasWidth;
	}
	public double getA4Freq()
	{
		return mA4Freq;
	}
	
	public void setA4Freq(double a4Freq)
	{
		mA4Freq = a4Freq;
	}
	
	public int getScale()
	{
	    return mScale;
	}
	public void setScale(int scale)
	{
	    mScale = scale;
	}
    public int getScaleStartNote()
    {
        return mScaleStartNote;
    }
    public void setStartNote(int startNote)
    {
        mScaleStartNote = startNote;
    }
	
	public void setCalibrationFactor(int calibrationFactor)
	{
		mCalibrationFactor = calibrationFactor;
	}
	public void adjustCalibrationFactor(int adjustment)
	{
		mCalibrationFactor += adjustment;
	}
	public int getCalibrationFactor()
	{
		return mCalibrationFactor;
	}
	
	public double getOpenProportion()
	{
		return mOpenProportion;
	}
	public int getOpenProportionPercent()
	{
		return (int)(this.mOpenProportion * 100.0);
	}
	public void setOpenProportionPercent(int openPercent)
	{
		this.mOpenProportion = openPercent / 100.0;
	}
	public int getFlashThreshold()
	{
		return mFlashThreshold;
	}
	public void setFlashThreshold(int flashThreshold)
	{
		this.mFlashThreshold = flashThreshold;
	}

	public boolean getAutoDetect()
	{
		return mAutoDetect;
	}
	public void setAutoDetect(boolean autoDetect)
	{
		mAutoDetect = autoDetect;
	}
	
	public void setColor(int color)
	{
		mPaintBright.setColor(color);
		mPaintMedium.setColor(color);
		mPaintMedium.setAlpha(mPaintBright.getAlpha()/2);
	}
	public int getColor()
	{
		return mPaintBright.getColor();
	}
	
	public double getTargetFreq()
	{
		// set note4 based on A4 frequency
		//double targetFreq = mA4Freq * Math.pow(2, (mNote-NOTE_A) / 12.0); 
		//shift octaves
		//return targetFreq * Math.pow(2, mOctave - 4);
		
	    double startNoteFreq = mA4Freq / C.SCALES[mScale].factors[(Recorder.NOTE_A-mScaleStartNote+Recorder.NOTES.length)%Recorder.NOTES.length];
        startNoteFreq *= Math.pow(2, mOctave-4); //adjust for octave : A4 is the default
        if (mScaleStartNote > Recorder.NOTE_A) //hack, but it works
        {
            startNoteFreq *= 2;
        }
        return startNoteFreq * C.SCALES[mScale].factors[(mNote-mScaleStartNote+Recorder.NOTES.length)%Recorder.NOTES.length];

	}
	
	private void read()
	{
		short[] audioBuffer = new short[SAMPLE_FREQUENCY / FRAME_RATE];
		mSamplesRead = mAudio.read(audioBuffer, 0, audioBuffer.length);
		
		//if (DEBUG) Log.v(TAG, "Time Before calc: " + mTimerFormat.format((System.nanoTime() - mTime)));
		
		if (mSamplesRead > 0)
		{
			process(audioBuffer, mSamplesRead);
		}
		else
		{
			Log.e(TAG, "Read 0 shorts");
			mZerosRead++;
			if (mZerosRead > 50)
			{
				//lots of zeros read... bad things must be happening let's quit while we're ahead
				//Toast.makeText(mContext, "Too many samples failed", Toast.LENGTH_LONG);
				if (mStrobeTunerActivity != null)
				{
					mStrobeTunerActivity.runOnUiThread(new Thread()
					{
						@Override
						public void run() 
						{
							Toast.makeText(mContext, "Too many samples failed. Please restart the Strobe Tuner.", Toast.LENGTH_LONG).show();
						}
					});
				}
				Log.e(TAG, "Failing - no shorts read for 50 cycles");
				mZerosRead = 0;
				mIsRunning = false;
			}
		}
	}
	
	private void makeWindow(short[] audioBuffer, int length)
	{
		if (mWindowSize + length >= WINDOW_SIZE)
		{
			double freq = fft();
			freqToNote(freq);
			mWindowSize = 0;
			mWindow = new double[WINDOW_SIZE];
		}
		for (int i=0 ; i<length ; i++)
		{
			mWindow[mWindowSize+i] = audioBuffer[i];
		}
		mWindowSize += length;
	}
	
	private double fft()
	{
		DoubleFFT_1D dft = new DoubleFFT_1D(mWindowSize);
		dft.realForward(mWindow);
		
		double max = 0;
		int index = 0;
		
		double scale = 0; 
		for (int i=2 ; i+1<mWindowSize ; i+=2)
		{
			scale = mWindow[i]*mWindow[i]+mWindow[i+1]*mWindow[i+1];
			
			if (max < scale)
			{
				max = scale;
				index = i;
			}
		}
		
		double freq = ( (double)SAMPLE_FREQUENCY ) * index / mWindowSize / 2;
		return freq;
	}
	
	//frequency to note
	// sets mNote and mOctave if possible, and resets the frequency accordingly
	private void freqToNote(double f)
	{
		// sanity check
		if (f<13 || f>10000)
		{
			return;
		}
		
		int note = -1;
		int octave = -1;
		
		int n = 4*12 + NOTE_A;
		
		if (f == mA4Freq)
		{
			note = NOTE_A;
			octave = 4;
		}
		else if (f > mA4Freq)
		{
			double tt = Math.pow(2.0, 1.0/12.0);
			double freq = mA4Freq * Math.pow(2.0, 1.0/24.0);
			while (f > freq)
			{
				freq *= tt;
				n++;
			}
		}
		else
		{
			double tt = Math.pow(2.0, -1.0/12.0);
			double freq = mA4Freq * Math.pow(2.0, -1.0/24.0);
			while (f < freq)
			{
				freq *= tt;
				n--;
			}
		}
		
		note = n%12;
		octave = n/12;
		//Log.d(TAG, "Freq Note: " + note + " : " + octave);
		mNote = note;
		mOctave = octave;
	}
	
	/**
	 * Populates field mBins with the brightness at each time
	 * Assumes there will be enough samples to fill the bins
	 * 
	 * @param audioBuffer An array of audio samples to use
	 * @param length How much of the array to use in the calculation
	 */
	private void process(short[] audioBuffer, int length)
	{
		short maxSample = 0;
		long aveSample = 0;
		int aveLength = 0;
		for (int i=0 ; i<length ; i++)
		{
			if (audioBuffer[i] > maxSample)
			{
				maxSample = audioBuffer[i];
			}
			if (audioBuffer[i] > 0) // only take +ve values into account
			{
				aveSample += audioBuffer[i];
				aveLength++;
			}
		}
		if (aveLength > 0)
		{
			aveSample /= aveLength;
		}
		short flashThreshold = (short) Math.max((maxSample * 0.7), mFlashThreshold);
		flashThreshold = (short) Math.max(flashThreshold, (aveSample+maxSample)/2);
		
		if (maxSample > 2*mFlashThreshold)
		{
			//consider doing frequency analysis since we might have enough data
			if (mAutoDetect)
			{
				makeWindow(audioBuffer, mSamplesRead);
			}
		}
		
		int[] flashBins = new int[BIN_COUNT]; //Flash intensity per bin
		
		double samplesPerSec = SAMPLE_FREQUENCY; // e.g. 44100
		
		double secondsPerBucket = 1.0 / (getTargetFreq() * (1.0 + (mCalibrationFactor / CALIBRATION_FACTOR_DIVISOR))) * BUCKETS_PER_HZ; // e.g. 1/880
		double bucketsPerBin = 1.0 / BIN_COUNT;
		double samplesPerBin = samplesPerSec * secondsPerBucket * bucketsPerBin;
		
		//if (DEBUG) Log.i(TAG, "SamplesPerBin = " + samplesPerBin + " Proportion = " + mProportion);
		
		// calculate flash intensity for each rotational position (flashbins)
		// slightly different math if there are more samples or more bins
		if (samplesPerBin < 1)
		{
			int bin = 0;
			for (int i = 0 ; i<length ; i++)
			{
				while (mProportion < 1)
				{
					if (audioBuffer[i % length] > flashThreshold)
					{
						flashBins[(bin+mFlashBinOffset) % BIN_COUNT] += audioBuffer[i % length] * samplesPerBin;
					}
					bin += 1;
					mProportion += samplesPerBin;
				}
				mProportion -= 1;
			}
			mFlashBinOffset = (bin+mFlashBinOffset) % BIN_COUNT;
		}
		else
		{
			int bin = 0;
			for (int i = 0 ; i<length ; i++)
			{
				mProportion += 1;
				if (mProportion > samplesPerBin)
				{
					bin += 1;
					mProportion -= samplesPerBin;
				}
				if (audioBuffer[i % length] > flashThreshold)
				{
					flashBins[(bin+mFlashBinOffset) % BIN_COUNT] += audioBuffer[i % length];
				}
			}
			mFlashBinOffset = (bin+mFlashBinOffset) % BIN_COUNT;
		}
		
		//clear the bins
		for (int r=0 ; r<RINGS ; r++)
		{
			for (int b=0 ; b<BIN_COUNT ; b++)
			{
				mBins[r][b] = 0;
			}
		}
		
		/* Using flash intensity, calculate the brightness at each bin */
		for (int r=0 ; r<RINGS ; r++)
		{
			// fill in the first mask (light + dark)
			int totalMaskSize = BIN_COUNT / (int) Math.pow(2, r);
			int maskSize = (int) (totalMaskSize * mOpenProportion); //BIN_COUNT / (int) Math.pow(2, r+1);
			//int openSize = totalMaskSize - maskSize;
			for (int b=0 ; b<BIN_COUNT ; b++)
			{
				if (flashBins[b] > 0)
				{
					for (int m=0 ; m<maskSize ; m++)
					{
						int index = (b+m) % (totalMaskSize);
						mBins[r][index] += flashBins[b];
					}
				}
			}
			
			// fill in subsequent masks by copying first mask
			int offset = totalMaskSize;
			while (offset < BIN_COUNT-maskSize)
			{
				for(int m=0 ; m<totalMaskSize ; m++)
				{
					mBins[r][m+offset] = mBins[r][m];
				}
				offset += totalMaskSize;
			}
		}
	}
	
	/*
	private double rescale(double value, double oldLower, double oldUpper, double lower, double upper)
	{
		if (value <= oldLower) return lower;
		if (value >= oldUpper) return upper;
		
		double newValue = (value - oldLower)/(oldUpper - oldLower);
		newValue = newValue * (upper - lower) + lower;
		return (int) newValue;
				
	}
	// */
	
	private void doDraw(Canvas canvas)
	{
		if (canvas == null)
		{
			//whoa... WTF...
			return;
		}
			
		int cx = canvas.getWidth()/2;
		int cy = canvas.getHeight()/2;
		int radius = (int) (Math.min(canvas.getWidth(), canvas.getHeight()) / 2.2);
		int sizes = radius / (RINGS + 2); // 2 for centre
		
		// int flashesPerBin = mSamplesRead / BIN_COUNT;

		// blank out the canvas
		canvas.drawPaint(mPaintBackground);
		
		// draw initial circle
		canvas.drawCircle(cx, cy, radius + 5, mPaintCircle);
		
		float sweepAngle = 1 / (float)BIN_COUNT * 180;
		
		int tempRadius = radius;
		for (int r=RINGS-1 ; r>=0 ; r--)
		{
			// black out strobe
			canvas.drawCircle(cx, cy, tempRadius+2, mPaintDark);
			
			//calc the average
			double ave = 0;
			for (int i=0 ; i<BIN_COUNT ; i++)
			{
				ave += mBins[r][i];
			}
			ave /= BIN_COUNT;
			ave *= DRAW_AVE_RATIO;
			
			// find lit bins and draw them
			for (int i=0 ; i<BIN_COUNT ; i++)
			{
				int litBins = 0;
				int startBin = i;
				
				//draw medium bins
				while (i < BIN_COUNT && mBins[r][i] > 0 && mBins[r][i] <= ave)
				{
					litBins++;
					i++;
				}
				if (litBins > 0)
				{
					canvas.drawArc(new RectF(cx-tempRadius, cy-tempRadius, cx+tempRadius, cy+tempRadius), 
							-startBin*sweepAngle, -litBins*sweepAngle, true, mPaintMedium);
					canvas.drawArc(new RectF(cx-tempRadius, cy-tempRadius, cx+tempRadius, cy+tempRadius), 
							-startBin*sweepAngle + 180, -litBins*sweepAngle, true, mPaintMedium);
				}
				
				//*
				litBins = 0;
				startBin = i;
				
				//draw bright bins
				while (i < BIN_COUNT && mBins[r][i] > ave)
				{
					litBins++;
					i++;
				}
				if (litBins > 0)
				{
					canvas.drawArc(new RectF(cx-tempRadius, cy-tempRadius, cx+tempRadius, cy+tempRadius), 
							-startBin*sweepAngle, -litBins*sweepAngle, true, mPaintBright);
					canvas.drawArc(new RectF(cx-tempRadius, cy-tempRadius, cx+tempRadius, cy+tempRadius), 
							-startBin*sweepAngle + 180, -litBins*sweepAngle, true, mPaintBright);
				}
				// */
			}
			tempRadius -= sizes;
		}
		// black out centre
		canvas.drawCircle(cx, cy, sizes*2, mPaintDark);
		
		// draw sharp + flat
		Path sharp = new Path();
		sharp.addArc(new RectF(cx-radius-10, cy-radius-10, cx+radius+10, cy+radius+10), 300, 30);
		canvas.drawTextOnPath("Sharp   >", sharp, 0, 0, mPaintTextFreq);
		
		Path flat = new Path();
		flat.addArc(new RectF(cx-radius-10, cy-radius-10, cx+radius+10, cy+radius+10), 226, 30);
		canvas.drawTextOnPath("<   Flat", flat, 0, 0, mPaintTextFreq);
		
		// draw note in centre
		String t1 = NOTES[mNote];
		String t3 = "" + mOctave;
		
		Rect b1 = new Rect();
		Rect b2 = new Rect();
		Rect b3 = new Rect();
		
		mPaintText.getTextBounds(t1, 0, t1.length(), b1);
		mPaintTextSharp.getTextBounds("#", 0, 1, b2);
		mPaintText.getTextBounds(t3, 0, t3.length(), b3);
		
		int width = b1.right + b2.right + b3.right;
		canvas.drawText(t1, cx - width/2 , cy-2 , mPaintText);
		if (SHARPS[mNote]) // check for #
		{
			canvas.drawText("#", cx - width/2 + b1.right , cy-2-b2.top+b1.top , mPaintTextSharp);
		}
		canvas.drawText(t3, cx - width/2 + b1.right+b2.right , cy-2 , mPaintText);
		
		
		//draw frequency
		String text = mDecimalFormat.format(getTargetFreq()) + " Hz";
		Rect bounds = new Rect();
		mPaintTextFreq.getTextBounds(text, 0, text.length(), bounds);
		canvas.drawText(text, cx - bounds.right/2, cy - bounds.top + 1, mPaintTextFreq);
		
		if (mAutoDetect)
		{
			String textAuto = "Auto";
			Rect boundsAuto = new Rect();
			mPaintTextFreq.getTextBounds(textAuto, 0, textAuto.length(), boundsAuto);
			canvas.drawText(textAuto, cx-boundsAuto.right/2, cy-bounds.top-boundsAuto.top+2, mPaintTextFreq);
		}
	}
}
