import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine {
    private Map<String, List<PageEntry>> indexing = new HashMap<>();
    private List<String> stop = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] pdfsList = pdfsDir.listFiles();
        for (int pdf = 0; pdf < pdfsList.length; pdf++) {
            var doc = new PdfDocument(new PdfReader(pdfsList[pdf]));
            for (int page = 1; page <= doc.getNumberOfPages(); page++) {
                var pdfPage = doc.getPage(page);
                var text = PdfTextExtractor.getTextFromPage(pdfPage);
                var words = text.toLowerCase().split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    if (indexing.containsKey(entry.getKey())) {
                        indexing.get(entry.getKey()).add(new PageEntry(pdfsList[pdf].getName(), page, entry.getValue()));
                        Collections.sort(indexing.get(entry.getKey()));
                    } else {
                        indexing.put(entry.getKey(), new ArrayList<>());
                        indexing.get(entry.getKey()).add(new PageEntry(pdfsList[pdf].getName(), page, entry.getValue()));
                    }
                }
            }
        }
        File stopTxt = new File("stop-ru.txt");
        try (Scanner sc = new Scanner(stopTxt);) {
            while (sc.hasNextLine()) {
                stop.add(sc.nextLine());
            }
        }
    }

    public String search(String text) throws IOException {
        List<PageEntry> search = new ArrayList<>();
        List<String> wordsList = new ArrayList<>();
        var words = text.toLowerCase().split("\\P{IsAlphabetic}+");
        Collections.addAll(wordsList, words);
        wordsList.removeAll(stop);
        for (int i = 0; i < wordsList.size(); i++) {
            for (Map.Entry<String, List<PageEntry>> entry : indexing.entrySet()) {
                if (entry.getKey().equals(wordsList.get(i))) {
                    search.addAll(indexing.get(entry.getKey()));
                }
            }
        }
        for (int i = 0; i < search.size(); i++) {
            for (int i2 = 1; i2 < search.size(); i2++) {
                if (search.get(i).getPdfName().equals(search.get(i2).getPdfName()) &&
                        search.get(i).getPage() == search.get(i2).getPage() && i != i2) {
                    search.get(i).setCount(search.get(i).getCount() + search.get(i2).getCount());
                    search.get(i2).setCount(0);
                }
            }
        }
        search = search.stream()
                .filter(x -> x.getCount() > 0)
                .sorted()
                .collect(Collectors.toList());
        Collections.sort(search);
        Gson gson = new Gson();
        return gson.toJson(search, List.class);
    }
}
