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

    public XMLparserTakeTwo() {
    }

    public void parse(String PATH) throws ParserConfigurationException, IOException, SAXException {
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(new File(PATH));

        doc.getDocumentElement().normalize();

        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("Constraint");

        System.out.println("-------------------------");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            System.out.println("\n Current Element: " + nNode.getNodeName());

            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;

                System.out.println("ElementType : " + eElement.getAttribute("xsi:type"));
                System.out.println("Description : " + eElement.getElementsByTagName("Text").item(0).getTextContent());
                System.out.println("Desc Index : " + eElement.getElementsByTagName("Object").item(0).getAttributes().getNamedItem("index").getNodeValue());
                System.out.println("Value : " + eElement.getElementsByTagName("Text").item(1).getTextContent());
                System.out.println("Value Index : " + eElement.getElementsByTagName("Object").item(1).getAttributes().getNamedItem("index").getNodeValue());
            }
        }
    }
}
