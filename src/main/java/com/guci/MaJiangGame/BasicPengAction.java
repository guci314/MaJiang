package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.List;

/**
 * go方法返回值 -1 表示不碰,其它值表示碰后打出的牌
 */
public class BasicPengAction implements PengAction{
    List<Integer> gui = new ArrayList<>();
    @Override
    public int go(int input, Player me) {
        boolean isQue=false;
        switch (me.DingQue){
            case WAN:
                isQue=input>= MaJiangDef.WAN1 && input<=MaJiangDef.WAN9;
                break;
            case TIAO:
                isQue=input>= MaJiangDef.TIAO1 && input<=MaJiangDef.TIAO9;
                break;
            case TONG:
                isQue=input>= MaJiangDef.TONG1 && input<=MaJiangDef.TONG9;
                break;
        }
        if (isQue) return -1;
        // TODO: 2022/2/8 添加杠牌算法
        boolean r=AIUtil.pengAI(me.cards,gui,input,0);
        if (!r) return -1;
        List<Integer> temp=new ArrayList<>(me.cards);
        temp.remove((Integer) input);
        temp.remove((Integer) input);
        return AIUtil.outAI(temp,gui);
        //gggggg
    }
}
