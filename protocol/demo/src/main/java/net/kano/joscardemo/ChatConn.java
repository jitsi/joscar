/*
 *  Copyright (c) 2002-2003, The Joust Project
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
 *  File created by keith @ Mar 26, 2003
 *
 */

package net.kano.joscardemo;

import net.kano.joscar.common.ByteBlock;
import net.kano.joscar.flap.ClientFlapConn;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.net.ClientConnEvent;
import net.kano.joscar.net.ConnDescriptor;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snaccmd.FullRoomInfo;
import net.kano.joscar.snaccmd.FullUserInfo;
import net.kano.joscar.snaccmd.chat.ChatMsg;
import net.kano.joscar.snaccmd.chat.RecvChatMsgIcbm;
import net.kano.joscar.snaccmd.chat.SendChatMsgIcbm;
import net.kano.joscar.snaccmd.chat.UsersJoinedCmd;
import net.kano.joscar.snaccmd.chat.UsersLeftCmd;
import net.kano.joscardemo.security.SecureSession;
import net.kano.joscardemo.security.SecureSessionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.io.Serializable;

public class ChatConn extends ServiceConn {
    protected FullRoomInfo roomInfo;

    protected List<ChatConnListener> listeners = new ArrayList<ChatConnListener>();

    protected boolean joined = false;

    protected Set<FullUserInfo> members = new HashSet<FullUserInfo>();
    private SecureSession secureSession;

    public ChatConn(ConnDescriptor cd, JoscarTester tester,
            ByteBlock cookie, FullRoomInfo roomInfo) {
        super(cd, tester, cookie, 0x000e);
        this.roomInfo = roomInfo;
        this.secureSession = tester.getSecureSession();
    }

    public void sendMsg(String msg) {
        request(new SendChatMsgIcbm(new ChatMsg(msg)));
    }

    public FullRoomInfo getRoomInfo() { return roomInfo; }

    public String getRoomName() {
        return roomInfo.getRoomName();
    }

    public FullUserInfo[] getMembers() {
        return members.toArray(new FullUserInfo[0]);
    }

    protected void handleStateChange(ClientConnEvent e) {
        super.handleStateChange(e);

        State state = e.getNewState();

        if (state == ClientFlapConn.STATE_CONNECTED) {
            fireConnectedEvent();

        } else if (state == ClientFlapConn.STATE_FAILED) {
            fireConnFailedEvent(e.getReason());

        } else if (state == ClientFlapConn.STATE_NOT_CONNECTED) {
            if (joined) fireLeftEvent(e.getReason());
            else fireConnFailedEvent(e.getReason());
        }
    }

    protected void handleSnacPacket(SnacPacketEvent e) {
        super.handleSnacPacket(e);
        
        SnacCommand cmd = e.getSnacCommand();

        if (cmd instanceof UsersJoinedCmd) {
            UsersJoinedCmd ujc = (UsersJoinedCmd) cmd;

            members.addAll(ujc.getUsers());

            if (!joined) {
                fireJoinedEvent(ujc.getUsers());
                joined = true;
            } else {
                fireUsersJoinedEvent(ujc.getUsers());
            }
        } else if (cmd instanceof UsersLeftCmd) {
            UsersLeftCmd ulc = (UsersLeftCmd) cmd;

            members.removeAll(ulc.getUsers());

            fireUsersLeftEvent(ulc.getUsers());
        } else if (cmd instanceof RecvChatMsgIcbm) {
            RecvChatMsgIcbm icbm = (RecvChatMsgIcbm) cmd;

            fireMsgEvent(icbm.getSenderInfo(), icbm.getMessage());
        }
    }

    public void addChatListener(ChatConnListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeChatListener(ChatConnListener l) {
        listeners.remove(l);
    }

    protected void fireConnectedEvent() {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.connected(this);
        }
    }

    protected void fireConnFailedEvent(Serializable reason) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.connFailed(this, reason);
        }
    }
    protected void fireJoinedEvent(List<FullUserInfo> members) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.joined(this, members);
        }
    }
    protected void fireLeftEvent(Serializable reason) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.left(this, reason);
        }
    }
    protected void fireUsersJoinedEvent(List<FullUserInfo> members) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.usersJoined(this, members);
        }
    }
    protected void fireUsersLeftEvent(List<FullUserInfo> members) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.usersLeft(this, members);
        }
    }
    protected void fireMsgEvent(FullUserInfo sender, ChatMsg msg) {
        for (Object listener : listeners) {
            ChatConnListener l = (ChatConnListener) listener;

            l.gotMsg(this, sender, msg);
        }
    }

    public String toString() {
        return "ChatConn: " + roomInfo.getRoomName();
    }

    public void sendEncMsg(String msg) {
        byte[] encrypted;
        try {
            encrypted = secureSession.encryptChatMsg(this.getRoomName(), msg);

            request(new SendChatMsgIcbm(
                    new ChatMsg("application/pkcs7-mime", "binary", "us-ascii",
                            ByteBlock.wrap(encrypted), Locale.getDefault())));
            System.out.println("sent encrypted msg..");
        } catch (SecureSessionException e) {
            e.printStackTrace();
        }
    }

}
