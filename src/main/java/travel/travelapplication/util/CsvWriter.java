package travel.travelapplication.util;

import com.opencsv.CSVWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CsvWriter {

  private final Path filePath;

  public CsvWriter(
      @Value("${app.export.user-like-csv-path:like.csv}") String path
  ) {
    this.filePath = Paths.get(path);
  }

  private static String[] nullToEmpty(String[] row) {
    String[] r = new String[row.length];
    for (int i = 0; i < row.length; i++) {
      r[i] = row[i] == null ? "" : row[i];
    }
    return r;
  }

  public String writeWithHeader(String[] header, List<String[]> rows) {
    try {
      Path parent = filePath.toAbsolutePath().getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }

      try (Writer out = Files.newBufferedWriter(
          filePath,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE
      );
          CSVWriter writer = new CSVWriter(out,
              CSVWriter.DEFAULT_SEPARATOR,         // ','
              CSVWriter.DEFAULT_QUOTE_CHARACTER,   // '"'
              CSVWriter.DEFAULT_ESCAPE_CHARACTER,  // '\\'
              CSVWriter.DEFAULT_LINE_END)) {

        writer.writeNext(header, false);
        for (String[] row : rows) {
          writer.writeNext(nullToEmpty(row), false);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("like.csv 생성 실패: " + filePath.toAbsolutePath(), e);
    }
    return filePath.toAbsolutePath().toString();
  }

}
