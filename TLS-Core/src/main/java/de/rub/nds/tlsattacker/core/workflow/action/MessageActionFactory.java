/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.connection.AliasedConnection;
import de.rub.nds.tlsattacker.core.protocol.TlsMessage;
import de.rub.nds.tlsattacker.core.workflow.action.executor.ActionOption;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import java.util.*;

public class MessageActionFactory {

    public static MessageAction createAction(Config tlsConfig, AliasedConnection connection,
        ConnectionEndType sendingConnectionEndType, TlsMessage... tlsMessages) {
        return createAction(tlsConfig, connection, sendingConnectionEndType,
            new ArrayList<>(Arrays.asList(tlsMessages)));
    }

    public static MessageAction createAction(Config tlsConfig, AliasedConnection connection,
        ConnectionEndType sendingConnectionEnd, List<TlsMessage> tlsMessages) {
        MessageAction action;
        if (connection.getLocalConnectionEndType() == sendingConnectionEnd) {
            action = new SendAction(tlsMessages);
        } else {
            action = new ReceiveAction(getFactoryReceiveActionOptions(tlsConfig), tlsMessages);
        }
        action.setConnectionAlias(connection.getAlias());
        return action;
    }

    public static AsciiAction createAsciiAction(AliasedConnection connection, ConnectionEndType sendingConnectionEnd,
        String message, String encoding) {
        AsciiAction action;
        if (connection.getLocalConnectionEndType() == sendingConnectionEnd) {
            action = new SendAsciiAction(message, encoding);
        } else {
            action = new GenericReceiveAsciiAction(encoding);
        }
        return action;
    }

    private static Set<ActionOption> getFactoryReceiveActionOptions(Config tlsConfig) {
        Set<ActionOption> globalOptions = new HashSet<>();
        if (tlsConfig.getMessageFactoryActionOptions().contains(ActionOption.CHECK_ONLY_EXPECTED)) {
            globalOptions.add(ActionOption.CHECK_ONLY_EXPECTED);
        }
        if (tlsConfig.getMessageFactoryActionOptions().contains(ActionOption.IGNORE_UNEXPECTED_WARNINGS)) {
            globalOptions.add(ActionOption.IGNORE_UNEXPECTED_WARNINGS);
        }

        return globalOptions;
    }

    private MessageActionFactory() {
    }
}
