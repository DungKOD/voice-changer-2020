#include <jni.h>
#include <string>
#include <thread>
#include <fmod.hpp>
#include <android/log.h>
#include <unistd.h>
#include <arpa/nameser.h>
#include "com_soundmaker_voicechanger_bridge_VoiceUtil.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"zph",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zph",FORMAT,##__VA_ARGS__);
#define TYPE_NORMAL  0
#define TYPE_LOLITA  1
#define TYPE_UNCLE   2
#define TYPE_THRILLER  3
#define TYPE_FUNNY  4
#define TYPE_ETHEREAL  5
#define TYPE_BIGGUY 6
#define TYPE_CHILD 7
#define TYPE_FEMALE 8
#define TYPE_MALE 9
#define TYPE_OLD_MAN 10
#define TYPE_OLD_WOMAN 11
#define TYPE_ANGLE 12
#define TYPE_CAVE_MONSTER 13
#define TYPE_DEMON 14
#define TYPE_FRANKENSEIN 17
#define TYPE_GOBLIN 18
#define TYPE_HEROIC 19
#define TYPE_PIXIE 20
#define TYPE_PIRATE 21
#define TYPE_SUPER_VILLAIN 22
#define TYPE_ASTRONAUT 23
#define TYPE_ALIEN 24
#define TYPE_CYBORG 25
#define TYPE_DARTH_VADER 26
#define TYPE_LOST_IN_SPACE 27
#define TYPE_PROTOCOL_DROID 28
#define TYPE_ROBOT 29
#define TYPE_CHIPMUNK 30
#define TYPE_CARTOON 31
#define TYPE_ETHEREAL2 32
#define TYPE_GEEK 33
#define TYPE_JUMPIN_JACK 34
#define TYPE_JELLY_FISH 35
#define TYPE_SQUEAKY 36
#define TYPE_AM_RADIO 37
#define TYPE_TELEPHONE 41
#define TYPE_AUDITORIUM 42
#define TYPE_BATHROOM 43
#define TYPE_CAVE 44
#define TYPE_CONCERT_HALL 45
#define TYPE_GRAND_CANYON 46
#define TYPE_HANGAR 47
#define TYPE_STUCK_IN_A_WELL 48

using namespace FMOD;

static bool requestStop = false;
static DSP *dsp, *dsp2, *dsp3, *dsp4, *dsp5;
Channel *channel;
Sound *sound;
System *field_system;

JNIEXPORT int JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_init
        (JNIEnv *env, jclass jcls, jstring path_jstr) {
    if (field_system == NULL) {
        //sound->release();
        //field_system->close();
        //field_system->release();
        System_Create(&field_system);
    }

    const char *path_cstr = env->GetStringUTFChars(path_jstr, NULL);
    LOGE("%s", path_cstr);
    field_system->init(32, FMOD_INIT_NORMAL, NULL);
    //if (sound == NULL) {
    field_system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sound);
    //}
    LOGI("%s", "--> sound created");
    return 1;
}

extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_fix
        (JNIEnv *env, jclass jcls, jint type) {
    LOGI("%s", "--> start");

    Sound *localSound;
    float frequency;
    unsigned int position = 0;
    bool isPlaying = true;
    FMOD_RESULT result;
    jclass cls;
    jmethodID methodid;
    unsigned int length = 0;
    try {
        localSound = sound;
        cls = (env)->FindClass("com/audioeffect/voicechanger/bridge/VoicePlayUtil");
        methodid = (env)->GetStaticMethodID(cls, "onStartVoice", "()V");
        setChannel(field_system, sound, type);
        (env)->CallStaticVoidMethod(cls, methodid);
    } catch (...) {
        LOGE("%s", "catch exception...")
        goto end;
    }
    field_system->update();
    // 每隔一秒检测是否播放结束
    methodid = (env)->GetStaticMethodID(cls, "onPlayingVoice", "(II)V");
    sound->getLength(&length, FMOD_TIMEUNIT_MS);
    while (isPlaying) {
        if (requestStop) {
            //channel->stop();
            goto end;
        } else {
            channel->getPosition(&position, FMOD_TIMEUNIT_MS);
            channel->isPlaying(&isPlaying);
            (env)->CallStaticVoidMethod(cls, methodid, position, length);
            usleep(1000 * 1000);
        }
    }

    goto end;

    //释放资源
    end:
    channel->stop();
    methodid = (env)->GetStaticMethodID(cls, "onStopVoice", "()V");
    (env)->CallStaticVoidMethod(cls, methodid);

}

extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_stop
        (JNIEnv *env, jclass jcls) {
    LOGI("%s", "--> stop");
    requestStop = true;
};


extern "C" void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_pause
        (JNIEnv *env, jclass jcls, jboolean pause) {
    LOGI("%s", "--> pause");
    channel->setPaused(pause);
};

extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_record
        (JNIEnv *env, jclass jcls, jstring path_jstr, jstring outputPath, jint type) {
    LOGI("%s", "--> record");

    System *system;
    Sound *sound;
    bool isPlaying;
    bool isRecording = true;
    unsigned int length = 0;
    unsigned int position = 0;
    float percent;
    jclass cls;
    jmethodID methodid;

    System_Create(&system);
    const char *path_cstr = env->GetStringUTFChars(path_jstr, NULL);
    const char *output_path_cstr = env->GetStringUTFChars(outputPath, NULL);
    try {
        system->setOutput(FMOD_OUTPUTTYPE_WAVWRITER_NRT);
        system->init(32, FMOD_INIT_NORMAL, (void *) output_path_cstr);
        system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sound);
        setChannel(system, sound, type);
        system->recordStart(19, sound, false);
        channel->isPlaying(&isPlaying);
        sound->getLength(&length, FMOD_TIMEUNIT_MS);
        cls = (env)->FindClass("com/audioeffect/voicechanger/bridge/RecordUtils");
        methodid = (env)->GetStaticMethodID(cls, "onStartRecord", "()V");
        (env)->CallStaticVoidMethod(cls, methodid);
        if (!methodid) {
            goto end;
        }
    } catch (...) {
        goto end;
    }
    try {
        methodid = (env)->GetStaticMethodID(cls, "onRecording", "(F)V");
    } catch (...) {
        goto end;
    }

    while (isPlaying) {
        system->update();
        channel->isPlaying(&isPlaying);
        channel->getPosition(&position, FMOD_TIMEUNIT_MS);
        system->isRecording(19, &isRecording);
        percent = (float) position / length;
        (env)->CallStaticVoidMethod(cls, methodid, percent);
    }
    methodid = (env)->GetStaticMethodID(cls, "onRecordComplete", "()V");
    (env)->CallStaticVoidMethod(cls, methodid);

    end:
    system->recordStop(19);
    channel->stop();
    env->ReleaseStringUTFChars(path_jstr, path_cstr);
    sound->release();
    system->close();
    system->release();
};


extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_pitch
        (JNIEnv *env, jclass jcls, jfloat pitch) {
    LOGI("%s", "--> pitch");

    if (dsp != NULL && channel != NULL) {
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, pitch);
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_FFTSIZE, 256);
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_MAXCHANNELS, 1);
        channel->addDSP(0, dsp);
    } else {
        field_system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, pitch);
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_FFTSIZE, 256);
        dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_MAXCHANNELS, 1);
        channel->addDSP(0, dsp);
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_seekToPercent
        (JNIEnv *, jclass, jfloat progress) {
    Sound *sound;
    unsigned int length;
    unsigned int position;

    channel->setPaused(true);
    channel->getCurrentSound(&sound);
    if ((sound != NULL)) {
        sound->getLength(&length, FMOD_TIMEUNIT_MS);
        position = progress * length;
        channel->setPosition(position, FMOD_TIMEUNIT_MS);
        channel->setPaused(false);
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_audioeffect_voicechanger_bridge_VoiceUtil_release
        (JNIEnv *, jclass) {
    channel->stop();
    sound->release();
    //field_system->close();
    //field_system->release();
}

void setChannel(System *system, Sound *sound, int type) {
    requestStop = false;
    switch (type) {
        case TYPE_NORMAL:  // 普通
//            LOGI("%s", path_cstr)
//            LOGE("%s", (void *) output_path_cstr)
            system->playSound(sound, 0, false, &channel);
            LOGI("%s", "fix normal");
            break;
        case TYPE_LOLITA:  // 萝莉
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);    // 可改变音调
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 8.0);     // 8.0 为一个八度
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;

        case TYPE_UNCLE:  // 大叔
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.8);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_FFTSIZE, 256);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_MAXCHANNELS, 1);
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;

        case TYPE_THRILLER:   // 惊悚
            system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);       //可改变颤音
            dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 5);           // 时间偏移低频振荡周期
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;
        case TYPE_FUNNY:  // 搞怪
            system->createDSPByType(FMOD_DSP_TYPE_NORMALIZE, &dsp);    //放大声音
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            float frequency;
            channel->getFrequency(&frequency);
            frequency = frequency * 2;                                  //频率*2
            channel->setFrequency(frequency);
            break;
        case TYPE_ETHEREAL: // 空灵
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);          // 控制回声
            dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 300);           // 延时
            dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 20);         // 回波衰减的延迟

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;
        case TYPE_BIGGUY:
            LOGE("%s", "TYPE_BIGGUY");
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 2500);
            //voice pitch shifter not exist. Use PITCHSHIFT instead of it
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.6718f);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_CHILD:
            LOGE("%s", "TYPE_CHILD");
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 1);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -7);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, -10);
            //voice pitch shifter not exist. Use PITCHSHIFT instead of it
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.7658f);
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 120);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_FEMALE:
            LOGE("%s", "TYPE_FEMALE");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.5f);
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_HIGHPASS_SIMPLE_CUTOFF, 120);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_MALE:
            LOGE("%s", "TYPE_MALE");
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 2000);
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.82f);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_PARAMEQ_GAIN, 18);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_OLD_MAN:
            LOGE("%s", "TYPE_OLDMALE");
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 2000);
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.5f);
            system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_DEPTH, 0.5f);
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_FREQUENCY, 6);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_PARAMEQ_GAIN, 18);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_OLD_WOMAN:
            LOGE("%s", "TYPE_OLD_WOMAN");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.205f);
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 1250);
            system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_DEPTH, 0.9f);
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_FREQUENCY, 10);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_ANGLE:
            break;
        case TYPE_CAVE_MONSTER:
            LOGE("%s", "TYPE_CAVE_MONSTER");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.73f);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 12);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -10);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, 0);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 39);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_DELAY, 212);
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 1600);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_DEMON:
            LOGE("%s", "TYPE_DEMON");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.76f);
            system->createDSPByType(FMOD_DSP_TYPE_DISTORTION, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_DISTORTION_LEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 1760);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 66);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, -13);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 1600);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_FRANKENSEIN:
            LOGE("%s", "TYPE_FRANKENSTEIN");
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_CHORUS_MIX, 80);
            dsp->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.5);
            dsp->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 5);
