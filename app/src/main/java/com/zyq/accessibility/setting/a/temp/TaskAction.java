package com.zyq.accessibility.setting.a.temp;


import com.zyq.accessibility.setting.a.ServiceConfigUtil;

/**
 * @author zyq 16-5-17
 */
public final class TaskAction {
	String action;
	Task task;
	boolean actionFinished;//
	boolean c;

	TaskAction(Task arg1, String arg2, byte arg3) {
		this(arg1, arg2);
	}

	public TaskAction(Task arg3, String arg4) {
		super();
		this.task = arg3;
		this.actionFinished = false;
		this.c = false;
		this.action = arg4;
	}

	public final Action getAction() {
		return ServiceConfigUtil.getInstance().getSettingAction(this.action);
	}

	public final void init() {
		this.actionFinished = false;
		this.c = false;
	}
}
