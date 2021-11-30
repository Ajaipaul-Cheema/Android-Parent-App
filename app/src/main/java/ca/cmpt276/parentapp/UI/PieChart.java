package ca.cmpt276.parentapp.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Circular Pie Chart Control
 */
public class PieChart extends View{
    private final float density = getResources().getDisplayMetrics().density;
    /**The angle of the interval between the pie charts*/
    private final float ANGLE_DIS = 1;
    /**Space distance between left and right*/
    private final float LR_PADDING = 25 * density;
    /**Headspace distance*/
    private final float TOP_PADDING = 25 * density;
    /**The distance between the pie chart and the text below*/
    private final float PIE_TEXT_DIS = 22 * density;
    /**The distance between the upper and lower text*/
    private final float TEXT_TEXT_DIS = 15 * density;
    /**The distance between the bottom and the text is actually BOTTOM_DIS+TEXT_TEXT_DIS*/
    private final float BOTTOM_DIS = 10 * density;
    /**Minimum distance between title and value*/
    private final float TITLE_VALUE_DIS = 18 * density;

    /**The color value of each part, there are 10 colors by default */
    private int[] mColors = new int[]{0xfff5a002,0xfffb5a2f,0xff36bc99,0xff43F90C,0xff181A18,0xffF802F6
            ,0xff022DF8,0xffECF802,0xff02F8E8,0xffEA0F8E};
    /**value*/
    private double[] mValues;
    /**Value converted to angle*/
    private float[] mAngles;
    /**Pie chart diameter*/
    private float pieR;
    /**The total angle of the pie chart*/
    private float pieAngle;

    private String[] mTitles ; ///Content of each part
    private String mEmptyMsg = "no data"; //Content without data prompt

    private float mTitleSize;
    private float mValueSize;
    /**The size of the text in the pie chart*/
    private float mPieTextSize;

    private int mTitleColor = 0xFF595959;
    private int mValueColor = 0xFF595959;
    private int mDefaultPointColor = 0xfff5a002; //The color of the prompt text when there is no data

    private Rect mTextBound;
    private Paint mTextPaint;
    private Paint mPiePaint;

    public PieChart(Context context) {
        this(context,null);
    }

