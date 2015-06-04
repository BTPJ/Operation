package com.cyjt.operation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * �Զ����ShimmerTextView�� <BR>
 * ����ʵ��TextView�ķ���Ч��
 * 
 * @author Administrator
 * 
 */
public class ShimmerTextView extends TextView {
	/** LinearGradient �����������Խ�����Ⱦ */
	private LinearGradient mLinearGradient;
	/** �任������� */
	private Matrix mGradientMatrix;
	private Paint mPaint;
	/** �ؼ���� */
	private int mViewWidth = 0;
	/** �ƶ����� */
	private int mTranslate = 0;

	private boolean mAnimating = true;

	public ShimmerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mViewWidth == 0) {
			mViewWidth = getMeasuredWidth();
			if (mViewWidth > 0) {
				mPaint = getPaint();
				/**
				 * ����һ��Ϊ������������x��λ�ã�<BR>
				 * ������ ��Ϊ������������y��λ�ã�<BR>
				 * ������ ��Ϊ�����յ������x��λ�ã�<BR>
				 * ������ ��Ϊ�����յ������y��λ�ã�<BR>
				 * ������ ����ɫ�� ��Щ��ɫֵ�����������ݶȷ�Χ��Ӧ���Ƿ�ɢ��<BR>
				 * ������ �� positions ����Ϊ��. ���в���new float[]{ 0, 0.3f,0.5f,0.7f, 1
				 * }�Ƕ���ÿ����ɫ���ڵĽ������λ��<BR>
				 * ������ ��ƽ�̷�ʽ
				 */
				mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, 0,
						new int[] { 0x33777777, 0xffffffff, 0xffffffff,
								0xffffffff, 0x33777777 }, new float[] { 0,
								0.3f, 0.5f, 0.7f, 1 }, TileMode.CLAMP);
				mPaint.setShader(mLinearGradient);
				mGradientMatrix = new Matrix();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mAnimating && mGradientMatrix != null) {
			mTranslate += mViewWidth / 10;
			if (mTranslate > 2 * mViewWidth) {
				mTranslate = -mViewWidth;
			}
			// ˮƽ�ƶ����󣬶�View����ʵʱ����
			mGradientMatrix.setTranslate(mTranslate, 0);
			mLinearGradient.setLocalMatrix(mGradientMatrix);
			// ���������ˢ��View�����ݹ�ִ��onDraw����
			postInvalidateDelayed(30);
		}
	}

}