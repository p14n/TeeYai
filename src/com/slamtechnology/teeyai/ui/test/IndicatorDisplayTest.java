package com.slamtechnology.teeyai.ui.test;

import static com.slamtechnology.teeyai.ui.test.TestUtil.desktopFor;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.swing.JDesktopPane;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.Containers;
import org.fest.swing.fixture.FrameFixture;
import org.junit.BeforeClass;
import org.junit.Test;

import com.slamtechnology.teeyai.ui.StandardIndicatorDisplay;
import com.slamtechnology.teeyai.ui.TeeYaiDisplay;

public class IndicatorDisplayTest {

	static FrameFixture window;
	
	@BeforeClass public static void setup(){
		
		JDesktopPane frame = GuiActionRunner.execute(new GuiQuery<JDesktopPane>() {
			protected JDesktopPane executeInEDT() {
				TeeYaiDisplay d = mock(TeeYaiDisplay.class);
				return desktopFor(
						new StandardIndicatorDisplay("Test indicator",d));  
			}
		});
    	window = Containers.showInFrame(frame);
		window.show();
	}
	
	@Test public void shouldPass(){
		assertNotNull(window.label("chartLabel").component());
		
	}
}
