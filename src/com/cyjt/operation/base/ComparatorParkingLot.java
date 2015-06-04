package com.cyjt.operation.base;

import java.util.Comparator;

import com.cyjt.operation.bean.ParkingLot;

/**
 * 比较两个车位的铭牌顺序 用作排序
 */
public class ComparatorParkingLot implements Comparator<ParkingLot> {

	@Override
	public int compare(ParkingLot lhs, ParkingLot rhs) {
		// Long l1 = Long.parseLong(pl1.getCode());
		// Long l2 = Long.parseLong(pl2.getCode());
		// return l1.compareTo(l2);
		return lhs.getCode().compareTo(rhs.getCode());
	}

}