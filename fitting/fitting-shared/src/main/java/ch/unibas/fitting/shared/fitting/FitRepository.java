/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;

import ch.unibas.fitting.shared.base.MemoryRepository;
import ch.unibas.fitting.shared.molecules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User: mhelmer
 * Date: 03.12.13
 * Time: 14:45
 */
public class FitRepository extends MemoryRepository<Fit> {

    public int getFitCount() {
        return loadAll().size();
    }

    public int getNextFitId() {

        Optional<Integer> max = loadAll()
                .stream()
                .map(Fit::getId)
                .max(Integer::compareTo);
        if (max.isPresent())
            return max.get() +1;
        else
            return 0;
    }

    @Override
    public void save(Fit data) {
        if (loadAll().stream().anyMatch(fit1 -> fit1.getId() == data.getId()))
            throw new RuntimeException("Fit with id [" + data.getId() + "] already exists.");
        super.save(data);
    }

    public Optional<Fit> findById(int fitId) {
        return loadAll().stream()
                .filter(fit -> fit.getId() == fitId)
                .findFirst();
    }

    public void remove(int index) {
        Optional<Fit> f = findById(index);
        if (f.isPresent())
            remove(f.get());
    }
}
