package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.shared.javaextensions.Function1;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRun;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunResult;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class SingleRunResult {
    boolean wasSuccessful, isLowestScore;
    String dirName;
    Double _eps, _sigma, _VDWGAS, _MTPGAS, _MTPSOL, _VDWSOL, _GASTOTAL, _SOLTOTAL, _calcdeltaG, _expdeltaG, _calcdeltaH, _expdeltaH, _calcdensity, _expdensity, _deltaG, _deltaH, _density, _Score;

    public SingleRunResult(LjFitRun result, Option<Double> minRun) {
        this.wasSuccessful = result.wasSuccessful();
        this._eps = valueOrNan(result, p1 -> p1.lambdaEpsilon);
        this._sigma = valueOrNan(result, p1 -> p1.lambdaSigma);
        this._calcdeltaG = valueOrNan(result, p1 -> p1.calcdeltaG);
        this._VDWGAS = valueOrNan(result, p1 -> p1.vdwGas);
        this._MTPGAS = valueOrNan(result, p1 -> p1.mtpGas);
        this._MTPSOL = valueOrNan(result, p1 -> p1.mtpSol);
        this._VDWSOL = valueOrNan(result, p1 -> p1.mtpSol);
        this._GASTOTAL = valueOrNan(result, p1 -> p1.totalGas);
        this._SOLTOTAL = valueOrNan(result, p1 -> p1.totalSol);
        this._expdeltaG = valueOrNan(result, p1 -> p1.expdeltaG);
        this._calcdeltaH = valueOrNan(result, p1 -> p1.calcdeltaH);
        this._expdeltaH = valueOrNan(result, p1 -> p1.expdeltaH);
        this._calcdensity = valueOrNan(result, p1 -> p1.calcdensity);
        this._expdensity = valueOrNan(result, p1 -> p1.expdensity);
        this._deltaG = valueOrNan(result, p1 -> p1.deltaG);
        this._deltaH = valueOrNan(result, p1 -> p1.deltaH);
        this._density = valueOrNan(result, p1 -> p1.density);
        this._Score = valueOrNan(result, p1 -> p1.score);
        this.dirName = result.dirName;

        minRun.peek(minScore -> isLowestScore = !minScore.isNaN()
                && !_Score.isNaN()
                && minScore.equals(_Score));
    }

    private Double valueOrNan(LjFitRun result,
                              Function1<LjFitRunResult, Double> callback) {
        return result.result.map(callback::apply).getOrElse(Double.NaN);
    }

    public String getDirName() {
        return dirName;
    }

    public Double get_eps() { return _eps; }

    public Double get_sigma() { return _sigma; }

    public Double get_calcdeltaG() { return _calcdeltaG; }

    public Double get_VDWGAS() { return _VDWGAS;}

    public Double get_MTPGAS() { return _MTPGAS; }

    public Double get_MTPSOL() { return _MTPSOL; }

    public Double get_VDWSOL() { return _VDWSOL; }

    public Double get_GASTOTAL() { return _GASTOTAL; }

    public Double get_SOLTOTAL() { return _SOLTOTAL; }

    public Double get_expdeltaG() { return _expdeltaG; }

    public Double get_calcdeltaH() { return _calcdeltaH;    }

    public Double get_expdeltaH() { return _expdeltaH;    }

    public Double get_calcdensity() { return _calcdensity;    }

    public Double get_expdensity() { return _expdensity;    }

    public Double get_deltaG() { return _deltaG;    }

    public Double get_deltaH() { return _deltaH;    }

    public Double get_density() { return _density;    }

    public Double get_Score() { return _Score;    }

    public boolean hasLowestScore() {
        return isLowestScore;
    }
}