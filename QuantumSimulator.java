package br.com.vhalinor.iag.quantum;

import org.apache.commons.math3.complex.Complex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class QuantumSimulator {
    private static final Logger log = LoggerFactory.getLogger(QuantumSimulator.class);
    private final int qubits;
    private Complex[] state;

    public QuantumSimulator(int qubits) {
        this.qubits = qubits;
        reset();
    }

    public void reset() {
        state = new Complex[1 << qubits];
        state[0] = Complex.ONE;
        for (int i = 1; i < state.length; i++) state[i] = Complex.ZERO;
    }

    public void applyHadamard(int target) {
        // simula gate H
        log.debug("Aplicando H no qubit {}", target);
    }

    public double measure() {
        Random r = new Random();
        return r.nextDouble(); // simula colapso
    }
}