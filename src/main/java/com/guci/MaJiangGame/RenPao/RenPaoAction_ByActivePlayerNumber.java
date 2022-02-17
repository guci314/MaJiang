package com.guci.MaJiangGame.RenPao;

import com.guci.MaJiangGame.*;

/**
 * 忍炮策略
 */
public class RenPaoAction_ByActivePlayerNumber extends BasicDianPaoHuAction {
    public int threshold_active_player = 3;
    @Override
    public ActionResult go(int input, Player me) {
        long numberOfActivePlayer=me.matrix.players.stream().filter(p->p.status== Status.Playing).count();
        if (numberOfActivePlayer>=threshold_active_player) {
            return new ActionResult(ResultCode.NoAction,null);
        }
        return super.go(input,me);
    }
}
