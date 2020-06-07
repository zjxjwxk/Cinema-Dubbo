package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    private FTPClient ftpClient = null;

    private void initFTPClient() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName, port);
            ftpClient.login(userName, password);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.error("FTP连接失败！");
            } else {
                log.info("FTP连接成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            if (ftpClient == null) {
                return null;
            }
            ftpClient.enterLocalPassiveMode();
            InputStream inputStream = ftpClient.retrieveFileStream(fileAddress);
            if (inputStream == null) {
                throw new IOException(ftpClient.getReplyString());
            }
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            inputStream
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
            e.printStackTrace();
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
}
