package com.zyq.accessibility.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zyq.accessibility.R;
import com.zyq.accessibility.setting.FloatViewResult;
import com.zyq.accessibility.setting.OneKeySettingView;
import com.zyq.accessibility.setting.SettingService;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;
import com.zyq.accessibility.setting.a.SettingsUtil;
import com.zyq.accessibility.setting.a.temp.Task;
import com.zyq.accessibility.setting.common.receiver.CloseDialogReceiverUtil;
import com.zyq.accessibility.setting.common.receiver.PhoneStateReceiverUtil;
import com.zyq.accessibility.setting.common.receiver.ScreenOffReceiverUtil;
import com.zyq.accessibility.setting.ui.WindowViewManager;
import com.zyq.accessibility.utils.MIUIUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bintou on 16/4/19.
 */
public class SettingActivity extends BaseActivity {

	public static final boolean DEBUG = true;
	public static final String TAG = "SettingActivity";

	private WindowManager wm;
	private WindowManager.LayoutParams mParams;
	private View mFloatView;
	public static boolean open = false;
	private static SettingActivity instance = null;
	protected static int a = 0;
	public static boolean isOpen = false;//表示是否打开
	public static boolean f = false; //也不知干什么的
	public static boolean h = false;//settingsActivity中,暂时不知
	public static int g;
	public static boolean d = false;

	private long time;

	protected static View view;//todo 这个view记得初始化
	private static View[] alertViews;

	private int height = 0;//表示该activity的高度

	private boolean startOneKeySetting = false;
	private boolean v = false;

	private TextView mTvOneKeySetting;

	private OneKeySettingView mOneKeySettingViewAdd;
	private OneKeySettingView mOneKeySettingViewInit;
	private View mInitView;
	private List<Task> taskList;

	public static boolean hasBeenInitConfig = false;
	public String[] float_win = new String[]{"FLOAT_WIN","FLOAT_WIN_VIVO","FLOAT_WIN_YUNOS","FLOAT_WIN-1","FLOAT_WIN_COLOROS"};



//    public static boolean back = false;

	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0://
					//表示添加一个task时候,记得判断是否判断有开启辅助服务
					//也有可能在调用ServiceConfigUtil.init,update,save,clear()会发送msg.what = 0;但是msg.object == null
					StringBuilder sb = new StringBuilder();
					List<Task> list = (List<Task>)msg.obj;
					if(list != null){
						for(int i = 0;i<list.size();i++){
							sb.append(list.get(i).getTaskName());
						}
					}else{

					}
					if(GlobalConstants.DEBUG) {
						Log.i("11", "恭喜你又有一个新任务了" + "任务名字为：" + sb.toString());
					}

					if( f || SettingService.d(SettingActivity.this)){
						//更新对应的状态,就是标识哪些操作已经完成.....
					}

					taskList = ServiceConfigUtil.getInstance().getSettingTasks();

					if(GlobalConstants.DEBUG) {
						Log.d("29", "当前多少任务:" + taskList.size());
					}

					mOneKeySettingViewInit.setTaskList(taskList);
					break;
				case 1://添加视图,有刷新操作,
					Log.d("1","msg1");
					Object object = msg.obj;
					if(object != null){
						if(GlobalConstants.DEBUG) {
							Log.d(TAG,msg.what + " object:"+object);
						}
					}
//                    ToastUtils.show(SettingActivity.this,String.valueOf(msg.what));
					WindowManager windowManager = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
					try{
						WindowViewManager.removeView(windowManager,view);
					}catch (Throwable e){
						if(GlobalConstants.DEBUG) {
							e.printStackTrace();
						}
					}
					WindowViewManager.addView(windowManager,view);
					removeAlertViews(windowManager);
					break;

				case 3:
					//表示这时候已经在进行自动跑了,应该是更新进度了
					Float[] fArr = (Float[]) msg.obj;
					if(GlobalConstants.DEBUG) {
						Log.d(TAG,msg.what + " 更新进度:"+fArr[0].floatValue() + "  "+fArr[1].floatValue());
					}
					if(mOneKeySettingViewAdd != null){
						mOneKeySettingViewAdd.updateProgress(fArr);
					}
					if(mOneKeySettingViewInit != null){
						mOneKeySettingViewInit.updateProgress(fArr);
					}
					break;

