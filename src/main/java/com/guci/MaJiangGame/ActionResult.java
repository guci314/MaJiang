package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActionResult {
    public ResultCode code;
    public Integer value;
    public Object from;
    public Player to;

    public ActionResult(ResultCode c,Integer v){
        code=c;
        value=v;
    }

    @Override
    public String toString() {
        String sfrom= from==null ? "null":from.toString();
        String sto= to==null ? "null":to.toString();
        String s= "ActionResult{" + "code=" + code+", value= %s,from=%s,to=%s}";
        s=String.format(s,value==null ? "null" : MaJiangDef.cardToString(value),sfrom,sto);
        return s;
    }
}
