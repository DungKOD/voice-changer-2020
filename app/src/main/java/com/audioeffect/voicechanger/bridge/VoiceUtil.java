package com.audioeffect.voicechanger.bridge;

import com.audioeffect.voicechanger.R;
import com.audioeffect.voicechanger.item.ItemEffect;

import java.util.ArrayList;

public class VoiceUtil {
    private static final String TAG = "VoiceUtil";

    public static final int TYPE_NORMAL = 0;    // 普通
    public static final int TYPE_LOLITA = 1;      // 萝莉
    public static final int TYPE_UNCLE = 2;       // 大叔
    public static final int TYPE_THRILLER = 3;    // 惊悚
    public static final int TYPE_FUNNY = 4;       // 搞怪
    public static final int TYPE_ETHEREAL = 5;    // 空灵
    public static final int TYPE_BIGGUY = 6;
    public static final int TYPE_CHILD = 7;
    public static final int TYPE_FEMALE = 8;
    public static final int TYPE_MALE = 9;
    public static final int TYPE_OLD_MAN = 10;
    public static final int TYPE_OLD_WOMAN = 11;
    public static final int TYPE_ANGLE = 12;
    public static final int TYPE_CAVE_MONSTER = 13;
    public static final int TYPE_DEMON = 14;
    public static final int TYPE_FRANKENSTEIN = 17;
    public static final int TYPE_GOBLIN = 18;
    public static final int TYPE_HEROIC = 19;
    public static final int TYPE_PIXIE = 20;
    public static final int TYPE_PIRATE = 21;
    public static final int TYPE_SUPER_VILLAIN = 22;
    public static final int TYPE_ASTRONAUT = 23;
    public static final int TYPE_ALIEN = 24;
    public static final int TYPE_CYBORG = 25;
    public static final int TYPE_DARTH_VADER = 26;
    public static final int TYPE_LOST_IN_SPACE = 27;
    public static final int TYPE_PROTOCOL_DROID = 28;
    public static final int TYPE_ROBOT = 29;
    public static final int TYPE_CHIPMUNK = 30;
    public static final int TYPE_CARTOON = 31;
    public static final int TYPE_ETHEREAL2 = 32;
    public static final int TYPE_JUMPING_JACK = 34;
    public static final int TYPE_AM_RADIO = 37;
    public static final int TYPE_TELEPHONE = 41;
    public static final int TYPE_AUDITORIUM = 42;
    public static final int TYPE_BATHROOM = 43;
    public static final int TYPE_CAVE = 44;
    public static final int TYPE_CONCERT_HALL = 45;
    public static final int TYPE_GRAND_CANYON = 46;
    public static final int TYPE_HANGAR = 47;
    public static final int TYPE_STUCK_IN_A_WELL = 48;

    public static final String NORMAL = "Normal";
    public static final String LOLITA = "Lolita";
    public static final String UNCLE = "Uncle";
    public static final String THRILLER = "Thriller";
    public static final String FUNNY = "Funny";
    public static final String ETHEREAL = "Ethereal";
    public static final String BIG_GUY = "Big guy";
    public static final String CHILD = "Child";
    public static final String FEMALE = "Female";
    public static final String MALE = "Male";
    public static final String OLD_MAN = "Old man";
    public static final String OLD_WOMAN = "Old woman";
    public static final String ANGLE = "Angle";
    public static final String CAVE_MONSTER = "Cave monster";
    public static final String DEMON = "Demon";
    public static final String FRANKENSTEIN = "Frankenstein";
    public static final String GOBLIN = "Goblin";
    public static final String HEROIC = "Heroic";
    public static final String PIXIE = "Pixie";
    public static final String PIRATE = "Pirate";
    public static final String SUPPER_VILLAIN = "Supper villain";
    public static final String ASTRONAUT = "Astronaut";
    public static final String ALIEN = "Alien";
    public static final String CYBORG = "Cyborg";
    public static final String DARTH_VADER = "Darth vader";
    public static final String LOST_IN_SPACE = "Lost in space";
    public static final String PROTOCAL_DROID = "Protocol droid";
    public static final String REBOT = "Robot";
    public static final String CHIPMUNK = "Chipmunk";
    public static final String CARTOON = "Cartoon";
    public static final String ETHEREA12 = "Ethereal2";
    public static final String JUMPING_JACK = "Jumping jack";
    public static final String AM_RADIO = "Am radio";
    public static final String TELEPHONE = "Telephone";
    public static final String AUDITORIUM = "Auditorium";
    public static final String BATHROOM = "Bathroom";
    public static final String CAVE = "Cave";
    public static final String CONCERT_HALL = "Concert hall";
    public static final String GRAND_CANYON = "Grand canyon";
    public static final String HANGAR = "Hangar";
    public static final String STUCK_IN_A_WELL = "Stuck in a well";

