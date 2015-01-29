/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class CHARMM_output implements CHARMM_inout{
    
    private final static Logger logger = Logger.getLogger(CHARMM_output.class);
    
    private String textOut;
    
    private File fileOut;
    
    private Boolean errorOccured = false;
    private final String hasFailed = "NORMAL TERMINATION BY NORMAL STOP";
    
    public CHARMM_output(File charmmout){
        
        fileOut = charmmout;
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
    public String getTextOut() {
        return textOut;
    }
    
    
    
//    public static void main(String args[]){
//        File choutf = new File("density.out");
//        
//        CHARMM_output out = new CHARMM_output(choutf);
//        
//        String outs = out.getTextOut();
//        
//        System.out.println(outs);
//    }

    /**
     * @return the fileOut
     */
    public File getFileOut() {
        return fileOut;
    }

    /**
     * @return the errorOccured
     */
    public Boolean getErrorOccured() {
        return errorOccured;
    }
  
}
