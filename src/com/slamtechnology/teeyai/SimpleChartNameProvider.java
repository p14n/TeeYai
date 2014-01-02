package com.slamtechnology.teeyai;

import com.slamtechnology.teeyai.name.ChartNameProvider;

public class SimpleChartNameProvider implements ChartNameProvider {

	String name;
	public String getChartName() {
		return name;
	}
	public SimpleChartNameProvider(String name) {
		super();
		this.name = name;
	}

}
