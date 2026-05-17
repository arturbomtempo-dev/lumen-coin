package br.pucminas.lumen_coin_api.coin_transfer.scheduler;

import br.pucminas.lumen_coin_api.user.entity.Teacher;
import br.pucminas.lumen_coin_api.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SemesterCoinTopUpScheduler {

    private final TeacherRepository teacherRepository;

    @Scheduled(cron = "0 0 0 1 2,8 *", zone = "America/Sao_Paulo")
    @Transactional
    public void topUpTeacherBalances() {
        List<Teacher> teachers = teacherRepository.findAll();
        teachers.forEach(t -> t.setBalance(t.getBalance() + 1000));
        teacherRepository.saveAll(teachers);
        log.info("Semester top-up: added 1000 coins to {} teachers", teachers.size());
    }
}