    private static ArrayList<ItemEffect> sItemEffects = new ArrayList<>();

    public static Thread thread = null;

    public static native int init(String path);

    public static native void fix(int type);

    public static native void stop();

    public static native void pause(boolean isPaused);

    public static native void record(String path, String outputPath, int type);

    public static native void pitch(float pitch);

    public static native void seekToPercent(float percent);

    public static native void release();

    static {
        System.loadLibrary("fmod");
        System.loadLibrary("fmodL");
        System.loadLibrary("voicechanger");
    }

    public static void createListEffect() {
        if (sItemEffects.isEmpty()) {
                    sItemEffects.add(new ItemEffect(NORMAL, VoiceUtil.TYPE_NORMAL, R.drawable.item_normal));
            sItemEffects.add(new ItemEffect(LOLITA, VoiceUtil.TYPE_LOLITA, R.drawable.item_lolita));
            sItemEffects.add(new ItemEffect(UNCLE, VoiceUtil.TYPE_UNCLE, R.drawable.item_uncle));
            sItemEffects.add(new ItemEffect(THRILLER, VoiceUtil.TYPE_THRILLER, R.drawable.item_thriller));
            sItemEffects.add(new ItemEffect(FUNNY, VoiceUtil.TYPE_FUNNY, R.drawable.item_funny));
            sItemEffects.add(new ItemEffect(ETHEREAL, VoiceUtil.TYPE_ETHEREAL, R.drawable.item_ethereal));
            sItemEffects.add(new ItemEffect(BIG_GUY, VoiceUtil.TYPE_BIGGUY, R.drawable.item_big_guy));
            sItemEffects.add(new ItemEffect(CHILD, VoiceUtil.TYPE_CHILD, R.drawable.item_child));
            sItemEffects.add(new ItemEffect(FEMALE, VoiceUtil.TYPE_FEMALE, R.drawable.item_female));
            sItemEffects.add(new ItemEffect(MALE, VoiceUtil.TYPE_MALE, R.drawable.item_male));
            sItemEffects.add(new ItemEffect(OLD_MAN, VoiceUtil.TYPE_OLD_MAN, R.drawable.item_old_man));
            sItemEffects.add(new ItemEffect(OLD_WOMAN, VoiceUtil.TYPE_OLD_WOMAN, R.drawable.item_old_woman));
            sItemEffects.add(new ItemEffect(ANGLE, VoiceUtil.TYPE_ANGLE, R.drawable.item_angle));
            sItemEffects.add(new ItemEffect(CAVE_MONSTER, VoiceUtil.TYPE_CAVE_MONSTER, R.drawable.item_cave_monster));
            sItemEffects.add(new ItemEffect(DEMON, VoiceUtil.TYPE_DEMON, R.drawable.item_demon));
            sItemEffects.add(new ItemEffect(FRANKENSTEIN, VoiceUtil.TYPE_FRANKENSTEIN, R.drawable.item_frankenstein));
            sItemEffects.add(new ItemEffect(GOBLIN, VoiceUtil.TYPE_GOBLIN, R.drawable.item_goblin));
            sItemEffects.add(new ItemEffect(HEROIC, VoiceUtil.TYPE_HEROIC, R.drawable.item_heroic));
            sItemEffects.add(new ItemEffect(PIXIE, VoiceUtil.TYPE_PIXIE, R.drawable.item_pixie));
            sItemEffects.add(new ItemEffect(PIRATE, VoiceUtil.TYPE_PIRATE, R.drawable.item_pirate));
            sItemEffects.add(new ItemEffect(SUPPER_VILLAIN, VoiceUtil.TYPE_SUPER_VILLAIN, R.drawable.item_supper_villain));
            sItemEffects.add(new ItemEffect(ASTRONAUT, VoiceUtil.TYPE_ASTRONAUT, R.drawable.item_astronaut));
            sItemEffects.add(new ItemEffect(ALIEN, VoiceUtil.TYPE_ALIEN, R.drawable.item_alien));
            sItemEffects.add(new ItemEffect(CYBORG, VoiceUtil.TYPE_CYBORG, R.drawable.item_cyborg));
            sItemEffects.add(new ItemEffect(DARTH_VADER, VoiceUtil.TYPE_DARTH_VADER, R.drawable.item_darth_vader));
            sItemEffects.add(new ItemEffect(LOST_IN_SPACE, VoiceUtil.TYPE_LOST_IN_SPACE, R.drawable.item_lost_in_space));
            sItemEffects.add(new ItemEffect(PROTOCAL_DROID, VoiceUtil.TYPE_PROTOCOL_DROID, R.drawable.item_protocol_droid));
            sItemEffects.add(new ItemEffect(REBOT, VoiceUtil.TYPE_ROBOT, R.drawable.item_robot));
            sItemEffects.add(new ItemEffect(CHIPMUNK, VoiceUtil.TYPE_CHIPMUNK, R.drawable.item_chipmunk));
            sItemEffects.add(new ItemEffect(CARTOON, VoiceUtil.TYPE_CARTOON, R.drawable.item_cartoon));
            sItemEffects.add(new ItemEffect(ETHEREA12, VoiceUtil.TYPE_ETHEREAL2, R.drawable.item_ethereal2));
            sItemEffects.add(new ItemEffect(JUMPING_JACK, VoiceUtil.TYPE_JUMPING_JACK, R.drawable.item_jumping_jack));
            sItemEffects.add(new ItemEffect(AM_RADIO, VoiceUtil.TYPE_AM_RADIO, R.drawable.item_am_radio));
            sItemEffects.add(new ItemEffect(TELEPHONE, VoiceUtil.TYPE_TELEPHONE, R.drawable.item_telephone));
            sItemEffects.add(new ItemEffect(AUDITORIUM, VoiceUtil.TYPE_AUDITORIUM, R.drawable.item_auditorium));
            sItemEffects.add(new ItemEffect(BATHROOM, VoiceUtil.TYPE_BATHROOM, R.drawable.item_bathroom));
            sItemEffects.add(new ItemEffect(CAVE, VoiceUtil.TYPE_CAVE, R.drawable.item_cave_monster));
            sItemEffects.add(new ItemEffect(CONCERT_HALL, VoiceUtil.TYPE_CONCERT_HALL, R.drawable.item_concert_hall));
            sItemEffects.add(new ItemEffect(GRAND_CANYON, VoiceUtil.TYPE_GRAND_CANYON, R.drawable.item_grand_canyon));
            sItemEffects.add(new ItemEffect(HANGAR, VoiceUtil.TYPE_HANGAR, R.drawable.item_hangar));
            sItemEffects.add(new ItemEffect(STUCK_IN_A_WELL, VoiceUtil.TYPE_STUCK_IN_A_WELL, R.drawable.item_stuck_in_a_well));
        }
    }

