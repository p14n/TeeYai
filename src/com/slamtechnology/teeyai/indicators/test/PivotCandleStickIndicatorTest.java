package com.slamtechnology.teeyai.indicators.test;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.slamtechnology.teeyai.indicators.PivotCandleStickIndicator;
import com.slamtechnology.teeyai.prices.CandleStick;
import com.slamtechnology.teeyai.prices.Price;
import com.sun.source.tree.AssertTree;

public class PivotCandleStickIndicatorTest {
	
	PivotCandleStickIndicator i;
	
	@Before public void setup(){
		i = new PivotCandleStickIndicator();
		
		CandleStick yesterday = new CandleStick();
		yesterday.add(new Price(5000));
		yesterday.add(new Price(5100));
		yesterday.add(new Price(4950));
		yesterday.add(new Price(5050));
		i.setClosingPrices(yesterday);
		/*	S3:4816.666666666666
				4850.0
			S2:4883.333333333333
				4925.0
			S1:4966.666666666666
				5000.0
			P:5033.333333333333
				5075.0
			R1:5116.666666666666
				:5150.0
			R2:5183.333333333333
				:5225.0
			R3:5266.666666666666
		 */
		
	}
	
	private void updateWithStick(double open,double high,double low,double close){
		i.update(new Price(open));
		i.update(new Price(high));
		i.update(new Price(low));
		i.update(new Price(close));
		i.setSwitchCandleSticks(true);
	}
	
	@Test public void shouldShowBuyAfter50pcJump(){

		updateWithStick(5080,5115,5075,5100);
		updateWithStick(5110,5140,5100,5130);
		i.update(new Price(5130));
		
		assertTrue(i.isBuy());
		assertTrue(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldShowBuyAfterBreakAndCandleOverTheLine(){

		updateWithStick(5080,5125,5075,5120);
		updateWithStick(5120,5140,5100,5130);
		i.update(new Price(5130));
		
		assertTrue(i.isBuy());
		assertTrue(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowBuyAfterNoBreakAndCandleOverTheLine(){

		updateWithStick(5080,5125,5075,5115);
		updateWithStick(5120,5140,5100,5130);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowBuyAfterBreakAndCandleNotOverTheLine(){

		updateWithStick(5080,5125,5075,5117);
		updateWithStick(5115,5140,5100,5130);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowBuyAfter50pcJumpWhenPreviousCrossedLine(){

		updateWithStick(5080,5117,5075,5117);
		updateWithStick(5110,5135,5105,5130);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowBuyAfter50pcJumpWhenPreviousWasRed(){

		updateWithStick(5110,5117,5095,5100);
		updateWithStick(5110,5135,5105,5130);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldShowSellAfter50pcDrop(){

		updateWithStick(4975,4975,4965,4970);
		updateWithStick(4970,4955,4975,4960);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertTrue(i.isShort());
		assertTrue(i.isSell());
	}
	@Test public void shouldShowSellAfterBreakAndCandleOverTheLine(){

		updateWithStick(4970,4975,4965,4965);
		updateWithStick(4965,4975,4955,4960);
		i.update(new Price(4960));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertTrue(i.isShort());
		assertTrue(i.isSell());
	}
	@Test public void shouldNotShowSellAfterNoBreakAndCandleOverTheLine(){

		updateWithStick(4970,4975,4965,4967);
		updateWithStick(4965,4975,4955,4960);
		i.update(new Price(4960));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowSellAfterBreakAndCandleNotOverTheLine(){

		updateWithStick(4970,4975,4965,4965);
		updateWithStick(4967,4975,4955,4960);
		i.update(new Price(4960));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowSellAfter40pcDrop(){

		updateWithStick(5000,5000,4945,4975);
		updateWithStick(4990,4955,4975,4960);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowSellAfter50pcDropWhenPreviousCrossedLine(){

		updateWithStick(4950,4975,4945,4975);
		updateWithStick(4970,4955,4975,4960);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	@Test public void shouldNotShowBuyAfter40pcJump(){
		
		updateWithStick(5080,5085,5090,5100);
		updateWithStick(5110,5115,5125,5120);
		i.update(new Price(5130));
		
		assertFalse(i.isBuy());
		assertFalse(i.isCover());
		assertFalse(i.isShort());
		assertFalse(i.isSell());
	}
	

}
