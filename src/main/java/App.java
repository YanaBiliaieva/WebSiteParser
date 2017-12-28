
public class App {
    private static String frauenUrl = "https://www.aboutyou.de/frauen/bekleidung/";
    private static String mannerUrl = "https://www.aboutyou.de/maenner/bekleidung/";
    private static String kinderUrl = "https://www.aboutyou.de/kinder/maedchen/kids-gr-92-140/";
    public static long startTime;
    public static long beforeUsedMem;

    public static void main(String[] args) {
        //start counting runtime
        startTime = System.nanoTime();
        //start counting memory
        beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // searchKeyword = args[0];
        String searchKeyword = "jeans";
        String manClothesSearch = mannerUrl + searchKeyword;
        String ladyClothesSearch = frauenUrl + searchKeyword;
        String kinderClothesSearch = kinderUrl + searchKeyword;
        try {
            Parser parser = new Parser();
            parser.parseWebSite(manClothesSearch, ladyClothesSearch, kinderClothesSearch);
        } catch (Exception e) {
            System.out.println("Cannot parse website");
        }

    }
}
