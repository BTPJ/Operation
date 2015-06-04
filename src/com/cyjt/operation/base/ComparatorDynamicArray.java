package com.cyjt.operation.base;

import java.util.Comparator;

import com.cyjt.operation.bean.DynamicArray;


/**
 * Created by Administrator on 2014-11-25.
 */
public class ComparatorDynamicArray  implements Comparator<DynamicArray> {
   @Override
   public int compare(DynamicArray lhs, DynamicArray rhs) {
      return rhs.getSubmitAt().compareTo(lhs.getSubmitAt());
   }
}
