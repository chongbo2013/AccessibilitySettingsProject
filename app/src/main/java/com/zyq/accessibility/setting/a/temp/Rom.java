package com.zyq.accessibility.setting.a.temp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author zyq 16-5-18
 */
public class Rom {//rom基本信息

	private String[] a = null;
	private boolean b = true;
	private List c = new ArrayList();

	public final String[] getRomNames() {
		return this.a;
	}

	public final boolean matched() {//表示全部条件都要满足,一个不满足都不满足.....
		boolean z = false;
		int i = 0;
		while (i < this.c.size()) {
			boolean matched = ((Feature) this.c.get(i)).matched();
			if (this.b != matched) {

				return matched;
			}
			i++;
			z = matched;
		}
		return z;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "feature")
	public final void setFeature(Feature featureVar) {
//		Log.d("1","setFeature:");
		this.c.add(featureVar);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "logical")
	public final void setLogical(String str) {
//		Log.d("1","setLogical"+str);
		if ("or".equals(str.toLowerCase(Locale.ENGLISH))) {
			this.b = false;
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "name")
	public final void setRomName(String str) {
//		Log.d("1","setRomName"+str);
		this.a = str.split(",");
	}
}
