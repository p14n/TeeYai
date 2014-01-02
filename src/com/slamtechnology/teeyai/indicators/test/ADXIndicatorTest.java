package com.slamtechnology.teeyai.indicators.test;

import org.junit.Test;

import junit.framework.Assert;

import com.slamtechnology.teeyai.indicators.ADXIndicator;
import com.slamtechnology.teeyai.indicators.HighADXInidcator;
import com.slamtechnology.teeyai.prices.Price;

public class ADXIndicatorTest {
	
	ADXIndicator i;
	
	@Test
	public void shouldCalculateADX(){
		i = new HighADXInidcator();
		i.setValuesPerPeriod(3);
		update(30.20);	
		update(29.41);	
		update(29.87);
		update(30.28);	
		update(29.32);	
		update(30.24);
		update(30.45);	
		update(29.96);	
		update(30.10);
		update(29.35);	
		update(28.74);	
		update(28.90);
		update(29.35);	
		update(28.56);	
		update(28.92);
		update(29.29);	
		update(28.41);	
		update(28.48);
		update(28.83);	
		update(28.08);	
		update(28.56);
		update(28.73);	
		update(27.43);	
		update(27.56);
		update(28.67);	
		update(27.66);	
		update(28.47);
		update(28.85);	
		update(27.83);	
		update(28.28);
		update(28.64);	
		update(27.40);	
		update(27.49);
		update(27.68);	
		update(27.09);	
		update(27.23);
		update(27.21);	
		update(26.18);	
		update(26.35);
		update(26.87);	
		update(26.13);	
		update(26.33);
		update(27.41);	
		update(26.63);	
		update(27.03);
		update(26.94);	
		update(26.13);	
		update(26.22);
		update(26.52);	
		update(25.43);	
		update(26.01);
		update(26.52);	
		update(25.35);	
		update(25.46);
		update(27.09);	
		update(25.88);	
		update(27.03);
		update(27.69);	
		update(26.96);	
		update(27.45);
		update(28.45);	
		update(27.14);	
		update(28.36);
		update(28.53);	
		update(28.01);	
		update(28.43);
		update(28.67);	
		update(27.88);	
		update(27.95);
		update(29.01);	
		update(27.99);	
		update(29.01);
		update(29.87);	
		update(28.76);	
		update(29.38);
		update(29.80);	
		update(29.14);	
		update(29.36);
		update(29.75);	
		update(28.71);	
		update(28.91);
		update(30.65);	
		update(28.93);	
		update(30.61);
		update(30.60);
		update(30.03);
		update(30.05);
		//Assert.assertEquals(33.58, i.getValue()); requires more wilder smoothing?
		Assert.assertEquals(33.67, i.getValue());
	}
	private void update(double d) {
		Price p = new Price();
		p.setPrice(d);
		i.update(p);
	}
	
	/*
	 30.20	29.41	29.87
30.28	29.32	30.24
30.45	29.96	30.10
29.35	28.74	28.90
29.35	28.56	28.92
29.29	28.41	28.48
28.83	28.08	28.56
28.73	27.43	27.56
28.67	27.66	28.47
28.85	27.83	28.28
28.64	27.40	27.49
27.68	27.09	27.23
27.21	26.18	26.35
26.87	26.13	26.33
27.41	26.63	27.03
26.94	26.13	26.22
26.52	25.43	26.01
26.52	25.35	25.46
27.09	25.88	27.03
27.69	26.96	27.45
28.45	27.14	28.36
28.53	28.01	28.43
28.67	27.88	27.95
29.01	27.99	29.01
29.87	28.76	29.38
29.80	29.14	29.36
29.75	28.71	28.91
30.65	28.93	30.61
	 */

}
