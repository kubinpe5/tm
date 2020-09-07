package com.homework.solution.service;

import com.homework.solution.repository.PaymentRecord;
import com.homework.solution.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ApplicationArguments applicationArguments;

    private SolutionService service;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
        final PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPartner("X");
        paymentRecord.setPosition(1);
        paymentRecord.setNameOfTransaction("name_of_transaction");
        paymentRecord.setDateTime(LocalDateTime.of(LocalDate.of(2020, 10, 10), LocalTime.of(10, 10, 10)));
        paymentRepository.addRecord(paymentRecord);

        final PaymentRecord paymentRecord2 = new PaymentRecord();
        paymentRecord2.setPartner("Y");
        paymentRecord2.setPosition(1);
        paymentRecord2.setNameOfTransaction("name_of_transaction");
        paymentRecord2.setDateTime(LocalDateTime.of(LocalDate.of(2020, 11, 10), LocalTime.of(10, 10, 10)));
        paymentRepository.addRecord(paymentRecord2);

        service = new SolutionService(applicationContext, applicationArguments, paymentRepository);
    }

    @Test
    void initializeDataset() {
        assertThat(paymentRepository.getOrderedElements().size()).isEqualTo(2);
    }

    @Test
    void solution() {
        final String actualOutput = service.solution("src/test/resources/test_data.txt");
        assertTrue(actualOutput.contains("Netflix|02|payment weekly\nApple|1|game Of Thrones"));
        assertTrue(actualOutput.contains("Netflix|06|monthly subscription\n" +
                "Netflix|08|O2TV, SportTV\n" +
                "Netflix|04|game Of thrones\n" +
                "Netflix|05|yearly subscription\n" +
                "Netflix|10|recharging of 987654321\n"));
    }

}