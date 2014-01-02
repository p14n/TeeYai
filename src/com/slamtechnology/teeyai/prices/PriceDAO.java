package com.slamtechnology.teeyai.prices;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Session;

import com.slamtechnology.db.HibernateSessionFactory;

public class PriceDAO {
	
	public static void main(String args[]){
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"));
		cal.set(Calendar.YEAR, 2007);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.DAY_OF_MONTH, 3);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		System.out.println(cal.getTime());
		System.out.println(cal.getTimeInMillis());

		
		/*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date startDate=null;
		Date endDate=null;
		try {
			startDate = sdf.parse("02/08/2007");
			endDate=sdf.parse("04/08/2007");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("range "+startDate+" "+endDate);
		getPriceHistory("BBCFTSE", new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()));
		*/
	}
	
	public static void savePrice(String name,double price){
		
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		
		Price p = new Price();
		p.setName(name);
		p.setPrice(price);
		p.setTime(System.currentTimeMillis());
		session.save(p);
		
		session.getTransaction().commit();
		HibernateSessionFactory.closeSession();
		
		
	}
	public static List<Price> getPriceHistory(String name,long start,long end){
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		List<Price> prices = (List<Price>)session.createQuery("from Price where timestamp between ? and ? and name=? order by timestamp asc")
		.setTimestamp(0, new Date(start))
		.setTimestamp(1, new Date(end))
		.setString(2, name)
			.list();
		//System.out.println("values from db "+(prices==null?-1:prices.size())+" between "+start+" and "+end);
		//System.out.println("values from db "+prices.get(0).getTime());
		session.getTransaction().commit();
		HibernateSessionFactory.closeSession();
		
		return prices;
	}
	public static List<Price> getAllPriceHistory(){
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		List<Price> prices = (List<Price>)session.createQuery("from Price order by timestamp asc")
			.list();
		session.getTransaction().commit();
		HibernateSessionFactory.closeSession();
		
		return prices;
	}


}
