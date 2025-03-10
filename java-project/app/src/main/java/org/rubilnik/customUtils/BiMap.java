package org.rubilnik.customUtils;

import java.util.HashMap;
import java.util.Map;

public class BiMap<T1,T2> {
    Map<T1,T2> map1 = new HashMap<>();
    Map<T2,T1> map2 = new HashMap<>();

    public void put(T1 val1, T2 val2) {
        map1.put(val1, val2);
        map2.put(val2, val1);
    }
    public T2 get1(T1 val) { 
        return map1.get(val); 
    }
    public T1 get2(T2 val) { 
        return map2.get(val); 
    }

    public T2 remove1(T1 val){
        T2 res = map1.remove(val);
        map2.remove(res);
        return res;
    }
    public T1 remove2(T2 val){
        T1 res = map2.remove(val);
        map1.remove(res);
        return res;
    }
}
