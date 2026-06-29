package br.pucminas.lumen_coin_api.coin_transfer.service.impl;

import br.pucminas.lumen_coin_api.coin_transfer.dto.request.SendCoinTransferRequest;
import br.pucminas.lumen_coin_api.coin_transfer.dto.response.CoinTransferResponse;
import br.pucminas.lumen_coin_api.coin_transfer.entity.CoinTransfer;
import br.pucminas.lumen_coin_api.coin_transfer.exception.InsufficientBalanceException;
import br.pucminas.lumen_coin_api.coin_transfer.messaging.CoinTransferMessage;
import br.pucminas.lumen_coin_api.coin_transfer.messaging.CoinTransferProducer;
import br.pucminas.lumen_coin_api.coin_transfer.repository.CoinTransferRepository;
import br.pucminas.lumen_coin_api.coin_transfer.service.CoinTransferService;
import br.pucminas.lumen_coin_api.email.service.EmailService;
import br.pucminas.lumen_coin_api.whatsapp.service.WhatsAppService;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import br.pucminas.lumen_coin_api.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service OK. Só alguns pequenos detalhes poderiam melhorar (veja os comentários).
 */
@Service
@RequiredArgsConstructor
public class CoinTransferServiceImpl implements CoinTransferService {

        private final CoinTransferRepository coinTransferRepository;
        private final TeacherRepository teacherRepository;
        private final StudentRepository studentRepository;
        private final CoinTransferProducer producer;
        private final EmailService emailService;
        private final WhatsAppService whatsAppService;

        // Possibilidade de usar construtores com parâmetros outra vez.
        @Override
        @Transactional
        public CoinTransferResponse send(UUID teacherId, SendCoinTransferRequest request) {
                Teacher teacher = teacherRepository.findById(teacherId)
                                .orElseThrow(() -> new UserNotFoundException(teacherId));

                if (teacher.getBalance() < request.amount()) {
                        throw new InsufficientBalanceException(teacher.getBalance(), request.amount());
                }

                Student student = studentRepository.findById(request.studentId())
                                .orElseThrow(() -> new UserNotFoundException(request.studentId()));

                teacher.setBalance(teacher.getBalance() - request.amount());
                teacherRepository.save(teacher);

                /* CoinTransfer transfer = new CoinTransfer(teacherId, req.studentId(), req.amount(), req.message()); */
                CoinTransfer transfer = new CoinTransfer();
                transfer.setSenderId(teacherId);
                transfer.setRecipientId(request.studentId());
                transfer.setAmount(request.amount());
                transfer.setMessage(request.message());
                CoinTransfer saved = coinTransferRepository.save(transfer);

                producer.send(new CoinTransferMessage(
                                request.studentId(),
                                request.amount(),
                                student.getEmail(),
                                student.getName(),
                                teacher.getName(),
                                request.message(),
                                student.getPhone()));

                emailService.sendCoinSent(
                                teacher.getEmail(),
                                teacher.getName(),
                                student.getName(),
                                request.amount(),
                                request.message());

                whatsAppService.sendCoinSent(
                                teacher.getPhone(),
                                teacher.getName(),
                                student.getName(),
                                request.amount(),
                                request.message());

                return toResponse(saved, teacher.getName(), student.getName());
        }

        @Override
        @Transactional(readOnly = true)
        public List<CoinTransferResponse> getSentByTeacher(UUID teacherId) {
                return coinTransferRepository.findBySenderIdOrderBySentAtDesc(teacherId)
                                .stream()
                                .map(t -> {
                                        String senderName = teacherRepository.findById(t.getSenderId())
                                                        .map(Teacher::getName).orElse("Desconhecido");
                                        String recipientName = studentRepository.findById(t.getRecipientId())
                                                        .map(Student::getName).orElse("Desconhecido");
                                        return toResponse(t, senderName, recipientName);
                                })
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<CoinTransferResponse> getReceivedByStudent(UUID studentId) {
                return coinTransferRepository.findByRecipientIdOrderBySentAtDesc(studentId)
                                .stream()
                                .map(t -> {
                                        String senderName = teacherRepository.findById(t.getSenderId())
                                                        .map(Teacher::getName).orElse("Desconhecido");
                                        String recipientName = studentRepository.findById(t.getRecipientId())
                                                        .map(Student::getName).orElse("Desconhecido");
                                        return toResponse(t, senderName, recipientName);
                                })
                                .toList();
        }

        private CoinTransferResponse toResponse(CoinTransfer t, String senderName, String recipientName) {
                return new CoinTransferResponse(
                                t.getId(),
                                t.getSenderId(),
                                senderName,
                                t.getRecipientId(),
                                recipientName,
                                t.getAmount(),
                                t.getMessage(),
                                t.getSentAt());
        }
}
