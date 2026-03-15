package com.chriso.financetransaction.dto;

import java.util.List;

public class DashboardResponse {

    public static class Totals {
        public double monthlyTotal;
        public double titheTotal;
        public double offeringTotal;
        public double donationTotal;
    }

    public static class MonthlyData {
        public List<Double> tithe;
        public List<Double> offering;
        public List<Double> donation;
        public List<Double> monthlyIncome;
    }

    public static class PaymentMethod {
        public String method;
        public double total;
    }

    public Totals totals;
    public MonthlyData monthlyData;
    public List<PaymentMethod> paymentMethods;
}
