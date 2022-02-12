package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 碰杠行为
 */
public class BasicPengGangAction implements PengGangAction {
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        if (Collections.frequency(me.cards,(Integer)input)==3){
            return gang(input,me);
        }
        //如果还未定缺,直接碰或杠
        if (!me.hasQue()){
            if(Collections.frequency(me.cards,(Integer) input)==2){
                return peng(input,me);
            }
        }

        boolean r=AIUtil.pengAI(me.cards,gui,input,0);
        if (!r) return new ActionResult(ResultCode.NoAction,null);
        return peng(input, me);

    }

    private ActionResult peng(int input, Player me) {
        me.cardsOnTable.add(input);
        me.cardsOnTable.add(input);
        me.cardsOnTable.add(input);
        me.cards.remove((Integer) input);
        me.cards.remove((Integer) input);
        int rvalue= AIUtil.outAI(me.cards,gui);
        me.cards.remove((Integer) rvalue);
        ActionResult result=new ActionResult(ResultCode.Peng, (Integer) rvalue);
        result.from=me;
        return result;
    }

    private ActionResult gang(int input,Player me){
        for (int i=0;i<3;i++){
            me.cards.remove((Integer) input);
            me.cardsOnTable.add((Integer) input);
        }
        me.cardsOnTable.add((Integer) input);
        int pai=me.matrix.cards.remove(0);
        return me.mopai(pai);
    }
}
