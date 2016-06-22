package com.zyq.accessibility.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.zyq.accessibility.NativeParams;
import com.zyq.accessibility.R;
import com.zyq.accessibility.activity.GlobalConstants;
import com.zyq.accessibility.adapter.TaskStatusAdapter;
import com.zyq.accessibility.setting.a.temp.Task;
import com.zyq.accessibility.utils.RandomUtils;
import com.zyq.accessibility.view.ProgressWheel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zyq 16-5-25
 */
public class OneKeySettingView implements View.OnClickListener{

	public void clear(){
		if(mTasks != null){
			for(int i=0;i<mTasks.size();i++){
				mTasks.get(i).clear();
			}
		}
		mTasks = null;
		if(service != null){
			service.shutdownNow();
		}
		service = null;
		this.mHandler = null;
        pwOne = null;
		mContext = null;

	}
	private ProgressWheel pwOne;
	private Context mContext;
	private TextView mTvStart;
	private TextView mTvProgress;
	private TextView mTvPercent;
	private RecyclerView mRecyclerView;
	private View layout_progress;
	private TaskStatusAdapter mTaskStatusAdapter;
	private List<Task> mTasks = new ArrayList<>();
	private Float[]  mTasksProgress = new Float[]{0.0F,0.0F};
	private LinearLayoutManager mLayoutManager;
	private boolean isInit = true;
	private ScheduledExecutorService service;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1){
				Object object = msg.obj;
				if(object instanceof Integer){
					if(mTvProgress != null){
						mTvProgress.setText((int)object+"");
					}
				}
			}
		}
	};

	public OneKeySettingView(Context context){
		this.mContext = context;
	}




	public View init(boolean isInit){
		this.isInit = isInit;
		service= Executors.newScheduledThreadPool(2);
		View view = View.inflate(this.mContext, R.layout.layout_onekey_setting,null);
		pwOne = (ProgressWheel)view.findViewById(R.id.progressBarTwo);
		layout_progress = view.findViewById(R.id.layout_progress);
		layout_progress.setVisibility(View.GONE);
//        styleRandom(pwOne,mContext);
		mTvProgress = (TextView)view.findViewById(R.id.progress);
		mTvPercent =(TextView)view.findViewById(R.id.percent);
		mTvPercent.setVisibility(View.GONE);
		mTvStart = (TextView)view.findViewById(R.id.start);
		mTvStart.setVisibility(View.VISIBLE);
//      mTvStart.setOnClickListener(this);
//      mTvStart.setClickable(isClickable);
		mRecyclerView = (RecyclerView)view.findViewById(R.id.tasks);
		mLayoutManager = new LinearLayoutManager(mContext);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setHasFixedSize(true);
		mTaskStatusAdapter = new TaskStatusAdapter(mContext,mTasks);
		mRecyclerView.setAdapter(mTaskStatusAdapter);

		if(!isInit){
			monitorUpdateProgress();
		}
		return view;
	}


	public void setTaskList(List<Task> taskList){
		this.mTasks = taskList;
		mTaskStatusAdapter.updateData(this.mTasks);
	}

	public void updateProgress(Float[] progress){
		pwOne.startSpinning();
		layout_progress.setVisibility(View.VISIBLE);
		mTvStart.setVisibility(View.GONE);
		mTvPercent.setVisibility(View.VISIBLE);
		mTasksProgress = progress;
		//更新
		mTaskStatusAdapter.notifyDataSetChanged();
	}

	public void monitorUpdateProgress(){
		service.scheduleWithFixedDelay(new UpdateProgressRunnable(),0,100, TimeUnit.MILLISECONDS);
	}

	private int currentProgress;
	public class UpdateProgressRunnable implements Runnable{

		@Override
		public void run() {

			int currentMin = (int)(mTasksProgress[0]*100);
			int currentMax = (int)(mTasksProgress[1]*100);

			int tempProgress = currentProgress;
			if(tempProgress < currentMin){
				tempProgress = currentMin;
			}

			currentProgress = RandomUtils.getRandom(tempProgress,currentMax);

			Message msg = new Message();
			msg.what = 1;
			msg.obj = currentProgress;
			mHandler.sendMessage(msg);
		}
	}

	public void complete(){
		pwOne.stopSpinning();
		layout_progress.setVisibility(View.GONE);
		mTvPercent.setVisibility(View.GONE);
		mTvStart.setVisibility(View.VISIBLE);
		mTvStart.setText(mContext.getString(R.string.onekey_setting_finish));
		mTaskStatusAdapter.notifyDataSetChanged();
		mTvStart.setClickable(false);
		if(service != null){
			service.shutdownNow();
		}
	}

	public void check(){
		pwOne.startSpinning();
		layout_progress.setVisibility(View.GONE);
		mTvPercent.setVisibility(View.GONE);
		mTvStart.setVisibility(View.VISIBLE);
		mTvStart.setText(mContext.getString(R.string.onekey_setting_check));
		mTaskStatusAdapter.notifyDataSetChanged();
	}

	public void init(){
		pwOne.stopSpinning();
		layout_progress.setVisibility(View.GONE);
		mTvPercent.setVisibility(View.GONE);
		mTvStart.setVisibility(View.VISIBLE);
		mTvStart.setText(mContext.getString(R.string.onekey_setting_start));
		mTaskStatusAdapter.notifyDataSetChanged();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.start:
				pwOne.startSpinning();
				break;
		}
	}


}