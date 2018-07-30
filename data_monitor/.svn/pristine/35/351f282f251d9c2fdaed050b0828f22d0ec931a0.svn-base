package zibo.fileTrans;

import java.io.File;
import java.io.IOException;

public interface FtpFileInterface {
    String getFileSystemType();
    String getFileIdentification();
    String getFileAbsolutePath();
    File getFile();
    String getFileName();
    String getCodeByName();
    String getContentOfFile() throws IOException;
    Integer getEnableValueOfConfig() throws IOException;
    void writeConfigFile(String content) throws IOException;
    void operConfigWhenChange() throws IOException;
    String getSystemDirOfMine();
    Integer getEnableValue() throws IOException;
    void newEnableValueBeforeDelete() throws IOException;
    boolean completeUploadOfFile();
}
