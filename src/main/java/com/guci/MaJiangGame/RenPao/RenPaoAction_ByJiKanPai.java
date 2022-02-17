package com.guci.MaJiangGame.RenPao;

import com.guci.MaJiangGame.*;

import java.util.HashSet;

/**
 * 根据手上有几坎牌 决定是否忍炮
 */
public class RenPaoAction_ByJiKanPai extends BasicDianPaoHuAction {
    @Override
    public ActionResult go(int input, Player me) {
        long numberOfActivePlayer=me.matrix.players.stream().filter(p->p.status== Status.Playing).count();
        if (numberOfActivePlayer==4) {
            int numberOfKanPai=0;
            HashSet<Integer> cardsSet=new HashSet<>(me.cards);
            for (Integer c:cardsSet){
                long l=me.cards.stream().filter(x->x==c).count();
                if (l==3) {
                    if (me.matrix.cardsOnTable.stream().filter(x->x==c).count()==0) {
                        numberOfKanPai++;
                    }
                }
            }
            if (numberOfKanPai>0) {
                return new ActionResult(ResultCode.NoAction,null);
            }
            //if (me.matrix.cards.size()>20){
            //    return new ActionResult(ResultCode.NoAction,null);
            //}
        }
        return super.go(input,me);
    }
}
