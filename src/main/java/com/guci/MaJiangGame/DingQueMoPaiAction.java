package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.List;

public class DingQueMoPaiAction implements MoPaiAction{
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        if (me.hasQue()){
            me.cards.remove((Integer) input);
            return new ActionResult(ResultCode.NoAction,null);
        }
        //如果能杠,则无行为
        me.cards.remove((Integer) input);
        if (me.keGang(input)){
            return new ActionResult(ResultCode.NoAction,null);
        }
        me.cards.add((Integer) input);

        long num=0;
        switch (me.dingQue){
            case WAN:
                num=me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).findFirst().get();
                    me.cards.remove(n);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) n);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
                break;
            case TIAO:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
                if (num>0) {
                    Integer n=me.cards.stream().filter(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).findFirst().get();
                    me.cards.remove(n);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) n);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
                break;
            case TONG:
                num=me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
                if (num>0) {
                    Integer n= me.cards.stream().filter(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).findFirst().get();
                    me.cards.remove(n);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) n);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
                break;
        }
        me.cards.remove((Integer) input);
        return new ActionResult(ResultCode.NoAction,null);
    }
}
