package br.pucminas.lumen_coin_api.benefit_redemption.service.impl;

import br.pucminas.lumen_coin_api.benefit.entity.Benefit;
import br.pucminas.lumen_coin_api.benefit.exception.BenefitNotFoundException;
import br.pucminas.lumen_coin_api.benefit.repository.BenefitRepository;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.request.CreateBenefitRedemptionRequest;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.BenefitRedemptionResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.dto.response.ValidateBenefitRedemptionResponse;
import br.pucminas.lumen_coin_api.benefit_redemption.entity.BenefitRedemption;
import br.pucminas.lumen_coin_api.benefit_redemption.enums.RedemptionStatus;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.BenefitRedemptionNotFoundException;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.RedemptionAlreadyUsedException;
import br.pucminas.lumen_coin_api.benefit_redemption.exception.UnauthorizedRedemptionException;
import br.pucminas.lumen_coin_api.benefit_redemption.messaging.BenefitRedemptionMessage;
import br.pucminas.lumen_coin_api.benefit_redemption.messaging.BenefitRedemptionProducer;
import br.pucminas.lumen_coin_api.benefit_redemption.repository.BenefitRedemptionRepository;
import br.pucminas.lumen_coin_api.benefit_redemption.service.BenefitRedemptionService;
import br.pucminas.lumen_coin_api.coin_transfer.exception.InsufficientBalanceException;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.user.entity.Company;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.repository.CompanyRepository;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BenefitRedemptionServiceImpl implements BenefitRedemptionService {

    private static final String COUPON_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BenefitRedemptionRepository redemptionRepository;
    private final BenefitRepository benefitRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final BenefitRedemptionProducer redemptionProducer;
    private final EmailService emailService;

    @Override
    @Transactional
    public BenefitRedemptionResponse redeem(UUID studentId, CreateBenefitRedemptionRequest request) {
        Benefit benefit = benefitRepository.findById(request.benefitId())
                .orElseThrow(() -> new BenefitNotFoundException(request.benefitId()));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        if (student.getBalance() < benefit.getCost()) {
            throw new InsufficientBalanceException(student.getBalance(), benefit.getCost());
        }

        student.setBalance(student.getBalance() - benefit.getCost());
        studentRepository.save(student);

        String couponCode = generateUniqueCouponCode();

        BenefitRedemption redemption = new BenefitRedemption();
        redemption.setStudentId(studentId);
        redemption.setBenefitId(benefit.getId());
        redemption.setCompanyId(benefit.getCompanyId());
        redemption.setCoinsSpent(benefit.getCost());
        redemption.setCouponCode(couponCode);

        BenefitRedemption saved;
        try {
            saved = redemptionRepository.saveAndFlush(redemption);
        } catch (DataIntegrityViolationException e) {
            redemption.setCouponCode(generateUniqueCouponCode());
            saved = redemptionRepository.saveAndFlush(redemption);
        }

        Company company = companyRepository.findById(benefit.getCompanyId())
                .orElseThrow(() -> new UserNotFoundException(benefit.getCompanyId()));

        redemptionProducer.send(new BenefitRedemptionMessage(
                student.getEmail(),
                student.getName(),
                company.getEmail(),
                company.getName(),
                benefit.getName(),
                saved.getCouponCode(),
                saved.getCoinsSpent()));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitRedemptionResponse> getMyRedemptions(UUID studentId) {
        return redemptionRepository.findByStudentIdOrderByRedeemedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitRedemptionResponse> getCompanyRedemptions(UUID companyId) {
        return redemptionRepository.findByCompanyIdOrderByRedeemedAtDesc(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateBenefitRedemptionResponse validateCoupon(String couponCode, UUID companyId) {
        BenefitRedemption redemption = redemptionRepository.findByCouponCode(couponCode.strip().toUpperCase())
                .orElseThrow(() -> new BenefitRedemptionNotFoundException(couponCode));

        if (!redemption.getCompanyId().equals(companyId)) {
            throw new UnauthorizedRedemptionException();
        }

        if (redemption.getStatus() == RedemptionStatus.USED) {
            throw new RedemptionAlreadyUsedException();
        }

        Student student = studentRepository.findById(redemption.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(redemption.getStudentId()));

        Benefit benefit = benefitRepository.findById(redemption.getBenefitId())
                .orElseThrow(() -> new BenefitNotFoundException(redemption.getBenefitId()));

        return new ValidateBenefitRedemptionResponse(
                redemption.getId(),
                student.getName(),
                benefit.getName(),
                redemption.getCoinsSpent(),
                redemption.getRedeemedAt(),
                redemption.getCouponCode());
    }

    @Override
    @Transactional
    public BenefitRedemptionResponse markAsUsed(String couponCode, UUID companyId, String usageNotes) {
        BenefitRedemption redemption = redemptionRepository.findByCouponCode(couponCode.strip().toUpperCase())
                .orElseThrow(() -> new BenefitRedemptionNotFoundException(couponCode));

        if (!redemption.getCompanyId().equals(companyId)) {
            throw new UnauthorizedRedemptionException();
        }

        if (redemption.getStatus() == RedemptionStatus.USED) {
            throw new RedemptionAlreadyUsedException();
        }

        redemption.setStatus(RedemptionStatus.USED);
        redemption.setUsedAt(Instant.now());

        BenefitRedemption saved = redemptionRepository.save(redemption);

        Student student = studentRepository.findById(saved.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(saved.getStudentId()));

        Benefit benefit = benefitRepository.findById(saved.getBenefitId())
                .orElseThrow(() -> new BenefitNotFoundException(saved.getBenefitId()));

        emailService.sendBenefitRedemptionApprovedToStudent(
                student.getEmail(),
                student.getName(),
                benefit.getName(),
                usageNotes);

        return toResponse(saved);
    }

    private String generateUniqueCouponCode() {
        String code;
        do {
            code = "LUMEN-" + randomSegment(5) + "-" + randomSegment(3);
        } while (redemptionRepository.existsByCouponCode(code));
        return code;
    }

    private String randomSegment(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(COUPON_CHARS.charAt(RANDOM.nextInt(COUPON_CHARS.length())));
        }
        return sb.toString();
    }

    private BenefitRedemptionResponse toResponse(BenefitRedemption r) {
        return new BenefitRedemptionResponse(
                r.getId(),
                r.getStudentId(),
                r.getBenefitId(),
                r.getCompanyId(),
                r.getCouponCode(),
                r.getCoinsSpent(),
                r.getStatus(),
                r.getRedeemedAt(),
                r.getUsedAt());
    }
}
