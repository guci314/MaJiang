package com.guci.MaJiangGame;

import java.util.ArrayList;
import java.util.List;

public class ActionList<M extends Action> extends ArrayList implements Action {

    @Override
    public int go(int input, Player me) {
        for(Object o : this){
            M a=(M) o;
            int r=a.go(input,me);
            if (r != -1) return r;
        }
        return -1;
    }
}
