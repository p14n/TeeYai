package com.slamtechnology.teeyai.indicators.test;

//import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Test;

import com.slamtechnology.teeyai.IndicatorOption;
import com.slamtechnology.teeyai.indicators.EMAIndicator;
import com.slamtechnology.teeyai.prices.Price;

public class EMAIndicatorTest {
	
	@Test
	public void shouldWork() throws Exception {
		// TODO Auto-generated method stub
		eai= new EMAIndicator();
		eai.setInterval(1);
		eai.setOption(IndicatorOption.VALS_TO_MONITOR, 10);
		Price p = new Price();
		for(int i=0;i<10;i++){
			p.setPrice(i+1);
			eai.update(p);
			System.out.println("Value "+eai.getValue()+" price "+p.getPrice());
			assertTrue(eai.getValue()<=p.getPrice()&&eai.getValue()>0);
		}
	}
	private EMAIndicator eai;

	@Test public void shouldCalculate10DayEMACorrectly(){
		
		double[] prices = new double[]{
				22.27,
				22.19,
				22.08,
				22.17,
				22.18,
				22.13,
				22.23,
				22.43,
				22.24,
				22.29,
				22.15,
				22.39,
				22.38,
				22.61,
				23.36,
				24.05,
				23.75,
				23.83,
				23.95,
				23.63,
				23.82,
				23.87,
				23.65,
				23.19,
				23.10,
				23.33,
				22.68,
				23.10,
				22.40,
				22.17
		};
		eai = new EMAIndicator();
		eai.setInterval(1);
		eai.setOption(IndicatorOption.VALS_TO_MONITOR, 10);
		eai.setOption(IndicatorOption.ROUNDING, 2);
		
		for(double price:prices){
			eai.update(new Price(price));
			System.out.println(eai.getValue());
		}
		assertEquals(22.92, eai.getValue(),0.01);
	}
	

}
