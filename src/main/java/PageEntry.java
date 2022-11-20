public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        return o.getCount() - count;
    }

    @Override
    public String toString() {
        return "PageEntry{pdf=" + pdfName + ", " + "page=" + page + ", " + "count=" + count + "}";
    }
}
