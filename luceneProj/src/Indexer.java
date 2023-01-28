import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        /*
         * This piece of code takes the directory path which contains all the Documents
         * (Java Class) with Fields.
         * It then creates a Directory object which simply stores a list of files. An
         * analyzer is used and is
         */
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(indexDirectory, iwc);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        /*
         * A Document is a Java Class which is a representation of the document which is
         * stored in your file structure. A document contains one or more fields to
         * uniquely define a document object (or a document in your file structure).
         */
        Document document = new Document();

        // index file contents
        TextField contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
        // index file name
        TextField fileNameField = new TextField(LuceneConstants.FILE_NAME, file.getName(), TextField.Store.YES);
        // index file path
        TextField filePathField = new TextField(LuceneConstants.FILE_PATH, file.getCanonicalPath(),
                TextField.Store.YES);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        // Converts the input file into documents and indexes it
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter)
            throws IOException {
        // Get all the files in the data directory into an array
        File[] files = new File(dataDirPath).listFiles();
        // Iterate over the array and index each and every file and returns the nimber of documents
        for (File file : files) {
            if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
                indexFile(file);
            }
        }
        return writer.getDocStats().numDocs;
    }
}