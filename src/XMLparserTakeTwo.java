import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Rikard Eide on 18/03/15.
 * Description:
 */
public class XMLparserTakeTwo {


    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    Document doc; // Insert file here

    public static final String SUBSUMPTION = "SubClassOf";
    public static final String DISJUNCTION = "DisjointClasses";
    public static final String EXISTENTIAL = "ObjectSomeValuesFrom";

    public XMLparserTakeTwo() {
    }

    public void parse(String PATH) throws ParserConfigurationException, IOException, SAXException {

        loadDocument(PATH);
        NodeList nList = doc.getElementsByTagName("Constraint");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            String test = "SubClassOf";

            if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if (eElement.getAttribute("xsi:type").equals(test)) {
                    System.out.println("HEEEY");
                    System.out.println("ElementType : " + eElement.getAttribute("xsi:type"));
                    System.out.println("Description : " + eElement.getElementsByTagName("Text").item(0).getTextContent());
                    System.out.println("Desc Index : " + eElement.getElementsByTagName("Object").item(0).getAttributes().getNamedItem("index").getNodeValue());
                    System.out.println("Value : " + eElement.getElementsByTagName("Text").item(1).getTextContent());
                    System.out.println("Value Index : " + eElement.getElementsByTagName("Object").item(1).getAttributes().getNamedItem("index").getNodeValue());
                }
            }
        }
    }

    public void loadDocument(String PATH) throws ParserConfigurationException, IOException, SAXException {
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(new File(PATH));

        doc.getDocumentElement().normalize();

        System.out.println("Document loaded... Root element: " + doc.getDocumentElement().getNodeName());

    }

    public Constraint getSchema(String type){
        NodeList nList = doc.getElementsByTagName("Constraint");
        Node node;
        Element element;
        String properties = "";
        Constraint con = null;

        for (int i = 0; i < nList.getLength(); i++) {
            node = nList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){
                element = (Element) node;

                con = new Constraint();

                if(element.getAttribute("xsi:type").equals(type)){
//                    System.out.println("ElementType : " + element.getAttribute("xsi:type"));
//                    System.out.println("Description : " + element.getElementsByTagName("Text").item(0).getTextContent());
//                    System.out.println("Desc Index : " + element.getElementsByTagName("Object").item(0).getAttributes().getNamedItem("index").getNodeValue());
//                    System.out.println("Value : " + element.getElementsByTagName("Text").item(1).getTextContent());
//                    System.out.println("Value Index : " + element.getElementsByTagName("Object").item(1).getAttributes().getNamedItem("index").getNodeValue());

                    con.setText1(element.getElementsByTagName("Text").item(0).getTextContent());
                    con.setPos1(Integer.parseInt(element.getElementsByTagName("Object").item(0).getAttributes().getNamedItem("index").getNodeValue()));
                    con.setText2(element.getElementsByTagName("Text").item(1).getTextContent());
                    con.setPos2(Integer.parseInt(element.getElementsByTagName("Object").item(1).getAttributes().getNamedItem("index").getNodeValue()));
                    return con;
                }
            }
        }
        return con;
    }
}
