package cn.yangtaocun.androidreversi.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.yangtaocun.androidreversi.R;
import cn.yangtaocun.reversi.Reversi;

/**
 * TODO: document your custom view class.
 */
public class ReversiBoard extends View {
    private static int LINE = 9;
    private int mWidth;
    private float mLineHeight;
    private Paint mPaint;

    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;

    private float mTextWidth;
    private float mTextHeight;
    private Paint mPiecePaint;

    private Reversi mEngine;
    // if auto move with computer
    private boolean mAI = true;

    public ReversiBoard(Context context) {
        super(context);
        init(null, 0);
    }

    public ReversiBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ReversiBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ReversiBoard, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.ReversiBoard_exampleString);
        mExampleColor = a.getColor(
                R.styleable.ReversiBoard_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.ReversiBoard_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.ReversiBoard_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.ReversiBoard_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mPiecePaint = new Paint();

        mEngine = new Reversi();
        mEngine.newGame();

        // Update TextPaint and text measurements from attributes
        //invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = widthSize<heightSize?widthSize:heightSize;

        if(widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }
        else if(heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width,width);
    }

//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mLineHeight = w * 1.0f / LINE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPiece(canvas);
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;


        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);

        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }

    private void drawBoard(Canvas canvas) {
        for(int i=0;i<LINE;i++){
            float startX = mLineHeight / 2 , endX = mWidth - startX ;
            float y = (i+0.5f)*mLineHeight;
            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    private void drawPiece(Canvas canvas) {
        int[][] chessBoard = mEngine.getChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(chessBoard[i][j]!=0){
                    if(chessBoard[i][j]==1) mPiecePaint.setColor(Color.BLACK);
                    else mPiecePaint.setColor(Color.WHITE);
                    float centerX = (1+i)*mLineHeight;
                    float centerY = (1+j)*mLineHeight;
                    canvas.drawCircle(centerX,centerY,mLineHeight/2,mPiecePaint);
                }

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX()/mLineHeight - 0.5);
        int y = (int) (event.getY()/mLineHeight - 0.5);
        try {
            mEngine.move(x,y);
            invalidate();
            if(mAI){

                postDelayed(new Thread(){
                    @Override
                    public void run() {
                        int[] aiMove = mEngine.getBestMove();
                        //AI
                        if(aiMove!=null){
                            try {
                                mEngine.move(aiMove);
                            } catch (Reversi.InvalidMoveException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println(" has no possible move! Go to opponent's turn!");
                        }
                        invalidate();
                    }
                },1000);
            }

        } catch (Reversi.InvalidMoveException e) {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
        //drawPiece();
        //Toast.makeText(getContext(),"touch "+ x +","+y,Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        //invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        //invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        //invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
