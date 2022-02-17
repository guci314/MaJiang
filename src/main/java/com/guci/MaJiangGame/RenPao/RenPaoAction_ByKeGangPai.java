package com.guci.MaJiangGame.RenPao;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;

import java.util.HashSet;
import java.util.List;

/**
 * 根据手上有几坎牌 决定是否忍炮
 */
public class RenPaoAction_ByKeGangPai extends BasicDianPaoHuAction {
    public int threshold_hupaizhangsu=1;
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
                if (total>=threshold_hupaizhangsu) return new ActionResult(ResultCode.NoAction,null);
            }
        }
        return super.go(input,me);
    }
}
