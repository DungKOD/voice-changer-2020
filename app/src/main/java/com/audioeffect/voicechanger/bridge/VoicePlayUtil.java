package com.audioeffect.voicechanger.bridge;

public class VoicePlayUtil {

    private static final String TAG = "VoicePlay";
    private static boolean isPlaying;
    private static Callback callback;
    private static int fieldCurrentTime;
    private static int fieldTotalTime;
    private static float ratio;

    public static void onStartVoice() {
        if (callback != null) {
            callback.onStartVoice();
        }
        isPlaying = true;
        ratio = 0;
    }

    public static void onPlayingVoice(int currentTime, int totalTime) {
        if (ratio == 0 && currentTime > 0) {
            ratio = (float) currentTime / 1000;
        }
        if (ratio != 0) {
            fieldCurrentTime = Math.round((currentTime / ratio) / 1000) * 1000;
            fieldTotalTime = Math.round((totalTime / ratio) / 1000) * 1000;
            if (callback != null) {
                callback.onPlayingVoice(fieldCurrentTime, fieldTotalTime);
            }
            if (fieldCurrentTime / 1000 == fieldTotalTime / 1000) {
                if (callback != null) {
                    callback.onComplete();
                }
            }
        }
    }

    public static void onStopVoice() {
        isPlaying = false;
        if (callback != null) {
            callback.onStopVoice();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public interface Callback {
        void onStartVoice();

        void onPlayingVoice(int currentTime, int totalTime);

        void onStopVoice();

        void onComplete();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void playVoice(final int effectID) {
        setPlaying(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                VoiceUtil.fix(effectID);
            }
        }).start();
    }

    public void stopVoice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VoiceUtil.stop();
            }
        }).start();
    }
}
