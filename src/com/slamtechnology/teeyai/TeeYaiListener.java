package com.slamtechnology.teeyai;

import com.slamtechnology.teeyai.prices.Price;

public interface TeeYaiListener {

	void flushGraph();

	void updatePrice(String name, Price price, boolean backTesting);


}
