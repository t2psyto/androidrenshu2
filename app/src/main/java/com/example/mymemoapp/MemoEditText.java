package com.example.mymemoapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.EditText;

import org.w3c.dom.Attr;

import java.net.ContentHandler;
import java.util.jar.Attributes;

public class MemoEditText extends EditText {
    //ビットマスク
    //直線
    private static final int SOLID = 1;
    //破線
    private static final int DASH = 2;
    //通常の太さ
    private static final int NORMAL = 4;
    //太線
    private static final int BOLD = 8;

    //このViewの横幅
    private int mMeasuredWidth;
    //1行の高さ
    private int mLineHeight;
    //画面上に表示可能な行数
    private int mDisplayLineCount;

    //罫線のパス
    private Path mPath;
    //どのように描画するかを保持する
    private Paint mPaint;

    public MemoEditText(Context context) {
        this(context, null);
    }

    public MemoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初期設定
        init(context, attrs);

    }

    public MemoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初期設定
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //Path は、複数の直線や曲線などの情報をカプセル化する
        mPath = new Path();
        // Paint は、「どのように描画するか」という情報を保持する
        mPaint = new Paint();

        // Paint.style.STOROKE は塗りつぶしなしで、輪郭線を描画するスタイル
        mPaint.setStyle(Paint.style.STOROKE);

        //インスタンス生成時に、属性情報が渡されている場合
        //かつ、Android Studio のプレビュー表示ではない場合
        if (attrs != null && !isInEditMode()) {
            //属性情報を取得
            int lineEffectBit;
            int lineColor;

            Resources resources = context.getResources();
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MemoEditText);

            try {
                //属性に設定された値を取得
                lineEffectBit = typedArray.getInteger(R.styleable.MemoEditText_lineEffect, SOLID);
                lineColor = ((TypedArray) typedArray).getColor(R.styleable.MemoEditText_lineColor, Color.GRAY);

            } finally {
                //必ずrecycle()を呼ぶ
                typedArray.recycle();
            }

            //罫線のエフェクトを設定
            if ((lineEffectBit & DASH) == DASH) {
                //破線が設定されている場合
                DashPathEffect effect = new DashPathEffect(new float[]{
                        resources.getDimension(R.dimen.text_rule_interval_on),
                        resources.getDimension(R.dimen.text_rule_interval_off)},
                        Of);
                mPaint.setPathEffect(effect);
            }

            float strokeWidth;
            if ((lineEffectBit & BOLD) == BOLD) {
                //太線が設定されている場合
                strokeWidth = ((Resources) resources).getDimension(
                        R.dimen.text_rule_width_normal);
            }
            mPaint.setStrokeWidth(strokeWidth);

            //色を指定
            mPaint.setColor(lineColor);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //横幅
        mMeasuredWidth = getMeasuredWidth();

        //高さ
        int measureHeight = getMeasuredHeight();

        //1行の高さ
        mLineHeight = getLineHeight();

        //画面内に何行表示できるか
        mDisplayLineCount = measureHeight / mLineHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //パディング
        int paddingTop = getExtendedPaddingTop();
        //Y軸方向にスクロールされている量
        int scrollY = getScrollY();
        //画面所に表示されている最初の行
        int firstVisibleLine = getLayout().getLineForVertical(scrollY);
        //画面上に表示される最後の行
        int lastVisibleLine = firstVisibleLine + mDisplayLineCount;

        mPath.reset();
        for ( int i = firstVisibleLine; i <= lastVisibleLine; i++ ) {
            //行の左端に移動
            mPath.moveTo( 0, i * mLineheight + paddingTop);
            //右端へ線を引く
            mPath.lineTo(mmeasuredWidth, i * mLineHeight + paddingTop);

        }

        //Pathの描画
        canvas.drawpath(mPath, mPaint);

        super.onDraw(canvas);
    }
}
