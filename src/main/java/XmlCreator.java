import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XmlCreator {
    public void createXml(HashSet<Map<String, String>> elements) {
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            }
            Document doc = dBuilder.newDocument();
            // root element
            Element rootElement = doc.createElement("offers");
            doc.appendChild(rootElement);
            Iterator iterator = elements.iterator();
            String name = null;
            String number = null;
            String brand = null;
            String price = null;
            String initialPrice = null;
            String articleId = null;
            int i = 0;
            while (iterator.hasNext()) {
                Map<String, String> offer = (Map<String, String>) iterator.next();
                number = String.valueOf(i++);
                System.out.println(number);
                // offers element
                Element offerEl = doc.createElement("offer");
                rootElement.appendChild(offerEl);
                // setting attribute to element
                Attr attr = doc.createAttribute("number");
                attr.setValue(number);//iterator
                offerEl.setAttributeNode(attr);
                for (Map.Entry<String, String> entry : offer.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    if (entry.getKey().equals("name")) {

                        if ((entry.getValue()) != null) {
                            name = entry.getValue();
                            // offer element name
                            Element nameEl = doc.createElement("name");
                            nameEl.appendChild(doc.createTextNode(name));
                            offerEl.appendChild(nameEl);
                        }

                    }
                    if (entry.getKey().equals("brand")) {
                        if ((entry.getValue()) != null) {
                            brand = entry.getValue();
                            // offer element brand
                            Element brandEl = doc.createElement("brand");
                            brandEl.appendChild(doc.createTextNode(brand));
                            offerEl.appendChild(brandEl);
                        }

                    }
                    if (entry.getKey().equals("price")) {
                        if ((entry.getValue()) != null) {
                            price = entry.getValue();
                            // offer element price
                            Element priceEl = doc.createElement("price");
                            priceEl.appendChild(doc.createTextNode(price));
                            offerEl.appendChild(priceEl);
                        }

                    }
                    if (entry.getKey().equals("initialPrice")) {
                        if ((entry.getValue()) != null) {
                            initialPrice = entry.getValue();
                            // offer element initialPrice
                            Element initialPriceEl = doc.createElement("initialPrice");
                            initialPriceEl.appendChild(doc.createTextNode(initialPrice));
                            offerEl.appendChild(initialPriceEl);
                        }

                    }
                    if (entry.getKey().equals("articleId")) {
                        if ((entry.getValue()) != null) {
                            articleId = entry.getValue();
                            // offer element articleId
                            Element articleIdEl = doc.createElement("articleId");
                            articleIdEl.appendChild(doc.createTextNode(articleId));
                            offerEl.appendChild(articleIdEl);
                        }

                    }
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e1) {
                e1.printStackTrace();
            }
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("src\\main\\resources\\output.xml"));
            try {
                transformer.transform(source, result);
            } catch (TransformerException e1) {
                e1.printStackTrace();
            }

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            try {
                transformer.transform(source, consoleResult);
            } catch (TransformerException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

