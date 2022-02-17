package com.guci.MaJiangGame.RenPao;

import com.guci.MaJiangGame.*;

public class RenPaoAction_ByCardsNumber extends BasicDianPaoHuAction {
    public int threshold_active_player = 4;
    public int threshold_cards_number = 27;

    @Override
    public ActionResult go(int input, Player me) {
//        if (me.matrix.cards.size() > threshold_cards_number) {
//            return new ActionResult(ResultCode.NoAction, null);
//        } else {
//            return super.go(input, me);
//        }
        long numberOfActivePlayer=me.matrix.players.stream().filter(p->p.status== Status.Playing).count();
        if (numberOfActivePlayer>=threshold_active_player) {
            if (me.matrix.cards.size()>=threshold_cards_number){
                return new ActionResult(ResultCode.NoAction,null);
            }
        }
        return super.go(input,me);
    }
}
