package com.zyq.accessibility.setting.common.data;

import android.os.Handler;
import android.os.Message;

/**
 * @author zyq 16-5-17
 */
public class d {

	final  b a;
	public a b;
	public int c;
	private Handler d;
	public Message e = new Message();

	public d(b bVar, a aVar, int i, Handler handler, int i2) {
		this.a = bVar;
		this.b = aVar;
		this.c = i;
		this.d = handler;
		this.e.what = i2;
	}

	static void d(d dVar) {
		if (dVar.d != null) {
			dVar.d.sendMessage(dVar.e);
		}
	}

	public final boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(d.class)) {
			return false;
		}
		d dVar = (d) obj;
		return this.b.equals(dVar.b) && this.c == dVar.c;
	}

	public final int hashCode() {
		return (this.b.hashCode() * 7) + (this.c * 13);
	}
}
