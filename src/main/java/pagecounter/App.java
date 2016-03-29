package pagecounter;

import pagecounter.counter.PdfCounter;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("[Usage] java -jar {pdf directory}");
        }
        String dir = args[0];
        PdfCounter counter = new PdfCounter();
        counter.count(dir);
    }

}
