package com.slamtechnology.teeyai.ui;

import javax.swing.JCheckBox;

import com.slamtechnology.teeyai.name.IndicatorInstanceName;

public class StrategyCheckBox extends JCheckBox {
	
	private IndicatorInstanceName indicatorInstanceName;
	
	public StrategyCheckBox(IndicatorInstanceName indicatorInstanceName){
		super(indicatorInstanceName.getName());
		this.indicatorInstanceName = indicatorInstanceName;
	}

	public IndicatorInstanceName getIndicatorInstanceName() {
		return indicatorInstanceName;
	}

}
