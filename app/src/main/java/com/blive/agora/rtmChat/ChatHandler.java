package com.blive.agora.rtmChat;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmMessage;

/**
 * Created by sans on 29-04-2019.
 **/

public interface ChatHandler {

    void onLoginSuccess();

    void onLoginFailed(ErrorInfo errorInfo);

    void onChannelJoinSuccess();

    void onChannelJoinFailed(ErrorInfo errorCode);

    void onMessageReceived(final RtmMessage message, final RtmChannelMember fromMember);

    void onMemberJoined(RtmChannelMember rtmChannelMember);

    void onMemberLeft(RtmChannelMember rtmChannelMember);
}
