package com.zyq.accessibility.setting.common.data;

import android.os.Message;
import android.util.Log;

/**
 * @author zyq 16-5-17
 */
public class c extends Thread {

	public boolean isCompleted = false;
	final  b b;

	public c(b bVar) {
		this.b = bVar;
	}

	@Override
	public final void run() {
		Log.d("1","c.run");
		try {
			while (!this.isCompleted && this.b.c != null && !this.b.c.isEmpty()) {
				int i = 0;
				while (this.b.c != null && i < this.b.c.size()) {
					boolean isSuccess;
					d dVar = (d) this.b.c.get(i);
					Message a2 = dVar.e;
					switch (dVar.c) {
						case 1:
							isSuccess = dVar.b.initConfig(a2);
							break;
						case 2:
							Log.d("1","c.run.2");
							isSuccess = dVar.b.startConfig();
							break;
						case 4://对save操作不做处理
							Log.d("1","c.run.4");
							isSuccess = false;
							break;
						case 8:
							Log.d("1","c.run.8");
							isSuccess = dVar.b.clearConfig();
							break;
						default:
							isSuccess = true;
							break;
					}
					if (isSuccess) {
						if(dVar.c == 1){
							Log.d("10","完成了配置阶段");
						}else if(dVar.c == 2){
							Log.d("10","完成了更新阶段");
						}
						d.d(dVar);
					}
					if (isSuccess) {
						this.b.c.remove(i);
						i--;
					}
					i++;
				}
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.isCompleted = true;
			this.b.b = null;
		}catch (Throwable e){
			e.printStackTrace();
		}
	}
}
