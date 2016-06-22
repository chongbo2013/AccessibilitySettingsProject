package com.zyq.accessibility.setting.common.data;

import android.os.Handler;
import android.os.Message;

/**
 * @author zyq 16-5-17
 */
public abstract class a {
	public a() {
		super();
	}

	protected abstract boolean startConfig();

	protected abstract boolean initConfig(Message arg1);

	protected abstract boolean clearConfig();

	public final void clear() {
		this.save(null, 0);
	}

	public final void clear(Handler arg3, int arg4) {
		b.a().a(this, 8, arg3, arg4);
	}

	public final void init() {
		this.init(null, 0);
	}

	//开始处理消息,就是ServiceConfigUtil.initConfig();
	public final void init(Handler arg3, int arg4) {
		b.a().a(this, 1, arg3, arg4);
	}

	//不做处理
	public final void save(Handler arg3, int arg4) {
		b.a().a(this, 4, arg3, arg4);
	}

	public final void save() {
		this.save(null, 0);
	}

	public final void update() {
		this.update(null, 0);
	}

	//开始进行自动化配置了
	public final void update(Handler arg3, int arg4) {
		b.a().a(this, 2, arg3, arg4);
	}
}