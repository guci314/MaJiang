package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IsolatingMoPaiAction implements MoPaiAction{
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        if (me.keZuoQingYiShe() != null) return new ActionResult(ResultCode.NoAction,null);
        me.cards.add(input);
//        if (HuUtil.isHuExtra(me.cards, gui, 0)){
//            ActionResult r=new ActionResult(ResultCode.ZiMo,input);
//            r.from=me.matrix;
//            r.to=me;
//            me.cards.remove((Integer) input);
//            return r;
//        }

        //如果能杠,则无行为
        me.cards.remove((Integer) input);
        if (me.keGang(input)){
            return new ActionResult(ResultCode.NoAction,null);
        }
        me.cards.add((Integer) input);

        //查找幺九孤张
        for (Integer i : me.cards){
            if (Collections.frequency(me.cards,(Integer)i)>1) continue;
            if (i== MaJiangDef.WAN1){
                if (!me.cards.contains((Integer) MaJiangDef.WAN2) && !me.cards.contains((Integer) MaJiangDef.WAN3))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.WAN9){
                if (!me.cards.contains((Integer) MaJiangDef.WAN7) && !me.cards.contains((Integer) MaJiangDef.WAN8))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TIAO1){
                if (!me.cards.contains((Integer) MaJiangDef.TIAO2) && !me.cards.contains((Integer) MaJiangDef.TIAO3))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TIAO9){
                if (!me.cards.contains((Integer) MaJiangDef.TIAO7) && !me.cards.contains((Integer) MaJiangDef.TIAO8))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TONG1){
                if (!me.cards.contains((Integer) MaJiangDef.TONG2) && !me.cards.contains((Integer) MaJiangDef.TONG3))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TONG9){
                if (!me.cards.contains((Integer) MaJiangDef.TONG7) && !me.cards.contains((Integer) MaJiangDef.TONG8))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
        }
        //查找2,8孤张
        for(Integer i : me.cards){
            if (Collections.frequency(me.cards,(Integer)i)>1) continue;
            if (i== MaJiangDef.WAN2){
                if (!me.cards.contains((Integer) MaJiangDef.WAN1)
                        && !me.cards.contains((Integer) MaJiangDef.WAN3)
                        && !me.cards.contains((Integer) MaJiangDef.WAN4))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.WAN8){
                if (!me.cards.contains((Integer) MaJiangDef.WAN9)
                        && !me.cards.contains((Integer) MaJiangDef.WAN7)
                        && !me.cards.contains((Integer) MaJiangDef.WAN6))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TIAO2){
                if (!me.cards.contains((Integer) MaJiangDef.TIAO1)
                        && !me.cards.contains((Integer) MaJiangDef.TIAO3)
                        && !me.cards.contains((Integer) MaJiangDef.TIAO4))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TIAO8){
                if (!me.cards.contains((Integer) MaJiangDef.TIAO9)
                        && !me.cards.contains((Integer) MaJiangDef.TIAO7)
                        && !me.cards.contains((Integer) MaJiangDef.TIAO6))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TONG2){
                if (!me.cards.contains((Integer) MaJiangDef.TONG1)
                        && !me.cards.contains((Integer) MaJiangDef.TONG3)
                        && !me.cards.contains((Integer) MaJiangDef.TONG4))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
            if (i== MaJiangDef.TONG8){
                if (!me.cards.contains((Integer) MaJiangDef.TONG9)
                        && !me.cards.contains((Integer) MaJiangDef.TONG7)
                        && !me.cards.contains((Integer) MaJiangDef.TONG6))
                {
                    me.cards.remove((Integer) i);
                    ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                    r.to=me;
                    r.from=me.matrix;
                    r.in=input;
                    return r;
                }
            }
        }
        //查找其它孤张
        for (Integer i :me.cards){
            if (Collections.frequency(me.cards,(Integer)i)>1) continue;
            if (!me.cards.contains(i+1) && !me.cards.contains(i+2) && !me.cards.contains(i-1) && !me.cards.contains(i-2))
            {
                me.cards.remove((Integer) i);
                ActionResult r=new ActionResult(ResultCode.ChuPai,(Integer) i);
                r.to=me;
                r.from=me.matrix;
                r.in=input;
                return r;
            }
        }
        me.cards.remove((Integer) input);
        return new ActionResult(ResultCode.NoAction,null);
    }
}
