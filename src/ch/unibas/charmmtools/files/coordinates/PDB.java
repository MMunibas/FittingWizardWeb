/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.coordinates;

import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public abstract class PDB {
    
    protected static final Logger logger = Logger.getLogger(PDB.class);
    
    protected String fname = null;
    protected boolean charmmPDBtype = true;
    
//        if (this.isExtendedFormat) {
//            /*fortran :
//             write(iunit,'(i10,2x,a)') nslct,'EXT'
//             fm2='(2I10,2X,A8,2X,A8,3F20.10,2X,A8,2X,A8,F20.10)'
//             */
//            format = "%10d%10d  %-8s  %-8s%20.10f%20.10f%20.10f  %-8s  %-8s%20.10f\n";
//        } else {
//            /*fortran :
//             write(iunit,'(i5)') nslct
//             fm2='(2I5,1X,A4,1X,A4,3F10.5,1X,A4,1X,A4,F10.5)'
//             */
//            format = "%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4s%10.5f\n";
//        }
    
    /**
    IF(OFFICIAL)THEN
    WRITE(IUNIT, &
            '(A6,I5,1X,A4,1X,A3,1X,A1,A5,3X,3F8.3,2F6.2,6X,A4)') &
            'ATOM  ',I,ATYPEI,REN,SID,ARID,X(I),Y(I),Z(I),1.0,WMAIN(I),SID
    ELSE
    WRITE(IUNIT, &
            '(A6,I5,1X,A4,1X,A4,1X,   A5,3X,3F8.3,2F6.2,6X,A4)') &
            'ATOM  ',I,ATYPEI,REN,ARID,X(I),Y(I),Z(I),1.0,WMAIN(I) &
            ,SID
    ENDIF
    */
    
    //protected final static String officialFormat = "";
    protected final static String charmmFormat = "%-6s%5d %-4s %-4s %-5s   %8.3f%8.3f%8.3f%6.2f%6.2f      %-4s";
    
    public abstract String getTextContent();
    public abstract void writeFile() throws IOException;
}