    public PieChart(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTitleSize = sp2px(14);
        mPieTextSize = sp2px(12);
        mValueSize = sp2px(16);

        mPiePaint = new Paint();
        mTextPaint = new Paint();
        mTextBound = new Rect();
        mTextPaint.setColor(0xff595959);
        mTextPaint.setTextSize(mTitleSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPiePaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        /**------------------------------no data--------------------------------*/
        if(mTitles == null || mTitles.length == 0 || isValueEmpty()){
            mPiePaint.setColor(mDefaultPointColor);
            mTextPaint.setColor(0xffffffff);
            float cr = (getWidth()<getHeight()?getWidth()/2:getHeight()/2) - LR_PADDING;
            canvas.drawCircle(getWidth()/2, getHeight()/2, cr, mPiePaint);
            mTextPaint.getTextBounds(mEmptyMsg, 0, mEmptyMsg.length(), mTextBound);
            canvas.drawText(mEmptyMsg, (getWidth()-mTextBound.width())/2, (getHeight()+mTextBound.height())/2, mTextPaint);
            return;
        }
        /**------------------------------no data--------------------------------*/

        /**------------------------------Draw a pie chart--------------------------------*/
        int textHeight = getTextHeight("00", Math.max(mTitleSize, mValueSize));
        float r1 = getWidth() - LR_PADDING*2;
        float r2 = getHeight() - TOP_PADDING - PIE_TEXT_DIS - (TEXT_TEXT_DIS+textHeight)*(mTitles.length) - BOTTOM_DIS;
        pieR = Math.min(r1, r2);//In order to prevent the pie chart from crossing the boundary, select the smallest diameter of the pie chart
        RectF oval = new RectF((getWidth()-pieR)/2, TOP_PADDING, (getWidth()+pieR)/2, TOP_PADDING+pieR);
        mPiePaint.setStyle(Paint.Style.FILL);
        mAngles = getAngles();

        float startAngle = 0;
        for(int i=0;i<mAngles.length;i++){
            mPiePaint.setColor(mColors[i]);
            if(mAngles[i] == 0) continue;
            canvas.drawArc(oval, startAngle, mAngles[i], true, mPiePaint);
            startAngle += (mAngles[i]+ANGLE_DIS);
        }
        /**------------------------------Draw a pie chart--------------------------------*/

        /**------------------------------The bottom text, title and value--------------------------------*/
        float cr = 5 * density; //Dot radius
        float ctd = 8 * density; //Distance between dot and text on the right
        float titleLen = getMaxTextWidth(mTitles, mTitleSize) + TITLE_VALUE_DIS;
        float len = 2*cr + ctd + titleLen + getMaxTextWidth(mValues, mValueSize);
        float topDis = TOP_PADDING + PIE_TEXT_DIS +pieR + textHeight; //第一行文字底部和控件顶部距离
        float cX = (getWidth()-len)/2+cr;
        float titleX = cX+cr+ctd;
        float valueX = titleX + titleLen;
        int valueLen = mValues.length-1;
        for(int i=0;i<mTitles.length;i++){
            mPiePaint.setColor(mColors[i]);
            float yDis = topDis+(textHeight+TEXT_TEXT_DIS)*i;
            canvas.drawCircle(cX, yDis-textHeight/2, cr, mPiePaint);
            mTextPaint.setTextSize(mTitleSize);
            mTextPaint.setColor(mTitleColor);
            canvas.drawText(mTitles[i], titleX, yDis, mTextPaint);
            mTextPaint.setColor(mValueColor);
            mTextPaint.setTextSize(mValueSize);
            canvas.drawText(Double.toString(i>valueLen?0:mValues[i]), valueX, yDis, mTextPaint);
        }
        /**------------------------------The bottom text, title and value--------------------------------*/

        //Text on pie chart
        setPieContentText(canvas);
    }

    /**
     * Set the name of each pie chart
     * @param titles
     */
    public void setTitles(List<String> titles){
        mTitles = (String[])titles.toArray();
    }


    /**
     * Set the name of each pie chart
     * @param titles
     */
    public void setTitles(String[] titles){
        mTitles = titles;
    }

    /**
     * Set up values
     * @param values
     */
    public void setValues(List<Double> values){
        mValues = new double[values.size()];
        for(int i=0;i<values.size();i++){
            mValues[i] = values.get(i);
        }
    }

    /**
     * Set up values
     * @param values
     */
    public void setValues(double[] values){
        mValues = values;
    }

    /**
     * Set the color of each pie chart
     * @param colors
     */
    public void setColors(List<Integer> colors){
        mColors = new int[colors.size()];
        for(int i=0;i<colors.size();i++){
            mColors[i] = colors.get(i);
        }
    }

    /**
     * Set the color of each pie chart
     * @param colors
     */
    public void setColors(int[] colors){
        mColors = colors;
    }

    /**
     * Set the font size of the name
     * @param size
     */
    public void setTitleSize(float size){
        mTitleSize = sp2px(size);
    }

    /**
     * Set the size of the value
     * @param size
     */
    public void setValueSize(float size){
        mValueSize = sp2px(size);
    }

    /**
     * Set the size of the text on the pie chart
     * @param size
     */
    public void setPieTextSize(float size){
        mPieTextSize = sp2px(size);
    }

    /**
     * Name color
     * @param titleColor
     */
    public void setTitleColor(int titleColor){
        mTitleColor = titleColor;
    }

    /**
     * The color of the value
     * @param valueColor
     */
    public void setValueColor(int valueColor){
        mValueColor = valueColor;
    }

    /**
     * Set the color of the prompt text when there is no data
     * @param color
     */
    public void setDefaultPointColor(int color){
        mDefaultPointColor = color;
    }
    /**
     * No data prompt content
     * @param msg
     */
    public void setEmptyMsg(String msg){
        mEmptyMsg = msg;
    }

    private boolean isValueEmpty(){
        if(mValues == null || mValues.length == 0)
            return true;
        for(double va:mValues){
            if(va > 0)
                return false;
        }
        mEmptyMsg = "no data";
        return true;
    }

    /**
     * Get the angle occupied by each value
     * @return
     */
    private float[] getAngles(){
        if(mValues == null || mValues.length == 0) return null;
        double sum = 0;
        int len = mTitles.length;
        float[] angles = new float[len];
        int gapCount = 0;//饼图间隙条数
        for(int i=0;i<len;i++){
            sum += mValues[i];
            if(mValues[i]>0)
                gapCount++;
        }
        float angle = 0;
        pieAngle = 360 - gapCount*ANGLE_DIS;
        for(int i=0;i<len-1;i++){
            angles[i] = (float)(pieAngle*mValues[i]/sum);
            angle += angles[i];
        }
        if(mValues[len-1]>0)
            angles[len - 1] = pieAngle - angle;

        return angles;
    }

    /**
     * Write the percentage on each pie of the pie chart, which must be placed after the angles occupied by each value is calculated
     * @param canvas
     */
    private void setPieContentText(Canvas canvas){
        float pre = 0;
        float[] centerAngle = new float[mAngles.length];
        for(int i=0;i<mAngles.length;i++){
            if(mAngles[i] == 0) continue;
            centerAngle[i] = ANGLE_DIS+mAngles[i]/2 + pre;
            pre += mAngles[i];
        }
        float cenR = pieR*1.0f/2*3/5;
        float lastPir = 1.0f;//In order to make all the% ratios add up to 1
        mTextPaint.setColor(0xFFFFFFFF);
        float cenX = 0;
        float cenY = 0;
        for(int i=0;i<centerAngle.length;i++){
            if(centerAngle[i]==0 ) continue;
            float xa = (float) (cenR * Math.cos(centerAngle[i] * (Math.PI / 180)));
            float ya = (float) (cenR * Math.sin(centerAngle[i] * (Math.PI / 180)));
            cenX = getWidth()*1.0f/2+xa;
            cenY = TOP_PADDING + pieR*1.0f/2 + ya;
            mTextPaint.setTextSize(mPieTextSize);
            double curPer = numDecimals(mAngles[i]/pieAngle);
            String perMsg = i==centerAngle.length-1?percentFormat(lastPir):percentFormat(curPer);
            canvas.drawText(perMsg, cenX-getTextWidth(perMsg, mPieTextSize)*1.0f/2, cenY+getTextHeight(perMsg, mPieTextSize)*1.0f/2/2, mTextPaint);
            lastPir -= curPer;
        }
    }

    private int getMaxTextWidth(double[] ds,float size){
        String[] strs = new String[ds.length];
        for(int i=0;i<ds.length;i++){
            strs[i] = ds[i]+"";
        }
        return getMaxTextWidth(strs, size);
    }

    private int getMaxTextWidth(String[] strs,float size){
        Rect textBound = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(size);
        int len = 0;
        for(int i=0;i<strs.length;i++){
            paint.getTextBounds(strs[i], 0, strs[i].length(), textBound);
            len = Math.max(len, textBound.width());
        }
        return len;
    }

    private int getTextWidth(String str,float size){
        return getTextRect(str, size).width();
    }

    private int getTextHeight(String str,float size){
        return getTextRect(str, size).height();
    }

    private Rect getTextRect(String str,float size){
        Rect textBound = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(size);
        paint.getTextBounds(str, 0, str.length(), textBound);
        return textBound;
    }

    private float sp2px(float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * Format as percentage format
     * @param num
     * @return
     */
    private String percentFormat(double num){
        DecimalFormat df = new DecimalFormat("#0.00%");
        return df.format(num);
    }

    /**
     * Four decimal places
     * @param num
     * @return
     */
    public static double numDecimals(double num){
        return ((int)(num*10000))*1.0d/10000;
    }
}