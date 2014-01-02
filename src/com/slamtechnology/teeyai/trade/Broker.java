package com.slamtechnology.teeyai.trade;

public interface Broker {
	
	public long getID();
	public void setOpenPosition(ExecutionListener ex,Position p,double tolerance);
	public String getName();
	public void setClosePosition(ExecutionListener ex,Position p,double tolerance);
	public void execute();

}
