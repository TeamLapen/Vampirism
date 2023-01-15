package de.teamlapen.lib.network;


public interface IMessage {

    interface IClientBoundMessage extends IMessage {
    }
    interface IServerBoundMessage extends IMessage {
    }
}
