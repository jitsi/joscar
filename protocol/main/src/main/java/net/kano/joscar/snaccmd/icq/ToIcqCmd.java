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
 *  File created by jkohen @ Oct 13, 2003
 *
 */

package net.kano.joscar.snaccmd.icq;

import net.kano.joscar.flapcmd.SnacPacket;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A SNAC command used to send an ICQ command to the server.
 *
 * @snac.src client
 * @snac.cmd 0x15 0x02
 * @see FromIcqCmd
 */
public class ToIcqCmd extends AbstractIcqCmd {
    /**
     * Generates an incoming ICQ command from the given incoming SNAC packet.
     *
     * @param packet an incoming rendezvous ICBM packet
     */
    protected ToIcqCmd(SnacPacket packet) {
        super(CMD_TO_ICQ, packet);
    }

    /**
     * Creates a new outgoing ICQ command with the given properties.
     *
     * @param uin        an ICQ UIN as an integer value
     * @param type       the ICQ subtype for the command
     * @param id         the sequence ID for this command
     */
    protected ToIcqCmd(long uin, IcqType type, int id) {
        super(CMD_TO_ICQ, uin, type, id);
    }

    public void writeIcqData(OutputStream out) throws IOException { }
}
