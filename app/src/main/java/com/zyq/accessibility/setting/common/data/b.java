package com.zyq.accessibility.setting.common.data;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyq 16-5-17
 */
public class b {

	private static b a = null;
	public c b = null;
	public List c = null;

	private b() {
	}

	public static final b a() {
		if (a == null) {
			a = new b();
		}
		return a;
	}

	final void a(a aVar, int i, Handler handler, int i2) {
		if (this.c == null) {
			this.c = new ArrayList();
		}
		d dVar = new d(this, aVar, i, handler, i2);
		if (!this.c.contains(dVar)) {
			this.c.add(dVar);
		}
		if (this.b == null) {
			this.b = new c(this);
			this.b.start();
		}
	}
}
