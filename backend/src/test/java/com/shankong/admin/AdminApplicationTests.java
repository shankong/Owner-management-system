package com.shankong.admin;

import com.shankong.admin.dto.UserRegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class AdminApplicationTests {

	@Test
	void contextLoads() {
        UserRegisterDTO dto = new UserRegisterDTO();     // 故意全空
        Set<ConstraintViolation<UserRegisterDTO>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        violations.forEach(v -> System.out.println(v.getPropertyPath() + " -> " + v.getMessage()));
	}

}
