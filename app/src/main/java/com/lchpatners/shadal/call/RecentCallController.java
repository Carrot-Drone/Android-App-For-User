package com.lchpatners.shadal.call;

import java.util.ArrayList;

/**
 * Created by youngkim on 2015. 8. 22..
 */
public class RecentCallController {
    public static final String ORDER_BY_NAME = "name";
    public static final String ORDER_BY_CALL_RECENT = "call_recent";
    public static final String ORDER_BY_CALL_COUNT = "call_count";

    public ArrayList<RecentCall> getRecentCallList(String orderby) {
        ArrayList<RecentCall> recentCallList = null;
        return recentCallList;
    }
}
