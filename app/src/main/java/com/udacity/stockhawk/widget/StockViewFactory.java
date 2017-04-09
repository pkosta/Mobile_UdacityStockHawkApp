/*
 * Copyright (c) 2017. The Android Open Source Project
 */
package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/*
 * Created by Palash on 09/04/17.
 */

class StockViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor data = null;

    private Context mContext = null;

    StockViewFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // nothing to do
    }

    @Override
    public void onDataSetChanged() {
        loadDataFromDb();
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        data.moveToPosition(position);
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_quote);
        mView.setTextViewText(R.id.symbol,
                data.getString(Contract.Quote.POSITION_SYMBOL));

        mView.setTextViewText(R.id.price,
                data.getString(Contract.Quote.POSITION_PRICE));

        mView.setTextViewText(R.id.change,
                data.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE));

        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadDataFromDb() {
        if (data != null) {
            data.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        data = mContext.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL
        );
        Binder.restoreCallingIdentity(identityToken);
    }
}
