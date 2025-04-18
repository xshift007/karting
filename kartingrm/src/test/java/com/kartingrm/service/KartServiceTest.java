package com.kartingrm.service;

import com.kartingrm.entity.Kart;
import com.kartingrm.entity.KartStatus;
import com.kartingrm.repository.KartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase      //  ◀◀ usa H2 en memoria
class KartServiceTest {

    @Autowired KartService svc;
    @Autowired KartRepository repo;

    @BeforeEach
    void init() {
        repo.deleteAll();
        IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new Kart(null, "K%03d".formatted(i),
                        KartStatus.AVAILABLE, null, null))
                .forEach(repo::save);
    }

    @Test
    void allocateThreeKartsSuccessfully() {
        List<Kart> list = svc.allocate(3);

        assertThat(list)
                .hasSize(3)
                .allMatch(k -> k.getStatus() == KartStatus.RESERVED);
    }
}