				case 7://initConfig,返回taskList;
					if(GlobalConstants.DEBUG) {
						Log.d("10", "发现有这么多任务要做");
					}
					hasBeenInitConfig = true;
					List<Task> tasks = ServiceConfigUtil.getInstance().getSettingTasks();
					boolean onekeySetting = false;
					for(int i = 0;i<tasks.size();i++){
						if(!tasks.get(i).isInitFinished()){
							onekeySetting = true;
							break;
						}
					}
					if(!onekeySetting){
						if(mOneKeySettingViewInit!=null){
							mOneKeySettingViewInit.complete();
						}
						if(mOneKeySettingViewAdd !=null){
							mOneKeySettingViewAdd.complete();
						}
//						if(tasks.size() == 0){
//							PreferencesUtils.putBoolean(SettingActivity.this,GlobalConstants.PREFS_INIT_CONFIG_FILE,true);
//						}
					}else{
						if(mOneKeySettingViewInit!=null){
							mOneKeySettingViewInit.init();
						}
						if(mOneKeySettingViewAdd !=null){
							mOneKeySettingViewAdd.init();
						}
//						PreferencesUtils.putBoolean(SettingActivity.this,GlobalConstants.PREFS_INIT_CONFIG_FILE,true);
					}
					break;

				case 2://暂时不知道干嘛用的
					if(GlobalConstants.DEBUG) {
						Log.d("10", "message2");
					}

					Rect rect = (Rect) msg.obj;
					if (alertViews == null) {
						alertViews = new View[3];
						alertViews[0] = getLayoutInflater().inflate(R.layout.setting_alert_cover_view, null);
						alertViews[1] = getLayoutInflater().inflate(R.layout.setting_alert_tips_view, null);
						alertViews[2] = getLayoutInflater().inflate(R.layout.setting_alert_cover_view, null);
					}
					WindowManager windowManager2 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
					WindowViewManager.addView(windowManager2, alertViews[0], rect.top - (rect.height() / 2));
					WindowViewManager.addView(windowManager2, alertViews[2], (height - rect.bottom) - (rect.height() / 2), rect.bottom + (rect.height() / 2));
					int intrinsicHeight = alertViews[1].getBackground().getIntrinsicHeight() / 2;
					WindowViewManager.addView(windowManager2, alertViews[1], (rect.height() * 2) + intrinsicHeight, (rect.top - (rect.height() / 2)) - intrinsicHeight);
					WindowViewManager.removeView(windowManager2, view);
					break;
				case 4://表示更新

