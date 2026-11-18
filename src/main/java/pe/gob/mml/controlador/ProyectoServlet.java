package pe.gob.mml.controlador;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import pe.gob.mml.dao.ProyectoDAO;
import pe.gob.mml.modelo.Proyecto;

/**
 * Controlador de Proyectos de Inversión Pública.
 * GET  /ProyectoServlet?accion=listar | seleccionar&id=N
 * POST /ProyectoServlet  accion=registrar | actualizar | eliminar
 */
@WebServlet("/ProyectoServlet")
public class ProyectoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VISTA = "/proyectos.jsp";
    private final ProyectoDAO dao = new ProyectoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = valor(req, "accion");

        try {
            if ("seleccionar".equals(accion)) {
                Proyecto p = dao.buscarPorId(Integer.parseInt(valor(req, "id")));
                if (p != null) {
                    req.setAttribute("form", aFormulario(p));
                } else {
                    mensaje(req, "danger", "El proyecto seleccionado no existe.");
                }
            }
            mensajeDeRedireccion(req);
            mostrar(req, resp);
        } catch (NumberFormatException e) {
            mensaje(req, "danger", "Id de proyecto inválido.");
            mostrar(req, resp);
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = valor(req, "accion");

        try {
            switch (accion) {
                case "registrar":
                    guardar(req, resp, false);
                    break;
                case "actualizar":
                    guardar(req, resp, true);
                    break;
                case "eliminar":
                    eliminar(req, resp);
                    break;
                default: // "listar" o cualquier otra
                    resp.sendRedirect(req.getContextPath() + "/ProyectoServlet?accion=listar");
            }
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    /* ------------------------------------------------------------------ */

    private void guardar(HttpServletRequest req, HttpServletResponse resp, boolean esActualizacion)
            throws SQLException, ServletException, IOException {

        Map<String, String> form = leerFormulario(req);
        Map<String, String> errores = new LinkedHashMap<>();
        Proyecto p = validar(form, errores);

        if (esActualizacion) {
            try {
                p.setId(Integer.parseInt(form.get("id")));
            } catch (NumberFormatException e) {
                errores.put("general", "Primero selecciona un proyecto de la tabla (ícono ℹ) para actualizarlo.");
            }
        }

        if (!errores.isEmpty()) {
            req.setAttribute("form", form);
            req.setAttribute("errores", errores);
            mensaje(req, "danger", "Corrige los campos marcados.");
            mostrar(req, resp);
            return;
        }

        if (esActualizacion) {
            dao.actualizar(p);
            redirigir(req, resp, "actualizado");
        } else {
            dao.registrar(p);
            redirigir(req, resp, "registrado");
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        try {
            int id = Integer.parseInt(valor(req, "id"));
            dao.eliminar(id);
            redirigir(req, resp, "eliminado");
        } catch (NumberFormatException e) {
            mensaje(req, "warning", "Selecciona un proyecto de la tabla para eliminarlo.");
            req.setAttribute("form", leerFormulario(req));
            mostrar(req, resp);
        }
    }

    /** Validaciones del lado del servidor (las del navegador se pueden saltar). */
    private Proyecto validar(Map<String, String> f, Map<String, String> errores) {
        Proyecto p = new Proyecto();

        // Nombre: requerido, 4 a 60 caracteres
        String nombre = f.get("nombre");
        if (nombre.isEmpty()) {
            errores.put("nombre", "El nombre del proyecto es obligatorio.");
        } else if (nombre.length() < 4 || nombre.length() > 60) {
            errores.put("nombre", "El nombre debe tener entre 4 y 60 caracteres.");
        }
        p.setNombre(nombre);

        // Descripción: opcional
        String desc = f.get("descripcion");
        if (desc.length() > 500) {
            errores.put("descripcion", "La descripción no debe superar los 500 caracteres.");
        }
        p.setDescripcion(desc.isEmpty() ? null : desc);

        // Fechas: requeridas, fin >= inicio
        LocalDate inicio = fecha(f.get("fechaInicio"), "fechaInicio", "inicio", errores);
        LocalDate fin = fecha(f.get("fechaFin"), "fechaFin", "culminación", errores);
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            errores.put("fechaFin", "La fecha de culminación no puede ser anterior a la fecha de inicio.");
        }
        p.setFechaInicio(inicio);
        p.setFechaFin(fin);

        // Asesor: requerido, solo letras, nombres y apellidos (mínimo 2 palabras)
        String asesor = f.get("asesorTecnico").replaceAll("\\s+", " ");
        if (asesor.isEmpty()) {
            errores.put("asesorTecnico", "El asesor técnico es obligatorio.");
        } else if (!asesor.matches("[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+( [A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)+")) {
            errores.put("asesorTecnico", "Ingresa nombres y apellidos (solo letras).");
        } else if (asesor.length() > 100) {
            errores.put("asesorTecnico", "Máximo 100 caracteres.");
        }
        p.setAsesorTecnico(asesor);

        // Presupuesto: requerido, numérico y mayor a 0
        String pres = f.get("presupuesto");
        if (pres.isEmpty()) {
            errores.put("presupuesto", "El presupuesto asignado es obligatorio.");
        } else {
            try {
                BigDecimal monto = new BigDecimal(pres);
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    errores.put("presupuesto", "El presupuesto debe ser mayor a 0.");
                } else if (monto.scale() > 2 || monto.precision() - monto.scale() > 12) {
                    errores.put("presupuesto", "Monto inválido (máx. 12 enteros y 2 decimales).");
                }
                p.setPresupuesto(monto);
            } catch (NumberFormatException e) {
                errores.put("presupuesto", "El presupuesto debe ser un número válido.");
            }
        }
        return p;
    }

    private LocalDate fecha(String texto, String campo, String etiqueta, Map<String, String> errores) {
        if (texto.isEmpty()) {
            errores.put(campo, "La fecha de " + etiqueta + " es obligatoria.");
            return null;
        }
        try {
            return LocalDate.parse(texto); // formato yyyy-MM-dd del input type="date"
        } catch (DateTimeParseException e) {
            errores.put(campo, "Fecha de " + etiqueta + " inválida.");
            return null;
        }
    }

    /* ------------------------------------------------------------------ */

    private Map<String, String> leerFormulario(HttpServletRequest req) {
        Map<String, String> f = new LinkedHashMap<>();
        for (String c : new String[] {"id", "nombre", "descripcion", "fechaInicio",
                                      "fechaFin", "asesorTecnico", "presupuesto"}) {
            f.put(c, valor(req, c));
        }
        return f;
    }

    private Map<String, String> aFormulario(Proyecto p) {
        Map<String, String> f = new LinkedHashMap<>();
        f.put("id", String.valueOf(p.getId()));
        f.put("nombre", p.getNombre());
        f.put("descripcion", p.getDescripcion() == null ? "" : p.getDescripcion());
        f.put("fechaInicio", p.getFechaInicio().toString());
        f.put("fechaFin", p.getFechaFin().toString());
        f.put("asesorTecnico", p.getAsesorTecnico());
        f.put("presupuesto", p.getPresupuesto().toPlainString());
        return f;
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("lista", dao.listar());
        } catch (SQLException e) {
            mensaje(req, "danger", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }

    /** Patrón Post/Redirect/Get: evita registros duplicados al presionar F5. */
    private void redirigir(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/ProyectoServlet?accion=listar&msg=" + msg);
    }

    private void mensajeDeRedireccion(HttpServletRequest req) {
        String msg = valor(req, "msg");
        switch (msg) {
            case "registrado": mensaje(req, "success", "Proyecto registrado correctamente."); break;
            case "actualizado": mensaje(req, "success", "Proyecto actualizado correctamente."); break;
            case "eliminado": mensaje(req, "success", "Proyecto eliminado correctamente."); break;
            default: break;
        }
    }

    private void mensaje(HttpServletRequest req, String tipo, String texto) {
        req.setAttribute("tipoMensaje", tipo);
        req.setAttribute("mensaje", texto);
    }

    private void errorBD(HttpServletRequest req, HttpServletResponse resp, SQLException e)
            throws ServletException, IOException {
        log("Error de base de datos", e);
        req.setAttribute("form", leerFormulario(req));
        mensaje(req, "danger", "Error de base de datos: " + e.getMessage());
        mostrar(req, resp);
    }

    private static String valor(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        return v == null ? "" : v.trim();
    }
}
