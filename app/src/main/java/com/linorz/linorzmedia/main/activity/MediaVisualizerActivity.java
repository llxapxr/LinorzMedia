package com.linorz.linorzmedia.main.activity;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.linorz.linorzmedia.R;
import com.linorz.linorzmedia.customview.MyVisualizerView;
import com.linorz.linorzmedia.mediatools.AudioPlay;

public class MediaVisualizerActivity extends SwipeBackActivity {
    private MyVisualizerView mVisualizerView;
    private LinearLayout layout;
    // 定义播放声音的MediaPlayer
    private MediaPlayer mPlayer;
    // 定义系统的频谱
    private Visualizer mVisualizer;
    // 定义系统的均衡器
    private Equalizer mEqualizer;
    // 定义系统的重低音控制器
    private BassBoost mBass;
    // 定义系统的预设音场控制器
    private PresetReverb mPresetReverb;
    private List<Short> reverbNames = new ArrayList<Short>();
    private List<String> reverbVals = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);//将布局添加到 Activity
        initView();

        // 创建MediaPlayer对象,并添加音频
        mPlayer = AudioPlay.instance.getMediaPlayer();
        // 初始化示波器
        setupVisualizer();
//        // 初始化均衡控制器
//        setupEqualizer();
        // 初始化重低音控制器
        setupBassBoost();
//        // 初始化预设音场控制器
//        setupPresetReverb();
//        // 开发播放音乐
//        mPlayer.start();
    }

    private void initView() {
        layout = (LinearLayout) findViewById(R.id.test_layout);
        // 创建MyVisualizerView组件，用于显示波形图
        mVisualizerView = (MyVisualizerView) findViewById(R.id.test_visualizer);
    }

    /**
     * 初始化频谱
     */
    private void setupVisualizer() {
        // 以MediaPlayer的AudioSessionId创建Visualizer
        // 相当于设置Visualizer负责显示该MediaPlayer的音频数据
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        //设置需要转换的音乐内容长度，专业的说这就是采样，该采样值一般为2的指数倍，如64,128,256,512,1024。
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 为mVisualizer设置监听器
        /*
         * Visualizer.setDataCaptureListener(OnDataCaptureListener listener, int rate, boolean waveform, boolean fft
		 *
		 * 		listener，表监听函数，匿名内部类实现该接口，该接口需要实现两个函数
		 		rate， 表示采样的周期，即隔多久采样一次，联系前文就是隔多久采样128个数据
				iswave，是波形信号
				isfft，是FFT信号，表示是获取波形信号还是频域信号

		 */
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    //这个回调应该采集的是快速傅里叶变换有关的数据
                    @Override
                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] fft, int samplingRate) {
                    }

                    //这个回调应该采集的是波形数据
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] waveform, int samplingRate) {
                        // 用waveform波形数据更新mVisualizerView组件
                        mVisualizerView.updateVisualizer(waveform);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }

    /**
     * 初始化均衡控制器
     */
    private void setupEqualizer() {
        // 以MediaPlayer的AudioSessionId创建Equalizer
        // 相当于设置Equalizer负责控制该MediaPlayer
        mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
        // 启用均衡控制效果
        mEqualizer.setEnabled(true);
        TextView eqTitle = new TextView(this);
        eqTitle.setText("均衡器：");
        layout.addView(eqTitle);
        // 获取均衡控制器支持最小值和最大值
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围
        // 获取均衡控制器支持的所有频率
        short brands = mEqualizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            TextView eqTextView = new TextView(this);
            // 创建一个TextView，用于显示频率
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            // 设置该均衡控制器的频率
            eqTextView.setText((mEqualizer.getCenterFreq(i) / 1000)
                    + " Hz");
            layout.addView(eqTextView);
            // 创建一个水平排列组件的LinearLayout
            LinearLayout tmpLayout = new LinearLayout(this);
            tmpLayout.setOrientation(LinearLayout.HORIZONTAL);
            // 创建显示均衡控制器最小值的TextView
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最小值
            minDbTextView.setText((minEQLevel / 100) + " dB");
            // 创建显示均衡控制器最大值的TextView
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最大值
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            LinearLayout.LayoutParams layoutParams = new
                    LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            // 定义SeekBar做为调整工具
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(i));
            final short brand = i;
            // 为SeekBar的拖动事件设置事件监听器
            bar.setOnSeekBarChangeListener(new SeekBar
                    .OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                    // 设置该频率的均衡值
                    mEqualizer.setBandLevel(brand,
                            (short) (progress + minEQLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            // 使用水平排列组件的LinearLayout“盛装”3个组件
            tmpLayout.addView(minDbTextView);
            tmpLayout.addView(bar);
            tmpLayout.addView(maxDbTextView);
            // 将水平排列组件的LinearLayout添加到myLayout容器中
            layout.addView(tmpLayout);
        }
    }

    /**
     * 初始化重低音控制器
     */
    private void setupBassBoost() {
        // 以MediaPlayer的AudioSessionId创建BassBoost
        // 相当于设置BassBoost负责控制该MediaPlayer
        mBass = new BassBoost(0, mPlayer.getAudioSessionId());
        // 设置启用重低音效果
        mBass.setEnabled(true);
        TextView bbTitle = new TextView(this);
        bbTitle.setText("重低音：");
        layout.addView(bbTitle);
        // 使用SeekBar做为重低音的调整工具
        SeekBar bar = new SeekBar(this);
        // 重低音的范围为0～1000
        bar.setMax(1000);
        bar.setProgress(0);
        // 为SeekBar的拖动事件设置事件监听器
        bar.setOnSeekBarChangeListener(new SeekBar
                .OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar
                    , int progress, boolean fromUser) {
                // 设置重低音的强度
                mBass.setStrength((short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        layout.addView(bar);
    }

    /**
     * 初始化预设音场控制器
     */
    private void setupPresetReverb() {
        // 以MediaPlayer的AudioSessionId创建PresetReverb
        // 相当于设置PresetReverb负责控制该MediaPlayer
        mPresetReverb = new PresetReverb(0,
                mPlayer.getAudioSessionId());
        // 设置启用预设音场控制
        mPresetReverb.setEnabled(true);
        TextView prTitle = new TextView(this);
        prTitle.setText("音场");
        layout.addView(prTitle);
        // 获取系统支持的所有预设音场
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            reverbNames.add(i);
            reverbVals.add(mEqualizer.getPresetName(i));
        }
        // 使用Spinner做为音场选择工具
        Spinner sp = new Spinner(this);
        sp.setAdapter(new ArrayAdapter<String>(MediaVisualizerActivity.this,
                android.R.layout.simple_spinner_item, reverbVals));
        // 为Spinner的列表项选中事件设置监听器
        sp.setOnItemSelectedListener(new Spinner
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0
                    , View arg1, int arg2, long arg3) {
                // 设定音场
                mPresetReverb.setPreset(reverbNames.get(arg2));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        layout.addView(sp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mPlayer != null) {
            // 释放所有对象
            mVisualizer.release();
//            mEqualizer.release();
//            mPresetReverb.release();
            mBass.release();
//            mPlayer.release();
//            mPlayer = null;
        }
    }
}