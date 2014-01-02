package com.slamtechnology.teeyai.indicators;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.slamtechnology.teeyai.CandleStickChartCreator;
import com.slamtechnology.teeyai.ChartCreator;
import com.slamtechnology.teeyai.Indicator;
import com.slamtechnology.teeyai.ServiceManager;
import com.slamtechnology.teeyai.name.ChartNameProvider;
import com.slamtechnology.teeyai.prices.CandleStick;
import com.slamtechnology.teeyai.prices.Price;

public class PivotCandleStickIndicator extends Indicator {

	int interval = 300000;
	int periods = 2;
	boolean switchCandleSticks = false;
	private int lowPivotPassed,highPivotPassed,pivotPointIndex;

	
	public void setSwitchCandleSticks(boolean switchCandleSticks) {
		this.switchCandleSticks = switchCandleSticks;
	}

	List<Double> pivots;
	List<ChartNameProvider> pivotNames;
	
	CandleStick closingPrices;
	CandleStick currentPrices;
	LinkedList<CandleStick> prices;
	
	public PivotCandleStickIndicator() {
		super();
		setHasOwnChart(true);
		setIndicatorName("PIV");
		clearInternalValues();
		
	}
	private ChartNameProvider createPivotNameProvider(final String name){
		return new ChartNameProvider() {
			public String getChartName() {
				return name;
			}
		};
	}
	public ChartCreator createOwnChart(){
		return new CandleStickChartCreator(getIndicatorName(),null,getInstanceName());
	}


	@Override
	protected void clearInternalValues() {
		closingPrices = null;
		currentPrices = null;
		prices = new LinkedList<CandleStick>();
		setBuy(false);
		setShort(false);
		setSell(false);
		setCover(false);
	}
	public void setClosingPrices(CandleStick c){
		closingPrices = c;
		determinePivotPoints(c);
	}	
	private void determinePivotPoints(CandleStick c){
		if(c==null){
			throw new NullPointerException("No close prices provided to use on date "+new Date(getLatestPrice().getTime()));
		}
		pivots = new ArrayList<Double>(11);
		pivotNames = new ArrayList<ChartNameProvider>();
		double H = c.getHigh().doubleValue();
		double L = c.getLow().doubleValue();
		double C = c.getClose().doubleValue();
		double P = (H+L+C)/3;
		double S3 = L - (2*(H - P));
		double S2 = P - (H-L);
		double S1 = P - (H - P);
		double R1 = P + (P - L);
		double R2 = P + (H - L);
		double R3 = H + (2*(P - L));
		pivots.add(S3);
		pivotNames.add(createPivotNameProvider("s3"));
		pivots.add((S3+S2)/2);
		pivotNames.add(createPivotNameProvider("s3m"));
		pivots.add(S2);
		pivotNames.add(createPivotNameProvider("s2"));
		pivots.add((S1+S2)/2);
		pivotNames.add(createPivotNameProvider("s2m"));
		pivots.add(S1);
		pivotNames.add(createPivotNameProvider("s1"));
		pivots.add((S1+P)/2);
		pivotNames.add(createPivotNameProvider("s1m"));
		pivots.add(P);
		pivotPointIndex = pivots.size()-1;
		lowPivotPassed = pivotPointIndex;
		highPivotPassed = pivotPointIndex;
		pivotNames.add(createPivotNameProvider("p"));
		pivots.add((P+R1)/2);
		pivotNames.add(createPivotNameProvider("r1m"));
		pivots.add(R1);
		pivotNames.add(createPivotNameProvider("r1"));
		pivots.add((R1+R2)/2);
		pivotNames.add(createPivotNameProvider("r2m"));
		pivots.add(R2);
		pivotNames.add(createPivotNameProvider("r2"));
		pivots.add((R2+R3)/2);
		pivotNames.add(createPivotNameProvider("r3"));
		pivots.add(R3);
		pivotNames.add(createPivotNameProvider("r3m"));
		pivots.add(Double.MAX_VALUE);
		for(Double d:pivots){
			System.out.println("\nPivot:"+d);
		}
	}
	private int determinePosition(double value){
		for(int position=pivots.size()-2;position>=0;position--){
			if(value>pivots.get(position)) return position+1;
		}
		return 0;
	}

