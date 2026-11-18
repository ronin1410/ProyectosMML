<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.*, pe.gob.mml.modelo.Proyecto, pe.gob.mml.util.Html" %>
<%
    // Si se abre el JSP directamente, pasamos primero por el Servlet para cargar la lista
    if (request.getAttribute("lista") == null && request.getAttribute("mensaje") == null) {
        response.sendRedirect(request.getContextPath() + "/ProyectoServlet?accion=listar");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Proyecto> lista = (List<Proyecto>) request.getAttribute("lista");
    if (lista == null) lista = new ArrayList<>();

    @SuppressWarnings("unchecked")
    Map<String, String> form = (Map<String, String>) request.getAttribute("form");
    if (form == null) form = new HashMap<>();

    @SuppressWarnings("unchecked")
    Map<String, String> errores = (Map<String, String>) request.getAttribute("errores");
    if (errores == null) errores = new HashMap<>();

    String mensaje = (String) request.getAttribute("mensaje");
    String tipoMensaje = (String) request.getAttribute("tipoMensaje");

    DecimalFormat soles = new DecimalFormat("S/ #,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
    String ctx = request.getContextPath();
    String idSel = form.getOrDefault("id", "");
%>
<%!
    // Helpers para el formulario
    private String v(Map<String, String> f, String campo) {
        return Html.esc(f.getOrDefault(campo, ""));
    }
    private String inv(Map<String, String> e, String campo) {
        return e.containsKey(campo) ? " is-invalid" : "";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Proyectos de Inversión Pública - MML</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        .acciones a, .acciones button { color: #000; font-size: 1.1rem; }
        .acciones button { border: 0; background: none; padding: 0 0 0 .5rem; }
        tr.seleccionado > td { background-color: #fff3cd !important; }
    </style>
</head>
<body>
<div class="container-fluid px-5 py-4">

    <% if (mensaje != null) { %>
        <div class="alert alert-<%= Html.esc(tipoMensaje) %> alert-dismissible fade show" role="alert">
            <%= Html.esc(mensaje) %>
            <% if (errores.containsKey("general")) { %><br><%= Html.esc(errores.get("general")) %><% } %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    <% } %>

    <div class="row g-5">
        <!-- ===================== FORMULARIO ===================== -->
        <div class="col-lg-5">
            <h1 class="h2 mb-4">Registrar Proyecto</h1>

            <form id="frmProyecto" action="<%= ctx %>/ProyectoServlet" method="post" novalidate>
                <input type="hidden" name="id" value="<%= v(form, "id") %>">

                <div class="mb-3">
                    <label for="nombre" class="form-label">Nombre del Proyecto</label>
                    <input type="text" class="form-control<%= inv(errores, "nombre") %>" id="nombre" name="nombre"
                           value="<%= v(form, "nombre") %>" required minlength="4" maxlength="60">
                    <div class="invalid-feedback"><%= errores.containsKey("nombre") ? Html.esc(errores.get("nombre"))
                        : "El nombre debe tener entre 4 y 60 caracteres." %></div>
                </div>

                <div class="mb-3">
                    <label for="descripcion" class="form-label">Descripción técnica del Proyecto (Opcional)</label>
                    <textarea class="form-control<%= inv(errores, "descripcion") %>" id="descripcion" name="descripcion"
                              rows="3" maxlength="500"><%= v(form, "descripcion") %></textarea>
                    <div class="invalid-feedback"><%= Html.esc(errores.getOrDefault("descripcion", "Máximo 500 caracteres.")) %></div>
                </div>

                <div class="mb-3">
                    <label for="fechaInicio" class="form-label">Fecha de inicio del Proyecto</label>
                    <input type="date" class="form-control<%= inv(errores, "fechaInicio") %>" id="fechaInicio"
                           name="fechaInicio" value="<%= v(form, "fechaInicio") %>" required>
                    <div class="invalid-feedback"><%= Html.esc(errores.getOrDefault("fechaInicio", "La fecha de inicio es obligatoria.")) %></div>
                </div>

                <div class="mb-3">
                    <label for="fechaFin" class="form-label">Fecha de culminación del Proyecto</label>
                    <input type="date" class="form-control<%= inv(errores, "fechaFin") %>" id="fechaFin"
                           name="fechaFin" value="<%= v(form, "fechaFin") %>" required>
                    <div class="invalid-feedback" id="fbFechaFin"><%= Html.esc(errores.getOrDefault("fechaFin", "La fecha de culminación es obligatoria.")) %></div>
                </div>

                <div class="mb-3">
                    <label for="asesorTecnico" class="form-label">Asesor Técnico del Proyecto (Nombres y Apellidos)</label>
                    <input type="text" class="form-control<%= inv(errores, "asesorTecnico") %>" id="asesorTecnico"
                           name="asesorTecnico" value="<%= v(form, "asesorTecnico") %>" required maxlength="100"
                           pattern="\s*[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+(\s+[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)+\s*">
                    <div class="invalid-feedback"><%= Html.esc(errores.getOrDefault("asesorTecnico", "Ingresa nombres y apellidos (solo letras).")) %></div>
                </div>

                <div class="mb-4">
                    <label for="presupuesto" class="form-label">Presupuesto Asignado</label>
                    <input type="number" class="form-control<%= inv(errores, "presupuesto") %>" id="presupuesto"
                           name="presupuesto" value="<%= v(form, "presupuesto") %>" required min="0.01" step="0.01">
                    <div class="invalid-feedback"><%= Html.esc(errores.getOrDefault("presupuesto", "Ingresa un monto mayor a 0.")) %></div>
                </div>

                <button type="submit" name="accion" value="registrar" class="btn btn-primary">Registrar</button>
                <button type="submit" name="accion" value="actualizar" class="btn btn-info text-white">Actualizar</button>
                <a href="<%= ctx %>/ProyectoServlet?accion=listar" class="btn btn-warning">Listar</a>
                <button type="submit" name="accion" value="eliminar" class="btn btn-danger" formnovalidate
                        onclick="return confirmarEliminar();">Eliminar</button>
            </form>
        </div>

        <!-- ===================== LISTADO ===================== -->
        <div class="col-lg-7">
            <h1 class="h2 mb-4">Proyectos Disponibles</h1>

            <div class="table-responsive">
                <table class="table table-bordered table-striped align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>Id</th>
                        <th>Nombre del Proyecto</th>
                        <th>Descripcion</th>
                        <th>Inicio</th>
                        <th>Fin</th>
                        <th>Asesor Técnico</th>
                        <th>Presupuesto</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (lista.isEmpty()) { %>
                        <tr><td colspan="8" class="text-center text-muted">No hay proyectos registrados.</td></tr>
                    <% } %>
                    <% for (Proyecto p : lista) { %>
                        <tr class="<%= String.valueOf(p.getId()).equals(idSel) ? "seleccionado" : "" %>"
                            title="<%= Html.esc(p.getDescripcion()) %>">
                            <td><%= p.getId() %></td>
                            <td><%= Html.esc(p.getNombre()) %></td>
                            <td><%= p.getDescripcion()== null ? "-" : Html.esc(p.getDescripcion()) %></td>
                            <td><%= p.getFechaInicio() %></td>
                            <td><%= p.getFechaFin() %></td>
                            <td><%= Html.esc(p.getAsesorTecnico()) %></td>
                            <td><%= soles.format(p.getPresupuesto()) %></td>
                            <td class="acciones text-nowrap">
                                <a href="<%= ctx %>/ProyectoServlet?accion=seleccionar&id=<%= p.getId() %>"
                                   title="Ver / editar"><i class="bi bi-info-circle-fill"></i></a>
                                <form action="<%= ctx %>/ProyectoServlet" method="post" class="d-inline"
                                      data-nombre="<%= Html.esc(p.getNombre()) %>"
                                      onsubmit="return confirm('¿Eliminar el proyecto \u0022' + this.dataset.nombre + '\u0022?');">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="<%= p.getId() %>">
                                    <button type="submit" title="Eliminar"><i class="bi bi-trash-fill"></i></button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Validación del lado del cliente con estilos de Bootstrap
    const frm = document.getElementById('frmProyecto');
    const fIni = document.getElementById('fechaInicio');
    const fFin = document.getElementById('fechaFin');
    const fbFin = document.getElementById('fbFechaFin');

    function validarFechas() {
        if (fIni.value && fFin.value && fFin.value < fIni.value) {
            fFin.setCustomValidity('rango');
            fbFin.textContent = 'La fecha de culminación no puede ser anterior a la fecha de inicio.';
        } else {
            fFin.setCustomValidity('');
            if (!fFin.value) fbFin.textContent = 'La fecha de culminación es obligatoria.';
        }
    }
    fIni.addEventListener('change', validarFechas);
    fFin.addEventListener('change', validarFechas);

    frm.addEventListener('submit', function (e) {
        if (e.submitter && e.submitter.hasAttribute('formnovalidate')) return;
        frm.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
        validarFechas();
        if (!frm.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        frm.classList.add('was-validated');
    });

    function confirmarEliminar() {
        if (!frm.querySelector('[name=id]').value) {
            alert('Primero selecciona un proyecto de la tabla con el ícono ℹ.');
            return false;
        }
        return confirm('¿Seguro que deseas eliminar el proyecto seleccionado?');
    }
</script>
</body>
</html>
