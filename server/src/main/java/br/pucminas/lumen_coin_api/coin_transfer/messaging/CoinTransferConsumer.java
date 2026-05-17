package br.pucminas.lumen_coin_api.coin_transfer.messaging;

import br.pucminas.lumen_coin_api.config.RabbitMQConfig;
import br.pucminas.lumen_coin_api.user.entity.Student;
import br.pucminas.lumen_coin_api.user.exception.UserNotFoundException;
import br.pucminas.lumen_coin_api.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinTransferConsumer {

    private final StudentRepository studentRepository;

    @RabbitListener(queues = RabbitMQConfig.COIN_TRANSFER_QUEUE)
    @Transactional
    public void process(CoinTransferMessage message) {
        Student student = studentRepository.findById(message.recipientId())
                .orElseThrow(() -> new UserNotFoundException(message.recipientId()));
        student.setBalance(student.getBalance() + message.amount());
        studentRepository.save(student);
        log.info("Credited {} coins to student {}", message.amount(), message.recipientId());
    }
}
