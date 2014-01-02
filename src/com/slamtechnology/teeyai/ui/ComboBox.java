package com.slamtechnology.teeyai.ui;

import javax.swing.JComboBox;

public class ComboBox<T> extends JComboBox {

	@Override
	public T getSelectedItem() {
		return (T)super.getSelectedItem();
	}


}
