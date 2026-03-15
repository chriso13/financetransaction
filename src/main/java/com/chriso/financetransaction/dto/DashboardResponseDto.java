package com.chriso.financetransaction.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponseDto {

    public static class Totals {
        public double monthlyTotal = 0;
        public double titheTotal = 0;
        public double offeringTotal = 0;
        public double donationTotal = 0;
    }

    public static class MonthlyData {
        public List<Double> tithe = new ArrayList<>();
        public List<Double> offering = new ArrayList<>();
        public List<Double> donation = new ArrayList<>();
        public List<Double> monthlyIncome = new ArrayList<>();
    }

    public Totals totals = new Totals();
    public MonthlyData monthlyData = new MonthlyData();
    public List<com.chriso.financetransaction.dto.PaymentMethodSummary> paymentMethods = new ArrayList<>();
}