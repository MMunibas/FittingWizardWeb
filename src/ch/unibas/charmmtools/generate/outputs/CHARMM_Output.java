/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.outputs;

import ch.unibas.charmmtools.generate.CHARMM_InOut;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_Output implements CHARMM_InOut{
    
    private final static Logger logger = Logger.getLogger(CHARMM_Output.class);
    
    private String textOut;
    
    private final File fileOut;
    
    private final String type;
    
    private Boolean errorOccured = false;
    private final static String hasFailed = "NORMAL TERMINATION BY NORMAL STOP";
    
    public CHARMM_Output(File _charmmout, String _type){
        
        fileOut = _charmmout;
        type = _type;
        
        try {
            textOut = new String(
                    Files.readAllBytes(
                            Paths.get(fileOut.getAbsolutePath())
                    )
            );
        } catch (IOException ex) {
            logger.error("Error when loading the CHARMM output file " + fileOut.getAbsolutePath());
        }
        
        if (!textOut.contains(hasFailed)){
            errorOccured=true;
        }
        
    }// ctor

    /**
     * @return the textOut
     */
    @Override
    public String getText() {
        return textOut;
    }

    /**
     * @return the fileOut
     */
    public File getFile() {
        return fileOut;
    }

    /**
     * @return the errorOccured
     */
    public Boolean getErrorOccured() {
        return errorOccured;
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }
  
}
