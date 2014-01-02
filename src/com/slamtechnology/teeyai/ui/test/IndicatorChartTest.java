package com.slamtechnology.teeyai.ui.test;

import static org.junit.Assert.*;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.Containers;
import org.fest.swing.fixture.FrameFixture;
import org.junit.BeforeClass;
import org.junit.Test;

import com.slamtechnology.teeyai.ui.IndicatorChart;

public class IndicatorChartTest {

	static FrameFixture window;
	
	@BeforeClass public static void setup(){
		
		IndicatorChart frame = GuiActionRunner.execute(new GuiQuery<IndicatorChart>() {
			protected IndicatorChart executeInEDT() {
				return new IndicatorChart(null, null);  
			}
		});
    	window = Containers.showInFrame(frame);
		window.show();
	}
	
	@Test public void shouldPass(){
		assertNotNull(window.label("chartLabel").component());
		
	}
}
