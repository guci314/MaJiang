package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 摸牌行为。带有暗杠智能判断
 */
public class SmartMoPaiAction extends BasicMoPaiAction{
    @Override
    protected ActionResult anGang(int input, Player me) {
        boolean b= AIUtil.gangAI(me.cards,gui,input,0);
        if (b){
            return super.anGang(input,me);
        }
        else {
            return new ActionResult(ResultCode.NoAction,null);
        }
    }

    @Override
    protected ActionResult mingGang(int input, Player me) {
        return super.mingGang(input, me);
    }
}
