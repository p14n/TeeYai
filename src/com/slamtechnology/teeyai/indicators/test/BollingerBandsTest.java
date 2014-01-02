package com.slamtechnology.teeyai.indicators.test;

import junit.framework.TestCase;

import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.indicators.BollingerBands;
import com.slamtechnology.teeyai.prices.Price;

public class BollingerBandsTest extends TestCase {

	private Price constructPrice(double price){
		Price p = new Price();
		p.setPrice(price);
		return p;
	}
	public void testAll(){
		BollingerBands b = new BollingerBands();
		b.setOption(IndicatorOption.VALS_TO_MONITOR,5);
		b.update(constructPrice(31.8750));
		b.update(constructPrice(32.1250));
		b.update(constructPrice(32.3125));
		b.update(constructPrice(32.1250));
		b.update(constructPrice(31.8750));
		b.update(constructPrice(32.3125));
		b.update(constructPrice(32.2500));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.8125));
		b.update(constructPrice(32.3750));
		b.update(constructPrice(32.5000));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.7500));
		b.update(constructPrice(33.1875));
		b.update(constructPrice(33.0625));
		b.update(constructPrice(33.0625));
		b.update(constructPrice(33.1250));
		b.update(constructPrice(33.0625));
		b.update(constructPrice(32.8125));
		b.update(constructPrice(32.8750));
		b.update(constructPrice(33.2500));
		b.update(constructPrice(33.1250));
		b.update(constructPrice(33.5625));
		b.update(constructPrice(33.5000));
		b.update(constructPrice(32.7500));
		b.update(constructPrice(32.6875));
		b.update(constructPrice(32.6875));
		b.update(constructPrice(33.0000));
		b.update(constructPrice(32.9375));
		b.update(constructPrice(33.2500));
		b.update(constructPrice(33.1250));
		b.update(constructPrice(33.3125));
		b.update(constructPrice(33.3750));
		b.update(constructPrice(33.3750));
		b.update(constructPrice(33.0625));
		b.update(constructPrice(33.3125));
		b.update(constructPrice(33.2500));
		b.update(constructPrice(32.6250));
		b.update(constructPrice(32.7500));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.9375));
		b.update(constructPrice(33.0625));
		b.update(constructPrice(32.6250));
		b.update(constructPrice(32.6250));
		b.update(constructPrice(32.6875));
		b.update(constructPrice(32.7500));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.6250));
		b.update(constructPrice(32.6250));
		b.update(constructPrice(32.3750));
		b.update(constructPrice(32.4375));
		b.update(constructPrice(32.8125));
		b.update(constructPrice(32.9375));
		b.update(constructPrice(32.7500));
		b.update(constructPrice(32.8125));
		/*b.update(constructPrice(21.4375));
		b.update(constructPrice(21.6875));
		b.update(constructPrice(22.1250));
		b.update(constructPrice(21.5625));
		b.update(constructPrice(21.8125));*/


	}

}
