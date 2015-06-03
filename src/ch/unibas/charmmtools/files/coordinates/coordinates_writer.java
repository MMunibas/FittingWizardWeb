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

/**
 *
 * @author hedin
 */
public interface coordinates_writer {
    
    public abstract String getTextContent();
    public void writeFile() throws IOException;
    public void setModifiedTextContent(String content) throws IOException;
}
