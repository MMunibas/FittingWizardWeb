/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.coordinates;

import ch.unibas.charmmtools.files.structure.PSF;
import ch.unibas.charmmtools.internals.Atom;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author hedin
 */
public class PDB_generate extends PDB {

    protected Writer writer = null;
    //private XyzFile xyzf;
    private PSF psff;

    public PDB_generate(/*XyzFile _xyzf, */PSF _psff) {

        //this.xyzf = _xyzf;
        this.psff = _psff;

        try {

            writer = new CharArrayWriter();
            this.writeTitle();
            this.writePdbCore();
            this.writePdbEnd();

        } catch (IOException ex) {

            logger.error("Error while generating PDB file : " + ex);

        }

    }

    private void writeTitle() throws IOException {

        DateFormat df = new SimpleDateFormat();
        df.setTimeZone(TimeZone.getDefault());
        Date d = new Date(df.format(new Date()));
        
        String myname = psff.getMyname();
        writer.write("REMARK PDB file for " + myname + ".xyz\n");
        writer.write("REMARK generated on " + d.toString() + "\n");
        writer.write("REMARK by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n");
//        writer.write("\n");

    }

    private void writePdbCore() throws IOException {

        //int natom = xyzf.getAtomCount();
        //int natom_psf = psff.getNatom();
        //List<XyzAtom> atlist = xyzf.getAtoms();
        String line = "";
        List<Atom> atlist = psff.getAtomList();

//        psff.
        /**
         * IF(OFFICIAL)THEN WRITE(IUNIT, & '(A6,I5,1X,A4,1X,A3,1X,A1,A5,3X,3F8.3,2F6.2,6X,A4)') & 'ATOM
         * ',I,ATYPEI,REN,SID,ARID,X(I),Y(I),Z(I),1.0,WMAIN(I),SID ELSE WRITE(IUNIT, & '(A6,I5,1X,A4,1X,A4,1X,
         * A5,3X,3F8.3,2F6.2,6X,A4)') & 'ATOM ',I,ATYPEI,REN,ARID,X(I),Y(I),Z(I),1.0,WMAIN(I) & ,SID ENDIF
         */
        
        for (Atom a : atlist) {
            line = String.format(PDB.charmmFormat, "ATOM  ", a.getCHARMMAtomID(), a.getRtfType(),
                    a.getResName(), a.getResID(), a.getX(), a.getY(), a.getZ(),
                    1.0, 0.0, a.getSegName());
            writer.write(line+"\n");
        }

    }

    private void writePdbEnd() throws IOException {
        writer.write("END\n");
    }

    public String getTextContent() {
        return writer.toString();
    }

}
