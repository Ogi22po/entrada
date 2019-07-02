package nl.sidnlabs.entrada.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component("local")
public class LocalFileManagerImpl implements FileManager {

  private static final String LOCAL_SCHEME = "file://";


  @Override
  public String schema() {
    return LOCAL_SCHEME;
  }

  @Override
  public boolean exists(String file) {
    File f = new File(file);
    return f.exists();
  }

  @Override
  public List<String> files(String dir, String... filter) {

    return Arrays
        .stream(new File(dir).listFiles())
        .filter(File::isFile)
        .map(File::getAbsolutePath)
        .filter(f -> checkFilter(f, Arrays.asList(filter)))
        .collect(Collectors.toList());
  }

  private boolean checkFilter(String file, List<String> filters) {
    if (filters.isEmpty()) {
      return true;
    }
    return filters.stream().anyMatch(f -> StringUtils.endsWith(file, f));
  }

  @Override
  public Optional<InputStream> open(String location) {
    log.info("Open local file: " + location);

    File f = FileUtils.getFile(location);
    try {
      return Optional.of(FileUtils.openInputStream(f));
    } catch (IOException e) {
      log.error("Cannot open file: {}", f, e);
    }

    return Optional.empty();
  }

  @Override
  public boolean upload(String src, String dst, boolean archive) {
    log.info("Upload work location: {} to target location: {}", src, dst);

    try {
      FileUtils.copyDirectory(new File(src), new File(dst), true);
      return true;
    } catch (Exception ex) {
      log.error("Cannot upload directory: {} to {}", src, dst, ex);
    }

    return false;
  }

  @Override
  public boolean rmdir(String location) {
    log.info("Delete local directory: " + location);

    File f = new File(location);
    if (f.exists() && f.isDirectory()) {
      return FileSystemUtils.deleteRecursively(f);
    }

    return false;
  }

  @Override
  public boolean delete(String location) {
    log.info("Delete local file: " + location);

    File f = new File(location);
    if (f.exists() && f.isFile()) {
      try {
        Files.delete(Paths.get(location));
        return true;
      } catch (IOException e) {
        log.error("Cannot delete file: {}", location, e);
      }
    }

    return false;
  }


  @Override
  public boolean move(String src, String dst, boolean archive) {
    log.info("Move local file: {} to: {} ", src, dst);
    Path dstPath = Paths.get(dst);
    // make sure the directory exists
    mkdir(dstPath.getParent().toString());

    try {
      Files.move(Paths.get(src), dstPath, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (IOException e) {
      log.error("Error while moving file {} to {}", src, dst, e);
    }
    return false;
  }

  @Override
  public boolean supported(String location) {
    return StringUtils.startsWith(location, "/");
  }

  @Override
  public boolean isLocal() {
    return true;
  }

  @Override
  public boolean mkdir(String path) {
    log.info("Create directory: {} ", path);
    File f = new File(path);

    if (!f.exists()) {
      return f.mkdirs();
    }

    // dir already exists
    return true;
  }

  @Override
  public boolean chown(String path, String owner, String group) {
    throw new UnsupportedOperationException();
  }

}