package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.web.application.algorithms.ljfit.LjFitRun;
import io.vavr.control.Option;

public class SingleRunResult {
    boolean wasSuccessful, isLowestScore;
    String dirName;
    Double _eps, _sigma, _VDWGAS, _MTPGAS, _MTPSOL, _VDWSOL, _GASTOTAL, _SOLTOTAL, _calcdeltaG, _expdeltaG, _calcdeltaH, _expdeltaH, _calcdensity, _expdensity, _deltaG, _deltaH, _density, _Score;

    public SingleRunResult(LjFitRun result, Option<Double> minRun) {
        this.wasSuccessful = result.wasSuccessful();
        result.input.peek(in -> {
            this._eps = in.lambda_epsilon;
            this._sigma = in.lambda_sigma;
        });
        result.result.peek(res -> {
            this._calcdeltaG = res.calcdeltaG;
            this._VDWGAS = res.vdwGas;
            this._MTPGAS = res.mtpGas;
            this._MTPSOL = res.mtpSol;
            this._VDWSOL = res.vdwSol;
            this._GASTOTAL = res.totalGas;
            this._SOLTOTAL = res.totalSol;
            this._expdeltaG = res.expdeltaG;
            this._calcdeltaH = res.calcdeltaH;
            this._expdeltaH = res.expdeltaH;
            this._calcdensity = res.calcdensity;
            this._expdensity = res.expdensity;
            this._deltaG = res.deltaG;
            this._deltaH = res.deltaH;
            this._density = res.density;
            this._Score = res.score;
        });

        this.dirName = result.dirName;

        minRun.peek(minScore -> isLowestScore = !minScore.isNaN()
                && _Score != null
                && !_Score.isNaN()
                && minScore.equals(_Score));
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