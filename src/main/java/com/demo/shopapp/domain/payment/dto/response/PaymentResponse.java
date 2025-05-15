package com.demo.shopapp.domain.payment.dto.response;


public class PaymentResponse {
    private String paymentId;
    private String approvalUrl;

    public PaymentResponse(String paymentId, String approvalUrl) {
        this.paymentId = paymentId;
        this.approvalUrl = approvalUrl;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getApprovalUrl() {
        return approvalUrl;
    }
}