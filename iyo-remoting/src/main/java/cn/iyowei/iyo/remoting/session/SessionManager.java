package cn.iyowei.iyo.remoting.session;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vick on 2016/8/15.
 */
public class SessionManager {

    private ConcurrentHashMap<String, IoSession> sessions = new ConcurrentHashMap<String, IoSession>();

    public IoSession getSession(String key) {
        return sessions.get(key);
    }


    public IoSession remove(String key) {
        return sessions.remove(key);
    }

    public IoSession putIfAbsent(String key, IoSession newSession) {
        return sessions.putIfAbsent(key, newSession);
    }
}
