package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 碰杠行为
 */
public class BasicPengGangAction implements PengGangAction {
    List<Integer> gui = new ArrayList<>();
    //protected HuaShe qingYiShe;
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        //qingYiShe=me.zuoQingYiShe();
        if (Collections.frequency(me.cards,(Integer)input)==3){
            return gang(input,me);
        }
        //如果还未定缺,直接碰
        return processPeng(input, me);

    }

    protected ActionResult processPeng(int input, Player me) {

        //如果未定缺,直接碰
        if (!me.hasQue()){
            if(Collections.frequency(me.cards,(Integer) input)==2){
                return peng(input, me);
            }
        }

        boolean r=AIUtil.pengAI(me.cards,gui, input,0);
        if (!r) return new ActionResult(ResultCode.NoAction, null);
        return peng(input, me);
    }

    ActionResult peng(int input, Player me) {
        me.cardsOnTable.add(input);
        me.cardsOnTable.add(input);
        me.cardsOnTable.add(input);
        me.cards.remove((Integer) input);
        me.cards.remove((Integer) input);
        int rvalue= AIUtil.outAI(me.cards,gui);
        me.cards.remove((Integer) rvalue);
        ActionResult result=new ActionResult(ResultCode.Peng, (Integer) rvalue);
        result.from=me;
//        if (me.matrix.showDetail){
//            System.out.println("碰  Player"+me.id+" "+ MaJiangDef.cardToString(result.value));
//        }
        return result;
    }

    ActionResult gang(int input,Player me){
        if (me.matrix.cards.size()==0) return new ActionResult(ResultCode.NoAction,null);
        for (int i=0;i<3;i++){
            me.cards.remove((Integer) input);
            me.cardsOnTable.add((Integer) input);
        }
        me.cardsOnTable.add((Integer) input);
        ActionResult r=new ActionResult(ResultCode.PengGang,(Integer) input);
        r.to=me;
        r.from=me.matrix.currentPlayer;
        //System.out.println("碰杠 player"+me.id+" "+MaJiangDef.cardToString(r.value));
        me.matrix.settle(r);
        int pai=me.matrix.cards.remove(0);
        return me.mopai(pai);
    }
}
