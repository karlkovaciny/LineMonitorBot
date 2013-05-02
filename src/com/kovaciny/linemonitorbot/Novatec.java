package com.kovaciny.linemonitorbot;

class Novatec implements Hopper {
	float mCapacity;
	private float mControllerSetpoint;
	float mLbsContained;
	private float mScrewSize;
	private float mRate;
	
	Novatec(float capacity, float setpoint, float screwSize) {
		mCapacity = Math.max(capacity, 0);
		mControllerSetpoint = Math.max(setpoint, 0);
		mScrewSize = Math.max(screwSize,0);
		mLbsContained = 0;
	}
	
	Novatec() {
		this(0,0,1);
	}
	
	public float getRate(){
		return mControllerSetpoint * mScrewSize;
	}
	
	public float setControllerSetpoint(float setpoint) {
		if (setpoint < 0 ) return 0;
		else return mControllerSetpoint = setpoint;
	}
	public float getControllerSetpoint() {
		return mControllerSetpoint;
	}
	
	public float estimateMinimumFullCapacity() {
		return mCapacity;
	}
	
	public float getLbsContained() {
		return mLbsContained;
	}
	
	public float setLbsContained(float lbs) {
		if (lbs < 0) return 0;
		else return mLbsContained = lbs;
	}

	public float getmScrewSize() {
		return mScrewSize;
	}

	public void setmScrewSize(float mScrewSize) {
		this.mScrewSize = mScrewSize;
	}
}