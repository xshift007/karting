package com.kartingrm.service;

import com.kartingrm.entity.Kart;
import com.kartingrm.entity.KartStatus;
import com.kartingrm.repository.KartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class KartServiceTest {

    @Autowired
    KartService svc;
    @Autowired
    KartRepository repo;

    @BeforeEach
    void init(){
        repo.deleteAll();
        IntStream.rangeClosed(1,5)
                .mapToObj(i -> new Kart(null,"K%03d".formatted(i), KartStatus.AVAILABLE,
                        null,null))
                .forEach(repo::save);
    }

    @Test
    void allocateThree() {
        List<Kart> list = svc.allocate(3);
        assertEquals(3, list.size());
        assertTrue(list.stream().allMatch(k -> k.getStatus()==KartStatus.RESERVED));
    }
}
