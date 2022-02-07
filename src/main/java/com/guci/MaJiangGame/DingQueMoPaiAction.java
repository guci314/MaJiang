package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.List;

public class DingQueMoPaiAction implements MoPaiAction{
    @Override
    public int go(int input,Player me) {
        me.cards.add(input);
        long num=0;
        switch (me.DingQue){
            case WAN:
                num=me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).findFirst().get();
                    me.cards.remove(n);
                    return n;
                }
                else {
                    me.cards.remove((Integer) input);
                    return -1;
                }
            case TIAO:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
                if (num>0) {
                    Integer n=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).findFirst().get();
                    me.cards.remove(n);
                    return n;
                }
                else {
                    me.cards.remove((Integer) input);
                    return -1;
                }
            case TONG:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).findFirst().get();
                    me.cards.remove(n);
                    return n;
                }
                else {
                    me.cards.remove((Integer) input);
                    return -1;
                }
        }
        return 0;
    }
}
