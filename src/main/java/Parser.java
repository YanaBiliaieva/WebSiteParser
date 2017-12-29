import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Parser {
    private volatile Integer requestsAmount = 0;
    private volatile HashSet<Map<String, String>> offersPages;

    /**
     * calls functions which get and parse Document, count number of search result pages in three categories: man, ladies, kinder;
     * calls functions which extract offers and its attributes in the categories;
     * calls XmlCreator to create XML file
     */
    public void parseWebSite(String manClothesSearch, String ladyClothesSearch, String kinderClothesSearch) {

        Document docMan = getAndParse(manClothesSearch);
        Document docLady = getAndParse(ladyClothesSearch);
        Document docKinder = getAndParse(kinderClothesSearch);

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
        XmlCreator creator = new XmlCreator();
        creator.createXml(allWebsiteOffers);

        System.out.println("Summary:");
        System.out.println("Amount of triggered HTTP request = " + requestsAmount);
        System.out.println("Amount of extracted products = " + creator.getXmlOfferCount());
        final long duration = System.nanoTime() - App.startTime;
        System.out.println("Run-time (nanoTime): " + duration);
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long actualMemUsed = afterUsedMem - App.beforeUsedMem;
        System.out.println("Memory Footprint:" + actualMemUsed);
    }

    class MyThread extends Thread {
        volatile String pageUrl;

        public MyThread(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        @Override
        public void run() {
            Document document = getAndParse(pageUrl);
            //get needed offer' attributes
            HashSet<Map<String, String>> offersPage = getProperties(document);
            offersPages.addAll(offersPage);
        }
    }

    /**
     * go through each page
     *
     * @param searchUrl
     * @param numberOfPages
     */
    public HashSet<Map<String, String>> getAllOffersInCategory(String searchUrl, Integer numberOfPages, Document firstPage) {
        HashSet<Map<String, String>> offersPageOne = getProperties(firstPage);
        offersPages = new HashSet<Map<String, String>>();
        if (numberOfPages != null) {
            for (int i = 2; i <= numberOfPages; i++) {
                String pageUrl = searchUrl + "?page=" + i;
                MyThread temp = new MyThread(pageUrl);
                temp.start();
                try {
                    temp.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
    public synchronized Document getAndParse(String pageUrl) {
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
    public Integer getPageNumbers(Document document) {
        String pagesClassName = "pageNumbers_ffrt32";
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
    public synchronized HashSet<Map<String, String>> getProperties(Document doc) {
        HashSet<Map<String, String>> strings = new HashSet<Map<String, String>>();
        String wrapperClassName = "anchor_wgmchy";
        Elements thisItemPrice = doc.getElementsByClass(wrapperClassName);

        for (Element el : thisItemPrice) {
            Map<String, String> offer = new HashMap<String, String>();

            String itemNameClassName = "name_1iurgbx";
            Elements nameEl = el.getElementsByClass(itemNameClassName);
            if (!nameEl.toString().isEmpty()) {
                for (Element cont : nameEl) {
                    String text = cont.text();
                    offer.put("name", text);
                }
                String brandClassName = "brand_ke66rm";
                Elements brandEl = el.getElementsByClass(brandClassName);
                for (Element cont : brandEl) {
                    String text = cont.text();
                    offer.put("brand", text);
                }
                String itemPriceClassName = "price_1543wg1-o_O-highlight_1t1mqn4";
                Elements itemPriceEl = el.getElementsByClass(itemPriceClassName);
                for (Element cont : itemPriceEl) {
                    String text = cont.text();
                    offer.put("price", text);
                }
                String initialPriceClassName = "price_codx7m-o_O-strikeOut_32pxry";
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
