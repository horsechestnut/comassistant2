package com.example.administrator.comassistant2.simulation.tool;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
/**
 * 文件工具类
 *
 * @author fangshu
 * @see [相关类/方法]
 * @since JDK6.0
 */
public class FileUtil {
    public static final String tag = "FileUtil";
    public static String SDCardRoot = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator;

    public static String readFile(String path) {
        File file = new File(path);
        byte[] databytes = readFileBytes(file);
        if (databytes != null) {
            return new String(databytes);
        }
        return null;
    }


    public static void writeFile(byte[] bytes,String fileName) {
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = new File(fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            createFile(fileName);
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static byte[] readFileBytes(File file)  {
        byte[] bytes = null;
        try {
            if (file != null) {
                InputStream is = new FileInputStream(file);
                int length = (int) file.length();
                if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
                {
                    System.out.println("this file is max ");
                    return null;
                }
                bytes = new byte[length];
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
                //如果得到的字节长度和file实际的长度不一致就可能出错了
                if (offset < bytes.length) {
                    System.out.println("file length is error");
                    return null;
                }
                is.close();
            }
        }catch (Exception e)
        {
            LogUtil.ee(e);
        }
        return bytes;
    }



    public static InputStream getFileInputStream(String filename,
                                                 Context context) {
        InputStream inputstream = null;
        if (null == filename || filename.length() == 0) {
            return null;
        }

        try {
            File pfile = new File(filename);
            if (pfile.exists() && (!pfile.isDirectory())) {
                inputstream = new FileInputStream(pfile);
            }
        } catch (IOException e) {
            return null;
        }

        return inputstream;
    }

    public static String getPathFromRri(Uri in_uri, Context in_context) {
        String picturePath = "";
        try {
            Uri selectedImage = in_uri; //获取系统返回的照片的Uri
            Cursor cursor = in_context.getContentResolver().query(selectedImage, null, null, null, null);

            if (cursor == null) {
                picturePath = selectedImage.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                picturePath = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            LogUtil.ee(e);
        }

        return picturePath;
    }

    /**
     * copy file form src to dest, the two files can be mounted on different
     * point
     *
     * @param srcFile  : src file
     * @param destFile : dest file
     * @return: true if everything is OK otherwise false
     */
    public static boolean copyFile(File srcFile, File destFile) {
        if ((null == srcFile) || (null == destFile) || !srcFile.exists()
                || srcFile.isDirectory()) {
            return false;
        }
        if (destFile.exists()) {
            destFile.delete();
        }

        destFile = createFile(destFile.getAbsolutePath());

        boolean isOK = true;
        FileChannel out = null;
        FileChannel in = null;
        try {
            out = new FileOutputStream(destFile).getChannel();
            in = new FileInputStream(srcFile).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(102400);
            int position = 0;
            int length = 0;
            while (true) {
                length = in.read(buffer, position);
                if (length <= 0) {
                    break;
                }
                buffer.flip();
                out.write(buffer, position);
                position += length;
                buffer.clear();
            }
        } catch (Exception e) {
            isOK = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return isOK;
    }

    public static void copyAllDirectory(String oldpath, String newpath,
                                        Context context) {
        if (null == oldpath || null == newpath) {
            return;
        }

        File oldFile = new File(oldpath);
        String[] appfiles = null;
        try {
            appfiles = oldFile.list();
        } catch (Exception e) {
            appfiles = null;
        }

        // 此时已经不是目录需要直接把文件移过去
        if (appfiles == null || appfiles.length == 0) {
            try {
                File outFile = new File(newpath);
                if (!outFile.exists()) {
                    createFile(newpath);
                }

                InputStream in = new FileInputStream(oldFile);
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                return;
            }
            return;

        } else {// 需要递归的把所有文件都创建了
            // 首先应该把文件夹创建了
            File fi = new File(newpath + "/");
            if (!fi.exists()) {
                fi.mkdirs();
            }
            int sz = appfiles.length;
            for (int i = 0; i < sz; i++) {
                String subdir = appfiles[i];
                String subPath = oldpath + "/" + subdir;
                String subRootPath = newpath + "/" + subdir;
                copyAllDirectory(subPath, subRootPath, context);
            }
        }
        return;
    }

    /**
     * 创建文件
     *
     * @param path 文件名 以“/”开头表示绝对路径
     * @return 文件File
     */
    public static File createFile(String path) {
        if (path.startsWith("./")) {
            path = path.substring(2);
        }

        File file = null;
        // 是一个绝对路径文件
        if (path.startsWith("/")) {
            file = new File(path);
        } else {
            // file = new File(GlobalConfig.FileRootPath + path);
        }

        if (file.exists()) { // 文件存在删掉存在文件
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (Exception e) {
            try {
                String parent = file.getParent() + "/";
                File pfile = new File(parent);
                pfile.mkdirs();
                file.createNewFile();
                return file;
            } catch (Exception x) {
                return new File("");
            }
        }

        return file;
    }

    /**
     * 删除文件或目录
     *
     * @param path
     * @return
     */
    public static boolean DeleteFile(String path) {
        File f = new File(path);
        boolean ret = true;

        if (!f.exists()) {
            ret = false;
        } else if (f.exists()) {
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (!DeleteFile(files[i].getPath())) {
                        ret = false;
                    }
                }
            } else if (!f.delete()) {
                ret = false;
            }
        }
        return ret;
    }

    public static boolean saveBitmapToFile(Bitmap mBitmap, String filepath) {
        if (null == mBitmap || null == filepath
                || 0 == filepath.trim().length()) {
            return false;
        }

        boolean result = true;
        File file = createFile(filepath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            result = mBitmap.compress(CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    //获取当前目录下文件的数量【不含有子目录】
    public static int getNumberOfSubfiles(String disname) {
        try {
            File f = new File(disname);// 定义文件路径
            if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
                int i = 0;
                for (File item : f.listFiles()
                        ) {
                    if (!item.isDirectory() && !item.isHidden()) {
                        i++;
                    }
                }
                return i;
            }
        } catch (Exception ec) {
        }
        return 0;
    }

    //按照修改时间排序，获取list的文档【不含有子目录】
    public static List<File> getSubFilesByModfiyTime(String path) {
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fs = file.listFiles();
                List<File> rlt = new ArrayList<>();
                for (File item : fs
                        ) {
                    if (!item.isDirectory() && !item.isHidden()) {
                        rlt.add(item);
                    }
                }
                File[] newfs = new File[rlt.size()];
                rlt.toArray(newfs);
                Arrays.sort(newfs, new CompratorByLastModified());

                return Arrays.asList(newfs);
            } else {
                return null;
            }
        }
        return null;
    }

    static class CompratorByLastModified implements Comparator<File> {

        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0) {
                return -1;
            } else if (diff == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    //获取当前目录下子文件的大小,以“byte”为单位
    public static long getDirSize(String path) {
        File file = new File(path);
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children) {
                    if (!f.isDirectory() && !f.isHidden()) {
                        size += f.length();
                    }
                }
//                    size += getDirSize(f.getAbsolutePath());
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
//                long size = file.length();
                return 0;
            }
        } else {
            return 0;
        }
    }


    // 获取文件目录
    public static String getFileDir(String pathstr) {
        if (pathstr != null) {
            // 获取相对路径
            int i = pathstr.lastIndexOf("/");
            return pathstr.substring(0, i);
        } else
            return "";
    }

    public static String getExtName(String s) {
        int i = s.lastIndexOf(".");
        int leg = s.length();
        return (i > 0 ? (i + 1) == leg ? " " : s.substring(i, s.length()) : " ");
    }

    public static String getFileName(String s) {
        int i = s.lastIndexOf("/");
        int leg = s.length();
        return (i > 0 ? (i + 1) == leg ? "" : s.substring(i + 1, s.length())
                : " ");
    }

    /** */
    /**
     * 文件重命名
     *
     * @param dir     文件目录
     * @param oldname 原来的文件名
     * @param newname 新文件名
     */
    public static void renameFileToPath(String dir, String oldname,
                                        String newname) {
        if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(dir + "/" + oldname);
            File newfile = new File(dir + "/" + newname);
            if (!oldfile.exists()) {
                LogUtil.ii(oldname + "不存在！");
                return;// 重命名文件不存在
            }
            if (newfile.exists())// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                LogUtil.ii(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
        }
    }

