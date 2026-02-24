package com.manolinho.infrastructure.util;

public final class AppConstants {

    private AppConstants() {
    }

    public static final class Security {
        public static final String ROLE_CLIENTE = "CLIENTE";
        public static final String ROLE_CLIENTE_BLOQUEADO = "CLIENTE_BLOQUEADO";
        public static final String ROLE_EMPLEADO = "EMPLEADO";
        public static final String ROLE_EMPLEADO_JEFE = "EMPLEADO_JEFE";
        public static final String ROLE_ADMIN = "ADMIN";

        public static final String AUTH_ROLE_EMPLEADO = "ROLE_EMPLEADO";
        public static final String AUTH_ROLE_EMPLEADO_JEFE = "ROLE_EMPLEADO_JEFE";

        public static final String PREAUTH_CLIENTE_EMPLEADO_JEFE_ADMIN =
                "hasAnyRole('CLIENTE','EMPLEADO','EMPLEADO_JEFE','ADMIN')";
        public static final String PREAUTH_EMPLEADO_JEFE_ADMIN =
                "hasAnyRole('EMPLEADO','EMPLEADO_JEFE','ADMIN')";

        public static final String USER_CLIENTE = "cliente";
        public static final String USER_CLIENTE_BLOQUEADO = "cliente_bloqueado";
        public static final String USER_EMPLEADO = "empleado";
        public static final String USER_EMPLEADO_JEFE = "empleado_jefe";
        public static final String USER_ADMIN = "admin";

        public static final String PASSWORD_CLIENTE = "cliente123";
        public static final String PASSWORD_CLIENTE_BLOQUEADO = "bloqueado123";
        public static final String PASSWORD_EMPLEADO = "empleado123";
        public static final String PASSWORD_EMPLEADO_JEFE = "jefe123";
        public static final String PASSWORD_ADMIN = "admin123";

        private Security() {
        }
    }

    public static final class Endpoint {
        public static final String H2_CONSOLE = "/h2-console/**";
        public static final String TIENDAS_BASE = "/tiendas";
        public static final String TIENDAS_ALL = "/tiendas/**";
        public static final String TIENDAS_PRECIO = "/tiendas/precio";
        public static final String PEDIDOS_BASE = "/pedidos";
        public static final String PEDIDOS_ALL = "/pedidos/**";
        public static final String DEVOLUCIONES_BASE = "/devoluciones";
        public static final String DEVOLUCIONES_ALL = "/devoluciones/**";
        public static final String CAMBIOS_TALLA_BASE = "/cambios-talla";
        public static final String CAMBIOS_TALLA_ALL = "/cambios-talla/**";
        public static final String PRECIO_RELATIVE = "/precio";

        private Endpoint() {
        }
    }

    public static final class RequestParam {
        public static final String FECHA_APLICACION = "fechaAplicacion";
        public static final String PRODUCT_ID = "productId";
        public static final String BRAND_ID = "brandId";

        private RequestParam() {
        }
    }

    public static final class Message {
        public static final String NO_HAY_TARIFA_APLICABLE = "No hay tarifa aplicable";
        public static final String LOG_POST_PEDIDOS = "POST /pedidos fechaCompra={}, lineas={}";
        public static final String LOG_POST_DEVOLUCIONES = "POST /devoluciones pedidoId={}, lineas={}";
        public static final String LOG_POST_CAMBIOS_TALLA = "POST /cambios-talla pedidoId={}, productId={}";
        public static final String LOG_POST_TIENDAS = "POST /tiendas brandId={}, productId={}";
        public static final String LOG_GET_TIENDAS_PRECIO = "GET /tiendas/precio fechaAplicacion={}, productId={}, brandId={}";

        private Message() {
        }
    }
}
