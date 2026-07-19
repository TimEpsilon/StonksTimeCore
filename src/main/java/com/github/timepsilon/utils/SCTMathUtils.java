package com.github.timepsilon.utils;

import com.github.timepsilon.config.STCConfigServer;

public class SCTMathUtils {

    public static float characteristicNumber(float P) {
        return STCConfigServer.CONFIG.SCT_REDUCTION_CHARACTERISTIC_AMOUNT.get() / P;
    }

    public static float currentPrice(float P, int n) {
        float N = characteristicNumber(P);
        double q = STCConfigServer.CONFIG.SCT_REDUCTION_LIMIT_PROPORTION.get();
        return (float) (P * Math.pow(2, -n/N) * (1 - q) + q*P);
    }

    /**
     * Computes the full effective price taking into account price reduction
     * @param P - The base SCT value
     * @param n0 - The amount of transactions for that item before selling
     * @param n1 - The new updated amount of transactions. If N items are sold, n1 = n0 + N
     */
    public static float effectivePrice(float P, int n0, int n1) {

        float N = characteristicNumber(P);
        double q = STCConfigServer.CONFIG.SCT_REDUCTION_LIMIT_PROPORTION.get();
        int dn = n1 - n0;
        double r = Math.pow(2, -1/N);

        return (float) (P * (
                q * dn
                + (1-q)
                * Math.pow(r, n0)
                * (1 - Math.pow(r, dn))
                / (1 - r) ));
    }

    public static float summingEffectivePrice(float P, int n0, int n1) {
        float S = 0;
        for (int i = n0; i < n1; i++) {
            S += currentPrice(P, i);
        }
        return S;
    }
}
