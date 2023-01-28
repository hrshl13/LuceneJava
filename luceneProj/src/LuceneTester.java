import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
    // parentDir is your project's parent
    String parentDir = "C:\\Users\\Admin\\Desktop\\Codes\\Sem6MiniProjs\\IR";
    String indexDir = parentDir + "\\Index";
    String dataDir = parentDir + "\\Data";
    Indexer indexer;
    Searcher searcher;

    public LuceneTester() {
        try {
            this.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndex() throws IOException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        System.out.println("Creating Index...");
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File indexed, time taken: "
                + (endTime - startTime) + " ms");
    }

    public TopDocs search(String searchQuery) throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();
        System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
        return hits;

    }
}