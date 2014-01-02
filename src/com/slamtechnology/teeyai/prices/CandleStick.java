package com.slamtechnology.teeyai.prices;

import java.util.Date;

import com.slamtechnology.teeyai.prices.Price;

public class CandleStick {
	private Double open,high,low,close;
	private long id,openedAt,closedAt;
	
	@Override
	public String toString() {
		return "CandleStick [open=" + open + ", high=" + high + ", low=" + low
				+ ", close=" + close + ", openedAt=" + openedAt + ", closedAt="
				+ closedAt + "]";
	}
	public boolean isGreen(){
		return open!=null&&close!=null&&open.compareTo(close)<0;
	}
	public boolean isRed(){
		return open!=null&&close!=null&&open.compareTo(close)>0;
	}
	
	public double bodyLength(){
		return Math.abs(open-close);
	}
	public double tailLength(){
		return isGreen()?open-low:close-low;
	}
	public double headLength(){
		return isGreen()?high-close:high-open;
	}

	public long getId() {
		return id;
	}

	public long getOpenedAt() {
		return openedAt;
	}

	public long getClosedAt() {
		return closedAt;
	}
	public Date getClosedDate() {
		return closedAt==0?null:new Date(closedAt);
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setClosedAt(long closedAt) {
		this.closedAt = closedAt;
	}
	public void setClosedDate(Date closedDate) {
		this.closedAt = closedDate==null?0:closedDate.getTime();
	}

	public Double getOpen() {
		return open;
	}

	public Double getHigh() {
		return high;
	}

	public Double getLow() {
		return low;
	}

	public Double getClose() {
		return close;
	}
	
	public void add(Price price){
		if(open==null){
			open = price.getPrice();
			high = price.getPrice();
			low = price.getPrice();
			openedAt = price.getTime();
		} else {
			if(high.compareTo(price.getPrice())<0) high = price.getPrice();
			if(low.compareTo(price.getPrice())>0) low = price.getPrice();
		}
		close = price.getPrice();
		closedAt = price.getTime();
	}

}
