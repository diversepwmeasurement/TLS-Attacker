/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package tlsattacker.fuzzer.analyzer;

import tlsattacker.fuzzer.config.analyzer.RuleConfig;
import tlsattacker.fuzzer.config.EvolutionaryFuzzerConfig;
import tlsattacker.fuzzer.result.Result;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;

/**
 * A is a class that can be used to analyze TestVectors. 
 * It seperates the different things an operator might want to look for in a
 * TestVector into different Classes.
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
public abstract class Rule {

    /**
     *
     */
    private static final Logger LOG = Logger.getLogger(Rule.class.getName());

    /**
     *
     */
    protected File ruleFolder;

    /**
     *
     */
    protected final String configFileName;

    /**
     *
     */
    protected EvolutionaryFuzzerConfig evoConfig;

    /**
     *
     */
    private boolean isActive = true;

    /**
     *
     * @param evoConfig
     * @param configFileName
     */
    protected Rule(EvolutionaryFuzzerConfig evoConfig, String configFileName) {
	this.configFileName = configFileName;
	this.evoConfig = evoConfig;
    }

    /**
     *
     * @return
     */
    public File getRuleFolder() {
	return ruleFolder;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
	return isActive;
    }

    /**
     *
     * @return
     */
    public abstract RuleConfig getConfig();

    /**
     *
     * @param result
     * @return
     */
    public abstract boolean applies(Result result);

    /**
     *
     * @param result
     */
    public abstract void onApply(Result result);

    /**
     *
     * @param result
     */
    public abstract void onDecline(Result result);

    /**
     *
     * @return
     */
    public abstract String report();

    /**
     *
     * @param c
     */
    protected void writeConfig(RuleConfig c) {
	File f = new File(evoConfig.getAnalyzerConfigFolder() + configFileName);
	if (f.exists()) {
	    LOG.log(Level.SEVERE, "Config File already exists, not writing new Config:{0}", configFileName);
	} else {
	    JAXB.marshal(c, f);
	}
    }

    /**
     *
     */
    protected void prepareConfigOutputFolder() {
	ruleFolder = new File(evoConfig.getOutputFolder() + this.getConfig().getOutputFolder());
	if (evoConfig.isCleanStart()) {
	    if (ruleFolder.exists()) {
		for (File tempFile : ruleFolder.listFiles()) {
		    tempFile.delete();
		}
	    }
	}
	ruleFolder.mkdirs();
    }


}
