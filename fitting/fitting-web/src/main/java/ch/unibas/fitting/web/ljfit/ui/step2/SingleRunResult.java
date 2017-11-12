package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunResult;

public class SingleRunResult {
    String dirName;
    Double _eps, _sigma, _VDWGAS, _MTPGAS, _MTPSOL, _VDWSOL, _GASTOTAL, _SOLTOTAL, _calcdeltaG, _expdeltaG, _calcdeltaH, _expdeltaH, _calcdensity, _expdensity, _deltaG, _deltaH, _density, _Score;

    public SingleRunResult(Double _eps,Double _sigma,Double _deltaGofhydration,Double _VDWGAS,
                           Double _MTPGAS,Double _MTPSOL,Double _VDWSOL,Double _GASTOTAL,Double _SOLTOTAL,
                           Double _expdeltaG,Double _calcdeltaH,Double _expdeltaH,Double _calcdensity,
                           Double _expdensity,Double _deltag,Double _DeltaH,Double _density,Double _Score,
                           String dirName) {
        this._eps = _eps;
        this._sigma = _sigma;
        this._calcdeltaG = _deltaGofhydration;
        this._VDWGAS = _VDWGAS;
        this._MTPGAS = _MTPGAS;
        this._MTPSOL = _MTPSOL;
        this._VDWSOL = _MTPSOL;
        this._GASTOTAL = _GASTOTAL;
        this._SOLTOTAL = _SOLTOTAL;
        this._expdeltaG = _expdeltaG;
        this._calcdeltaH = _calcdeltaH;
        this._expdeltaH = _expdeltaH;
        this._calcdensity = _calcdensity;
        this._expdensity = _expdensity;
        this._deltaG = _deltag;
        this._deltaH = _DeltaH;
        this._density = _density;
        this._Score = _Score;
        this.dirName = dirName;
    }

    public SingleRunResult(String dirName, LjFitRunResult result) {
        this(result.lambdaEpsilon,
                result.lambdaSigma,
                result.vdwGas,
                result.mtpGas,
                result.mtpSol,
                result.vdwSol,
                result.totalGas,
                result.totalSol,
                result.calcdeltaG,
                result.expdeltaG,
                result.calcdeltaH,
                result.expdeltaH,
                result.calcdensity,
                result.expdensity,
                result.deltaG,
                result.deltaH,
                result.density,
                result.score,
                dirName);
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
}