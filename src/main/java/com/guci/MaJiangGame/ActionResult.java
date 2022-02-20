package com.guci.MaJiangGame;

import com.alibaba.fastjson.JSONObject;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActionResult {
    public ResultCode code;
    public Integer out;
    public Integer in;
    public Object from;
    public Player to;

    public ActionResult(ResultCode c,Integer v){
        code=c;
        out =v;
    }

    int getId(Object o){
        if (o==null) return 0;
        if (o instanceof Player)
        {
            return ((Player)o).id;
        }else {
            return 0;
        }
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("code",code);
        object.put("out",out==null ? "null":MaJiangDef.cardToString(out));
        object.put("in",in==null ? "null": MaJiangDef.cardToString(in));
        object.put("from",getId(from));
        object.put("to",getId(to));
        return object.toJSONString();
//        String sfrom= from==null ? "null":from.toString();
//        String sto= to==null ? "null":to.toString();
//        String s= "ActionResult{" + "code=" + code+",in=%s, out= %s,from=%s,to=%s}";
//        s=String.format(s, in ==null ? "null" : MaJiangDef.cardToString(in),out ==null ? "null" : MaJiangDef.cardToString(out),sfrom,sto);
//        return s;
    }
}
