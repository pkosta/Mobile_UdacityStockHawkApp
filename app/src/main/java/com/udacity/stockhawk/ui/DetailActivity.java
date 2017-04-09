/*
 * Copyright (c) 2017. The Android Open Source Project
 */
package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.utility.BundleConstant;
import com.udacity.stockhawk.utility.GraphConstant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Palash on 09/04/17.
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_stock_symbol)
    TextView mTvStockSymbol;

    @BindView(R.id.tv_stock_price)
    TextView mTvStockPrice;

    @BindView(R.id.line_graph_stock_price_overtime)
    LineChart mLineGraphStockPriceOvertime;

    private String mHistoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        // initialize the butter knife.
        ButterKnife.bind(this);

        setIntentData();

        setChartData();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    /**
     * method to get the data from intent and set to the views.
     */
    private void setIntentData() {
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(BundleConstant.BUNDLE)) {
            Bundle bundle = startingIntent.getBundleExtra(BundleConstant.BUNDLE);
            if (bundle != null) {
                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_SYMBOL)) {
                    mTvStockSymbol.setText(bundle.getString(BundleConstant.BUNDLE_STOCK_SYMBOL));
                }

                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_PRICE)) {
                    mTvStockPrice.setText(dollarFormat.format(bundle.getFloat(BundleConstant.BUNDLE_STOCK_PRICE)));
                }

                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_HISTORY)) {
                    mHistoryData = bundle.getString(BundleConstant.BUNDLE_STOCK_HISTORY);
                }
            }
        }

    }

    /**
     * method to configure the chart data
     */
    private void setChartData() {

        // enable scaling and dragging
        mLineGraphStockPriceOvertime.setDragEnabled(true);
        mLineGraphStockPriceOvertime.setScaleEnabled(true);
        mLineGraphStockPriceOvertime.setDrawGridBackground(false);
        mLineGraphStockPriceOvertime.setHighlightPerDragEnabled(true);

        mLineGraphStockPriceOvertime.animateX(GraphConstant.ANIMATE_TIME_IN_MILLIS);

        // get the legend (only possible after setting data)
        Legend l = mLineGraphStockPriceOvertime.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(GraphConstant.GRAPH_TEXT_SIZE);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = mLineGraphStockPriceOvertime.getXAxis();
        xAxis.setTextSize(GraphConstant.GRAPH_TEXT_SIZE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        mLineGraphStockPriceOvertime.getAxisRight().setEnabled(false);

        setData();

    }

    /**
     * method to create the data from string history builder.
     * @return LineDataSet, data set with which we need to plot the graph.
     */
    private LineDataSet createXAxisDataFromHistory() {
        float maxYValue = GraphConstant.DEFAULT_MAX_Y_VALUE;

        List<Entry> entries = new ArrayList<>();
        String[] data = mHistoryData.split("\\n");
        int linesLength = data.length;
        final String[] dates = new String[linesLength];
        SimpleDateFormat formatter = new SimpleDateFormat(GraphConstant.DATE_FORMAT_Y_AXIS, Locale.US);
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < linesLength; i++) {
            String[] dateAndPrice = data[linesLength - i - 1].split(",");
            calendar.setTimeInMillis(Long.valueOf(dateAndPrice[0]));
            dates[i] = formatter.format(calendar.getTime());
            if(maxYValue < Float.valueOf(dateAndPrice[1])) {
                maxYValue = Float.valueOf(dateAndPrice[1]);
            }
            entries.add(new Entry(i, Float.valueOf(dateAndPrice[1])));
        }

        // configure y axis.
        YAxis leftAxis = mLineGraphStockPriceOvertime.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(maxYValue + GraphConstant.BUFFER_Y_VALUE);
        leftAxis.setAxisMinimum(GraphConstant.DEFAULT_MIN_Y_VALUE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        // configure x axis.
        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.graph_legend_text));
        XAxis xAxis = mLineGraphStockPriceOvertime.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates[(int) value];
            }

        });

        return dataSet;
    }

    /**
     * method to set data to the chart to draw.
     *
     */
    private void setData() {


        LineDataSet closePriceSet = createXAxisDataFromHistory();

        closePriceSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        closePriceSet.setColor(ColorTemplate.getHoloBlue());
        closePriceSet.setCircleColor(Color.WHITE);
        closePriceSet.setLineWidth(GraphConstant.DATA_LINE_WIDTH);
        closePriceSet.setCircleRadius(GraphConstant.CIRCLE_RADIUS);
        closePriceSet.setFillAlpha(GraphConstant.FILL_ALPHA);
        closePriceSet.setFillColor(ColorTemplate.getHoloBlue());
        closePriceSet.setHighLightColor(Color.rgb(244, 117, 117));
        closePriceSet.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(closePriceSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(GraphConstant.DATA_VALUE_TEXT_SIZE);

        // set data
        mLineGraphStockPriceOvertime.setData(data);
    }

}
