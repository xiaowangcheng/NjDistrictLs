package net.htwater.njdistrictfx.bean;

public class StackedBarBean {
	private float normalnum;
	private float guardnum;
	private String xval;

	public StackedBarBean(String xVal, float normalnum, float guardnum) {
		super();
		this.normalnum = normalnum;
		this.guardnum = guardnum;
		this.xval = xVal;
	}

	public float getNormalnum() {
		return normalnum;
	}

	public void setNormalnum(float normalnum) {
		this.normalnum = normalnum;
	}

	public float getGuardnum() {
		return guardnum;
	}

	public void setGuardnum(float guardnum) {
		this.guardnum = guardnum;
	}

	public String getXval() {
		return xval;
	}

	public void setXval(String xval) {
		this.xval = xval;
	}

	@Override
	public String toString() {
		return "StackedBarBean [normalnum=" + normalnum + ", guardnum="
				+ guardnum + "]";
	}

}
