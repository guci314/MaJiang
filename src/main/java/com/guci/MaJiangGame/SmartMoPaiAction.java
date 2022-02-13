package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;

public class SmartMoPaiAction extends BasicMoPaiAction{
    @Override
    ActionResult anGang(int input, Player me) {
        boolean b= AIUtil.gangAI(me.cards,gui,input,0);
        if (b){
            return anGang(input,me);
        }
        else {
            return new ActionResult(ResultCode.NoAction,null);
        }
    }

    @Override
    ActionResult mingGang(int input, Player me) {
        return super.mingGang(input, me);
    }
}
