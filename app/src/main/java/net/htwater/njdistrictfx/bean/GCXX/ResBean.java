package net.htwater.njdistrictfx.bean.GCXX;

/**
 * Created by LZY on 2017/5/26.
 */

public class ResBean extends GcxxBean{
    private String ENNM;
    private String ENTYNM;//类型
    private double FLOOD_NORMAL_Z;//正常蓄水位
    private double UTIL_S;//兴利库容
    private double FLOOD_LIMIT_Z;//防洪限制水位
    private double FLOOD_TOP_Z;//防洪高水位
    private double FLOOD_S;//防洪库容
    private double CHECK_FLOOD_Z;//校核洪水位
    private double TOTAL_S;//总库容
    private double DAM_ELEV;//坝顶高程
    private double LGTD;
    private double LATD;
    private String CITY;
    private String _s;

    public String getENNM() {
        return ENNM;
    }

    public void setENNM(String ENNM) {
        this.ENNM = ENNM;
    }

    public String getENTYNM() {
        return ENTYNM;
    }

    public void setENTYNM(String ENTYNM) {
        this.ENTYNM = ENTYNM;
    }

    public double getFLOOD_NORMAL_Z() {
        return FLOOD_NORMAL_Z;
    }

    public void setFLOOD_NORMAL_Z(double FLOOD_NORMAL_Z) {
        this.FLOOD_NORMAL_Z = FLOOD_NORMAL_Z;
    }

    public double getUTIL_S() {
        return UTIL_S;
    }

    public void setUTIL_S(double UTIL_S) {
        this.UTIL_S = UTIL_S;
    }

    public double getFLOOD_LIMIT_Z() {
        return FLOOD_LIMIT_Z;
    }

    public void setFLOOD_LIMIT_Z(double FLOOD_LIMIT_Z) {
        this.FLOOD_LIMIT_Z = FLOOD_LIMIT_Z;
    }

    public double getFLOOD_TOP_Z() {
        return FLOOD_TOP_Z;
    }

    public void setFLOOD_TOP_Z(double FLOOD_TOP_Z) {
        this.FLOOD_TOP_Z = FLOOD_TOP_Z;
    }

    public double getFLOOD_S() {
        return FLOOD_S;
    }

    public void setFLOOD_S(double FLOOD_S) {
        this.FLOOD_S = FLOOD_S;
    }

    public double getCHECK_FLOOD_Z() {
        return CHECK_FLOOD_Z;
    }

    public void setCHECK_FLOOD_Z(double CHECK_FLOOD_Z) {
        this.CHECK_FLOOD_Z = CHECK_FLOOD_Z;
    }

    public double getTOTAL_S() {
        return TOTAL_S;
    }

    public void setTOTAL_S(double TOTAL_S) {
        this.TOTAL_S = TOTAL_S;
    }

    public double getDAM_ELEV() {
        return DAM_ELEV;
    }

    public void setDAM_ELEV(double DAM_ELEV) {
        this.DAM_ELEV = DAM_ELEV;
    }

    public double getLGTD() {
        return LGTD;
    }

    public void setLGTD(double LGTD) {
        this.LGTD = LGTD;
    }

    public double getLATD() {
        return LATD;
    }

    public void setLATD(double LATD) {
        this.LATD = LATD;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String get_s() {
        return _s;
    }

    public void set_s(String _s) {
        this._s = _s;
    }
}
