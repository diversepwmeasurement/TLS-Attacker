/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.tlsattacker.core.protocol.parser.AlertParser;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.CompressionMethod;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.modifiablevariable.util.ArrayConverter;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
@RunWith(Parameterized.class)
public class AlertParserTest {

    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] { { new byte[] { 1, 2 }, 0, new byte[] { 1, 2 }, (byte) 1, (byte) 2 },
                { new byte[] { 4, 3, 1, 2 }, 0, new byte[] { 4, 3 }, (byte) 4, (byte) 3 } });
    }

    private byte[] message;
    private int start;
    private byte[] expectedPart;
    private byte level;
    private byte description;

    public AlertParserTest(byte[] message, int start, byte[] expectedPart, byte level, byte description) {
        this.message = message;
        this.start = start;
        this.expectedPart = expectedPart;
        this.level = level;
        this.description = description;
    }

    /**
     * Test of parse method, of class AlertParser.
     */
    @Test
    public void testParse() {
        AlertParser parser = new AlertParser(0, message, ProtocolVersion.TLS12);
        AlertMessage alert = parser.parse();
        assertArrayEquals(expectedPart, alert.getCompleteResultingMessage().getValue());
        assertTrue(level == alert.getLevel().getValue());
        assertTrue(description == alert.getDescription().getValue());
    }
}