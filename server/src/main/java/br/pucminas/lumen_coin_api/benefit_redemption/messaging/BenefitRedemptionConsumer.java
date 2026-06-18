package br.pucminas.lumen_coin_api.benefit_redemption.messaging;

import br.pucminas.lumen_coin_api.common.util.QrCodeGenerator;
import br.pucminas.lumen_coin_api.config.RabbitMQConfig;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BenefitRedemptionConsumer {

    private final EmailService emailService;
    private final QrCodeGenerator qrCodeGenerator;

    @RabbitListener(queues = RabbitMQConfig.BENEFIT_REDEMPTION_QUEUE)
    public void process(BenefitRedemptionMessage message) {
        log.info("Processing benefit redemption for student {} - benefit '{}'",
                message.studentEmail(), message.benefitName());

        byte[] qrCodePng = qrCodeGenerator.generatePng(message.couponCode(), 300);

        emailService.sendBenefitRedemptionConfirmationToStudent(
                message.studentEmail(),
                message.studentName(),
                message.benefitName(),
                message.couponCode(),
                qrCodePng);

        emailService.sendBenefitRedemptionNotificationToCompany(
                message.companyEmail(),
                message.companyName(),
                message.studentName(),
                message.benefitName(),
                message.couponCode());
    }
}
