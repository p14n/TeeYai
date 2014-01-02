package com.slamtechnology.teeyai.trade;

import java.util.HashSet;
import java.util.Set;

import com.slamtechnology.teeyai.name.IndicatorInstanceName;

public class TradeStrategy {
	
	public final static int BUY = 1;
	public final static int SELL = 2;
	public final static int SHORT = 3;
	public final static int COVER = 4;
	private boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private long id;
	private int executionType;
	private String name;
	private Set<String> decisions = new HashSet<String>();
	
	public TradeStrategy(String name,int executionType){
		this.executionType = executionType;
		this.name=name;
	}
	public TradeStrategy(){
	}
	
	public boolean isIndicator(IndicatorInstanceName indicator){
		if(indicator!=null)
			return decisions.contains(indicator.getName());
		return false;
	}
	
	public void addIndicator(IndicatorInstanceName indicator){
		if(indicator!=null)
			decisions.add(indicator.getName());
	}
	public void removeIndicators(){
		decisions.clear();
	}
	
	public String toString(){
		return name;
	}
/*package com.slamtechnology.teeyai.trade;

import java.util.HashSet;

public class TradeStrategy {
	
	public final static int BUY = 1;
	public final static int SELL = 2;
	public final static int SHORT = 3;
	public final static int COVER = 4;
	
	private double spread;
	private HashSet<String> buyDecisions = new HashSet<String>();
	private HashSet<String> sellDecisions = new HashSet<String>();
	private HashSet<String> shortDecisions = new HashSet<String>();
	private HashSet<String> coverDecisions = new HashSet<String>();
	
	public boolean isBuyIndicator(String indicator){
		return buyDecisions.contains(indicator);
	}
	public boolean isSellIndicator(String indicator){
		return sellDecisions.contains(indicator);
	}
	public boolean isShortIndicator(String indicator){
		return shortDecisions.contains(indicator);
	}
	public boolean isCoverIndicator(String indicator){
		return coverDecisions.contains(indicator);
	}
	
	public void addBuyIndicator(String indicator){
		buyDecisions.add(indicator);
	}
	public void addSellIndicator(String indicator){
		sellDecisions.add(indicator);
	}
	public void addShortIndicator(String indicator){
		shortDecisions.add(indicator);
	}
	public void addCoverIndicator(String indicator){
		coverDecisions.add(indicator);
	}
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}

}
*/

	public int getExecutionType() {
		return executionType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExecutionType(int executionType) {
		this.executionType = executionType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<String> getDecisions() {
		return decisions;
	}

	public void setDecisions(Set<String> decisions) {
		this.decisions = decisions;
	}

}
