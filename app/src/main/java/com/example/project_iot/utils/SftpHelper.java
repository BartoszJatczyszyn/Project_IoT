package com.example.project_iot.utils;

import android.content.Context;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Vector;

public class SftpHelper {
    public SftpHelper(Context context) throws JSchException, IOException {
        this.context = context;
        initAuthCredentials();
        initSessionAndSftpChannel();
        eraseAuthCredentials();
    }

    public String getWorkingDir() throws JSchException, SftpException {
        if (!channelSftp.isConnected()){
            channelSftp.connect();
        }
        return channelSftp.pwd();
    }

    public ArrayList<String> getFiles(int deviceId) throws JSchException, SftpException {

        ArrayList<String> fileNames = new ArrayList<String>();

        Vector<ChannelSftp.LsEntry> vector = channelSftp.ls(this.getWorkingDir()+"/files");

        // Sortuj pliki według daty utworzenia
        Collections.sort(vector, new Comparator<ChannelSftp.LsEntry>() {
            @Override
            public int compare(ChannelSftp.LsEntry entry1, ChannelSftp.LsEntry entry2) {
                long time1 = entry1.getAttrs().getMTime();
                long time2 = entry2.getAttrs().getMTime();
                return -Long.compare(time1, time2);
            }
        });


        for (ChannelSftp.LsEntry lse : vector) {

            if (!lse.getFilename().split("_")[0].equals(deviceId+"")) {
                continue;
            }

            if (lse.getFilename().contains(".jpg") || lse.getFilename().contains(".png"))  {
                fileNames.add(lse.getFilename());
            }
        }

        return fileNames;
    }

    public void getFile(String remoteSource, String destination) throws JSchException, SftpException {
        if (!channelSftp.isConnected()){
            channelSftp.connect();
        }

        channelSftp.get(remoteSource, destination);
    }
    private void initSessionAndSftpChannel() throws JSchException {
        JSch jSch = new JSch();
        jSch.addIdentity("sftp_key", this.authKey.getBytes(), null, null);
        Session jschSession = jSch.getSession(username, remoteHost, 22);
        jschSession.setConfig("StrictHostKeyChecking", "no");
        jschSession.connect();
        channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
    }

    private void initAuthCredentials() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = context.getAssets().open(propertiesFilename);
        properties.load(inputStream);
        this.authKey = properties.getProperty("sftp_key");
    }

    private void eraseAuthCredentials() {
        authKey = "";
    }

    private static final String remoteHost = "20.107.176.118";
    private static final String username = "ftp";
    private static final String propertiesFilename = "keys.properties";
    private final Context context;
    private String authKey;
    private ChannelSftp channelSftp;

}
