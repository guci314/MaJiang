package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.List;

public class BasicHuPaiAction implements HuPaiAction{
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
        List<Integer> tmp = new ArrayList<>(me.cards);
        tmp.add(input);
        if (HuUtil.isHuExtra(tmp, gui, 0)){
            return 0;
        }
        else return -1;
    }
}
