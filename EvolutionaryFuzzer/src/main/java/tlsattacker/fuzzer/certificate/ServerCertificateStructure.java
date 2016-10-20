/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package tlsattacker.fuzzer.certificate;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * A wrapper which logically binds a server private key file and a server certificate file
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
public class ServerCertificateStructure implements Serializable {

    /**
     *
     */
    private File keyFile;

    /**
     *
     */
    private File certificateFile;

    /**
     *
     * @param keyFile
     * @param certificateFile
     */
    public ServerCertificateStructure(File keyFile, File certificateFile) {
	this.keyFile = keyFile;
	this.certificateFile = certificateFile;
    }

    /**
     *
     */
    public ServerCertificateStructure() {
	this.keyFile = null;
	this.certificateFile = null;
    }

    /**
     *
     * @return
     */
    public File getKeyFile() {
	return keyFile;
    }

    /**
     *
     * @param keyFile
     */
    public void setKeyFile(File keyFile) {
	this.keyFile = keyFile;
    }

    /**
     *
     * @return
     */
    public File getCertificateFile() {
	return certificateFile;
    }

    /**
     *
     * @param certificateFile
     */
    public void setCertificateFile(File certificateFile) {
	this.certificateFile = certificateFile;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ServerCertificateStructure other = (ServerCertificateStructure) obj;
	if (!Objects.equals(this.keyFile, other.keyFile)) {
	    return false;
	}
	return Objects.equals(this.certificateFile, other.certificateFile);
    }
    private static final Logger LOG = Logger.getLogger(ServerCertificateStructure.class.getName());

}
