package com.bxf.hradmin.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@FixMethodOrder
public class ZipUtilsTest {

    private static File rootFolder = new File(System.getProperty("java.io.tmpdir"), "HRAdmin/UnitTest");

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder(rootFolder);

    static {
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        for (int i = 1; i <= 2; i++) {
            FileOutputStream output = new FileOutputStream(folder.newFile("file" + i + ".txt"));
            IOUtils.write("This is file" + i + "", output);
            IOUtils.closeQuietly(output);
        }
    }

    @Test
    public final void testZip() throws Exception {
        File destination = new File(folder.getRoot(), "zipFile.zip");
        File file1 = new File(folder.getRoot(), "file1.txt");
        File file2 = new File(folder.getRoot(), "file2.txt");
        ZipUtils.zip(destination, true, null, file1, file2);
        assertTrue(destination.exists());
    }

    @Test
    public final void testUnzip() throws Exception {
        File unzipFile = new File(folder.getRoot(), "zipFile.zip");
        File destination = new File(folder.getRoot(), "extract");
        ZipUtils.unzip(unzipFile, null, destination);
        assertEquals(2, destination.listFiles().length);
    }

}
