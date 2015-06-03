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
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author hedin
 */
public class COR_generate extends COR implements coordinates_writer{

    protected Writer writer = null;
    private String OutFileName = null;
    private String format = "";
    private PSF psff = null;
    
    public COR_generate(PSF _psff)
    {
        psff = _psff;
        OutFileName = psff.getMyname();
        writer = new CharArrayWriter();
        this.generate();
    }

    private void generate() {

        String line;

        if (this.isExtendedFormat) {
            /*fortran :
             write(iunit,'(i10,2x,a)') nslct,'EXT'
             fm2='(2I10,2X,A8,2X,A8,3F20.10,2X,A8,2X,A8,F20.10)'
             */
            format = "%10d%10d  %-8s  %-8s%20.10f%20.10f%20.10f  %-8s  %-8s%20.10f\n";
        } else {
            /*fortran :
             write(iunit,'(i5)') nslct
             fm2='(2I5,1X,A4,1X,A4,3F10.5,1X,A4,1X,A4,F10.5)'
             */
            format = "%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4s%10.5f\n";
        }

        try {

            // First put some comment line
            writer.write("* Generated with CHARMM_tools\n");
            writer.write("* User : " + System.getProperty("user.name") + "\n");
            writer.write("* Date : " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()) + "\n");
            writer.write("*\n");

            // then the number of atoms and possibly the extended keyword
            if (this.isExtendedFormat) {
                line = String.format("%10d  EXT\n", this.natom);
            } else {
                line = String.format("%5d\n", this.natom);
            }
            writer.write(line);

            //then loop and write all elements
            for (int i = 0; i < this.natom; i++) {
                line = String.format(format,
                        atomID[i],
                        resID[i],
                        resName[i],
                        atomName[i],
                        x[i],
                        y[i],
                        z[i],
                        segName[i],
                        segID[i],
                        weight[i]
                );
                writer.write(line);
            }// end for

            // close stream properly
            writer.close();

        } catch (IOException ex) {
            logger.error("Error while generating a COR file : " + ex);
        }

    }// end of dumpCOR

    @Override
    public String getTextContent() {
        return writer.toString();
    }
    
    @Override
    public void writeFile() throws IOException {
        Writer writerf = new BufferedWriter(
                new FileWriter(
                        new File("test",OutFileName+".cor")
                )
        );
        writerf.write(writer.toString());
        writerf.close();
    }
    
    @Override
    public void setModifiedTextContent(String content) throws IOException{
        writer.close();
        writer = null;
        writer = new CharArrayWriter();
        writer.write(content);
    }
    
}