    public static ArrayList<ItemEffect> getsItemEffects() {
        return sItemEffects;
    }

    public static int getEffectDrawable(int effectID) {
        int drawable;
        switch (effectID) {
            case TYPE_NORMAL:
                drawable = R.drawable.item_normal;
                break;
            case TYPE_LOLITA:
                drawable = R.drawable.item_lolita;
                break;
            case TYPE_UNCLE:
                drawable = R.drawable.item_uncle;
                break;
            case TYPE_THRILLER:
                drawable = R.drawable.item_thriller;
                break;
            case TYPE_FUNNY:
                drawable = R.drawable.item_funny;
                break;
            case TYPE_ETHEREAL:
                drawable = R.drawable.item_ethereal;
                break;
            case TYPE_BIGGUY:
                drawable = R.drawable.item_big_guy;
                break;
            case TYPE_CHILD:
                drawable = R.drawable.item_child;
                break;
            case TYPE_FEMALE:
                drawable = R.drawable.item_female;
                break;
            case TYPE_MALE:
                drawable = R.drawable.item_male;
                break;
            case TYPE_OLD_MAN:
                drawable = R.drawable.item_old_man;
                break;
            case TYPE_OLD_WOMAN:
                drawable = R.drawable.item_old_woman;
                break;
            case TYPE_ANGLE:
                drawable = R.drawable.item_angle;
                break;
            case TYPE_CAVE_MONSTER:
            case TYPE_CAVE:
                drawable = R.drawable.item_cave_monster;
                break;
            case TYPE_DEMON:
                drawable = R.drawable.item_demon;
                break;
            case TYPE_FRANKENSTEIN:
                drawable = R.drawable.item_frankenstein;
                break;
            case TYPE_GOBLIN:
                drawable = R.drawable.item_goblin;
                break;
            case TYPE_HEROIC:
                drawable = R.drawable.item_heroic;
                break;
            case TYPE_PIXIE:
                drawable = R.drawable.item_pixie;
                break;
            case TYPE_PIRATE:
                drawable = R.drawable.item_pirate;
                break;
            case TYPE_SUPER_VILLAIN:
                drawable = R.drawable.item_supper_villain;
                break;
            case TYPE_ASTRONAUT:
                drawable = R.drawable.item_astronaut;
                break;
            case TYPE_ALIEN:
                drawable = R.drawable.item_alien;
                break;
            case TYPE_CYBORG:
                drawable = R.drawable.item_cyborg;
                break;
            case TYPE_DARTH_VADER:
                drawable = R.drawable.item_darth_vader;
                break;
            case TYPE_LOST_IN_SPACE:
                drawable = R.drawable.item_lost_in_space;
                break;
            case TYPE_PROTOCOL_DROID:
                drawable = R.drawable.item_protocol_droid;
                break;
            case TYPE_ROBOT:
                drawable = R.drawable.item_robot;
                break;
            case TYPE_CHIPMUNK:
                drawable = R.drawable.item_chipmunk;
                break;
            case TYPE_CARTOON:
                drawable = R.drawable.item_cartoon;
                break;
            case TYPE_ETHEREAL2:
                drawable = R.drawable.item_ethereal2;
                break;
            case TYPE_JUMPING_JACK:
                drawable = R.drawable.item_jumping_jack;
                break;
            case TYPE_AM_RADIO:
                drawable = R.drawable.item_am_radio;
                break;
            case TYPE_TELEPHONE:
                drawable = R.drawable.item_telephone;
                break;
            case TYPE_AUDITORIUM:
                drawable = R.drawable.item_auditorium;
                break;
            case TYPE_BATHROOM:
                drawable = R.drawable.item_bathroom;
                break;
            case TYPE_CONCERT_HALL:
                drawable = R.drawable.item_concert_hall;
                break;
            case TYPE_GRAND_CANYON:
                drawable = R.drawable.item_grand_canyon;
                break;
            case TYPE_HANGAR:
                drawable = R.drawable.item_hangar;
                break;
            case TYPE_STUCK_IN_A_WELL:
                drawable = R.drawable.item_stuck_in_a_well;
                break;
            default:
                drawable = -1;
                break;
        }
        return drawable;
    }

