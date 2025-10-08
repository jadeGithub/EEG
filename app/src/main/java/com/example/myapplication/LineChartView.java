package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LineChartView {
    private LineChart lineChart;
    private YAxis yAxis;
    private XAxis xAxis;
    private LineData lineData;
    private List<LineDataSet> lineDataSets;
    private Context context;
    private int index=0;
    private int line_num;
    private int size;
    private int[] COLORS={Color.BLACK,Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GRAY,Color.GREEN,Color.LTGRAY};


    /**
     * @param mLineChart chart对象
     * @param mContext
     */
    public LineChartView(LineChart mLineChart, Context mContext, String[] names, int Xsize, float[] boundary) {
        this.context=mContext;
        this.lineChart = mLineChart;
        this.line_num=names.length;
        this.size=Xsize;
        initLineChart(boundary);

        lineDataSets=new ArrayList<>();
        lineData = new LineData();

        for(int i=0;i<line_num;i++){
            lineDataSets.add(initLineDataSet(names[i], COLORS[i]));
            lineData.addDataSet(lineDataSets.get(i));
        }
        lineChart.setData(lineData);
    }

    /**
     * 初始化图表
     */
    public void initLineChart(float[] Yboundary){
        lineChart.setDrawGridBackground(false); //是否展示网格线
        lineChart.setDrawBorders(true); //是否显示边界
        lineChart.setDragEnabled(false); //是否可以拖动
        lineChart.setScaleEnabled(true); // 是否可以缩放
        lineChart.getDescription().setEnabled(false);// 不显示数据描述
        lineChart.getAxisRight().setEnabled(false);//关闭右侧Y轴


        Legend legend = lineChart.getLegend();// 得到图例
        createLegend(legend);// 设置图例
        //获取x,y轴
        yAxis = lineChart.getAxisLeft();
        xAxis = lineChart.getXAxis();
        //设置x轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X轴设置显示位置在底部

        //设置y轴
        yAxis.setAxisMaximum(Yboundary[1]);
        yAxis.setAxisMinimum(Yboundary[0]);//保证Y轴从0开始，不然会上移一点
        yAxis.setLabelCount(10, false);
        yAxis.setDrawGridLines(false);
    }

    /**
     * 初始化dataset
     * @param name
     * @param color
     */
    private LineDataSet initLineDataSet(String name,int color) {
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(null, name);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setDrawCircles(false);//禁止在点上画圆
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighlightEnabled(false);//是否禁用点击高亮线
        lineDataSet.setHighLightColor(color);
        //不显示折线上的值
        lineDataSet.setDrawValues(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(true);//填充底部颜色
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);//LINEAR
//        LineChartMarkView mv = new LineChartMarkView(context, xAxis.getValueFormatter());
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);

        return lineDataSet;

    }

    /**
     * 功能：创建图例
     */
    private void createLegend(Legend legend) {
        /***折线图例 标签 设置***/
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }


    /**
     * 添加数据
     * @param
     */
    public void addEntry(double[] nums) {
        int count=lineDataSets.get(0).getEntryCount();
        if(count>this.size){
            for(LineDataSet lineDataSet:lineDataSets){
                lineDataSet.removeFirst();
            }
        }
        for(int i=0;i<nums.length;i++){
            lineData.addEntry(new Entry(index, (float) nums[i]), i);// 将entry添加到指定索引处的折线中
        }
        index++;
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.moveViewToX(index);


    }

    public void addEntryJson(JSONArray[] nums) throws JSONException {
        for(int i=0;i<nums[0].length();i++){
            for(int j=0;j<nums.length;j++){
                double data=nums[j].getDouble(i);
                int count=lineDataSets.get(0).getEntryCount();
                if(count>this.size){
                    lineDataSets.get(j).removeFirst();
                }
                lineData.addEntry(new Entry(index, (float) data), j);// 将entry添加到指定索引处的折线中
            }
            index++;
        }

        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.moveViewToX(index);

    }


}
