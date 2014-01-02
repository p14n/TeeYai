package com.slamtechnology.teeyai.ui.test;

import com.slamtechnology.teeyai.BarAndLineChartCreator;
import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.prices.Price;

public class TestChartCreator extends BarAndLineChartCreator {

	public TestChartCreator(
			ChartNameProvider datasetName) {
		super("test", new Price[]{new Price()}, datasetName);
	}

}
