package com.audioeffect.voicechanger.bridge;

public class RecordUtils {
    private static final String TAG = RecordUtils.class.getSimpleName();
    private static CallBack sCallBack;

    public static void onRecording(float percentage) {
        if (sCallBack != null) {
            sCallBack.onRecording(percentage);
        }
    }

    public static void onStartRecord() {
        if (sCallBack != null) {
            sCallBack.onStartRecord();
        }
    }

    public static void onRecordComplete() {
        if (sCallBack != null) {
            sCallBack.onRecordComple();
        }
    }

    public interface CallBack {
        void onStartRecord();

        void onRecording(float percentage);

        void onRecordComple();
    }

    public void setCallBack(CallBack callBack) {
        this.sCallBack = callBack;
    }
}
