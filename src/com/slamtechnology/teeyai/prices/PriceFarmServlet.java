package com.slamtechnology.teeyai.prices;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class PriceFarmServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		super.init();
		Thread t = new Thread(new PriceFarm(),"PriceFarm");
		t.start();
		System.out.println("Price farm started with process "+t.getId());
	}

}
