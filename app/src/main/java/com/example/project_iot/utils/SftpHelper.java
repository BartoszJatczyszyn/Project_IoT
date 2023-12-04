package com.example.project_iot.utils;

import android.util.Log;

import java.util.Vector;

import android.content.Context;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpHelper {
    public SftpHelper(Context context) throws JSchException {
        this.context = context;
        initializeSessionAndSftpChannel();
    }

    public String getWorkingDir() throws JSchException, SftpException {
        if (!channelSftp.isConnected()){
            channelSftp.connect();
        }
        return channelSftp.pwd();
    }

    public void getFile(String remoteSource, String destination) throws JSchException, SftpException {
        if (!channelSftp.isConnected()){
            channelSftp.connect();
        }

        channelSftp.get(remoteSource, destination);
    }
    private void initializeSessionAndSftpChannel() throws JSchException {
        Log.d("CINUS", "Debug print!");
        Log.d("CINUS", "app dir: " + context.getFilesDir());

        JSch jSch = new JSch();
        jSch.addIdentity(context.getFilesDir() + "/ftp-key.pem");
        Session jschSession = jSch.getSession(username, remoteHost, 22);
        jschSession.setConfig("StrictHostKeyChecking", "no");
        jschSession.connect();
        channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
    }

    private static final String remoteHost = "20.107.176.118";
    private static final String username = "ftp";
    private final Context context;
    private ChannelSftp channelSftp;

}
