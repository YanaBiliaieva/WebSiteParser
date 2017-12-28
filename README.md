# Web Shop Parser
The parser executes with a keyword as an argument.
Extracts all offers for the given keyword.
Extracts properties for every offer.
The extracted offers are written into the xml file.

Using ~$ java -jar WebSiteParser.jar jeans
the expected result is the following.
An example of the console output:
Summary:
Amount of triggered HTTP request = 49
Amount of extracted products = 4766
Run-time (nanoTime): 11540443161
Memory Footprint:31545256

The XML file for this keyword if here: [output.xml](https://github.com/YanaBiliaieva/WebSiteParser/blob/master/src/main/resources/output.xml)
