package com.slamtechnology.teeyai;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.slamtechnology.db.HibernateSessionFactory;
import com.slamtechnology.teeyai.trade.TradeManager;
import com.slamtechnology.teeyai.trade.TradeStrategy;

public class TeeYaiDAO {

	public static long addTradeStrategy(long trademanagerid,TradeStrategy ts){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		TradeManager tm = (TradeManager)session.load(TradeManager.class, trademanagerid);
		tm.getTradeStrategies().add(ts);
		session.update(tm);
		
		session.getTransaction().commit();
		session.close();
		return ts.getId();
		
	}
	public static long updateTradeStrategy(TradeStrategy ts){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		session.update(ts);
		
		session.getTransaction().commit();
		session.close();
		return ts.getId();
		
	}
	public static void deleteTradeStrategy(long strategyid){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		TradeStrategy ts = (TradeStrategy)session.load(TradeStrategy.class,strategyid);
		session.delete(ts);
		
		session.getTransaction().commit();
		session.close();

	}
	public static TradeManager saveTradeManager(TradeManager tm){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		System.out.println("dao before saving, strategy count is "+tm.getTradeStrategies().size());
		session.save(tm);
		System.out.println("dao After saving, strategy count is "+tm.getTradeStrategies().size());
		
		session.getTransaction().commit();
		session.close();
		return tm;
		
	}
	public static TradeManager getTradeManager(String teeYaiName){
		 
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		//Criteria c = session.createCriteria(TradeStrategy.class).createCriteria("teeYaiName", teeYaiName);
		//List results = c.list();
		List<TradeManager> managers = (List<TradeManager>)session.createQuery("from TradeManager where teeyainame=?")
		.setString(0, teeYaiName).list();

		session.getTransaction().commit();
		TradeManager tm = null;
		if(managers!=null&&managers.size()>0){
			tm = managers.get(0);
			if(tm.getTradeStrategies()!=null&&tm.getTradeStrategies().size()>0){
				for(TradeStrategy ts:tm.getTradeStrategies()){
					if(ts!=null&&ts.getDecisions()!=null){
						Iterator<String> it = ts.getDecisions().iterator(); 
						while(it.hasNext()){
							System.out.println(it.next());
						}
					}
				}
			}
		}
		session.close();
		return tm;
	}

	/*public static List getTradeStrategies(String teeYaiName){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		//Criteria c = session.createCriteria(TradeStrategy.class).createCriteria("teeYaiName", teeYaiName);
		//List results = c.list();
		List<TradeStrategy> prices = (List<TradeStrategy>)session.createQuery("from TradeStrategy where teeyainame=?")
		.setString(0, teeYaiName).list();

		session.getTransaction().commit();
		HibernateSessionFactory.getSessionFactory().close();
		return prices;
		
	}*/

	/*
	public static long saveTradeManager(TradeManager tm){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		session.save(tm);
		
		session.getTransaction().commit();
		HibernateSessionFactory.getSessionFactory().close();
		//return tm.getManagerID();
		return 1;
		
	}
	public static TradeManager getTradeManager(String teeYaiName){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		Criteria c = session.createCriteria(TradeManager.class).createCriteria("teeYaiName", teeYaiName);
		List results = c.list();
		session.getTransaction().commit();
		HibernateSessionFactory.getSessionFactory().close();
		return (results!=null&&results.size()>0?(TradeManager)results.get(0):null);
		
	}
	*/


}
