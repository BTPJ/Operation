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
 * 自定义的ShimmerTextView类 <BR>
 * 用以实现TextView的发光效果
 * 
 * @author Administrator
 * 
 */
public class ShimmerTextView extends TextView {
	/** LinearGradient 用来进行线性渐变渲染 */
	private LinearGradient mLinearGradient;
	/** 变换渐变矩阵 */
	private Matrix mGradientMatrix;
	private Paint mPaint;
	/** 控件宽度 */
	private int mViewWidth = 0;
	/** 移动距离 */
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
				 * 参数一：为渐变起点坐标的x轴位置，<BR>
				 * 参数二 ：为渐变起点坐标的y轴位置，<BR>
				 * 参数三 ：为渐变终点坐标的x轴位置，<BR>
				 * 参数四 ：为渐变终点坐标的y轴位置，<BR>
				 * 参数五 ：颜色集 这些颜色值在整个线性梯度范围内应该是分散的<BR>
				 * 参数六 ： positions 可能为空. 其中参数new float[]{ 0, 0.3f,0.5f,0.7f, 1
				 * }是定义每个颜色处于的渐变相对位置<BR>
				 * 参数七 ：平铺方式
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
			// 水平移动矩阵，对View进行实时处理
			mGradientMatrix.setTranslate(mTranslate, 0);
			mLinearGradient.setLocalMatrix(mGradientMatrix);
			// 这个方法会刷新View，并递归执行onDraw方法
			postInvalidateDelayed(30);
		}
	}

}