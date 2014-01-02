package com.slamtechnology.teeyai;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.Price;

public class CandleStickChartCreator extends ChartCreator {

	public CandleStickChartCreator(String chartName,Price[] history,ChartNameProvider datasetName){
		super(chartName, history, datasetName);
	}


	protected JFreeChart createChart(XYDataset dataset,String chartName) {

		JFreeChart chart = null;
		try {
			chart = ChartFactory.createCandlestickChart(
				chartName, // title
				"Time", // x-axis label
				"Price", // y-axis label
				(OHLCDataset)dataset, // data
				true
				);
		} catch (Exception e){
			System.out.println(chartName);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		chart.setBackgroundPaint(Color.white);
		
		
		/*XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}*/
		((NumberAxis)((XYPlot)chart.getPlot()).getRangeAxis()).setAutoRangeIncludesZero(false);
		return chart;
	}
	
	/*public void updatePrice(String datasetName,long time,double price,boolean line){
		if(timeSeriesCollection.getSeries(datasetName)==null){
			TimeSeries s1 = new TimeSeries(datasetName, Millisecond.class);
			timeSeriesCollection.addSeries(s1);
			lineRenderers.add(line);
		}
		timeSeriesCollection.getSeries(datasetName).addOrUpdate(new Millisecond(new Date(time)),price);
	}*/
	public void updatePrice(ChartNameProvider datasetName,long time,double[] price,boolean line){
		boolean candle = price.length==4; 
		if(getCollections().get(datasetName)==null){
			if(candle){
				firstkey = datasetName;
				OHLCSeriesCollection  timeSeriesCollection = new OHLCSeriesCollection();

				OHLCSeries s1 = new OHLCSeries(datasetName.getChartName());
				
				timeSeriesCollection.addSeries(s1);
				getCollections().put(datasetName, timeSeriesCollection);
				lineRenderers.put(datasetName,line);
				System.out.println("Storing renderer "+(line?"line":"spot")+" for "+datasetName);
			} else {
				TimeSeriesCollection  timeSeriesCollection = new TimeSeriesCollection();
				TimeSeries s1 = new TimeSeries(datasetName.getChartName(), Millisecond.class);
				timeSeriesCollection.addSeries(s1);
				getCollections().put(datasetName, timeSeriesCollection);
				lineRenderers.put(datasetName,line);
				System.out.println("Storing renderer "+(line?"line":"spot")+" for "+datasetName);
			}
		}
		//System.out.println("writing price ot chart "+datasetName+" price "+price+" time "+time);
		if(time>0){
			if(candle){
				OHLCSeriesCollection  timeSeriesCollection = (OHLCSeriesCollection)getCollections().get(datasetName);
				timeSeriesCollection.getSeries(0).add(new Millisecond(new Date(time)), price[0], price[1], price[2], price[3]);
			} else {
				TimeSeriesCollection  timeSeriesCollection = (TimeSeriesCollection)getCollections().get(datasetName);
				timeSeriesCollection.getSeries(datasetName.getChartName()).addOrUpdate(new Millisecond(new Date(time)),price[0]);
			}
		}
	}


	@Override
	protected XYDataset createNewDataCollection() {
		// TODO Auto-generated method stub
		return new OHLCSeriesCollection();
	}
	public static void main(String args[]){
		ChartNameProvider p = new ChartNameProvider() {
			public String getChartName() {
				return "test";
			}
		};
		ChartNameProvider p2 = new ChartNameProvider() {
			public String getChartName() {
				return "test2";
			}
		};
		ChartNameProvider p3 = new ChartNameProvider() {
			public String getChartName() {
				return "test3";
			}
		};
		ChartNameProvider p4 = new ChartNameProvider() {
			public String getChartName() {
				return "test4";
			}
		};
		ChartNameProvider p5 = new ChartNameProvider() {
			public String getChartName() {
				return "test5";
			}
		};
		CandleStickChartCreator c = new CandleStickChartCreator(p.getChartName(),null,new FeedMonitorName("test1",1));
		long t = System.currentTimeMillis();
		c.updatePrice(p, t, new double[]{4900,5100,4900,4950});
		c.updatePrice(p2, t, new double[]{4700});
		c.updatePrice(p4, t, new double[]{4650});
		c.updatePrice(p3, t, new double[]{5200});
		c.updatePrice(p5, t, new double[]{5250});
		t = System.currentTimeMillis();
		c.updatePrice(p, t, new double[]{4950,5050,4750,4800});
		c.updatePrice(p2, t, new double[]{4700});
		c.updatePrice(p4, t, new double[]{4650});
		c.updatePrice(p3, t, new double[]{5200});
		c.updatePrice(p5, t, new double[]{5250});
		c.writeImage("ohlc.gif",200, 200);
	}


	@Override
	protected AbstractXYItemRenderer getRenderer(boolean isLine,XYDataset ds) {
		if(ds instanceof TimeSeriesCollection){
			return new XYLineAndShapeRenderer(true,false);
		}
		CandlestickRenderer c = new CandlestickRenderer();
		return c;
	}


}
