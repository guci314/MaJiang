package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    public static List<Integer>gui=new ArrayList<>();

    /**
     * 打出一张牌后是否有叫
     * @param cards
     * @return
     */
    public boolean KeXiaJiao(List<Integer> cards){
        for (Integer i : cards){
            List<Integer> temp=new ArrayList<>(cards);
            temp.remove(i);
            List<Integer> l=HuUtil.isTingExtra(temp,gui);
            if (l.size()>0) return true;
        }
        return false;
    }
}