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
import org.jfree.data.xy.XYDataset;

import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.name.FeedMonitorName;
import com.slamtechnology.teeyai.prices.Price;

public abstract class ChartCreator {
	private HashMap<ChartNameProvider,XYDataset> collections;
	//JFreeChart chart;
	String chartName;
	//XYLineAndShapeRenderer lineRenderer;
	//XYLineAndShapeRenderer spotRenderer;
	SimpleDateFormat sdf;
	HashMap<ChartNameProvider,Boolean> lineRenderers;
	ChartNameProvider firstkey = null;
	
	public ChartCreator(String chartName,Price[] history,ChartNameProvider datasetName){
		/**
		* A demonstration application showing how to create a simple time series
		* chart. This example uses monthly data.
		*
		* @param title the frame title.
		*/
		sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GB"));
		
		//lineRenderer = new XYLineAndShapeRenderer(true,false);
		//spotRenderer = new XYLineAndShapeRenderer(false,true);
		//spotRenderer.setShapesFilled(false);
		lineRenderers = new HashMap<ChartNameProvider,Boolean>();

		collections = new HashMap<ChartNameProvider,XYDataset>();
		if(history!=null){
			collections.put(datasetName, createHistory(datasetName, history));
		}
		this.chartName = chartName;

		
	}


	protected abstract JFreeChart createChart(XYDataset dataset,String chartName);	
	private static TimeSeriesCollection createHistory(ChartNameProvider name,Price[] history) {
			
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		if(history!=null){
			TimeSeries s1 = new TimeSeries(name.getChartName(), Millisecond.class);
			for(Price p:history){
				s1.add(new Millisecond(new Date(p.getTime())), p.getPrice());
			}
			dataset.addSeries(s1);
		}
		return dataset;
	}
	
	public final void updatePrice(ChartNameProvider datasetName,long time,double[] price){
		updatePrice(datasetName,time,price,true);
	}
	/*public void updatePrice(String datasetName,long time,double price,boolean line){
		if(timeSeriesCollection.getSeries(datasetName)==null){
			TimeSeries s1 = new TimeSeries(datasetName, Millisecond.class);
			timeSeriesCollection.addSeries(s1);
			lineRenderers.add(line);
		}
		timeSeriesCollection.getSeries(datasetName).addOrUpdate(new Millisecond(new Date(time)),price);
	}*/
	public void updatePrice(ChartNameProvider datasetName,long time,double price,boolean line){
		updatePrice(datasetName, time, new double[]{price},line);
	}
	public abstract void updatePrice(ChartNameProvider datasetName,long time,double[] price,boolean line);
	
	public final void updatePrices(FeedMonitorName datasetName,Price[] prices){
		collections.put(datasetName,createHistory(datasetName, prices));
	}
	protected abstract XYDataset createNewDataCollection();
	public final BufferedImage getImage(int width,int height){
		Iterator<ChartNameProvider> keys = collections.keySet().iterator();
		XYDataset t = null;
		ChartNameProvider key = firstkey;
		if(key==null&&keys.hasNext()){
			key = keys.next();
		}
		t = collections.get(key);
		if(t==null){
			t = createNewDataCollection();
		}
		
		JFreeChart chart = createChart(t,chartName);
		chart.setTitle("");
		XYPlot plot = (XYPlot) chart.getPlot();
		
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(sdf);
		//plot.setRenderer(index, renderer)
		plot.setBackgroundPaint(Color.white);

		Boolean isLine = lineRenderers.get(key);
		if(isLine!=null){
			AbstractXYItemRenderer renderer = getRenderer(isLine,t);
			plot.setRenderer(renderer);
		}
		
		int count=1;
		while(keys.hasNext()){
			key = keys.next();
			XYDataset ds = collections.get(key);
			plot.setDataset(count, ds);
			//System.out.println("Searching for renderer for "+key);
			isLine = lineRenderers.get(key);
			AbstractXYItemRenderer renderer = getRenderer(isLine,ds);
			plot.setRenderer(count,renderer);
			//System.out.println("Adding renderer line ? "+isLine.booleanValue()+" for "+key+" at "+count);
			count++;
		}
		
		BufferedImage bi = chart.createBufferedImage(width, height);
		chart = null;
		return bi;
		
	}
	protected abstract AbstractXYItemRenderer getRenderer(boolean isLine, XYDataset ds);


	protected HashMap<ChartNameProvider, XYDataset> getCollections() {
		return collections;
	}


	public final void writeImage(String filename,int width,int height){
		
		BufferedImage bi = getImage(width, height);
		
		try {
			File outputFile = new File(filename);
			ImageIO.write(bi, "gif", outputFile);
			bi.getGraphics().dispose();
			bi = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public final void clearValues(){
		collections = new HashMap<ChartNameProvider,XYDataset>();
	}
}
