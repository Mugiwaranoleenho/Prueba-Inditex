package com.manolinho.infrastructure.security;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class RoleDiscountService {

    private static final BigDecimal EMPLEADO_DISCOUNT = new BigDecimal("0.05");
    private static final BigDecimal EMPLEADO_JEFE_DISCOUNT = new BigDecimal("0.15");

    public BigDecimal resolveDiscountPercent(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return BigDecimal.ZERO;
        }
        boolean isEmpleadoJefe = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_EMPLEADO_JEFE".equals(a.getAuthority()));
        if (isEmpleadoJefe) {
            return EMPLEADO_JEFE_DISCOUNT;
        }
        boolean isEmpleado = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_EMPLEADO".equals(a.getAuthority()));
        if (isEmpleado) {
            return EMPLEADO_DISCOUNT;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal applyDiscount(BigDecimal originalPrice, BigDecimal discountPercent) {
        BigDecimal factor = BigDecimal.ONE.subtract(discountPercent);
        return originalPrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