    public static String getEffectName(int effectID) {
        String effectName;
        switch (effectID) {
            case TYPE_NORMAL:
                effectName = NORMAL;
                break;
            case TYPE_LOLITA:
                effectName = LOLITA;
                break;
            case TYPE_UNCLE:
                effectName = UNCLE;
                break;
            case TYPE_THRILLER:
                effectName = THRILLER;
                break;
            case TYPE_FUNNY:
                effectName = FUNNY;
                break;
            case TYPE_ETHEREAL:
                effectName = ETHEREAL;
                break;
            case TYPE_BIGGUY:
                effectName = BIG_GUY;
                break;
            case TYPE_CHILD:
                effectName = CHILD;
                break;
            case TYPE_FEMALE:
                effectName = FEMALE;
                break;
            case TYPE_MALE:
                effectName = MALE;
                break;
            case TYPE_OLD_MAN:
                effectName = OLD_MAN;
                break;
            case TYPE_OLD_WOMAN:
                effectName = OLD_WOMAN;
                break;
            case TYPE_ANGLE:
                effectName = ANGLE;
                break;
            case TYPE_CAVE_MONSTER:
                effectName = CAVE_MONSTER;
                break;
            case TYPE_DEMON:
                effectName = DEMON;
                break;
            case TYPE_FRANKENSTEIN:
                effectName = FRANKENSTEIN;
                break;
            case TYPE_GOBLIN:
                effectName = GOBLIN;
                break;
            case TYPE_HEROIC:
                effectName = HEROIC;
                break;
            case TYPE_PIXIE:
                effectName = PIXIE;
                break;
            case TYPE_PIRATE:
                effectName = PIRATE;
                break;
            case TYPE_SUPER_VILLAIN:
                effectName = SUPPER_VILLAIN;
                break;
            case TYPE_ASTRONAUT:
                effectName = ASTRONAUT;
                break;
            case TYPE_ALIEN:
                effectName = ALIEN;
                break;
            case TYPE_CYBORG:
                effectName = CYBORG;
                break;
            case TYPE_DARTH_VADER:
                effectName = DARTH_VADER;
                break;
            case TYPE_LOST_IN_SPACE:
                effectName = LOST_IN_SPACE;
                break;
            case TYPE_PROTOCOL_DROID:
                effectName = PROTOCAL_DROID;
                break;
            case TYPE_ROBOT:
                effectName = REBOT;
                break;
            case TYPE_CHIPMUNK:
                effectName = CHIPMUNK;
                break;
            case TYPE_CARTOON:
                effectName = CARTOON;
                break;
            case TYPE_ETHEREAL2:
                effectName = ETHEREA12;
                break;
            case TYPE_JUMPING_JACK:
                effectName = JUMPING_JACK;
                break;
            case TYPE_AM_RADIO:
                effectName = AM_RADIO;
                break;
            case TYPE_TELEPHONE:
                effectName = TELEPHONE;
                break;
            case TYPE_AUDITORIUM:
                effectName = AUDITORIUM;
                break;
            case TYPE_BATHROOM:
                effectName = BATHROOM;
                break;
            case TYPE_CAVE:
                effectName = CAVE;
                break;
            case TYPE_CONCERT_HALL:
                effectName = CONCERT_HALL;
                break;
            case TYPE_GRAND_CANYON:
                effectName = GRAND_CANYON;
                break;
            case TYPE_HANGAR:
                effectName = HANGAR;
                break;
            case TYPE_STUCK_IN_A_WELL:
                effectName = STUCK_IN_A_WELL;
                break;
            default:
                effectName = "Unknown";
                break;
        }
        return effectName;
    }
}
