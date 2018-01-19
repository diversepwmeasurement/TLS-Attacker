/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.forensics.analyzer;

import de.rub.nds.tlsattacker.core.record.AbstractRecord;
import de.rub.nds.tlsattacker.core.record.layer.TlsRecordLayer;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.action.GenericReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.MessageAction;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendingAction;
import de.rub.nds.tlsattacker.core.workflow.action.TlsAction;
import de.rub.nds.tlsattacker.core.workflow.action.executor.MessageActionResult;
import de.rub.nds.tlsattacker.core.workflow.action.executor.ReceiveMessageHelper;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import de.rub.nds.tlsattacker.transport.stream.StreamTransportHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;

/**
 * This class tries to reconstruct WorkflowTraces
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class ForensicAnalyzer {

    protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ForensicAnalyzer.class
            .getName());

    public ForensicAnalyzer() {
    }

    public WorkflowTrace getRealWorkflowTrace(WorkflowTrace executedWorkflow) throws IOException {
        WorkflowTrace reconstructed = new WorkflowTrace();
        int tracePosition = 0; // The action we are currently looking at.
        State state = new State(); // initialise an empty state
        TlsContext context = state.getTlsContext();
        context.setRecordLayer(new TlsRecordLayer(context));

        if (!isSupported(executedWorkflow)) {
            return null;
        }
        while (tracePosition < executedWorkflow.getTlsActions().size()) {
            boolean sending;
            List<TlsAction> joinedActions;
            if (executedWorkflow.getTlsActions().get(tracePosition) instanceof SendingAction) {
                joinedActions = joinSendActions(tracePosition, executedWorkflow);
                sending = true;
            } else {
                joinedActions = joinReceiveActions(tracePosition, executedWorkflow);
                sending = false;
            }
            byte[] joinedRecordBytes = joinRecordBytes(joinedActions);

            context.setTransportHandler(new StreamTransportHandler(1, ConnectionEndType.CLIENT,
                    new ByteArrayInputStream(joinedRecordBytes), new ByteArrayOutputStream()));
            context.getTransportHandler().initialize();
            ReceiveMessageHelper helper = new ReceiveMessageHelper();
            MessageActionResult parsedMessageResult = helper.receiveMessages(context);
            tracePosition += joinedActions.size();
            if (sending) {
                SendAction reconstructedAction = new SendAction(parsedMessageResult.getMessageList());
                reconstructedAction.setRecords(parsedMessageResult.getRecordList());
                reconstructed.addTlsAction(reconstructedAction);
            } else {
                ReceiveAction reconstructedAction = new ReceiveAction(parsedMessageResult.getMessageList());
                reconstructedAction.setRecords(parsedMessageResult.getRecordList());
                reconstructed.addTlsAction(reconstructedAction);
            }
        }

        return reconstructed;
    }

    public byte[] joinRecordBytes(List<TlsAction> sendActions) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (TlsAction action : sendActions) {
            if (action instanceof MessageAction) {
                MessageAction msgAction = (MessageAction) action;
                List<AbstractRecord> records = msgAction.getRecords();
                for (AbstractRecord record : records) {
                    try {
                        stream.write(record.getCompleteRecordBytes().getValue());
                    } catch (IOException ex) {
                        LOGGER.warn("Could not write to ByteArrayOutputStream.", ex);
                    }
                }
            } else {
                throw new IllegalArgumentException("List contains non MessageActions");
            }
        }
        return stream.toByteArray();
    }

    public boolean isSupported(WorkflowTrace trace) {
        for (TlsAction action : trace.getTlsActions()) {
            if (!(action instanceof SendAction || action instanceof ReceiveAction || action instanceof GenericReceiveAction)) {
                return false;
            }
        }
        return true;
    }

    public List<TlsAction> joinSendActions(int position, WorkflowTrace trace) {
        List<TlsAction> joinedActions = new LinkedList<>();
        for (int i = position; i < trace.getTlsActions().size(); i++) {
            TlsAction action = trace.getTlsActions().get(i);
            if (action instanceof SendAction) {
                joinedActions.add(action);
            } else {
                return joinedActions;
            }
        }
        return joinedActions;
    }

    public List<TlsAction> joinReceiveActions(int position, WorkflowTrace trace) {
        List<TlsAction> joinedActions = new LinkedList<>();
        for (int i = position; i < trace.getTlsActions().size(); i++) {
            TlsAction action = trace.getTlsActions().get(i);
            if (action instanceof ReceiveAction || action instanceof GenericReceiveAction) {
                joinedActions.add(action);
            } else {
                return joinedActions;
            }
        }
        return joinedActions;
    }
}
