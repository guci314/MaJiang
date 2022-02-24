package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    public static List<Integer>gui=new ArrayList<>();

    /**
     * 打出一张牌后是否有叫
     * @param cards
     * @return
     */
    public static boolean KeXiaJiao(List<Integer> cards){
        for (Integer i : cards){
            List<Integer> temp=new ArrayList<>(cards);
            temp.remove(i);
            List<Integer> l=HuUtil.isTingExtra(temp,gui);
            if (l.size()>0) return true;
        }
        return false;
    }

    public static HuaShe type(int input){
        int x=MaJiangDef.type(input);
        if (x==MaJiangDef.TYPE_WAN) return HuaShe.WAN;
        if (x==MaJiangDef.TYPE_TONG) return HuaShe.TONG;
        if (x==MaJiangDef.TYPE_TIAO) return HuaShe.TIAO;
        return null;
    }

    public static HuaShe stringToHuaShe(String s){
        switch (s){
            case "TONG":
                return HuaShe.TONG;
            case "TIAO":
                return HuaShe.TIAO;
            case "WAN":
                return HuaShe.WAN;
            default:
                return null;
        }
    }

    public static Status stringToStatus(String s){
        switch (s){
            case "Hu":return Status.Hu;
            case "Playing":return Status.Playing;
            default:return null;
        }
    }
}
