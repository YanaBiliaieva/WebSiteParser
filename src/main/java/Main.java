import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private static String pagesClassName = "pageNumbers_ffrt32";
    public static Document docMan = null;
    public static String searchKeyword = null;
    private static String pagesSymbols = "?page=";
    public static String manClothesSearch;
    public static String ladyClothesSearch;
    public static String kinderClothesSearch;
    public static Document docLady = null;
    public static Document docKinder = null;
    public static Integer requestsAmount = 0;
    public static XmlCreator creator;

    public static void main(String[] args) {
        //start counting runtime
        final long startTime = System.nanoTime();
        //start counting memory
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // searchKeyword = args[0];
        searchKeyword = "jeans";
        manClothesSearch = mannerUrl + searchKeyword;
        ladyClothesSearch = frauenUrl + searchKeyword;
        kinderClothesSearch = kinderUrl + searchKeyword;
        try {
            parseWebSite();
        } catch (Exception e) {
            System.out.println("Cannot parse website");
        }
        System.out.println("");
        System.out.println("Summary:");
        System.out.println("Amount of triggered HTTP request = " + requestsAmount);
        System.out.println("Amount of extracted products = " + (creator.getXmlOfferCount()-1));
        final long duration = System.nanoTime() - startTime;
        //Date myTime = new Date(duration / 1000);//in seconds
        System.out.println("Run-time (nanoTime): " + duration);
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long actualMemUsed = afterUsedMem - beforeUsedMem;
        System.out.println("Memory Footprint:" + actualMemUsed);
    }

    /**
     * calls functions which get and parse Document, count number of search result pages in three categories: man, ladies, kinder;
     * calls functions which extract offers and its attributes in the categories;
     * calls XmlCreator to create XML file
     */
    public static void parseWebSite() {

        docMan = getAndParse(manClothesSearch);
        docLady = getAndParse(ladyClothesSearch);
        docKinder = getAndParse(kinderClothesSearch);

        Integer numberOfPagesMan = getPageNumbers(docMan);
        Integer numberOfPagesLady = getPageNumbers(docLady);
        Integer numberOfPagesKinder = getPageNumbers(docKinder);

        HashSet<Map<String, String>> manOffers = getAllOffersInCategory(manClothesSearch, numberOfPagesMan, docMan);
        HashSet<Map<String, String>> ladyOffers = getAllOffersInCategory(ladyClothesSearch, numberOfPagesLady, docLady);
        HashSet<Map<String, String>> kinderOffers = getAllOffersInCategory(kinderClothesSearch, numberOfPagesKinder, docKinder);

        HashSet<Map<String, String>> allWebsiteOffers = new HashSet<Map<String, String>>();
        allWebsiteOffers.addAll(manOffers);
        allWebsiteOffers.addAll(ladyOffers);
        allWebsiteOffers.addAll(kinderOffers);

        //save results to xml
        creator = new XmlCreator();
        creator.createXml(allWebsiteOffers);

    }

    /**
     * go through each page
     *
     * @param searchUrl
     * @param numberOfPages
     */
    public static HashSet<Map<String, String>> getAllOffersInCategory(String searchUrl, Integer numberOfPages, Document firstPage) {

        HashSet<Map<String, String>> offersPageOne = getProperties(firstPage);
        HashSet<Map<String, String>> offersPages = new HashSet<Map<String, String>>();
        if (numberOfPages != null) {
            for (int i = 2; i <= numberOfPages; i++) {
                String pageUrl = searchUrl + "?page=" + i;
                Document document = getAndParse(pageUrl);
                //get needed offer' attributes
                HashSet<Map<String, String>> offersPage = getProperties(document);
                offersPages.addAll(offersPage);
            }
        }
        offersPages.addAll(offersPageOne);
        return offersPages;
    }

    /**
     * gets Document from url using Jsoup connection
     * parses Document using Jsoup
     *
     * @param pageUrl
     * @return parsedDocFromUrl
     */
    public static Document getAndParse(String pageUrl) {
        Document parsedDocFromUrl = null;
        try {
            parsedDocFromUrl = Jsoup.connect(pageUrl).get();
            requestsAmount++;
        } catch (IOException e) {
            System.out.println("Error occurred whilst fetching the URL");
        }
        parsedDocFromUrl = Jsoup.parse(parsedDocFromUrl.html());
        return parsedDocFromUrl;

    }

    /**
     * finds search result pages quantity from first search page
     *
     * @param document
     * @return value
     */
    public static Integer getPageNumbers(Document document) {
        Elements pages = document.getElementsByClass(pagesClassName);
        List<Integer> integers = new ArrayList<Integer>();
        for (Element element : pages) {
            Integer value = Integer.valueOf(element.text());
            integers.add(value);
        }
        Integer value = null;
        for (int i = 0; i < integers.size() - 1; i++) {
            if (integers.get(i) < integers.get(i + 1)) {
                value = integers.get(i + 1);
            }
        }
        return value;
    }

    /**
     * using the name of the class that wraps the needed container of a single offer information
     * creates a HashSet of Maps with attributes and values.
     *
     * @param doc
     * @return strings
     */
    public static HashSet<Map<String, String>> getProperties(Document doc) {
        HashSet<Map<String, String>> strings = new HashSet<Map<String, String>>();
        Elements thisItemPrice = doc.getElementsByClass(wrapperClassName);

        for (Element el : thisItemPrice) {
            Map<String, String> offer = new HashMap<String, String>();

            Elements nameEl = el.getElementsByClass(itemNameClassName);
            if (!nameEl.toString().isEmpty()) {
                for (Element cont : nameEl) {
                    String text = cont.text();
                    offer.put("name", text);
                }
                Elements brandEl = el.getElementsByClass(brandClassName);
                for (Element cont : brandEl) {
                    String text = cont.text();
                    offer.put("brand", text);
                }
                Elements itemPriceEl = el.getElementsByClass(itemPriceClassName);
                for (Element cont : itemPriceEl) {
                    String text = cont.text();
                    offer.put("price", text);
                }
                Elements initialPriceEl = el.getElementsByClass(initialPriceClassName);

                for (int i = 0; i < 2; i++) {
                    String text = initialPriceEl.text();
                    offer.put("initialPrice", text);
                }
                String linkHref = el.attr("href");
                String itemId = linkHref.replaceAll("\\D+", "");//extract number
                offer.put("articleId", itemId);
                strings.add(offer);
            }
        }
        return strings;
    }

}
