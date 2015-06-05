package com.cyjt.operation.uidynamic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.LanLot;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.fragment.DialogFragmentForLaneArrayStatus;
import com.cyjt.operation.fragment.DynamicLaneArrayCheckFragment;
import com.cyjt.operation.fragment.DynamicLaneArrayCheckFragment.LaneArrayCheckFragListener;
import com.cyjt.operation.fragment.DynamicLaneArrayEditFragment;
import com.cyjt.operation.fragment.DynamicLaneArrayEditFragment.LaneArrayEidtFragListener;
import com.cyjt.operation.ui.ZxingCaptureActivity;

/**
 * 阵列信息界面
 * 
 * @author kullo<BR>
 *         2014-10-25 上午9:06:37<BR>
 */
public class DynamicLaneArrayInfoActivity extends Activity implements
		LaneArrayCheckFragListener, LaneArrayEidtFragListener {
	private DynamicLaneArrayCheckFragment dlCheckFrag;
	private DynamicLaneArrayEditFragment dlEditFrag;
	private LaneArray currentLaneArray;
	private ProgressBar actionbar_progressBar;
	private TextView actionbar_textView_title;
	private ImageView actionbar_imageView_back_button;
	private TextView actionbar_refresh_button;
	/** 当前页面标志，0表示查看页面、1标志编辑页面 、2标志添加页面 */
	private int currentActionFlag = 0;
	private FragmentManager fragmentmanager;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_SUCCEED:
				// 添加成功
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				onBackPressed();
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_FAILED:

				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_SUCCEED:
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				TurnActivityToStart();
				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_FAILED:

				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_SUCCEED:
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				// 删除成功,应该跳转回阵列列表，而不是直接返回上一层
				TurnActivityToStart();
				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_FAILED:

				break;
			default:
				break;
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentmanager = getFragmentManager();
		setContentView(R.layout.activity_dynamic_lane_array_info);
		initActionBar();
		if (getIntent().getSerializableExtra("laneArray") == null) {
			if (getIntent().getBooleanExtra("actionAddLaneArray", false)) {
				currentLaneArray = new LaneArray();
				currentActionFlag = 2;
			} else {
				Toast.makeText(DynamicLaneArrayInfoActivity.this,
						"出错了，没有阵列对象也没有其他指令", Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			currentLaneArray = (LaneArray) getIntent().getSerializableExtra(
					"laneArray");
		}
		initView();
		viewEvent();
		changeFragment(currentActionFlag);
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_imageView_back_button = (ImageView) findViewById(R.id.actionbar_imageView_back_button);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		/** 默认为查看 */
		changeActionbar(currentActionFlag);
		actionbar_imageView_back_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentActionFlag == 0) {
							// 查看界面下直接返回即可
							onBackPressed();
						} else if (currentActionFlag == 1) {
							// 交换页面为查看
							currentActionFlag = 0;
							changeFragment(currentActionFlag);
						} else if (currentActionFlag == 2) {
							// 添加界面下直接返回即可
							onBackPressed();
						}
					}
				});

		actionbar_refresh_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 刷新数据，或其他操作
				if (currentActionFlag == 0) {
					// 交换页面为编辑
					currentActionFlag = 1;
					changeFragment(currentActionFlag);
				} else if (currentActionFlag == 1) {
					// 请求网络提交阵列数据

				}
			}
		});
		stopProgress();
	}

	private void initView() {

	}

	private void viewEvent() {

	}

	/** 改变actionBar之前要先将currentActionFlag进行改变才可以 */
	private void changeActionbar(int currentActionFlag) {
		switch (currentActionFlag) {
		case 0:
			actionbar_textView_title.setText("查看阵列");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("编辑");
			break;
		case 1:
			actionbar_textView_title.setText("编辑阵列");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("提交");
			break;
		case 2:
			actionbar_textView_title.setText("添加阵列");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("提交");
			break;

		default:
			break;
		}
	}

	/** 改变Fragment之前要先将currentActionFlag进行改变才可以 */
	private void changeFragment(int currentActionFlag) {
		FragmentTransaction transaction = fragmentmanager.beginTransaction();
		// 先隐藏全部内容
		hideDrawerFragments(transaction);
		// 判断当前抽屉应该要显示的状态值，然后更改抽屉
		if (currentActionFlag == 0) {
			TurnFragmentToCheck(transaction);
		} else if (currentActionFlag == 1) {
			TurnFragmentToEdit(transaction);
		} else if (currentActionFlag == 2) {
			TurnFragmentToEdit(transaction);
		}
		transaction.commit();
		changeActionbar(currentActionFlag);
	}

	private void hideDrawerFragments(FragmentTransaction transaction) {
		if (dlCheckFrag != null) {
			transaction.hide(dlCheckFrag);
		}
		if (dlEditFrag != null) {
			transaction.hide(dlEditFrag);
		}
	}

	private void TurnFragmentToEdit(FragmentTransaction transaction) {
		if (dlEditFrag == null) {
			dlEditFrag = new DynamicLaneArrayEditFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", currentLaneArray);
			dlEditFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_lane_array, dlEditFrag);
		}
		transaction.show(dlEditFrag);
	}

	private void TurnFragmentToCheck(FragmentTransaction transaction) {
		if (dlCheckFrag == null) {
			dlCheckFrag = new DynamicLaneArrayCheckFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", currentLaneArray);
			dlCheckFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_lane_array, dlCheckFrag);
		}
		transaction.show(dlCheckFrag);
	}

	private void refreashLaneArrayCode(String code) {
		currentLaneArray.setCode(code);
		if (dlEditFrag != null) {
			dlEditFrag.refreshLaneArray(currentLaneArray);
		}
	}

	@Override
	public void onMapCheckBeClicked() {
		openMap();
	}

	@Override
	public void onMapEditBeClicked(LaneArray laneArray) {
		currentLaneArray = laneArray;
		openMap();
	}

	@Override
	public void onZxingButtonBeClicked() {
		// 跳转获取Zxing的返回值
		Intent i = new Intent(DynamicLaneArrayInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "请扫描阵列二维码");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	@Override
	public void onSubmitBeClicked(LaneArray laneArray) {
		currentLaneArray = laneArray;
		if (("").equals(laneArray.getCode())) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "阵列编号不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (("").equals(laneArray.getDescription())) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "阵列描述不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (laneArray.getLat() == -0.0 || laneArray.getLon() == -0.0) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "阵列坐标未填写",
					Toast.LENGTH_SHORT).show();
			return;
		}

		switch (currentActionFlag) {
		case 1:
			startProgress();
			// 编辑提交
			AppContext.getInstance().getVolleyTools()
					.dynamicUpdateLanearrayInfo(handler, laneArray);
			break;
		case 2:
			startProgress();
			// 新建提交
			AppContext.getInstance().getVolleyTools()
					.dynamicSubmitLanearrayInfo(handler, laneArray);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDeleteBeClicked(LaneArray laneArray) {
		startProgress();
		// 删除阵列
		AppContext.getInstance().getVolleyTools()
				.dynamicDeleteLanearray(handler, laneArray);
	}

	/** 调用地图 */
	private void openMap() {
		Intent i = new Intent(DynamicLaneArrayInfoActivity.this,
				DynamicMapActivity.class);

		i.putExtra("lanLot", new LanLot(currentLaneArray.getLat(),
				currentLaneArray.getLon(), "", currentLaneArray.getCode(),
				currentLaneArray.getDescription()));
		i.putExtra("currentActionFlag", currentActionFlag);
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP);
	}

	/** 跳转到地图界面 */
	private void TurnActivityToStart() {
		Intent intent1 = new Intent(DynamicLaneArrayInfoActivity.this,
				DynamicMapAndListActivity.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent1);
	}

	/** 打开车位状态的Dialog */
	@Override
	public void checkLaneArrayStatus() {
		DialogFragmentForLaneArrayStatus fragment = new DialogFragmentForLaneArrayStatus();
		Bundle args = new Bundle();
		args.putInt("type", Constants.BEAN_TYPE_LANEARRAY);
		args.putString("laneArrayCode", currentLaneArray.getCode());
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DialogFragmentForLaneArrayStatus");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP:
				if (data.getSerializableExtra("lanLot") != null) {
					LanLot lanLot = (LanLot) data
							.getSerializableExtra("lanLot");
					currentLaneArray.setLat(lanLot.getLat());
					currentLaneArray.setLon(lanLot.getLon());

					if (dlEditFrag != null) {
						dlEditFrag.refreshLaneArray(currentLaneArray);
					}
				}
				break;
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String code = (String) data.getBundleExtra("Zxing").get(
						"result");
				// 如果parkingCode中不全是数字则，提示用户重新扫描正确的车位铭牌
				// if (code.matches("^[A-Za-z0-9]*$")) {
				// "^[0-9]*$"表示parkingCode只能是数字
				// "^\d{m,n}$"表示parkingCode只能是从m到n位的数字
				refreashLaneArrayCode(code);
				// } else {
				// Toast.makeText(DynamicLaneArrayInfoActivity.this,
				// "请扫描符合协议的二维码", Toast.LENGTH_SHORT).show();
				// }
				break;

			default:
				break;
			}
		}
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
		actionbar_refresh_button.setVisibility(View.GONE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
		actionbar_refresh_button.setVisibility(View.VISIBLE);
	}
}
