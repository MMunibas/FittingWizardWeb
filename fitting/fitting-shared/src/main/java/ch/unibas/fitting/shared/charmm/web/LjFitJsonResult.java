package ch.unibas.fitting.shared.charmm.web;

import com.google.gson.JsonObject;
import io.vavr.control.Option;

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
    }
}
