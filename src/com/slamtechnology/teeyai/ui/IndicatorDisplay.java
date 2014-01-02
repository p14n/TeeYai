package com.slamtechnology.teeyai.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.IndicatorOption;
import static com.slamtechnology.teeyai.ui.Util.*;

public abstract class IndicatorDisplay extends JInternalFrame {
	
	public IndicatorDisplay(String title,TeeYaiDisplay tyDisplay) {
		super(title);
		this.teeYaiDisplay = tyDisplay;
	}

	Indicator myIndicator;
	TeeYaiDisplay teeYaiDisplay;
	
	private ImageIcon red;
	private ImageIcon amber;
	private ImageIcon green;
	
	JLabel buyImg;
	JLabel sellImg;
	JLabel shortImg;
	JLabel coverImg;
	JLabel priceLabel;
	
	boolean wasBuy;
	boolean wasSell;
	boolean wasShort;
	boolean wasCover;
	
	JComboBox optionBox;
	JTextField text;

	private ImageIcon getImage(String gif){
		try {
			return new ImageIcon(ImageIO.read(this.getClass().getResource(gif)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	

	public void setIndicator(Indicator indicator) {
		myIndicator = indicator;
	}
	
	public void updateValues(boolean backTesting) {
		
		if(!backTesting){
			//priceLabel.setText(String.valueOf(Math.round(myIndicator.getValue()*1000)/1000));
			priceLabel.setText(String.valueOf(myIndicator.getValue()));
		
			if(wasBuy&&!myIndicator.isBuy()){
				buyImg.setIcon(getAmber());
				wasBuy=false;
			} else if(!wasBuy&&myIndicator.isBuy()){
				buyImg.setIcon(getGreen());
				wasBuy=true;
			}
			
			if(wasSell&&!myIndicator.isSell()){
				sellImg.setIcon(getAmber());
				wasSell=false;
			} else if(!wasSell&&myIndicator.isSell()){
				sellImg.setIcon(getRed());
				wasSell=true;
			}
			
			if(wasShort&&!myIndicator.isShort()){
				shortImg.setIcon(getAmber());
				wasShort=false;
			} else if(!wasShort&&myIndicator.isShort()){
				shortImg.setIcon(getRed());
				wasShort=true;
			}
			
			if(wasCover&&!myIndicator.isCover()){
				coverImg.setIcon(getAmber());
				wasCover=false;
			} else if(!wasCover&&myIndicator.isCover()){
				coverImg.setIcon(getGreen());
				wasCover=true;
			}
		}

		/*if(myIndicator.getValue()>0){
			updateMainGraph(backTesting);
		}*/
		//updateOther(backTesting);
		
	}
	
	public void construct(){

		Dimension d = new Dimension(250,100);
		setMinimumSize(d);
		setSize(d);
		setResizable(true);
		setClosable(true);
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);

		
		add(new JLabel("Value"),GBxy(1, 1));
		add(new JLabel("Buy"),GBxy(2, 1));
		add(new JLabel("Sell"),GBxy(3, 1));
		add(new JLabel("Short"),GBxy(4, 1));
		add(new JLabel("Cover"),GBxy(5, 1));
		
		priceLabel = new JLabel(String.valueOf(Math.round(myIndicator.getValue()*100)/100));
		buyImg = new JLabel( getAmber());
		sellImg = new JLabel( getAmber());
		shortImg = new JLabel( getAmber());
		coverImg = new JLabel( getAmber());
		
		add(priceLabel,GBxy(1, 2));
		add(buyImg,GBxy(2, 2));
		add(sellImg,GBxy(3, 2));
		add(shortImg,GBxy(4, 2));
		add(coverImg,GBxy(5, 2));
		
		addInternalFrameListener(new InternalFrameAdapter() {

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						teeYaiDisplay.removeIndicator(myIndicator,IndicatorDisplay.this);;
					}
				});
				super.internalFrameClosing(e);
			}
		
		});

		HashMap<String, IndicatorOption> opts = myIndicator.getOptions();
		int count=0;
		
		if(opts!=null&&opts.size()>0){

			optionBox = new JComboBox();
			Set<String> keys = opts.keySet();
			text = new JTextField("");
			for(String key : keys){
				IndicatorOption o = opts.get(key); 
				optionBox.addItem(o);
				if(count==0)text.setText(String.valueOf(o.getValue()));
				count++;
			}
			optionBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					IndicatorOption o = (IndicatorOption)optionBox.getSelectedItem();
					text.setText(String.valueOf(o.getValue()));
				}
			});
			
			add(optionBox,GBxyhw(1, 3, 1, 2));
			add(text,GBxy(2, 3));
	
			JButton update = new JButton("Update");
			update.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TeeYaiDisplay tyd = (TeeYaiDisplay)IndicatorDisplay.this.getParent();
					IndicatorOption o = (IndicatorOption)optionBox.getSelectedItem();
					double value = 0;
					try{
						value = Double.parseDouble(text.getText());
						o.setValue(value);
					} catch(NumberFormatException fe){
						text.setText("");
						fe.printStackTrace();
					}
					tyd.updateIndicatorOption(myIndicator.getFeedName(),myIndicator.getInstanceName(), o.getName(), value);
				}
			});

		}
		
	}


	protected ImageIcon getAmber() {
		if(amber==null){
			amber = getImage("/resource/amber.gif"); 
		}
		return amber;
	}

	protected ImageIcon getGreen() {
		if(green==null){
			green = getImage("/resource/green.gif"); 
		}
		return green;
	}

	protected ImageIcon getRed() {
		if(red==null){
			red = getImage("/resource/red.gif"); 
		}
		return red;
	}


	public Indicator getIndicator() {
		return myIndicator;
	}

}
