/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.application.algorithms.xyz;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:14
 */
public class ZxyGenerator {
    public static final String zxyExtension = ".zxy";
    
    private List<String> content;
    
    private static final int HeaderLine = 0;
    private static final int FirstAtomLine = 2;

    public File generateFrom(File coordinateFile) {
        File zxyFile = prepareFile(coordinateFile);
        
        readLinesFromFile(coordinateFile);
        if (content.size()<=HeaderLine) {
        	throw new RuntimeException("Only header found in coordinate file " + coordinateFile);
        }
        
        try {
			FileUtils.writeLines(zxyFile, content.subList(FirstAtomLine, content.size()));
		} catch (IOException e) {
			throw new RuntimeException("Could not write to zxy file.");
		}
        
        return zxyFile;
    }
    
    private void readLinesFromFile(File coordinateFile) {
    	try {
        	content = FileUtils.readLines(coordinateFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not read coordinates file.");
        }
    }

    private File prepareFile(File coordinateFile) {
        return new File(coordinateFile.getParentFile(), FilenameUtils.removeExtension(coordinateFile.getName()) + zxyExtension);
    }
}
