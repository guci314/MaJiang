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
            int numberOfKanPai=me.numberOfKanPai();
            if (numberOfKanPai>0) {
                int total=me.xiaJiaoZhangSu();
                if (total>=threshold_hupaizhangsu) return new ActionResult(ResultCode.NoAction,null);
            }
        }
        return super.go(input,me);
    }
}
