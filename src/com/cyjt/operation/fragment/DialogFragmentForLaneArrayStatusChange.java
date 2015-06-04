package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.DynamicArray;

/**
 * Created by Administrator on 2014-11-25.
 */
public class DialogFragmentForLaneArrayStatusChange extends DialogFragment {
   private DynamicArray dynamicArray;
   private int status= -1;
   private TextView textView_array_code;
   private TextView textView_cancel_button;
   private TextView textView_submit_button;
   private RadioGroup radioGroup_array_status;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.dialog_array_info,
              container, false);
      textView_array_code = (TextView) rootView.findViewById(R.id.textView_array_code);
      textView_submit_button = (TextView) rootView.findViewById(R.id.textView_submit_button);
      textView_cancel_button = (TextView) rootView.findViewById(R.id.textView_cancel_button);
      radioGroup_array_status = (RadioGroup) rootView.findViewById(R.id.radioGroup_array_status);

      return rootView;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      dynamicArray = (DynamicArray) getArguments().getSerializable("dynamicArray");
      viewEvent();
      fillUI();
   }

   private void fillUI() {
      if(dynamicArray!=null){
         textView_array_code.setText(""+dynamicArray.getArrayCode());
         if(radioGroup_array_status.getChildCount()>=dynamicArray.getStatus()/2){
            radioGroup_array_status.check(radioGroup_array_status.getChildAt(dynamicArray.getStatus()/2).getId());
         }
      }
   }
   private void viewEvent() {
      textView_submit_button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dynamicArray.setStatus(status);
            listener.onSubmitClicked(dynamicArray);
            dismiss();
         }
      });
      textView_cancel_button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dismiss();
         }
      });
      radioGroup_array_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         private int positon = -1;
         @Override
         public void onCheckedChanged(RadioGroup radioGroup, int id) {
            for(int i = 0 ; i <radioGroup.getChildCount();i++){
               if(radioGroup.getChildAt(i).getId()==id){
                  positon = i*2+1;
               }
            }
            status = positon;
         }
      });
   }

   private ArrayDialogEnventListener listener;

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      listener = (ArrayDialogEnventListener) activity;
   }

   public interface ArrayDialogEnventListener {
      /**
       * 提交按钮被点击
       */
      public void onSubmitClicked(DynamicArray array);
   }
}
