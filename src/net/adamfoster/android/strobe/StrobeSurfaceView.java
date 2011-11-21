package net.adamfoster.android.strobe;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class StrobeSurfaceView extends SurfaceView implements Callback
{
	//private Context mContext;
	private static String TAG = "StrobeSurfaceView";
	
	Recorder mRecorder;
	
	public StrobeSurfaceView(Context context)
	{
		super(context);
		init(context);
	}
    
	public StrobeSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	
	public StrobeSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context)
	{
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mRecorder = new Recorder(holder, context);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		mRecorder.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
        setKeepScreenOn(true);

       	mRecorder.setRunning(true);
       	mRecorder.start();
	}

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.i(TAG, "Destroying Surface");
        boolean retry = true;
        mRecorder.setRunning(false);
        setKeepScreenOn(false);
        while (retry) 
        {
            try 
            {
            	mRecorder.join();
                retry = false;
            } 
            catch (InterruptedException e) 
            {
            }
        }
	}
	
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) 
    {
        if (!hasWindowFocus) 
    	{
        	//mRecorder.pause();
    	}
    }

	
	public Recorder getRecorder()
	{
		return mRecorder;
	}

}
