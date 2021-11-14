//
// CONFIDENTIAL - FORD MOTOR COMPANY
//
// This is an unpublished work, which is a trade secret, created in
// 2021. Ford Motor Company owns all rights to this work and intends
// to maintain it in confidence to preserve its trade secret status.
// Ford Motor Company reserves the right to protect this work as an
// unpublished copyrighted work in the event of an inadvertent or
// deliberate unauthorized publication. Ford Motor Company also
// reserves its rights under the copyright laws to protect this work
// as a published work. Those having access to this work may not copy
// it, use it, or disclose the information contained in it without
// the written authorization of Ford Motor Company.
//

package com.mvvm.fwk.utils.eventbus;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Event bus achieved by LiveData.
 *
 * @author WuJia.
 * @version 1.0
 * @date 2020/10/31
 */
public class LiveDataBus {

    private static class Lazy {
        static LiveDataBus sLiveDataBus = new LiveDataBus();
    }

    public static LiveDataBus getInstance() {
        return Lazy.sLiveDataBus;
    }

    private final ConcurrentHashMap<String, StickyLiveData> mHashMap = new ConcurrentHashMap<>();

    /**
     * Input event name.
     *
     * @param channelName event name
     * @return see {@link StickyLiveData}
     */
    public StickyLiveData getChannel(String channelName) {
        StickyLiveData liveData = mHashMap.get(channelName);
        if (liveData == null) {
            liveData = new StickyLiveData(channelName);
            liveData.setDestroyListener(new StickyLiveData.IDestroyListener() {
                @Override
                public void onLiveDataDestroy(String channelName) {
                    mHashMap.remove(channelName);
                }
            });
            mHashMap.put(channelName, liveData);
        }
        return liveData;
    }
}
