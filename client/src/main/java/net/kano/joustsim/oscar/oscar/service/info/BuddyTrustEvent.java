/*
 *  Copyright (c) 2004, The Joust Project
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
 *  File created by keith @ Feb 20, 2004
 *
 */

package net.kano.joustsim.oscar.oscar.service.info;

import net.kano.joustsim.Screenname;
import net.kano.joustsim.trust.BuddyCertificateInfo;
import net.kano.joscar.common.DefensiveTools;

public class BuddyTrustEvent {
    private final BuddyTrustManager manager;
    private final Screenname buddy;
    private final BuddyCertificateInfo certInfo;

    protected BuddyTrustEvent(BuddyTrustManager manager, Screenname buddy,
            BuddyCertificateInfo certInfo) {
        DefensiveTools.checkNull(manager, "manager");
        DefensiveTools.checkNull(buddy, "buddy");

        this.manager = manager;
        this.buddy = buddy;
        this.certInfo = certInfo;
    }

    public final BuddyTrustManager getTrustManager() { return manager; }

    public final Screenname getBuddy() { return buddy; }

    public final BuddyCertificateInfo getCertInfo() { return certInfo; }

    public boolean isFor(Screenname buddy) { return getBuddy().equals(buddy); }
}
