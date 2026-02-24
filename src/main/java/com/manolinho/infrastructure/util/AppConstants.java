package com.manolinho.infrastructure.util;

public final class AppConstants {

    private AppConstants() {
    }

    public static final class Seguridad {
        public static final String ROL_CLIENTE = "CLIENTE";
        public static final String ROL_CLIENTE_BLOQUEADO = "CLIENTE_BLOQUEADO";
        public static final String ROL_EMPLEADO = "EMPLEADO";
        public static final String ROL_EMPLEADO_JEFE = "EMPLEADO_JEFE";
        public static final String ROL_ADMIN = "ADMIN";

        public static final String AUTORIDAD_ROL_EMPLEADO = "ROLE_EMPLEADO";
        public static final String AUTORIDAD_ROL_EMPLEADO_JEFE = "ROLE_EMPLEADO_JEFE";

        public static final String PREAUTORIZACION_CLIENTE_EMPLEADO_JEFE_ADMIN =
                "hasAnyRole('CLIENTE','EMPLEADO','EMPLEADO_JEFE','ADMIN')";
        public static final String PREAUTORIZACION_EMPLEADO_JEFE_ADMIN =
                "hasAnyRole('EMPLEADO','EMPLEADO_JEFE','ADMIN')";

        public static final String USUARIO_CLIENTE = "cliente";
        public static final String USUARIO_CLIENTE_BLOQUEADO = "cliente_bloqueado";
        public static final String USUARIO_EMPLEADO = "empleado";
        public static final String USUARIO_EMPLEADO_JEFE = "empleado_jefe";
        public static final String USUARIO_ADMIN = "admin";

        public static final String CLAVE_CLIENTE = "cliente123";
        public static final String CLAVE_CLIENTE_BLOQUEADO = "bloqueado123";
        public static final String CLAVE_EMPLEADO = "empleado123";
        public static final String CLAVE_EMPLEADO_JEFE = "jefe123";
        public static final String CLAVE_ADMIN = "admin123";

        private Seguridad() {
        }
    }

    public static final class Rutas {
        public static final String CONSOLA_H2 = "/h2-console/**";
        public static final String TIENDAS_BASE = "/tiendas";
        public static final String TIENDAS_TODAS = "/tiendas/**";
        public static final String TIENDAS_PRECIO = "/tiendas/precio";
        public static final String PEDIDOS_BASE = "/pedidos";
        public static final String PEDIDOS_TODOS = "/pedidos/**";
        public static final String DEVOLUCIONES_BASE = "/devoluciones";
        public static final String DEVOLUCIONES_TODAS = "/devoluciones/**";
        public static final String CAMBIOS_TALLA_BASE = "/cambios-talla";
        public static final String CAMBIOS_TALLA_TODOS = "/cambios-talla/**";
        public static final String PRECIO_RELATIVA = "/precio";

        private Rutas() {
        }
    }

    public static final class Parametros {
        public static final String FECHA_APLICACION = "fechaAplicacion";
        public static final String ID_PRODUCTO = "productId";
        public static final String ID_MARCA = "brandId";

        private Parametros() {
        }
    }

    public static final class Mensajes {
        public static final String NO_HAY_TARIFA_APLICABLE = "No hay tarifa aplicable";
        public static final String LOG_POST_PEDIDOS = "POST /pedidos fechaCompra={}, lineas={}";
        public static final String LOG_POST_DEVOLUCIONES = "POST /devoluciones pedidoId={}, lineas={}";
        public static final String LOG_POST_CAMBIOS_TALLA = "POST /cambios-talla pedidoId={}, productId={}";
        public static final String LOG_POST_TIENDAS = "POST /tiendas brandId={}, productId={}";
        public static final String LOG_GET_TIENDAS_PRECIO = "GET /tiendas/precio fechaAplicacion={}, productId={}, brandId={}";

        private Mensajes() {
        }
    }
}
