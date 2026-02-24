package com.manolinho.infrastructure.security;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.manolinho.infrastructure.util.AppConstants.Seguridad.AUTORIDAD_ROL_EMPLEADO;
import static com.manolinho.infrastructure.util.AppConstants.Seguridad.AUTORIDAD_ROL_EMPLEADO_JEFE;

@Component
public class RoleDiscountService {

    private static final BigDecimal EMPLEADO_DISCOUNT = new BigDecimal("0.05");
    private static final BigDecimal EMPLEADO_JEFE_DISCOUNT = new BigDecimal("0.15");

    public BigDecimal resolverPorcentajeDescuento(Authentication autenticacion) {
        if (autenticacion == null || autenticacion.getAuthorities() == null) {
            return BigDecimal.ZERO;
        }
        boolean esEmpleadoJefe = autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> AUTORIDAD_ROL_EMPLEADO_JEFE.equals(autoridad.getAuthority()));
        if (esEmpleadoJefe) {
            return EMPLEADO_JEFE_DISCOUNT;
        }
        boolean esEmpleado = autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> AUTORIDAD_ROL_EMPLEADO.equals(autoridad.getAuthority()));
        if (esEmpleado) {
            return EMPLEADO_DISCOUNT;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal aplicarDescuento(BigDecimal precioOriginal, BigDecimal porcentajeDescuento) {
        BigDecimal factor = BigDecimal.ONE.subtract(porcentajeDescuento);
        return precioOriginal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
