package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicMoPaiAction implements MoPaiAction {
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        //是否自摸
        if (HuUtil.isHuExtra(me.cards, gui, 0)){
            ActionResult result=new ActionResult(ResultCode.ZiMo,null);
            result.from=me.matrix;
            result.to=me;
            return result;
        }
        //是否明杠. 默认算法始终明杠
        if (Collections.frequency(me.cardsOnTable,(Integer)input)==3){
            me.cards.remove((Integer) input);
            me.cardsOnTable.add((Integer) input);
            ActionResult result= new ActionResult(ResultCode.MingGang,input);
            result.from=me.matrix;
            result.to=me;
            return result;
        }
        //是否暗杠 默认算法始终暗杠
        if (Collections.frequency(me.cards,(Integer)input)==4){
            for (int i=0;i<4;i++){
                me.cards.remove((Integer) input);
                me.anGangCards.add((Integer) input);
            }
            ActionResult result= new ActionResult(ResultCode.AnGang,input);
            result.from=me.matrix;
            result.to=me;
            return result;
        }
        //打牌
        int out = AIUtil.outAI(me.cards, gui);
        me.cards.remove((Integer) out);
        ActionResult result=new ActionResult(ResultCode.ChuPai,(Integer)out);
        result.from=me;
        return result;
    }
}
