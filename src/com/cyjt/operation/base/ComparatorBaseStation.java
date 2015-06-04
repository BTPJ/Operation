package com.cyjt.operation.base;

import java.util.Comparator;

import com.cyjt.operation.bean.BaseStation;

/**
 * 比较基站Code顺序 用作排序
 */
public class ComparatorBaseStation implements Comparator<BaseStation> {

	@Override
	public int compare(BaseStation lbs, BaseStation rbs) {
//			Long l1 = Long.parseLong(pl1.getCode());
//			Long l2 = Long.parseLong(pl2.getCode());
//			return l1.compareTo(l2);
			 return lbs.getCode().compareTo(rbs.getCode());
		}

}