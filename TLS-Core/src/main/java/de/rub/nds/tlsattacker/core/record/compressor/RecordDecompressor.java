/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.record.compressor;

import de.rub.nds.tlsattacker.core.constants.CompressionMethod;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.layer.context.RecordContext;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.record.compressor.compression.CompressionAlgorithm;

public class RecordDecompressor extends Decompressor<Record> {

    private CompressionAlgorithm algorithm;
    private ProtocolVersion version;

    public RecordDecompressor(RecordContext context) {
        version = context.getChooser().getSelectedProtocolVersion();
        if (version.isTLS13()) {
            setMethod(CompressionMethod.NULL);
        } else {
            setMethod(context.getChooser().getSelectedCompressionMethod());
        }
    }

    @Override
    public void decompress(Record record) {
        byte[] compressedBytes = record.getCleanProtocolMessageBytes().getValue();
        byte[] cleanBytes = algorithm.decompress(compressedBytes);
        record.setCleanProtocolMessageBytes(cleanBytes);
    }

    public void setMethod(CompressionMethod method) {
        AlgorithmFactory factory = new AlgorithmFactory();
        algorithm = factory.getAlgorithm(version, method);
    }

}
