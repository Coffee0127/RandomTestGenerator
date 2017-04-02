/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Bo-Xuan Fan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bxf.hradmin.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bxf.hradmin.common.exception.HRAdminException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipUtil;
import net.lingala.zip4j.util.CRCUtil;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * ZipUtils
 *
 * @since 2017-03-26
 * @author Bo-Xuan Fan
 */
public final class ZipUtils {

    private static final int BUFFER_SIZE = 8192;

    private ZipUtils() {
    }

    /**
     * 壓縮檔案
     *
     * @param destination 要產出之壓縮檔
     * @param overwrite 是否覆蓋原本檔案
     * @param password 壓縮檔密碼 (optional)
     * @param unzipFiles 待壓縮檔案
     * @throws IOException
     */
    public static void zip(File destination, boolean overwrite, String password, File... unzipFiles) throws IOException {
        if (destination.isDirectory()) {
            throw new IOException(destination + " is a directory");
        }

        if (destination.exists()) {
            if (overwrite) {
                // 覆寫檔案
                Files.delete(destination.toPath());
            } else {
                // 備份原本檔案
                StringBuilder bckFileName = new StringBuilder();
                String fileName = destination.getName();
                bckFileName.append(fileName.substring(0, fileName.lastIndexOf('.')));
                bckFileName.append('_');
                bckFileName.append(System.currentTimeMillis());
                bckFileName.append(fileName.substring(fileName.lastIndexOf('.')));
                Files.move(destination.toPath(), new File(destination.getParentFile(), bckFileName.toString()).toPath());
            }
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destination)))) {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            if (StringUtils.isNotEmpty(password)) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                parameters.setPassword(password);
            }

            for (File unzipFile : unzipFiles) {
                doZip(buffer, out, parameters, unzipFile);
            }

            // output the zip file
            out.finish();
        } catch (ZipException e) {
            throw new HRAdminException(e);
        }
    }

    private static void doZip(byte[] buffer, ZipOutputStream out, ZipParameters parameters, File unzipFile)
            throws ZipException, IOException {
        int length;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(unzipFile));) {
            parameters.setSourceFileCRC((int) CRCUtil.computeFileCRC(unzipFile.getAbsolutePath()));
            out.putNextEntry(unzipFile, parameters);
            while ((length = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.closeEntry();
        }
    }

    /**
     * 解壓縮檔案
     *
     * @param zipFile 壓縮檔
     * @param password 壓縮檔密碼 (optional)
     * @throws IOException
     */
    public static void unzip(File unzipFile, String password) throws IOException {
        // 未指定 destination，則解壓縮至當前路徑
        File destination = new File(unzipFile.getParentFile(), unzipFile.getName().substring(0, unzipFile.getName().lastIndexOf('.')));
        unzip(unzipFile, password, destination);
    }

    /**
     * 解壓縮檔案
     *
     * @param zipFile 壓縮檔
     * @param password 壓縮檔密碼 (optional)
     * @param destination 目的資料夾
     * @throws IOException
     */
    public static void unzip(File unzipFile, String password, File destination) throws IOException {
        if (!unzipFile.exists()) {
            throw new IOException(unzipFile + " is not exist.");
        }

        if (!destination.exists()) {
            destination.mkdirs();
        } else {
            if (!destination.isDirectory()) {
                throw new IOException(destination + " is not a directory.");
            } else if (!(destination.canRead() && destination.canWrite())) {
                throw new IOException(destination + " is read only.");
            }
        }

        try {
            ZipFile zipFile = new ZipFile(unzipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }

            // get the header information for all the files in the ZipFile
            @SuppressWarnings("unchecked")
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

            for (FileHeader fileHeader : fileHeaderList) {
                if (fileHeader == null) {
                    continue;
                }

                doUnZip(destination, zipFile, fileHeader);
            }
        } catch (ZipException e) {
            throw new HRAdminException(e);
        }
    }

    private static void doUnZip(File destination, ZipFile zipFile, FileHeader fileHeader)
            throws IOException, ZipException {
        File outputFile = new File(destination, fileHeader.getFileName());
        // Get the InputStream from the ZipFile
        try (ZipInputStream inputStream = zipFile.getInputStream(fileHeader);
                // Initialize the output stream
                OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER_SIZE);) {

            int length = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((length = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        }

        // restore file attributes
        UnzipUtil.applyFileAttributes(fileHeader, outputFile);
    }
}
