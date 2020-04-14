package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;

/**
 * @author zjxjwxk
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private String uploadPath;

    private FTPClient ftpClient = null;

    private void initFTPClient() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName, port);
            ftpClient.login(userName, password);
        } catch (Exception e) {
            log.error("初始化FTP失败", e);
        }
    }

    /**
     * 输入一个路径，将路径文件里的字符串返回
     * @param fileAddress 文件路径
     * @return 文件里的字符串
     */
    public String getFileStrByAddress(String fileAddress) {
        BufferedReader bufferedReader = null;
        try {
            initFTPClient();
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            ftpClient.retrieveFileStream(fileAddress)
                    )
            );
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String lineStr = bufferedReader.readLine();
                if (lineStr == null) {
                    break;
                }
                stringBuilder.append(lineStr);
            }
            ftpClient.logout();
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 文件上传
     * @param fileName 文件名
     * @param file 文件File
     * @return 是否上传成功
     */
    public boolean uploadFile(String fileName, File file) {
        FileInputStream fileInputStream = null;
        try {
            initFTPClient();
            fileInputStream = new FileInputStream(file);

            // FTP相关
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            // 修改ftpClient的工作目录
            ftpClient.changeWorkingDirectory(this.getUploadPath());
            // 上传文件
            ftpClient.storeFile(fileName, fileInputStream);
            return true;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return false;
        } finally {
            try {
                fileInputStream.close();
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
