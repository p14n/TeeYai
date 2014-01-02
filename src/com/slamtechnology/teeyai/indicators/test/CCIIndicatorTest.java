package com.slamtechnology.teeyai.indicators.test;

import org.junit.Test;

import junit.framework.Assert;

import com.slamtechnology.teeyai.indicators.CCIOverDirectionalIndicator;
import com.slamtechnology.teeyai.indicators.CCIOverIndicator;
import com.slamtechnology.teeyai.prices.Price;

public class CCIIndicatorTest {
	
	CCIOverIndicator i;
	
	@Test
	public void shouldCalculateCCI(){
		i = new CCIOverDirectionalIndicator();
		i.setValuesPerPeriod(3);
		update(24.20	);
		update(23.85	);
		update(23.89);
		update(24.07);	
		update(23.72);	
		update(23.95);
		update(24.04);	
		update(23.64);	
		update(23.67);
		update(23.87);	
		update(23.37);	
		update(23.78);

		update(23.67);	
		update(23.46);	
		update(23.50);

		update(23.59);	
		update(23.18);	
		update(23.32);
		update(23.80);	
		update(23.40);	
		update(23.75);
		update(23.80);	
		update(23.57);	
		update(23.79);
		update(24.30);	
		update(24.05);	
		update(24.14);
		update(24.15);	
		update(23.77);	
		update(23.81);
		update(24.05);	
		update(23.60);	
		update(23.78);
		update(24.06);	
		update(23.84);	
		update(23.86);
		update(23.88);	
		update(23.64);	
		update(23.70);

		update(25.14);	
		update(23.94);	
		update(24.96);
		update(25.20);	
		update(24.74);	
		update(24.88);
		update(25.07);	
		update(24.77);	
		update(24.96);
		update(25.22);	
		update(24.90);	
		update(25.18);
		update(25.37);	
		update(24.93);	
		update(25.07);
		update(25.36);	
		update(24.96);	
		update(25.27);
		update(25.26);	
		update(24.93);	
		update(25.00);
		update(24.82);	
		update(24.21);	
		update(24.46);
		update(24.44);	
		update(24.21);	
		update(24.28);
		update(24.65);	
		update(24.43);	
		update(24.62);
		update(24.84);	
		update(24.44);	
		update(24.58);
		update(24.75);	
		update(24.20);	
		update(24.53);
		update(24.51);	
		update(24.25);	
		update(24.35);
		update(24.68);	
		update(24.21);	
		update(24.34);
		update(24.67);	
		update(24.15);	
		update(24.23);
		update(23.84);	
		update(23.63);	
		update(23.76);
		update(24.30);	
		update(23.76);	
		update(24.20);
		Assert.assertEquals(-73.07, i.getValue());

	}
	private void update(double d) {
		Price p = new Price();
		p.setPrice(d);
		i.update(p);
	}
}
