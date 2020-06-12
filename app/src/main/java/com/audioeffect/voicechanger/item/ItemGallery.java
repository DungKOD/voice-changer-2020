package com.audioeffect.voicechanger.item;

import java.io.Serializable;

public class ItemGallery implements Serializable {
    private int idAudio;
    private String mFilePath;
    private String mFileName;
    private String mName;
    private int mEffectID;
    private String mDuration;



    public ItemGallery(int id,String filePath, String fileName, String name, int effectID, String duration) {
        idAudio = id;
        mFilePath = filePath;
        mFileName = fileName;
        mName = name;
        mEffectID = effectID;
        mDuration = duration;
    }

    public int getIdAudio() {
        return idAudio;
    }

    public void setIdAudio(int idAudio) {
        this.idAudio = idAudio;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getName() {
        return mName;
    }

    public int getEffectID() {
        return mEffectID;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public void setName(String name) {
        mName = name;
    }
}
