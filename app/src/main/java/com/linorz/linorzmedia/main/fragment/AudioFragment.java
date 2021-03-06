package com.linorz.linorzmedia.main.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.linorz.linorzmedia.mediatools.AudioPlay;
import com.linorz.linorzmedia.main.adapter.PlayAudio;
import com.linorz.linorzmedia.main.adapter.AudioAdapter;
import com.linorz.linorzmedia.mediatools.Audio;
import com.linorz.linorzmedia.mediatools.AudioProvider;
import com.linorz.linorzmedia.tools.StaticMethod;

/**
 * Created by linorz on 2016/5/5.
 */
public class AudioFragment extends MediaFragment {
    private PlayAudio playAudio;
    public int last_num = 0;
    private AudioPlay audioPlay;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int n = message.what;
            boolean k = (boolean) message.obj;

            Map<String, Object> map = items.get(n);
            map.put("isPlay", k);
            if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                    || (!recyclerView.isComputingLayout())) {
                adapter.notifyItemChanged(n);
            }
            return false;
        }
    });

    public AudioFragment() {
        if (AudioPlay.instance == null) {
            this.audioPlay = new AudioPlay();
        } else {
            this.audioPlay = AudioPlay.instance;
        }
    }

    public AudioPlay getAudioPlay() {
        return audioPlay;
    }

    public void changeAudio(int i) {
        changeColor(last_num, false);
        last_num = i;
        changeColor(last_num, true);
    }

    public void changeColor(int i, boolean isPlay) {
        Message message = new Message();
        message.what = i;
        message.obj = isPlay;
        handler.sendMessageDelayed(message, 0);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AudioAdapter audioAdapter = new AudioAdapter(context, items);
        audioAdapter.setPlayAudioListener(new PlayAudio() {
            @Override
            public void playAudio(int i) {
                if (playAudio != null) playAudio.playAudio(i);
            }

            @Override
            public void playAudioTwo(int i) {
                if (playAudio != null) playAudio.playAudioTwo(i);
            }
        });
        return audioAdapter;
    }

    public void setPlayAudioListener(PlayAudio playAudio) {
        this.playAudio = playAudio;
    }

    @Override
    protected void load() {
        ArrayList<Audio> audios = AudioPlay.instance.getAudios();
        for (int i = 0; i < audios.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", audios.get(i).getTitle());
            map.put("artist",audios.get(i).getArtist());
            map.put("path", "file://" + audios.get(i).getPath());
            map.put("isPlay", false);
            items.add(map);
        }
        adapter.notifyDataSetChanged();
    }
}
