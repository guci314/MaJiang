package com.guci.MaJiangGame.RenPao;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;

import java.util.ArrayList;
import java.util.List;

public class RenPaoAction_ByXiaJiaoZhangShu extends BasicDianPaoHuAction {
    public int threshold=4;
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status== Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        //检查是否已定缺
        if (!me.hasQue()) return new ActionResult(ResultCode.NoAction,null);
        //当前牌是否是定缺
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        List<Integer> tmp = new ArrayList<>(me.cards);
        tmp.add(input);
        if (HuUtil.isHuExtra(tmp, gui, 0)){
            //检测是否忍炮
            if (me.matrix.players.stream().filter(x->x.status==Status.Playing).count() != 4)
                return new ActionResult(ResultCode.NoAction,null);
            if (me.matrix.cards.size()<28)
                return new ActionResult(ResultCode.NoAction,null);
            List<Integer> ting= HuUtil.isTingExtra(me.cards,gui);
            int total=ting.size()*4;
            for (Integer card:ting){
                long x=me.cards.stream().filter(x1->x1==card).count();
                total= (int) (total-x);
                x=me.matrix.cardsOnTable.stream().filter(x1->x1==card).count();
                total= (int) (total-x);
                for(Player p:me.matrix.players){
                    x=p.cardsOnTable.stream().filter(x1->x1==card).count();
                    total= (int) (total-x);
                }
            }
            if (total>=threshold) return new ActionResult(ResultCode.NoAction,null);
            return new ActionResult(ResultCode.DianPaoHu,input);
        }
        else return new ActionResult(ResultCode.NoAction,null);
    }
}
