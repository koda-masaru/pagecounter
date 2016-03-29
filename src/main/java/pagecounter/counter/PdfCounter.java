package pagecounter.counter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.support.project.common.util.StringUtils;

import com.sun.pdfview.PDFFile;

public class PdfCounter implements FileVisitor<Path> {
    protected int indentSize;
    
    public void count(String dir) throws IOException {
        Path start = Paths.get(dir);
        FileVisitor<Path> visitor = this;
        Files.walkFileTree(start, visitor);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        print("preVisitDirectory : " + dir.getFileName());
        this.indentSize++;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        print("visitFile : " + file.getFileName());
        
        if (StringUtils.getExtension(file.getFileName().toString()).equals(".pdf")) {
            print("PDFを見つけた！");
            
            countPdt(file);
            
        }
        
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        this.indentSize--;
        print("postVisitDirectory : " + dir.getFileName());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        String error = String.format(" [exception=%s, message=%s]", exc.getClass(), exc.getMessage());

        print("visitFileFailed : " + file.getFileName() + error);

        return FileVisitResult.CONTINUE;
    }

    protected void print(String message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indentSize; i++) {
            builder.append(" ");
        }
        builder.append(message);
        //System.out.println(builder.toString());
    }

    
    private void countPdt(Path file) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file.toFile(), "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                    channel.size());
            PDFFile pdfFile = new PDFFile(buf);
            int pages = pdfFile.getNumPages();
            
            StringBuilder builder = new StringBuilder();
            builder.append(file.getFileName());
            builder.append("\t");
            builder.append(pages);
            System.out.println(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }
    
}