	@Override
	protected void update() {
		Price p = getLatestPrice();
		if(closingPrices == null){
			closingPrices = ServiceManager.getInstance().getYesterdaysClosingPrices(p.getTime());
			determinePivotPoints(closingPrices);
		}
		if(currentPrices==null) {
			currentPrices = new CandleStick();
		}	
		if((currentPrices.getOpenedAt()>0&&
				currentPrices.getOpenedAt()<=(p.getTime()-interval))||switchCandleSticks){
			switchCandleSticks = false;
			prices.add(currentPrices);
			if(prices.size()>periods){
				prices.remove();
			}
			currentPrices = new CandleStick();
			determineDecisions();
			addToGraph(prices.getLast());
		}
		currentPrices.add(p);
	}
	

	private void addToGraph(CandleStick candleStick) {
		Price p = getLatestPrice();
		long t = p.getTime();
		updateChart(getInstanceName(), t, 
				new double[]{
			candleStick.getOpen(),
			candleStick.getHigh(),
			candleStick.getLow(),
			candleStick.getClose()
			},false);
		for(int i=0;i<pivotNames.size();i++){
			boolean addPoint = false;
			double level = pivots.get(i);
			if(i<pivotPointIndex){
				if(i>=lowPivotPassed){
					addPoint = true;
				} else if (candleStick.getLow()<=level){
					lowPivotPassed = i;
					addPoint = true;
				}
			} else if(i>pivotPointIndex){
				if(i<=highPivotPassed){
					addPoint = true;
				} else if (candleStick.getHigh()>=level){
					highPivotPassed = i;
					addPoint = true;
				}
			} else {
				addPoint = true;
			}
			if(addPoint)
				updateChart(pivotNames.get(i), t, level);
		}
		
	}

	private void determineDecisions() {
		
		setBuy(false);
		setCover(false);
		setSell(false);
		setShort(false);

		if(prices.size()<periods) return;
		CandleStick thisStick = prices.get(periods-1);
		CandleStick lastStick = prices.get(periods-2);
		
		int thisOpenPos = determinePosition(thisStick.getOpen());
		int thisClosePos = determinePosition(thisStick.getClose());
		int lastOpenPos = determinePosition(lastStick.getOpen());
		int lastClosePos = determinePosition(lastStick.getClose());

		//look for candlestick that has just broken
		if(thisOpenPos!=thisClosePos&&
				thisOpenPos==lastClosePos&&
				lastOpenPos==lastClosePos) {
			
			double thisMid = (thisStick.getOpen()+thisStick.getClose())/2;
			//check to see if it has broken by greater than 50%
			if(lastStick.isGreen()&&
					thisStick.isGreen()&&
					thisMid>=pivots.get(thisOpenPos)){
				setBuy(true);
				setCover(true);
				setSell(false);
				setShort(false);
			} else if(lastStick.isRed()&&
					thisStick.isRed()&&thisMid<=pivots.get(thisClosePos)){
				setBuy(false);
				setCover(false);
				setSell(true);
				setShort(true);
			}
		} else 
		
		//look for candlestick that opens and closes across the line 
		//after the previous one broke it
		if(thisOpenPos==thisClosePos&&lastOpenPos!=lastClosePos&&thisOpenPos==lastClosePos){
			boolean bull = thisStick.isGreen()&&lastStick.isGreen();
			boolean bear = thisStick.isRed()&&lastStick.isRed();
			setBuy(bull);
			setCover(bull);
			setShort(bear);
			setSell(bear);
		}
		
		//negate positive outcomes for certain types of candle

	}

	@Override
	public Indicator getNewInstance() {
		return new PivotCandleStickIndicator();
	}

	@Override
	protected void optionUpdated(String name) {
	}

}
