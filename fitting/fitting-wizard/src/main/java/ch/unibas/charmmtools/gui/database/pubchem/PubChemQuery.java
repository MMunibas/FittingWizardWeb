/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.pubchem;

import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.fittingwizard.gaussian.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A class for querying the PubChem DataBase and getting properties
 *
 * @author hedin
 */
public class PubChemQuery extends ModalDialog {

    private final String base_url = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound";

    private final String post_url = "/property/"
            + "IUPACName,"
            + "MolecularFormula,"
            + "InChI,"
            + "CanonicalSMILES,"
            + "MolecularWeight"
            + "/XML";

    private DB_model pbModel = null;

    private final Scene parent;

    private DocumentBuilderFactory dbf = null;
    private DocumentBuilder db = null;
    private Document doc = null;

    @FXML
    private Label label_waiting;

    public PubChemQuery(Scene _parent) {
        super("Please wait while PubChem Query is executed");
        parent = _parent;
        pbModel = new DB_model();
        this.setResizable(false);
    }

    public DB_model byPubChemId(String pid) {
        String search = base_url + "/cid/" + pid + post_url;
        CURL_like(search);
        return pbModel;
    }

    public DB_model byName(String name) {
        String search = base_url + "/name/" + name + post_url;
        CURL_like(search);
        return pbModel;
    }

    public DB_model byFormula(String formula) {
        String search = base_url + "/formula/" + formula + post_url;
        CURL_like(search);
        return pbModel;
    }

    public DB_model byInchi(String inchi) {
        String search = base_url + "/inchi/" + inchi + post_url;
        CURL_like(search);
        return pbModel;
    }

    public DB_model bySmiles(String smiles) {
        String search = base_url + "/smiles/" + smiles + post_url;
        CURL_like(search);
        return pbModel;
    }

    private void CURL_like(String searchURL) {

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(searchURL).openStream());

            // For checking in terminal the xml content
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer xform = factory.newTransformer();
            xform.transform(new DOMSource(doc), new StreamResult(System.out));

            // get root element, should be "PropertyTable" or "Waiting"
            Element root = doc.getDocumentElement();
            root.normalize();
            String FirstNodeName = root.getNodeName();
            logger.info("Root element: " + FirstNodeName);

            // first see if we have to wait or if results are available
            boolean dataAvailable = rootNodeChoice(FirstNodeName);

            // then extract data
            if(dataAvailable)
                propsParsing();

        } catch (IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            logger.error("Problem when parsing XML coming from PubChem : " + ex.getMessage());
            OverlayDialog.showError("Error when querying PubChem", "There was an error when querying PubChem. Check details from Log console / Log file");
            close();
        }

    }// end curl_like query function

    private boolean rootNodeChoice(String flag) throws ParserConfigurationException, SAXException, IOException {
        boolean ok = false;

        switch (flag) {
            case "PropertyTable":
                //do nothing because ready to parse
                ok = true;
                break;
            case "Waiting":
                // handle the waiting flag from pubchem
                ok = waiting();
        }

        return ok;
    }

    /**
     * If PubChem returns waiting with an associated ID, wait a few seconds
     * because query still running on DB and then check again
     */
    private boolean waiting() throws ParserConfigurationException, SAXException, IOException {
        boolean received_data = false;

        parent.getRoot().setEffect(new BoxBlur());

        //get list id from xml
        NodeList list = doc.getElementsByTagName("ListKey");
        Element nodeListKey = (Element) list.item(0);
        String wid = nodeListKey.getChildNodes().item(0).getNodeValue();
        long listid = Long.valueOf(wid);
        
        label_waiting.setText("Waiting for PubChem : waitID = " + listid);
        show();
        
        /*
         * Here the code logic checking if data received from PubChem
         */
        dbf = null;
        db = null;
        doc = null;
        
        String query =  base_url + "/listkey/" + listid + post_url;
        logger.info("Waiting query : " + query);
        
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        doc = db.parse(new URL(query).openStream());
       
        received_data=true;
        
        parent.getRoot().setEffect(null);
        close();

        return received_data;
    }

    private void propsParsing() {
        // expect only one group of "Properties"
        NodeList propList = doc.getElementsByTagName("Properties");
        Element property1 = (Element) propList.item(0);
        logger.info("Prop Name : " + property1.getNodeName());
        NodeList childs = property1.getChildNodes();

        String cid = "", formula = "", mass = "", smiles = "", inchi = "", name = "";

        for (int i = 0; i < childs.getLength(); i++) {

            Node n = childs.item(i);

            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element) n;
            String property = e.getNodeName();
            String value = e.getChildNodes().item(0).getNodeValue();

            switch (property) {
                case "CID":
                    cid = value;
                    break;

                case "MolecularFormula":
                    formula = value;
                    break;

                case "MolecularWeight":
                    mass = value;
                    break;

                case "CanonicalSMILES":
                    smiles = value;
                    break;

                case "InChI":
                    inchi = value;
                    break;

                case "IUPACName":
                    name = value;
                    break;

                default:
                    break;
            }

            pbModel = new DB_model(cid, name, formula, inchi, smiles, mass);

        }
    } //end func

    @FXML
    protected void cancel(ActionEvent event) {
        logger.info("Cancelled PubChem Query");
        parent.getRoot().setEffect(null);
        close();
    }

}
