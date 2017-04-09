/*
 * Copyright (c) 2017. The Android Open Source Project
 */
package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/*
 * Created by Palash on 09/04/17.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new StockViewFactory(getApplicationContext(), intent);

    }
}
