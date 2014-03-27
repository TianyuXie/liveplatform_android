package com.pplive.liveplatform.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import com.pplive.liveplatform.R;

public class Emoji {

    public static final ArrayList<String> EMOJI_KEY_LIST = new ArrayList<String>();
    public static final ArrayList<Integer> EMOJI_ICON_LIST = new ArrayList<Integer>();
    public static final HashMap<String, Integer> EMOJI_ICON_MAP = new HashMap<String, Integer>();
    
    public static final Pattern REG_EMOJI;

    static {
        EMOJI_KEY_LIST.add("微笑");
        EMOJI_KEY_LIST.add("难过");
        EMOJI_KEY_LIST.add("大笑");
        EMOJI_KEY_LIST.add("大哭");
        EMOJI_KEY_LIST.add("发怒");
        EMOJI_KEY_LIST.add("惊讶");
        EMOJI_KEY_LIST.add("调皮");
        EMOJI_KEY_LIST.add("害羞");
        EMOJI_KEY_LIST.add("偷笑");
        EMOJI_KEY_LIST.add("流汗");
        EMOJI_KEY_LIST.add("抓狂");
        EMOJI_KEY_LIST.add("呲牙");
        EMOJI_KEY_LIST.add("可爱");
        EMOJI_KEY_LIST.add("惊恐");
        EMOJI_KEY_LIST.add("咒骂");
        EMOJI_KEY_LIST.add("头晕");
        EMOJI_KEY_LIST.add("闭嘴");
        EMOJI_KEY_LIST.add("睡觉");
        EMOJI_KEY_LIST.add("拥抱");
        EMOJI_KEY_LIST.add("胜利");
        EMOJI_KEY_LIST.add("时间");
        EMOJI_KEY_LIST.add("示爱");
        EMOJI_KEY_LIST.add("握手");
        EMOJI_KEY_LIST.add("电话");

        EMOJI_ICON_LIST.add(R.drawable.emoji_001);
        EMOJI_ICON_LIST.add(R.drawable.emoji_002);
        EMOJI_ICON_LIST.add(R.drawable.emoji_003);
        EMOJI_ICON_LIST.add(R.drawable.emoji_004);
        EMOJI_ICON_LIST.add(R.drawable.emoji_005);
        EMOJI_ICON_LIST.add(R.drawable.emoji_006);
        EMOJI_ICON_LIST.add(R.drawable.emoji_007);
        EMOJI_ICON_LIST.add(R.drawable.emoji_008);
        EMOJI_ICON_LIST.add(R.drawable.emoji_009);
        EMOJI_ICON_LIST.add(R.drawable.emoji_010);
        EMOJI_ICON_LIST.add(R.drawable.emoji_011);
        EMOJI_ICON_LIST.add(R.drawable.emoji_012);
        EMOJI_ICON_LIST.add(R.drawable.emoji_013);
        EMOJI_ICON_LIST.add(R.drawable.emoji_014);
        EMOJI_ICON_LIST.add(R.drawable.emoji_015);
        EMOJI_ICON_LIST.add(R.drawable.emoji_016);
        EMOJI_ICON_LIST.add(R.drawable.emoji_017);
        EMOJI_ICON_LIST.add(R.drawable.emoji_018);
        EMOJI_ICON_LIST.add(R.drawable.emoji_019);
        EMOJI_ICON_LIST.add(R.drawable.emoji_020);
        EMOJI_ICON_LIST.add(R.drawable.emoji_021);
        EMOJI_ICON_LIST.add(R.drawable.emoji_022);
        EMOJI_ICON_LIST.add(R.drawable.emoji_023);
        EMOJI_ICON_LIST.add(R.drawable.emoji_024);
        
        StringBuilder sb = new StringBuilder();
        sb.append("\\[(");
        for (int i = 0; i < EMOJI_KEY_LIST.size(); ++i) {
            EMOJI_ICON_MAP.put(EMOJI_KEY_LIST.get(i), EMOJI_ICON_LIST.get(i));
            
            sb.append(String.format(Locale.US, i == 0 ? "%s" : "|%s", EMOJI_KEY_LIST.get(i)));
        }
        sb.append(")\\]");
        REG_EMOJI = Pattern.compile(sb.toString());
    }
}
