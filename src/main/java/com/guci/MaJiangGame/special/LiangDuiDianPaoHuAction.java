package com.guci.MaJiangGame.special;

import com.guci.MaJiangGame.ActionResult;
import com.guci.MaJiangGame.BasicDianPaoHuAction;
import com.guci.MaJiangGame.Player;
import com.guci.MaJiangGame.ResultCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.guci.MaJiangGame.GameUtil.isLiangDui;

public class LiangDuiDianPaoHuAction extends BasicDianPaoHuAction {
    @Override
    public ActionResult go(int input, Player me) {
        if (!isLiangDui(me.cards)) return super.go(input, me);
        return new ActionResult(ResultCode.NoAction,null);
    }

}
