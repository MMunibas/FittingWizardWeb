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
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A class for querying the PubChem DataBase and getting properties
 *
 * @author hedin
 */
public class PubChemQuery {

    protected final Logger logger = Logger.getLogger(PubChemQuery.class);

    private final String base_url = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound";

    private final String post_url = "/property/"
            + "IUPACName,"
            + "MolecularFormula,"
            + "InChI,"
            + "CanonicalSMILES,"
            + "MolecularWeight"
            + "/XML";

    private DB_model pbModel = new DB_model();

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

        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document doc = null;
        Element rootElement;

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(searchURL).openStream());

            // For checking in terminal the xml content
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer xform = factory.newTransformer();
            xform.transform(new DOMSource(doc), new StreamResult(System.out));

            // get root element, should be "PropertyTable"
            Element root = doc.getDocumentElement();
            root.normalize();
            logger.info("Root element: " + 
                        root.getNodeName());
            
            Node properties = root.getFirstChild();
            logger.info("First child element: " + 
                        properties.getNodeName());
            
            Node child = properties.getFirstChild();
            logger.info(child.getNodeName() + " : " + child.getNodeValue());
            while(child.getNextSibling() != null)
            {
                logger.info(child.getNodeName() + " : " + child.getNodeValue());
            }
            
            // expect only one group of "Properties"
//            NodeList propList = doc.getElementsByTagName("Properties");
//            Node property = propList.item(0);
//            Node child = property.getFirstChild();
//            logger.info(child.getNodeName() + " : " + child.getNodeValue());
//            while(child.getNextSibling() != null)
//            {
//                logger.info(child.getNodeName() + " : " + child.getNodeValue());
//            }


//            System.out.println ("Root element: " + 
//                        doc.getDocumentElement().getNodeName());
            
//            rootElement = doc.getDocumentElement();
//            String cid = rootElement.getAttribute("CID");
//            String formula = rootElement.getAttribute("MolecularFormula");
//            String mass = rootElement.getAttribute("MolecularWeight");
//            String smiles = rootElement.getAttribute("CanonicalSMILES");
//            String inchi = rootElement.getAttribute("InChI");
//            String name = rootElement.getAttribute("IUPACName");
//            pbModel = new DB_model()
            // Get the document's root XML node
//            NodeList root = doc.getChildNodes();
//            Node comp = getNode("Properties", root);
//            String name = "";

        } catch (IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            logger.error("Problem when parsing XML coming from PubChem : " + ex.getMessage());
        }

    }

//    protected Node getNode(String tagName, NodeList nodes) {
//        for (int x = 0; x < nodes.getLength(); x++) {
//            Node node = nodes.item(x);
//            if (node.getNodeName().equalsIgnoreCase(tagName)) {
//                return node;
//            }
//        }
//
//        return null;
//    }
    
//    protected String getNodeValue(Node node) {
//        NodeList childNodes = node.getChildNodes();
//        for (int x = 0; x < childNodes.getLength(); x++) {
//            Node data = childNodes.item(x);
//            if (data.getNodeType() == Node.TEXT_NODE) {
//                return data.getNodeValue();
//            }
//        }
//        return "";
//    }
}
