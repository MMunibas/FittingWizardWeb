package ch.unibas.fitting.application.algorithms.ljfit;

public class LjFitRunResult {
    public final double lambdaEpsilon,
            lambdaSigma,
            vdwGas,
            mtpGas,
            mtpSol,
            vdwSol,
            totalGas,
            totalSol,
            calcdeltaG,
            expdeltaG,
            calcdeltaH,
            expdeltaH,
            calcdensity,
            expdensity,
            deltaG,
            deltaH,
            density,
            score;

    public LjFitRunResult(LjFitSession session,
                          LjFitRunInput in,
                          LjFitJsonResult jsonResult){
        this(
                in.lambda_epsilon,
                in.lambda_sigma,
                jsonResult.dg_solv_vdw_gas,
                jsonResult.dg_solv_elec_gas,
                jsonResult.dg_solv_elec_solv,
                jsonResult.dg_solv_vdw_solv,
                jsonResult.dg_tot_gas_phase,
                jsonResult.dg_tot_solution_phase,
                jsonResult.dg_total,
                session.getSessionParameter().expectedDeltaG,
                jsonResult.vaporization_enthalpy,
                session.getSessionParameter().expectedDeltaH,
                jsonResult.pure_liquid_density,
                session.getSessionParameter().expectedDensity,
                jsonResult.score_deltaG,
                jsonResult.score_deltaH,
                jsonResult.score_density,
                jsonResult.score_total
        );
    }

    public LjFitRunResult(double lambdaEpsilon, double lambdaSigma, double vdwgas, double mtpgas, double mtpsol, double vdwsol, double gastotal, double soltotal, double calcdeltaG, double expdeltaG, double calcdeltaH, double expdeltaH, double calcdensity, double expdensity, double deltaG, double deltaH, double density, double score) {
        this.lambdaEpsilon = lambdaEpsilon;
        this.lambdaSigma = lambdaSigma;
        vdwGas = vdwgas;
        mtpGas = mtpgas;
        mtpSol = mtpsol;
        vdwSol = vdwsol;
        totalGas = gastotal;
        totalSol = soltotal;
        this.calcdeltaG = calcdeltaG;
        this.expdeltaG = expdeltaG;
        this.calcdeltaH = calcdeltaH;
        this.expdeltaH = expdeltaH;
        this.calcdensity = calcdensity;
        this.expdensity = expdensity;
        this.deltaG = deltaG;
        this.deltaH = deltaH;
        this.density = density;
        this.score = score;
    }
}
