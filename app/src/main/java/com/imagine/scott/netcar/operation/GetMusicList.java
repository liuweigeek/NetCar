package com.imagine.scott.netcar.operation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.imagine.scott.netcar.bean.Music;

import java.util.ArrayList;
import java.util.List;

public class GetMusicList {
  
    public static List<Music> getMusicData(Context context){
        List<Music> musicList=new ArrayList<>();
        ContentResolver cr=context.getContentResolver();
        if(cr!=null){  
            //获取所有歌曲
            Cursor cursor=cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
            if(null==cursor){  
                return null;  
            }  
            if(cursor.moveToFirst()){
                do{
                    Music m=new Music();
                    String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if("<unknown>".equals(singer)){
                        singer="未知艺术家";
                    }
                    String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    long size=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    long time=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    m.setTitle(title);
                    m.setSinger(singer);
                    m.setAlbum(album);
                    m.setSize(size);
                    m.setTime(time);
                    m.setUrl(url);
                    m.setName(name);
                    musicList.add(m);
                } while(cursor.moveToNext());
            }     
        }  
        return musicList;
    }  
}  