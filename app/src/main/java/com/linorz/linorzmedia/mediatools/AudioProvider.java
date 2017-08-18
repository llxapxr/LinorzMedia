package com.linorz.linorzmedia.mediatools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class AudioProvider extends AbstractProvider {
    //获得音频列表
    public AudioProvider(Context context) {
        super(context);
    }

    @Override
    public List<Audio> getList() {
        List<Audio> list = null;
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    int albumId = cursor
                            .getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String title = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String album = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String path = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String displayName = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                    long duration = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    Audio audio = new Audio(id, albumId,title, album, artist, path,
                            displayName, mimeType, duration, size);
                    list.add(audio);
                }
                cursor.close();
            }
        }
        return list;
    }
}