    /** */
    /**
     * 文件重命名
     *
     * @param filepath 文件目录
     * @param newname  新文件名
     */
    public static String renameToNewName(String filepath, String newname) {
        String oldname = getFileName(filepath);
        String newpath = "";
        if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(filepath);
            newpath = getFileDir(filepath) + "/" + newname;
            File newfile = new File(newpath);
            if (!oldfile.exists()) {
                return "";// 重命名文件不存在
            }
            if (newfile.exists())// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
        }
        return newpath;
    }

    // 遍历删除目录,包括入参目录
    public static void delDisWithSelf(String filepath) {
        try {
            File f = new File(filepath);// 定义文件路径
            if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
                if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                    f.delete();
                } else {// 若有则把文件放进数组，并判断是否有下级目录
                    File delFile[] = f.listFiles();
                    int i = f.listFiles().length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            delDis(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                        }
                        delFile[j].delete();// 删除文件
                    }
                }
                delDis(filepath);// 递归调用
            }

            if (f.exists() && f.isDirectory()) {
                f.delete();
            }
        } catch (Exception ec) {
        }
    }

    // 遍历删除目录,但不删除最上层的目录
    public static void delDis(String filepath) {
        try {
            delDisIfSelf(filepath, false);
        } catch (Exception ec) {
            LogUtil.ee(ec);
        }

    }

    /***
     * isSelfDel ：false 为不删除最上层目录，true 为删除最上层目录
     */
    private static void delDisIfSelf(String filepath, boolean isSelfDel) {
        try {
            File f = new File(filepath);// 定义文件路径
            if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
//                LogUtil.ii(f.getAbsolutePath());
                if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                    if (isSelfDel) {
                        f.delete();
                    } else {
                        return;
                    }
                } else {// 若有则把文件放进数组，并判断是否有下级目录
                    File delFile[] = f.listFiles();
                    int i = f.listFiles().length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            delDisIfSelf(delFile[j].getAbsolutePath(), true);// 递归调用del方法并取得子目录路径，子目录全部删除
                        }
                        delFile[j].delete();// 删除文件
                    }
                }
                delDisIfSelf(filepath, isSelfDel);// 递归调用
            }
        } catch (Exception ec) {
            LogUtil.ee(ec);
        }

    }

    // 遍历删除目录
    public static void delFile(String filename) {
        try {
            File file = new File(filename);
            if (file.isDirectory())
                return;
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ec) {
        }
    }

    /**
     * 判断sd中是否存在目录
     *
     * @param dirPath
     * @return
     */
    public static boolean isDirExist(String dirPath) {
        File file = new File(SDCardRoot + dirPath + File.separator);
        return file.exists();
    }

    /**
     * sd创建文件
     *
     * @param dirName
     * @return
     */
    public static File createSDDirs(String dirName) {
        File dir = new File(SDCardRoot + dirName + File.separator);
        dir.mkdirs();
        return dir;
    }

    /**
     * saveBitmap
     *
     * @param @param filename---完整的路径格式-包含目录以及文件名
     * @param @param bitmap
     * @param @param isDelete --是否只留一张
     * @return void
     * @throws
     */
    public static void saveBitmap(String dirpath, Bitmap bitmap, boolean isDelete) {
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dirpath);
        // 若存在即删除-默认只保留一张
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}