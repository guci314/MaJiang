package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicMoPaiAction implements MoPaiAction {
    List<Integer> gui = new ArrayList<>();
    @Override
    public int go(int input, Player me) {
        me.cards.add(input);
        //是否胡牌
        if (HuUtil.isHuExtra(me.cards, gui, 0)){
            return 0;
        }
        // TODO: 2022/2/8 添加杠牌算法
        //是否明杠
        //是否暗杠
        //打牌
        int out = AIUtil.outAI(me.cards, gui);
        me.cards.remove((Integer) out);
        return out;
    }
}
