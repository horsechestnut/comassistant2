package com.example.administrator.comassistant2.simulation.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtils {

    Context context;
    String name;
    int sp_mode = Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS;

    public SharedPreferencesUtils(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    /**
     * 根据key和预期的value类型获取value的值
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T getValue(String key, Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("请先调用带有context，name参数的构造！");
        }
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        return getValue(key, clazz, sp);
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param
     */
    public void setObject(String key, Object object) {

        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setList(String key, List list) {
        try {
//    		list = new ArrayList(new HashSet(list));
            String liString = SceneList2String(list);
            SharedPreferences mySharedPreferences = this.context.getSharedPreferences(this.name,
                    Context.MODE_PRIVATE);
            Editor editor = mySharedPreferences.edit();
            editor.putString(key, liString);
            editor.commit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public static String SceneList2String(List SceneList)
            throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    @SuppressWarnings({"rawtypes", "null"})
    public List getList(String key) {
        List list = new ArrayList();
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            String SceneListString = sharedPreferences.getString(key, "");
            try {
                list = String2SceneList(SceneListString);
            } catch (StreamCorruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (list == null || list.isEmpty()) {
            list = new ArrayList();
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public List String2SceneList(String SceneListString)
            throws StreamCorruptedException, IOException,
            ClassNotFoundException {
        List SceneList = new ArrayList();
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        SceneList = (List) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

    /**
     * 对于外部不可见的过渡方法
     *
     * @param key
     * @param clazz
     * @param sp
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {

            t = clazz.newInstance();

            if (t instanceof Integer) {
                return (T) Integer.valueOf(sp.getInt(key, 0));
            } else if (t instanceof String) {
                return (T) sp.getString(key, "");
            } else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            } else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            } else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (InstantiationException e) {
            LogUtil.ee(e);
        } catch (IllegalAccessException e) {
            LogUtil.ee(e);
        }
        return null;
    }

}
