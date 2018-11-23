package com.example.administrator.comassistant2.simulation.tool;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dongdaxing on 2018/8/3.
 */

public class binOperate {

    private static boolean D = false;

    public static byte[] readHdbFile(Context context, int PageId, int len) {
        String fileName="Hdb"+String.valueOf(PageId)+".bin";
        int StartPos = 0;
        byte[]   bytes = new byte[len];
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            if(D)Log.d(TAG, "readFile: fin.available len:" + length);
            if (length > (len)) {
                fin.skip(StartPos);
                fin.read(bytes, 0, len);
                if(D)Log.d(TAG, "readFile: fin.available len 22:" + len);
            } else if ((length - StartPos) > 0) {
                 fin.read(bytes, 0, length - StartPos);
                if(D) Log.d(TAG, "readFile: fin.available len length-StartPos:" + (length - StartPos));
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] readRdbFile(Context context, int PageId, int len) {
        String fileName="Rdb"+String.valueOf(PageId)+".bin";
        int StartPos = 0;
        byte[]   bytes = new byte[len];
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            if(D)Log.d(TAG, "readFile: fin.available len:" + length);
            if (length > (len)) {
                fin.skip(StartPos);
                fin.read(bytes, 0, len);
                if(D)Log.d(TAG, "readFile: fin.available len 22:" + len);
            } else if ((length - StartPos) > 0) {
                fin.read(bytes, 0, length - StartPos);
                if(D) Log.d(TAG, "readFile: fin.available len length-StartPos:" + (length - StartPos));
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }


    public static boolean writeHdbFile(Context context, String fileName, byte[] bytes, int StartPos, int len ,boolean append) {
        try {
            FileOutputStream fout;
            fout = context.openFileOutput(fileName, MODE_PRIVATE);
            fout.write(bytes, 0, len);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeRdbFile(Context context,byte[] bytes, int len ,int pageid) {
        try {
            FileOutputStream fout;
            String fileName="Rdb"+String.valueOf(pageid)+".bin";
             fout = context.openFileOutput(fileName, MODE_PRIVATE);
            fout.write(bytes, 0, len);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //文件处理
    public static boolean HdbFileInitial(Context context,int PageSize,int PageCounter)
    {
        byte []rd=new byte[PageSize];
        for(int i=0;i<PageSize;i++)rd[i]=(byte)(0x00);
        for(int i=0;i<PageCounter;i++)
        {
            String HdbFileName="Hdb"+String.valueOf(i)+".bin";
            if(isExistFile(context.getFileStreamPath(HdbFileName).toString()))
            {
               // Log.d(TAG, "HdbFileInitial: Exist"+HdbFileName);
                continue;
            }
            else
            {
                Log.d(TAG, "HdbFileInitial: CreateHdbFile:"+HdbFileName);
                writeHdbFile(context,HdbFileName,rd,0,rd.length,false);
            }
        }
        writeHdbFile(context,"RdbTemp.bin",rd,0,rd.length,false);
        return  true;
    }

    public static boolean saveFileToHdb(Context context,int PageCounter,int RdbPageId)
    {
        //[1]copy rdb to hdb
        for(int i=0;i<RdbPageId;i++)
        {
            String HdbFileName="Hdb"+String.valueOf(i)+".bin";
            String RdbName="Rdb"+String.valueOf(i)+".bin";
            boolean flag = copyFile(context.getFileStreamPath(RdbName).toString(),context.getFileStreamPath(HdbFileName).toString());
            if(flag) Log.d(TAG, "saveFileToHdb: copy file Sucess,fileName : "+i);
            else Log.d(TAG, "saveFileToHdb: copy file Failed,fileName : "+i);
            flag = deleteHdbFiles(context.getFileStreamPath(RdbName).toString());
            if(flag) Log.d(TAG, "saveFileToHdb: delete file Sucess,fileName : "+i);
            else Log.d(TAG, "saveFileToHdb: delete file Failed,fileName : "+i);
        }
        //[2]rst other hdb file
        for(int j=RdbPageId;j<PageCounter;j++)
        {
            String HdbFileName="Hdb"+String.valueOf(j)+".bin";
            String RdbName="RdbTemp.bin";
            boolean flag = copyFile(context.getFileStreamPath(RdbName).toString(),context.getFileStreamPath(HdbFileName).toString());
            if(flag) Log.d(TAG, "RstHdb: copy file Sucess,fileName : "+(j-RdbPageId));
            else Log.d(TAG, "RstHdb: copy file Failed,fileName : "+(j-RdbPageId));
        }
        return true;
    }


    public static boolean isExistFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return  true;
        }
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists()) {
            //System.out.println("存在文件" + fileName + "成功！");
            if (file.isFile()) {
               // System.out.println("删除单个文件" + fileName + "成功！");
                if (file.delete()) {
                  //  System.out.println("删除单个文件" + fileName + "成功！");
                    return true;
                } else {
                  //  System.out.println("删除单个文件" + fileName + "失败！");
                    return false;
                }
            }
        }
        else
                {
                System.out.println("删除单个文件失败：" + fileName + "不存在！");
                return false;
            }
        return  false;
}

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteHdbFiles(String filePath) {
        boolean flag = false;
        flag = deleteFile(filePath);
        //删除当前空目录
        return flag;
    }


    public static  boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
