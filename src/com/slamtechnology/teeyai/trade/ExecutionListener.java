package com.slamtechnology.teeyai.trade;

public interface ExecutionListener {
	
	public void confirmOpenPosition(Position p);
	public void confirmFailedOpen(Position p);
	public void confirmFailedClose(Position p);
	public void confirmClosePosition(Position p);

}
