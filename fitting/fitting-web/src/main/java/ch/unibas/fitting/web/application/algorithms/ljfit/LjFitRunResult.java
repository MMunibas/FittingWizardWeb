package ch.unibas.fitting.web.application.algorithms.ljfit;

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
