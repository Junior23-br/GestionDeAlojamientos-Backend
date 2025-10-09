package com.gestion.alojamientos;

import com.gestion.alojamientos.config.TestMailConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestMailConfig.class)
class GestionDeAlojamientosBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
