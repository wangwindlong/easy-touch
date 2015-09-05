/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.template.material.menu.utils;

//import com.google.api.client.util.IOUtils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utilities and constants related to files
 */
public class FileUtils {
    public static String rootDirPath = getSDPath() + "/yzximdemo";


    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        }
        return null;
    }

    public static void writeFile(String data, File file) throws IOException {
        writeFile(data.getBytes(Charset.forName("UTF-8")), file);
    }

    public static void writeFile(byte[] data, File file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(data);
        bos.close();
    }

//    public static String readFileAsString(File file) throws IOException {
//        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        IOUtils.copy(bis, bos);
//        byte[] contents = bos.toByteArray();
//        bis.close();
//        bos.close();
//        return new String(contents, Charset.forName("UTF-8"));
//    }

    public static void saveBitmapToFile(Bitmap bitmap, String name) {
        FileOutputStream fstream = null;
        BufferedOutputStream bStream = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();

            File file = new File(rootDirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File imageFile = new File(rootDirPath, name);

            fstream = new FileOutputStream(imageFile);
            bStream = new BufferedOutputStream(fstream);
            bStream.write(byteArray);
            if (bStream != null) {
                bStream.close();
                bStream = null;
            }
        } catch (Exception e) {

        } finally {

        }
    }

    public static void saveBitmapToPathFile(Bitmap bitmap, String pathname) {
        FileOutputStream fstream = null;
        BufferedOutputStream bStream = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();

            File file = new File(pathname);
            if (!file.exists()) {
                file.mkdirs();
            }
            File imageFile = new File(pathname);

            fstream = new FileOutputStream(imageFile);
            bStream = new BufferedOutputStream(fstream);
            bStream.write(byteArray);
            if (bStream != null) {
                bStream.close();
                bStream = null;
            }
        } catch (Exception e) {

        } finally {

        }
    }
}
