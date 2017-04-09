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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    }

    /**
     * method to get the data from intent and set to the views.
     */
    private void setIntentData() {

        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(BundleConstant.BUNDLE)) {
            Bundle bundle = startingIntent.getBundleExtra(BundleConstant.BUNDLE);
            if (bundle != null) {
                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_SYMBOL)) {
                    mTvStockSymbol.setText(bundle.getString(BundleConstant.BUNDLE_STOCK_SYMBOL));
                }

                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_PRICE)) {
                    mTvStockPrice.setText(bundle.getString(BundleConstant.BUNDLE_STOCK_PRICE));
                }

                if (bundle.containsKey(BundleConstant.BUNDLE_STOCK_HISTORY)) {
                    mHistoryData = bundle.getString(BundleConstant.BUNDLE_STOCK_HISTORY);
                }
            }
        }

    }

    private void setChartData() {

        // enable scaling and dragging
        mLineGraphStockPriceOvertime.setDragEnabled(false);
        mLineGraphStockPriceOvertime.setScaleEnabled(false);
        mLineGraphStockPriceOvertime.setDrawGridBackground(false);
        mLineGraphStockPriceOvertime.setHighlightPerDragEnabled(false);

        mLineGraphStockPriceOvertime.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mLineGraphStockPriceOvertime.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = mLineGraphStockPriceOvertime.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        mLineGraphStockPriceOvertime.getAxisRight().setEnabled(false);

        setData();

    }

    private LineDataSet createXAxisDataFromHistory() {
        float maxYValue = 50f;
        float bufferYValue = 100f;
        float minYValue = 0f;
        List<Entry> entries = new ArrayList<>();
        String[] data = mHistoryData.split("\\n");
        int linesLength = data.length;
        final String[] dates = new String[linesLength];
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.US);
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
        leftAxis.setAxisMaximum(maxYValue + bufferYValue);
        leftAxis.setAxisMinimum(minYValue);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        // configure x axis.
        LineDataSet dataSet = new LineDataSet(entries, "Close Price");
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
        closePriceSet.setLineWidth(2f);
        closePriceSet.setCircleRadius(3f);
        closePriceSet.setFillAlpha(65);
        closePriceSet.setFillColor(ColorTemplate.getHoloBlue());
        closePriceSet.setHighLightColor(Color.rgb(244, 117, 117));
        closePriceSet.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(closePriceSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mLineGraphStockPriceOvertime.setData(data);
    }


}
