package com.guci.MaJiangGame;

import java.util.List;

public interface Action {
    /**
     *
     * @param input
     * @return  -1表示未产生行为,0表示胡牌
     */
    public ActionResult go(int input, Player me);
}
