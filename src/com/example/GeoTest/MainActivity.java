package com.example.GeoTest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created with IntelliJ IDEA.
 * User: e.chernets
 * Date: 18.09.13
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator(getResources().getString(R.string.from));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator(getResources().getString(R.string.to));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
    }
}
