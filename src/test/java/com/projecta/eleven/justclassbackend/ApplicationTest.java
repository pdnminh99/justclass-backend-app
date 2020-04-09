package com.projecta.eleven.justclassbackend;

import com.projecta.eleven.justclassbackend.junit_config.CustomReplaceUnderscore;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(CustomReplaceUnderscore.class)
class ApplicationTest {

    @Test
    void contextLoads() {
    }

}
