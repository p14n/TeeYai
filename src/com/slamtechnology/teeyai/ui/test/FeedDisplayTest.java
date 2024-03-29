package com.slamtechnology.teeyai.ui.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.swing.JDesktopPane;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.Containers;
import org.fest.swing.fixture.FrameFixture;
import org.junit.BeforeClass;
import org.junit.Test;
import static com.slamtechnology.teeyai.ui.test.TestUtil.*;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.indicators.test.BinaryIndicator;
import com.slamtechnology.teeyai.ui.FeedDisplay;
import com.slamtechnology.teeyai.ui.TeeYaiDisplay;

public class FeedDisplayTest {

	static FrameFixture window;
	
	@BeforeClass public static void setup(){
		
		JDesktopPane frame = GuiActionRunner.execute(new GuiQuery<JDesktopPane>() {
			protected JDesktopPane executeInEDT() {
				TeeYaiDisplay d = mock(TeeYaiDisplay.class);
				return desktopFor(
						new FeedDisplay(d,new TestFeedMonitor(), new Indicator[]{new BinaryIndicator()}, true));  
			}
		});
    	window = Containers.showInFrame(frame);
		window.show();
	}
	
	@Test public void shouldPass(){
		assertNotNull(window.label("chartLabel").component());
		
	}
}
