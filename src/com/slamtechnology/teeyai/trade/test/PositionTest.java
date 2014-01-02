package com.slamtechnology.teeyai.trade.test;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.slamtechnology.teeyai.trade.Position;

public class PositionTest {
	
	@Test public void shouldConvertDates(){
		Position p1 = new Position("test", 1.0, true);
		p1.setOpened(new Date(System.currentTimeMillis()));
		p1.setClosed(new Date(System.currentTimeMillis()));
		String msg  = p1.toMessageString();
		Position p2 = Position.fromMessageString(msg);
		Assert.assertEquals(p1, p2);
	}

}
