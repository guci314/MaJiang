package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;

public class DingQueMoPaiAction implements MoPaiAction{
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        //如果能杠,则无行为
        me.cards.remove((Integer) input);
        if (me.keGang(input)){
            return new ActionResult(ResultCode.NoAction,null);
        }
        me.cards.add((Integer) input);

        long num=0;
        switch (me.DingQue){
            case WAN:
                num=me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).findFirst().get();
                    me.cards.remove(n);
                    return new ActionResult(ResultCode.ChuPai,(Integer) n);
                }
                break;
            case TIAO:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
                if (num>0) {
                    Integer n=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).findFirst().get();
                    me.cards.remove(n);
                    return new ActionResult(ResultCode.ChuPai,(Integer) n);
                }
                break;
            case TONG:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).findFirst().get();
                    me.cards.remove(n);
                    return new ActionResult(ResultCode.ChuPai,(Integer) n);
                }
                break;
        }
        me.cards.remove((Integer) input);
        return new ActionResult(ResultCode.NoAction,null);
    }
}
