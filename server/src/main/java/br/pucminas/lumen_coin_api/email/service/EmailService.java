package br.pucminas.lumen_coin_api.email.service;

public interface EmailService {

    void sendWelcome(String to, String name);

    void sendTeacherWelcome(String to, String name, String generatedPassword);

    void sendPasswordReset(String to, String name, String resetLink, Integer expirationHours);

    void sendCoinSent(String to, String teacherName, String studentName, int amount, String message);

    void sendCoinReceived(String to, String studentName, String teacherName, int amount, String message);

    void sendBenefitRedemptionConfirmationToStudent(String to, String studentName, String benefitName);

    void sendBenefitRedemptionNotificationToCompany(String to, String companyName, String studentName, String benefitName, String couponCode);

    void sendBenefitRedemptionApprovedToStudent(String to, String studentName, String benefitName, String usageNotes);

    void sendBenefitRedemptionDeniedToStudent(String to, String studentName, String benefitName, String denialReason);
}
