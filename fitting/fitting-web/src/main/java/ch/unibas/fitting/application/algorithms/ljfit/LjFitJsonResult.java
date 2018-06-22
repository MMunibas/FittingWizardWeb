package ch.unibas.fitting.application.algorithms.ljfit;

import com.google.gson.JsonObject;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class LjFitJsonResult {
    public final double molar_mass;
    public final double pure_liquid_density;
    public final double vaporization_enthalpy;
    public final double dg_solv_vdw_gas;
    public final double dg_solv_vdw_solv;
    public final double dg_solv_elec_gas;
    public final double dg_solv_elec_solv;
    public final double dg_tot_gas_phase;
    public final double dg_tot_solution_phase;
    public final double dg_total;

    public final double score_deltaG;
    public final double score_deltaH;
    public final double score_density;
    public final double score_total;

    public LjFitJsonResult(JsonObject json) {
        molar_mass = json.get("molar_mass").getAsDouble();
        pure_liquid_density = json.get("pure_liquid_density").getAsDouble();
        vaporization_enthalpy = json.get("vaporization_enthalpy").getAsDouble();
        dg_solv_vdw_gas = json.get("dg_solv_vdw_gas").getAsDouble();
        dg_solv_vdw_solv = json.get("dg_solv_vdw_solv").getAsDouble();
        dg_solv_elec_gas = json.get("dg_solv_elec_gas").getAsDouble();
        dg_solv_elec_solv = json.get("dg_solv_elec_solv").getAsDouble();
        dg_tot_gas_phase = json.get("dg_tot_gas_phase").getAsDouble();
        dg_tot_solution_phase = json.get("dg_tot_solution_phase").getAsDouble();
        dg_total = json.get("dg_total").getAsDouble();

        score_total = json.get("score_total").getAsDouble();
        score_density = json.get("score_density").getAsDouble();
        score_deltaG = json.get("score_dg").getAsDouble();
        score_deltaH = json.get("score_dh").getAsDouble();
    }
}
