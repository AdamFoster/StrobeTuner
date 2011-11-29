package net.adamfoster.android.strobe;

public class RecorderThread extends Thread
{
	private Recorder mRecorder;

	public RecorderThread(Recorder recorder)
	{
		mRecorder = recorder;
	}

	@Override
	public void run()
	{
		mRecorder.run();
	}
}
