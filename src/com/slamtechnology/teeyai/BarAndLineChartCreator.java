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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import antlr.CharFormatter;

import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.Price;

public class BarAndLineChartCreator extends ChartCreator {
	
	public BarAndLineChartCreator(String chartName,Price[] history,ChartNameProvider datasetName){
		super(chartName, history, datasetName);
	}
	protected JFreeChart createChart(XYDataset dataset,String chartName) {

		JFreeChart chart = null;
			try {
			chart = ChartFactory.createTimeSeriesChart(
		chartName, // title
		"Time", // x-axis label
		"Price", // y-axis label
		dataset, // data
		true, // create legend?
		false, // generate tooltips?
		false // generate URLs?
		);
			} catch (Exception e){
				System.out.println(chartName);
				e.printStackTrace();
			}
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		/*XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}*/
		return chart;
	}
	
	public void updatePrice(ChartNameProvider datasetName,long time,double[] price,boolean line){
		if(getCollections().get(datasetName)==null){
			TimeSeriesCollection  timeSeriesCollection = new TimeSeriesCollection();
			TimeSeries s1 = new TimeSeries(datasetName.getChartName(), Millisecond.class);
			timeSeriesCollection.addSeries(s1);
			getCollections().put(datasetName, timeSeriesCollection);
			//if(line)
				lineRenderers.put(datasetName,line);
			System.out.println("Storing renderer "+(line?"line":"spot")+" for "+datasetName);
		}
		if(time>0){
			TimeSeriesCollection  timeSeriesCollection = (TimeSeriesCollection)getCollections().get(datasetName);
			timeSeriesCollection.getSeries(datasetName.getChartName()).addOrUpdate(new Millisecond(new Date(time)),price[0]);
		}
	}
	
	public static void main(String args[]){
		BarAndLineChartCreator c = new BarAndLineChartCreator("test",null,new FeedMonitorName("test1",1));
		//c.getImage(200, 200);
	}
	@Override
	protected XYDataset createNewDataCollection() {
		return new TimeSeriesCollection();
	}
	@Override
	protected AbstractXYItemRenderer getRenderer(boolean isLine,XYDataset ds) {
		XYLineAndShapeRenderer renderer = null;
			if(isLine){
				renderer = new XYLineAndShapeRenderer(true,false);
			} else {
				renderer = new XYLineAndShapeRenderer(false,true);
				renderer.setShapesFilled(false);
			}
		
			return renderer;
	}
}
