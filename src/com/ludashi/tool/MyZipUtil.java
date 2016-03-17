package com.ludashi.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.ludashi.mains.LuDaShiActivity;



public class MyZipUtil {
	
	
	private static final int BUFF_SIZE = 1024 * 1024;
	/**
     * 解压文件名包含传入文字的文件
     * @param zipFile    压缩文件
     * @param folderPath    目标文件夹
     * @param nameContains    传入的文件匹配名
     * @return    解压后的文件列表
     * @throws ZipException    压缩格式异常
     * @throws IOException    IO错误异常
     */
    public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath) throws ZipException, IOException {
        ArrayList<File> fileList = new ArrayList<File>();

        File desDir = new File(folderPath);
        if (false == desDir.exists()) {
            desDir.mkdir();
        }
        
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
                InputStream in = zf.getInputStream(entry);
//              if(zipFile.l)
                String str = folderPath + File.separator + entry.getName();
                LuDaShiActivity.outLog("folderPath = " + str);
//              str = new String(str.getBytes("8859_1"), "GB2312");
                File desFile = new File(str);
                if (false == desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
                fileList.add(desFile);
        }
        return fileList;
    }

	
}