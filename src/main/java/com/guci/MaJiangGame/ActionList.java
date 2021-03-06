package com.guci.MaJiangGame;

import java.util.ArrayList;

// TODO: 2022/3/3 重构  添加NoDecision
public class ActionList<M extends Action> extends ArrayList implements Action {

    @Override
    public ActionResult go(int input, Player me) {
        for(Object o : this){
            M a=(M) o;
            ActionResult r=a.go(input,me);
            if (r.code != ResultCode.NoAction) return r;
        }
        return new ActionResult(ResultCode.NoAction,null);
    }
}