//                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
//                dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 6);
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.7);
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 1600);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
//                channel->addDSP(3, dsp4);
            break;
        case TYPE_GOBLIN:
            LOGE("%s", "TYPE_GOBLIN");
            system->createDSPByType(FMOD_DSP_TYPE_FLANGE, &dsp);
            dsp->setParameterFloat(FMOD_DSP_FLANGE_MIX, 50);
            dsp->setParameterFloat(FMOD_DSP_FLANGE_RATE, 0.5);
            dsp->setParameterFloat(FMOD_DSP_FLANGE_DEPTH, 0.5);
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2);
//                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp3);
//                dsp3->setParameterFloat(FMOD_DSP_ECHO_DELAY, 6);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_MIX, 70);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_RATE, 1.12);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 5);
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 1600);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_HEROIC:
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, -2);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, 4);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, 6);
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.88f);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 100);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 2500);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_PIXIE:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.73f);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_MIX, 80);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.5f);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 5);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 12);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, 1);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, -56);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_PIRATE:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.85f);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_MIX, 50);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.5f);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 5);
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 625);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_SUPER_VILLAIN:
            LOGE("%s", "TYPE_SUPER_VILLAIN");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.73f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 75);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 10);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, -9);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, 12);
            dsp3->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, -8);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_ASTRONAUT:
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp2);
            dsp3->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 4000);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 25);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 1);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 520);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 57);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_ALIEN:
            LOGE("%s", "TYPE_ALIEN");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.92f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 56);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 25);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_RATE, 2);
            dsp3->setParameterFloat(FMOD_DSP_CHORUS_MIX, 100);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 116);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_CYBORG:
            LOGE("%s", "TYPE_CYBORG");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.9f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 5);
            system->createDSPByType(FMOD_DSP_TYPE_FLANGE, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_RATE, 5);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_DEPTH, 0.7f);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_MIX, 50);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 10);
            dsp4->setParameterFloat(FMOD_DSP_ECHO_DELAY, 50);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp5);
            dsp5->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 64);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            channel->addDSP(4, dsp5);
            break;
        case TYPE_DARTH_VADER:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.9f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 5);
            system->createDSPByType(FMOD_DSP_TYPE_FLANGE, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_MIX, 26);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_RATE, 500);
            dsp3->setParameterFloat(FMOD_DSP_FLANGE_DEPTH, 0.44);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_LOST_IN_SPACE:
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 100);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 17);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 250);
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 2500);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_PROTOCOL_DROID:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.24f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 75);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 22);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_ROBOT:
            LOGE("%s", "TYPE_ROBOT");
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.9f);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 75);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 20);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 64);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_CHIPMUNK:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 8.0);
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            float frequencyChipmunk;
            channel->getFrequency(&frequencyChipmunk);
            frequencyChipmunk = frequencyChipmunk * 1.7;
            channel->setFrequency(frequencyChipmunk);
            break;
        case TYPE_CARTOON:
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 1.75f);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 5);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.5);
            dsp2->setParameterFloat(FMOD_DSP_CHORUS_MIX, 80);
            system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp3);       //可改变颤音
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 15);
            dsp3->setParameterFloat(FMOD_DSP_TREMOLO_DEPTH, 32);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_ETHEREAL2:
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 180);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 4);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -5);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, 5);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 3800);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, -19);
            dsp3->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, -16);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 10);
            dsp4->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.44);
            dsp4->setParameterFloat(FMOD_DSP_CHORUS_MIX, 80);
            system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp5);
            dsp5->setParameterFloat(FMOD_DSP_CHORUS_DEPTH, 7);
            dsp5->setParameterFloat(FMOD_DSP_CHORUS_RATE, 0.26);
            dsp5->setParameterFloat(FMOD_DSP_CHORUS_MIX, 80);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            channel->addDSP(4, dsp5);
            break;
        case TYPE_GEEK:

            break;
        case TYPE_JUMPIN_JACK:
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
            dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 500);
            dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 27);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 4960);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 36);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_DELAY, 3720);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 33);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_ECHO_DELAY, 2480);
            dsp4->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 30);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;
        case TYPE_JELLY_FISH:
            break;
        case TYPE_SQUEAKY:
            break;
        case TYPE_AM_RADIO:
            LOGE("%s", "TYPE_AM_RADIO");
            system->createDSPByType(FMOD_DSP_TYPE_DISTORTION, &dsp);
            dsp->setParameterFloat(FMOD_DSP_DISTORTION_LEVEL, 0.9f);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 4);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 0);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -17);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, -85);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_TELEPHONE:
            LOGE("%s", "TYPE_TELEPHONE");
            system->createDSPByType(FMOD_DSP_TYPE_HIGHPASS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_HIGHPASS_CUTOFF, 4000);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 2);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_AUDITORIUM:
            LOGE("%s", "TYPE_AUDITORIUM");
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 500);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 50);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;
        case TYPE_BATHROOM:
            LOGE("%s", "TYPE_BATHROOM");
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 500);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 50);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 2.32f);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_CAVE:
            LOGE("%s", "TYPE_CAVE");
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
            dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 200);
            dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 40);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 278);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_DELAY, 150);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 27);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_CONCERT_HALL:
            LOGE("%s", "TYPE_CONCERT_HALL");
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, 2);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -11);
            dsp->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, -22);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 3040);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 14);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 0.72f);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            break;
        case TYPE_GRAND_CANYON:
            LOGE("%s", "TYPE_GRAND_CANYON");
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 40);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 3);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, -63);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_DELAY, 750);
            dsp2->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 33);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_HANGAR:
            LOGE("%s", "TYPE_HANGAR");
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 1000);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 50);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, 0);
            system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_LOWGAIN, -65);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_MIDGAIN, -32);
            dsp2->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, 4);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            break;
        case TYPE_STUCK_IN_A_WELL:
            LOGE("%s", "TYPE_STUCK_IN_A_WELL");
            system->createDSPByType(FMOD_DSP_TYPE_LOWPASS, &dsp);
            dsp->setParameterFloat(FMOD_DSP_LOWPASS_CUTOFF, 300);
            system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp2);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 800);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 20);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, 0);
            dsp2->setParameterFloat(FMOD_DSP_SFXREVERB_DRYLEVEL, -15);
            system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp3);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_DELAY, 100);
            dsp3->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 50);
            system->createDSPByType(FMOD_DSP_TYPE_PARAMEQ, &dsp4);
            dsp4->setParameterFloat(FMOD_DSP_PARAMEQ_BANDWIDTH, 0.48);

            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            channel->addDSP(1, dsp2);
            channel->addDSP(2, dsp3);
            channel->addDSP(3, dsp4);
            break;

    }
}
