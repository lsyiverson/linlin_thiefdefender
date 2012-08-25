/**
 * 
 */

package com.linlin.thiefdefender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

/**
 * @author Tim.Lian
 */
public class ThiefDefenderUtils {

    /**
     * Dump the database file to external storage
     */
    static void dumpDatabase(String packageName, String fileName) throws IOException {
        File dbFile = new File("/data/data/" + packageName + "/databases/" + fileName);
        if (dbFile.exists()) {
            FileInputStream fis = new FileInputStream(dbFile);
            String outFileName = Environment.getExternalStorageDirectory() + "/" + fileName;
            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
        }
    }

    private ThiefDefenderUtils() {
    }
}
