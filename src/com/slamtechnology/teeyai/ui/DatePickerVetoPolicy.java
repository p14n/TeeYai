package com.slamtechnology.teeyai.ui;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.michaelbaranov.microba.calendar.VetoPolicy;
import com.michaelbaranov.microba.common.PolicyListener;

public class DatePickerVetoPolicy implements VetoPolicy {
	
	private Map<Integer, Map<Integer,Set<Integer>>> datesToInclude;

	public DatePickerVetoPolicy(
			Map<Integer, Map<Integer, Set<Integer>>> datesToInclude) {
		super();
		this.datesToInclude = datesToInclude;
	}

	public DatePickerVetoPolicy() {
		super();
		datesToInclude = new HashMap<Integer, Map<Integer,Set<Integer>>>();
	}
	
	public void addDate(int year,int month,int day){
		if(!datesToInclude.containsKey(year)){
			datesToInclude.put(year, new HashMap<Integer, Set<Integer>>());
		}
		if(!datesToInclude.get(year).containsKey(month)){
			datesToInclude.get(year).put(month, new HashSet<Integer>());
		}
		datesToInclude.get(year).get(month).add(day);
	}

	public void addVetoPolicyListener(PolicyListener listener) {
	}

	public void removeVetoPolicyListener(PolicyListener listener) {
	}

	public boolean isRestricted(Object source, Calendar date) {

		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day = date.get(Calendar.DAY_OF_MONTH);
		
		return !(datesToInclude.containsKey(year)&&
			datesToInclude.get(year).containsKey(month)&&
			datesToInclude.get(year).get(month).contains(day));
	}

	public boolean isRestrictNull(Object source) {
		return false;
	}

}
