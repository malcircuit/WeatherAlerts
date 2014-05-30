package net.theneophyte.weatheralerts.graphics;

public class PixelBuffer {

	private final String mThreadName;
	
	public PixelBuffer(){
		mThreadName = Thread.currentThread().getName();
	}
	
	private boolean checkIfGlThread(){
		if (Thread.currentThread().getName().equals(mThreadName)){
			return true;
		}
		return false;
	}
}
