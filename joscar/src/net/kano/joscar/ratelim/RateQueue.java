/*
 *  Copyright (c) 2003, The Joust Project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions 
 *  are met:
 *
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 *  - Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution. 
 *  - Neither the name of the Joust Project nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 *  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  File created by keith @ May 25, 2003
 *
 */

package net.kano.joscar.ratelim;

import net.kano.joscar.snaccmd.conn.RateClassInfo;
import net.kano.joscar.snaccmd.conn.RateChange;
import net.kano.joscar.DefensiveTools;
import net.kano.joscar.snac.SnacRequest;

import java.util.LinkedList;

public class RateQueue {
    private final RateClassSet parentSet;

    private RateClassInfo rateInfo;
    private LinkedList queue = new LinkedList();
    private long last = -1;
    private long runningAvg;
    private boolean limited = false;

    public RateQueue(RateClassSet parentSet, RateClassInfo rateInfo) {
        DefensiveTools.checkNull(parentSet, "parentSet");
        DefensiveTools.checkNull(rateInfo, "rateInfo");

        this.parentSet = parentSet;
        this.rateInfo = rateInfo;
        this.runningAvg = rateInfo.getMax();
    }

    public RateClassSet getParentSet() { return parentSet; }

    public synchronized RateClassInfo getRateInfo() { return rateInfo; }

    public synchronized void setRateInfo(RateClassInfo rateInfo) {
        DefensiveTools.checkNull(rateInfo, "rateInfo");

        this.rateInfo = rateInfo;
        runningAvg = rateInfo.getCurrentAvg();
    }

    public synchronized int getQueueSize() { return queue.size(); }

    public synchronized long getRunningAvg() { return runningAvg; }

    public synchronized boolean isLimited() {
        updateLimitedStatus();

        return limited;
    }

    private synchronized void updateLimitedStatus() {
        if (limited) {
            if (computeCurrentAvg() > rateInfo.getClearAvg()) {
                System.out.println("I guess we're not limited anymore");
                limited = false;
            }
        }
    }

    public synchronized long getOptimalWaitTime(int errorMargin) {
        long minAvg;
        if (isLimited()) minAvg = rateInfo.getClearAvg();
        else minAvg = rateInfo.getDisconnectAvg();

        return getWaitTime(minAvg + errorMargin);
    }

    public synchronized long getWaitTime(long minAvg) {
        if (last == -1) return 0;

        long winSize = rateInfo.getWindowSize();
        long sinceLast = System.currentTimeMillis() - last;

        long minLastDiff = (winSize * minAvg) - (runningAvg  * (winSize - 1));
        long toWait = minLastDiff - sinceLast;

        System.out.println("should be waiting " + toWait + "ms...");

        return Math.max(toWait, 0);
    }

    public synchronized void enqueue(SnacRequest req) {
        DefensiveTools.checkNull(req, "req");

        System.out.println("enqueuing within ratequeue...");

        queue.add(req);
    }

    public synchronized boolean hasRequests() {
        return !queue.isEmpty();
    }

    public synchronized SnacRequest dequeue() {
        if (queue.isEmpty()) return null;

        System.out.println("dequeueing from ratequeue..");

        long cur = System.currentTimeMillis();
        if (last != -1) {
            runningAvg = computeCurrentAvg(cur);
        }
        last = cur;

        return (SnacRequest) queue.removeFirst();
    }

    private synchronized long computeCurrentAvg(long currentTime) {
        long diff = currentTime - last;
        long winSize = rateInfo.getWindowSize();
        long max = rateInfo.getMax();
        return Math.min(max, (runningAvg * (winSize - 1) + diff) / winSize);
    }

    private synchronized long computeCurrentAvg() {
        return computeCurrentAvg(System.currentTimeMillis());
    }

    public synchronized void clear() {
        queue.clear();
    }

    public synchronized void setChangeCode(int changeCode) {
        if (changeCode == RateChange.CODE_LIMITED) {
            System.out.println("limited!");
            limited = true;
        } else if (changeCode == RateChange.CODE_LIMIT_CLEARED) {
            limited = false;
        }
    }
}