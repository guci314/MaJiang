package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 是否是暗七对
     * @param cards
     * @return
     */
    public static boolean isQiDui(List<Integer> cards){
        //每种牌的张数
        Map<Integer,Long> map=cards.stream().collect(Collectors.groupingBy(x->x,Collectors.counting()));
        //张数的张数
        Map<Long,Long> map1=map.values().stream().collect(Collectors.groupingBy(x->x,Collectors.counting()));
        //对子数量
        Long count2=map1.get(Long.valueOf(2));
        //四张数量
        Long count4=map1.get(Long.valueOf(4));
        int numOfDuiZi=0;
        if (count2 != null) numOfDuiZi= (int) (numOfDuiZi+count2);
        if (count4 != null) numOfDuiZi=(int)(numOfDuiZi+count4*2);
        return numOfDuiZi==7;
    }

    /**
     * 是否有五对或六对
     * @param cards
     * @return
     */
    public static boolean isWuDuiOrLiuDui(List<Integer> cards){
        //每种牌的张数
        Map<Integer,Long> map=cards.stream().collect(Collectors.groupingBy(x->x,Collectors.counting()));
        int numOfDuiZi=0;
        for(Integer key:map.keySet()){
            Long x=map.get(key);
            if (x==2 || x==3) numOfDuiZi++;
            if (x==4) numOfDuiZi=numOfDuiZi+2;
        }
        return numOfDuiZi==5 || numOfDuiZi==6;
    }

    /**
     * 是否是大对子
     * @param cards
     * @return
     */
    public static boolean isDaduizi(List<Integer> cards){
        Map<Integer, IntSummaryStatistics> groups = cards.stream()
                .collect(Collectors.groupingBy(singlePai -> singlePai, Collectors.summarizingInt(singlePai -> (int) singlePai)));
        int countJiang = 0;
        for (Integer key : groups.keySet()) {
            IntSummaryStatistics value = groups.get(key);
            if (value.getCount() != 3 && value.getCount() != 2) {
                return false;
            }
            if (value.getCount() == 2) {
                countJiang++;
            }
        }
        if (countJiang != 1) {
            return false;
        }
        return true;
    }
}
