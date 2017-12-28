import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Main {
    private static String startUrl = "https://www.aboutyou.de/Search/Default/find?term=";
    private static String frauenUrl = "https://www.aboutyou.de/frauen/bekleidung/";
    private static String mannerUrl = "https://www.aboutyou.de/maenner/bekleidung/";
    private static String kinderUrl = "https://www.aboutyou.de/kinder/maedchen/kids-gr-92-140/";
    private static String wrapperClassName = "anchor_wgmchy";
    private static String brandClassName = "brand_ke66rm";
    private static String itemNameClassName = "name_1iurgbx";
    private static String itemPriceClassName = "price_1543wg1-o_O-highlight_1t1mqn4";
    private static String initialPriceClassName = "price_codx7m-o_O-strikeOut_32pxry";
    private static String linkClassName = "imageContainer_11g402i-o_O-imageContainerWithPadding_dxtwdx";
    public static Document doc = null;
    private static String pagesSymbols = "?page=";


    public static void main(String[] args) {

        //String input = "Esprit";
        // String searchUrl = startUrl + input;
        String testUrl = "https://www.aboutyou.de/maenner/bekleidung/jeans?pl=1";
        Document doc1 = null;
        try {
            doc1 = Jsoup.connect(testUrl).get();
        } catch (IOException e) {
            System.out.println("Error occurred whilst fetching the URL");
        }
        String title = doc1.title();
        System.out.println(doc1.toString());

        File output = new File("src\\main\\resources\\webPage.txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
            writer.write(doc1.toString());

        } catch (IOException e) {
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
        }


        File file = new File("src\\main\\resources\\webPage.txt");//testHtmlPage
        try {
            doc = Jsoup.parse(file, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        getProperties(doc);

    }

    public void getPageNumbers() {
        //<li class="pageNumbers_ffrt32"><a href="https://www.aboutyou.de/maenner/bekleidung/jeans?page=12" tabindex="0" aria-label="Page 12">12</a></li>

    }

    public static void getProperties(Document doc) {
        HashSet<Map<String, String>> offers = getClassElement(wrapperClassName);
        XmlCreator creator = new XmlCreator();
        creator.createXml(offers);
    }

    public static HashSet<Map<String, String>> getClassElement(String wrapperClassName) {
        HashSet<Map<String, String>> strings = new HashSet<Map<String, String>>();
        Elements thisItemPrice = doc.getElementsByClass(wrapperClassName);

        for (Element el : thisItemPrice) {
            Map<String, String> offer = new HashMap<String, String>();

            Elements nameEl = el.getElementsByClass(itemNameClassName);
            System.out.println("nameEl.toString()" + nameEl.toString());
            if (!nameEl.toString().isEmpty()) {
                for (Element cont : nameEl) {
                    String text = cont.text();
                    System.out.println("name=" + text);
                    offer.put("name", text);
                }
                Elements brandEl = el.getElementsByClass(brandClassName);
                for (Element cont : brandEl) {
                    String text = cont.text();
                    String clearString = text.replace("&amp;", "");
                    System.out.println("brand=" + clearString);
                    offer.put("brand", clearString);
                }
                Elements itemPriceEl = el.getElementsByClass(itemPriceClassName);
                for (Element cont : itemPriceEl) {
                    String text = cont.text();
                    System.out.println("itemPrice=" + text);
                    offer.put("price", text);
                }
                Elements initialPriceEl = el.getElementsByClass(initialPriceClassName);

                for (int i = 0; i < 2; i++) {
                    String text = initialPriceEl.text();
                    System.out.println("initialPrice=" + text);
                    offer.put("initialPrice", text);
                }
                String linkHref = el.attr("href");
                String itemId = getIdFromLink(linkHref);
                offer.put("articleId", itemId);
                System.out.println("itemId=" + itemId);
                strings.add(offer);
            }

        }
        return strings;
    }


    public static String getIdFromLink(String link) {
        String itemId = link.replaceAll("\\D+", "");
        return itemId;
    }

}

