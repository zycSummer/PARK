package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/25 15:01
 * @desc
 */
@Data
public class ElectricityVO implements Serializable {
    private static final long serialVersionUID = 9159902829680616415L;
    private Double Ua;
    private Double Ub;
    private Double Uc;
    private Double Uab;
    private Double Ubc;
    private Double Uca;
    private Double Ia;
    private Double Ib;
    private Double Ic;
    private Double Pa;
    private Double Pb;
    private Double Pc;
    private Double P;
    private Double Qa;
    private Double Qb;
    private Double Qc;
    private Double Q;
    private Double Sa;
    private Double Sb;
    private Double Sc;
    private Double S;
    private Double PFa;
    private Double PFb;
    private Double PFc;
    private Double PF;
    private Double Ep_imp;
    private Double Ep_exp;
    private Double Eq_imp;
    private Double Eq_exp;

    public ElectricityVO() {
    }


    public ElectricityVO(Double ua, Double ub, Double uc, Double uab, Double ubc, Double uca, Double ia, Double ib, Double ic, Double pa, Double pb, Double pc, Double p, Double qa, Double qb, Double qc, Double q, Double sa, Double sb, Double sc, Double s, Double PFa, Double PFb, Double PFc, Double PF, Double ep_imp, Double ep_exp, Double eq_imp, Double eq_exp) {
        Ua = ua;
        Ub = ub;
        Uc = uc;
        Uab = uab;
        Ubc = ubc;
        Uca = uca;
        Ia = ia;
        Ib = ib;
        Ic = ic;
        Pa = pa;
        Pb = pb;
        Pc = pc;
        P = p;
        Qa = qa;
        Qb = qb;
        Qc = qc;
        Q = q;
        Sa = sa;
        Sb = sb;
        Sc = sc;
        S = s;
        this.PFa = PFa;
        this.PFb = PFb;
        this.PFc = PFc;
        this.PF = PF;
        Ep_imp = ep_imp;
        Ep_exp = ep_exp;
        Eq_imp = eq_imp;
        Eq_exp = eq_exp;
    }
}
