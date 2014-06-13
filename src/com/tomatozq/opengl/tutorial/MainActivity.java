package com.tomatozq.opengl.tutorial;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	private List<ResolveInfo> resList;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        queryActions();
        
        setListAdapter(new ResolveInfoListAdpater());
        
        getListView().setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ResolveInfo res = (ResolveInfo)getListAdapter().getItem(position);
				
				Intent intent = new Intent();
				
				String packageName = res.activityInfo.packageName;
				String name = res.activityInfo.name;
				
				intent.setClassName(packageName, name);
				
				startActivity(intent);
			}
        	
        });
        
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        
		LayoutAnimationController controller = new LayoutAnimationController(animation);
		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		
		getListView().setLayoutAnimation(controller);
        
    	super.onCreate(savedInstanceState);
    }
    
    private void queryActions() {
		// TODO Auto-generated method stub
        Intent intent = new Intent();

        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("se.jayway.opengl");
        
        PackageManager pm = getPackageManager();
        resList = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
	}

	private final class ResolveInfoListAdpater extends BaseAdapter{
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return resList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return resList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = new TextView(MainActivity.this);
			textView.setText(resList.get(position).loadLabel(getPackageManager()));
			textView.setPadding(20, 20, 20, 20);
			convertView = textView;
			
			return convertView;
		}
    	
    }
}
