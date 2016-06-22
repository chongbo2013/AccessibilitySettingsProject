package com.zyq.accessibility.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zyq.accessibility.R;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;
import com.zyq.accessibility.setting.a.temp.Task;

import java.util.List;

/**
 * @author zyq 16-6-22
 */
public class TaskStatusAdapter extends RecyclerView.Adapter<TaskStatusAdapter.TaskStatusViewHolder> {

	private List<Task> taskList;
	private Context mContext;

	public TaskStatusAdapter(Context context,List<Task> tasks){
		super();
		this.taskList = tasks;
		this.mContext = context;
	}

	@Override
	public TaskStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(this.mContext,R.layout.layout_item_task,null);
		return new TaskStatusViewHolder(view);
	}


	public void updateData(List<Task> taskList){
		this.taskList = taskList;
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(TaskStatusViewHolder holder, int position) {
		if(holder != null){
			holder.mTaskName.setText(taskList.get(position).getTaskName());
			int currentTaskIndex = ServiceConfigUtil.getInstance().getCurrentTaskIndex();
			if(ServiceConfigUtil.getInstance().getSettingTasks().get(position) instanceof Task){
				Task task = (Task) ServiceConfigUtil.getInstance().getSettingTasks().get(position);
				if(task.isInitFinished()){
					holder.mStatus1.setVisibility(View.VISIBLE);
					holder.mStatus2.setVisibility(View.GONE);
					holder.mStatus1.setImageResource(R.mipmap.setting_ic_right);
				}else{
					if(position < currentTaskIndex){
						//表示处理完成,不一定是可行的
						holder.mStatus1.setVisibility(View.VISIBLE);
						holder.mStatus2.setVisibility(View.GONE);

						holder.mStatus1.setImageResource(R.mipmap.setting_ic_right);
					}else if(position == currentTaskIndex){
						//表示正在处理中
						holder.mStatus2.setVisibility(View.VISIBLE);
						holder.mStatus1.setVisibility(View.GONE);
					}else{
						holder.mStatus1.setVisibility(View.VISIBLE);
						holder.mStatus2.setVisibility(View.GONE);
						holder.mStatus1.setImageResource(R.mipmap.setting_ic_wrong);
					}
				}
			}

		}
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	public class TaskStatusViewHolder extends RecyclerView.ViewHolder{

		private TextView mTaskName;
		private ImageView mStatus1;
		private ProgressBar mStatus2;

		public TaskStatusViewHolder(View view){
			super(view);
			mTaskName = (TextView)view.findViewById(R.id.taskname);
			mStatus1 = (ImageView)view.findViewById(R.id.status1);
			mStatus2 = (ProgressBar)view.findViewById(R.id.status2);
		}
	}
}
