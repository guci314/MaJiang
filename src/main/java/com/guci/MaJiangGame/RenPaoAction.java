package com.guci.MaJiangGame;

/**
 * 忍炮策略
 */
public class RenPaoAction extends BasicDianPaoHuAction{
    @Override
    public ActionResult go(int input, Player me) {
        return new ActionResult(ResultCode.NoAction,null);
    }
}