					break;
				case 5:
					break;
				case 6:
					Object alterText = msg.obj;
					if(alterText instanceof String) {
						WindowViewManager.guideToAlertBanner(SettingActivity.this, (String)alterText);
					}
					break;
				case 9:
					if(view != null){
						WindowViewManager.removeView((WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE),view);
					}
					SettingsUtil.checkFloatWindowAllowShow(getApplication(),new com.zyq.accessibility.setting.f(SettingActivity.this));
					break;
				case 10:
					WindowViewManager.guideToSettingYunos(SettingActivity.this,(String) msg.obj);
					break;
				case 11:
					if(mOneKeySettingViewInit != null){
						mOneKeySettingViewInit.complete();
					}
					WindowManager windowManager1 = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
					try{
						WindowViewManager.removeView(windowManager1,view);
					}catch (Throwable e){
						e.printStackTrace();
					}
					backActivity(SettingActivity.this);
					break;

			}
		}
	};

	private static void removeAlertViews(WindowManager windowManager) {
		if (alertViews!= null) {
			for (int i = 0; i < alertViews.length; i++) {
				WindowViewManager.removeView(windowManager, alertViews[i]);
				alertViews[i] = null;
			}
			alertViews = null;
		}
	}

	//表示已经打开了辅助设置页
	public static final boolean isOpenAccessibilityPage(){
		return instance != null && a == 1;
	}



	public void addView(){
		//若之前的view不存在
		if(view == null){
			//这里是添加view的地方....
			mOneKeySettingViewAdd = new OneKeySettingView(this);
			view = mOneKeySettingViewAdd.init(false);
			taskList = ServiceConfigUtil.getInstance().getSettingTasks();
			mOneKeySettingViewAdd.setTaskList(taskList);
		}
		//添加视图
		WindowViewManager.addView(((WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE)),view);
	}



	public static final SettingActivity getInstance(){
		return instance;
	}

	public static final void startActivity(Context context){
		try {
			if(GlobalConstants.DEBUG) {
				Log.d(TAG, "startActivity");
			}
			Intent intent = new Intent(context, SettingActivity.class);
			intent.setFlags(268435456);
			intent.putExtra("from", context.getClass().getSimpleName());
			context.startActivity(intent);
		}catch (Throwable e){
			if(GlobalConstants.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public  final void backActivity(Context context){
		try {
			Intent intent = new Intent(context, SettingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("from", getIntent().getStringExtra("from"));
			startActivity(intent);
		}catch (Throwable e){
			if(GlobalConstants.DEBUG) {
				e.printStackTrace();
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		this.height = rect.height();

		mOneKeySettingViewInit = new OneKeySettingView(this);

		mInitView = mOneKeySettingViewInit.init(true);
		setContentView(mInitView);

		if(GlobalConstants.DEBUG) {
			Log.e("37", "onCreate()!!!!!!!!!!!!!");
		}
		mOneKeySettingViewInit.check();
//		setTitle("设置");

		mInitView.findViewById(R.id.start).setOnClickListener(this);
		wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);

		d = false;//暂时不知干嘛的.....
		instance = this;
		//先检查是否可以有悬浮窗效果
//        SettingsUtil.checkFloatWindowAllowShow(this, new Result() {
//            @Override
//            public void onResult(boolean arg) {
//                if(!arg){
//                    //表示没有悬浮窗效果
//                    SharedPreferences sp = ServiceConfigUtil.getInstance().getSp();
//                    SharedPreferences.Editor editor = sp.edit();
//                    for(int i= 0;i<float_win.length;i++){
//                        if(sp.contains(float_win[i])){
//                            editor.putBoolean(float_win[i],false);//这样搞完了才去看看是否真正的初始.
//                            editor.commit();
//                            break;
//                        }
//                    }
//                }
//            }
//        });

		ServiceConfigUtil.getInstance(this).init(mHandler,0);

	}




	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (wm != null && mFloatView != null) {
				wm.removeViewImmediate(mFloatView);
				mFloatView = null;
			}

			//暂时搞不懂这里的360UI 咋回事
			if("360UI".equals(Build.BRAND)){
				SettingsUtil.checkFloatWindowAllowShow(this,new FloatViewResult(this));
			}

			if(a==2 && SettingService.d(this)){
				a= 1;
				startOneKeySetting();
			}
		}catch (Throwable e){
			if(GlobalConstants.DEBUG) {
				e.printStackTrace();
			}
		}
	}


	private final void startOneKeySetting() {
		try {
			this.startOneKeySetting = true;
			//更新 view;
//        this.isMiuiV6.wakeLockWhenPhoneCalling();
			if (SettingService.isAccessibilityEnabled((Context) this)) {
				if(GlobalConstants.DEBUG) {
					Log.i("10", "该应用已经打开了辅助功能");
				}

				this.a = 1;
				this.time = System.currentTimeMillis();
				//registerSomeListener();//注册一些监听器
				addView();
				ServiceConfigUtil.getInstance().update(this.mHandler, 4);
				return;
			}

			if(GlobalConstants.DEBUG) {
				Log.d("10", "引导它打开辅助功能");
			}
			List settingTasks = ServiceConfigUtil.getInstance().getSettingTasks();
			if (settingTasks != null && settingTasks.size() == 1 && ((Task) settingTasks.get(0)).isNotificationListenerSetting()) {
				this.v = true;
				return;
			}
			a = 2;
			SettingService.promptBringToAccessibilitySetting(this);
		}catch (Throwable e){
			if(GlobalConstants.DEBUG) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
				case R.id.start:
					startOneKeySetting();
					break;
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
	}

	private View initOpenPopupWindowView(){
		try{
			View contentView = null;
			if(MIUIUtils.isMIUI()){
				contentView = View.inflate(this,R.layout.layout_minu_open_flow,null);
			}
			return  contentView;
		}catch (Throwable e){
			e.printStackTrace();
		}
		return  null;
	}

	private View initCloseSystemPromptView(){
		try {
			View contentView = null;
			if (MIUIUtils.isMIUI()) {
				contentView = View.inflate(this,R.layout.layout_close_system_prompt,null);
			}
			return contentView;
		}catch (Throwable e){
			e.printStackTrace();
		}
		return null;
	}

	private String initCloseSystemTitle(){
		if(MIUIUtils.isMIUI()){
			return getString(R.string.setting_prompt_close_system_clock);
		}
		return getString(R.string.choose_no_psd_type);
	}



	class SecondTimeTask extends TimerTask {
		private  int MAX_WAIT_TIME = 5;
		private int usedSecond = 0;
		private View mTargetView;

		public SecondTimeTask(){
		}

		@Override
		public void run() {
			try {
				Log.d("second","开始等待");
				usedSecond++;
				if(usedSecond >= MAX_WAIT_TIME){
					usedSecond =0;
					try {
						if (wm != null && mFloatView != null) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									hideFloatView();
								}
							});
						}
						this.cancel();
					}catch (Throwable e){
						if(GlobalConstants.DEBUG) {
							e.printStackTrace();
						}
					}
				}
			}catch (Throwable e){
				if(GlobalConstants.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	public void showFloatView(){
		if(mFloatView == null) return;
		if(wm == null) return;

		AnimatorSet animatorSet = new AnimatorSet();
		Animator alphaAnimator = ObjectAnimator.ofFloat(mFloatView, "alpha", new float[]{
				0.0F, 1.0F}).setDuration(500);
		animatorSet.play(alphaAnimator);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animatorSet.start();
	}

	public void hideFloatView(){
		if(mFloatView == null) return;
		if(wm == null) return;

		AnimatorSet animatorSet = new AnimatorSet();
		Animator alphaAnimator = ObjectAnimator.ofFloat(mFloatView, "alpha", new float[]{
				1.0F, 0.0F}).setDuration(500);
		animatorSet.play(alphaAnimator);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(wm != null && mFloatView != null) {
					wm.removeViewImmediate(mFloatView);
					mFloatView = null;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				if(wm != null && mFloatView != null) {
					wm.removeViewImmediate(mFloatView);
					mFloatView = null;
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animatorSet.start();
	}

	/**
	 * addView 表示要添加view
	 * @param addView
	 */
	public View setFloatView(View addView, final String title){
		try {

			mFloatView = View.inflate(this, R.layout.layout_settings_prompt, null);
			mFloatView.setAlpha(0.0F);
			final View popupView = mFloatView.findViewById(R.id.popup_window);
			LinearLayout popupViewContent = (LinearLayout) mFloatView.findViewById(R.id.popup_window_content);
			TextView titleView = (TextView) mFloatView.findViewById(R.id.popup_window_title);
			TextView sure = (TextView) mFloatView.findViewById(R.id.popup_window_sure);
			if(addView != null) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				popupViewContent.addView(addView, params);
			}

			mFloatView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					Rect rect = new Rect();
					popupView.getGlobalVisibleRect(rect);
					if (!rect.contains(x, y)) {
						hideFloatView();
					}
					return false;
				}
			});

			mFloatView.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_BACK:
							hideFloatView();
							return true;
					}
					return false;
				}
			});

			if(!TextUtils.isEmpty(title)) {
				titleView.setText(title);
			}

			sure.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideFloatView();
				}
			});
		}catch (Throwable e){
			e.printStackTrace();
		}
		return mFloatView;
	}



	public void sendMessage(int arg3){
		sendMessage(arg3,null);
	}

	public void sendMessage(int arg3,Object arg4){
		Message message = new Message();
		message.what = arg3;
		if(arg4!= null){
			message.obj = arg4;
		}
		this.mHandler.sendMessage(message);
	}


	public void onWindowFocusChanged(boolean z) {
		if(GlobalConstants.DEBUG) {
			Log.d("10", "窗口焦点发生了变化");
		}
		super.onWindowFocusChanged(z);
	}


}
