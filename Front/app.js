/* ════════════════════════════════════════════════════════════════
   CONFIGURACIÓN Y ESTADO
════════════════════════════════════════════════════════════════ */
const API = 'http://localhost:8080/api';
let pacienteActual = null;

/* ════════════════════════════════════════════════════════════════
   UTILIDADES
════════════════════════════════════════════════════════════════ */
async function api(metodo, ruta, cuerpo) {
  try {
    const res = await fetch(API + ruta, {
      method: metodo,
      headers: { 'Content-Type': 'application/json' },
      body: cuerpo ? JSON.stringify(cuerpo) : undefined
    });
    const json = await res.json();
    if (!json.success) throw new Error(json.message || 'Error del servidor');
    return json.data;
  } catch (e) {
    if (e.message === 'Failed to fetch') throw new Error('No se puede conectar al servidor. ¿Está corriendo en localhost:8080?');
    throw e;
  }
}

function notificar(msg, tipo = 'ok') {
  const el = document.createElement('div');
  el.className = `notificacion ${tipo === 'error' ? 'error' : tipo === 'aviso' ? 'aviso' : ''}`;
  el.innerHTML = `<span>${tipo === 'ok' ? '✓' : tipo === 'aviso' ? '⚠' : '✕'}</span> ${msg}`;
  document.getElementById('contenedor-notificaciones').appendChild(el);
  setTimeout(() => el.remove(), 4000);
}

function abrirModal(id)  { document.getElementById(id).classList.add('abierto'); }
function cerrarModal(id) { document.getElementById(id).classList.remove('abierto'); }

function formatearFecha(d) {
  if (!d) return '—';
  return new Date(d).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
}

function formatearSoloFecha(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('es-CO', { dateStyle: 'medium' });
}

function insigniaEstado(estado) {
  const mapa = {
    PENDIENTE:  ['insignia-ambar', 'Pendiente'],
    CONFIRMADA: ['insignia-verde', 'Confirmada'],
    COMPLETADA: ['insignia-tinta', 'Completada'],
    CANCELADA:  ['insignia-rojo',  'Cancelada'],
    SOLICITADO: ['insignia-ambar', 'Solicitado'],
    PROGRAMADO: ['insignia-verde', 'Programado'],
    EN_PROCESO: ['insignia-ambar', 'En proceso'],
    COMPLETADO: ['insignia-tinta', 'Completado'],
    CANCELADO:  ['insignia-rojo',  'Cancelado'],
  };
  const [clase, etiqueta] = mapa[estado] || ['insignia-tinta', estado];
  return `<span class="insignia ${clase}">${etiqueta}</span>`;
}

function mostrarCargando(idBoton, cargando) {
  const btn = document.getElementById(idBoton);
  if (!btn) return;
  btn.disabled = cargando;
  btn.innerHTML = cargando
    ? `<span class="girador"></span> Procesando...`
    : btn.dataset.label || btn.innerHTML;
}

/* ════════════════════════════════════════════════════════════════
   AUTENTICACIÓN
════════════════════════════════════════════════════════════════ */
function mostrarPestanaAuth(pestana) {
  document.querySelectorAll('.auth-pestana').forEach((b, i) => b.classList.toggle('activa', (i === 0) === (pestana === 'login')));
  document.getElementById('formulario-login').style.display    = pestana === 'login' ? '' : 'none';
  document.getElementById('formulario-registro').style.display = pestana === 'registro' ? '' : 'none';
}

async function iniciarSesion() {
  const email    = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;
  if (!email || !password) { notificar('Completa todos los campos', 'error'); return; }

  document.getElementById('btn-login').disabled = true;
  document.getElementById('btn-login').innerHTML = '<span class="girador"></span> Ingresando...';

  try {
    const paciente = await api('POST', '/auth/login', { email, password });
    pacienteActual = paciente;
    entrarApp();
  } catch (e) {
    notificar(e.message, 'error');
  } finally {
    document.getElementById('btn-login').disabled = false;
    document.getElementById('btn-login').innerHTML = 'Ingresar';
  }
}

async function registrarse() {
  const cuerpo = {
    fullName:   document.getElementById('reg-nombre').value.trim(),
    documentId: document.getElementById('reg-doc').value.trim(),
    email:      document.getElementById('reg-email').value.trim(),
    password:   document.getElementById('reg-password').value,
    phone:      document.getElementById('reg-telefono').value.trim(),
    birthDate:  document.getElementById('reg-nacimiento').value || null,
    bloodType:  document.getElementById('reg-sangre').value || null,
    allergies:  document.getElementById('reg-alergias').value.trim() || null,
  };
  if (!cuerpo.fullName || !cuerpo.email || !cuerpo.password || !cuerpo.documentId) {
    notificar('Completa los campos obligatorios', 'error'); return;
  }

  document.getElementById('btn-registrar').disabled = true;
  document.getElementById('btn-registrar').innerHTML = '<span class="girador"></span> Registrando...';

  try {
    const paciente = await api('POST', '/auth/registro', cuerpo);
    pacienteActual = paciente;
    entrarApp();
    notificar('¡Cuenta creada exitosamente!');
  } catch (e) {
    notificar(e.message, 'error');
  } finally {
    document.getElementById('btn-registrar').disabled = false;
    document.getElementById('btn-registrar').innerHTML = 'Crear cuenta';
  }
}

function entrarApp() {
  document.getElementById('pantalla-auth').style.display = 'none';
  document.getElementById('pantalla-principal').classList.add('visible');
  const iniciales = pacienteActual.fullName.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  document.getElementById('avatar-usuario').textContent = iniciales;
  document.getElementById('nombre-usuario').textContent = pacienteActual.fullName.split(' ')[0];
  document.getElementById('nombre-bienvenida').textContent = pacienteActual.fullName.split(' ')[0];
  cargarDashboard();
}

function cerrarSesion() {
  pacienteActual = null;
  document.getElementById('pantalla-auth').style.display = '';
  document.getElementById('pantalla-principal').classList.remove('visible');
  irA('dashboard');
}

/* ════════════════════════════════════════════════════════════════
   NAVEGACIÓN
════════════════════════════════════════════════════════════════ */
function irA(pagina) {
  document.querySelectorAll('.pagina').forEach(p => p.classList.remove('activa'));
  document.querySelectorAll('.nav-boton').forEach(b => b.classList.remove('activo'));
  document.getElementById('pagina-' + pagina).classList.add('activa');
  document.querySelector(`[data-pagina="${pagina}"]`)?.classList.add('activo');

  if (pagina === 'dashboard')      cargarDashboard();
  if (pagina === 'citas')          cargarCitas();
  if (pagina === 'historia')       cargarHistoria();
  if (pagina === 'prescripciones') cargarPrescripciones();
  if (pagina === 'examenes')       cargarExamenes();
}

/* ════════════════════════════════════════════════════════════════
   DASHBOARD
════════════════════════════════════════════════════════════════ */
async function cargarDashboard() {
  const pid = pacienteActual.id;
  try {
    const [citas, historia, prescripciones, examenes] = await Promise.all([
      api('GET', `/citas/paciente/${pid}`),
      api('GET', `/historia-clinica/paciente/${pid}`),
      api('GET', `/prescripciones/paciente/${pid}`),
      api('GET', `/examenes/paciente/${pid}`),
    ]);
    document.getElementById('stat-citas').textContent          = citas.length;
    document.getElementById('stat-historia').textContent       = historia.length;
    document.getElementById('stat-prescripciones').textContent = prescripciones.length;
    document.getElementById('stat-examenes').textContent       = examenes.length;

    const proximas = citas.filter(c => c.status === 'PENDIENTE' || c.status === 'CONFIRMADA').slice(0, 4);
    const cont = document.getElementById('dashboard-citas-lista');
    if (!proximas.length) {
      cont.innerHTML = `<div class="vacio"><div class="vacio-icono">📅</div><h3>Sin citas próximas</h3><p>Agenda una cita desde la sección de Citas</p></div>`;
    } else {
      cont.innerHTML = `<div class="tabla-envolvente"><table>
        <thead><tr><th>Médico</th><th>Especialidad</th><th>Fecha</th><th>Estado</th></tr></thead>
        <tbody>${proximas.map(c => `<tr>
          <td><strong>${c.doctorName}</strong></td>
          <td>${c.specialty}</td>
          <td>${formatearFecha(c.appointmentDate)}</td>
          <td>${insigniaEstado(c.status)}</td>
        </tr>`).join('')}</tbody>
      </table></div>`;
    }
  } catch (e) { notificar(e.message, 'error'); }
}

/* ════════════════════════════════════════════════════════════════
   CITAS
════════════════════════════════════════════════════════════════ */
async function cargarCitas() {
  const cont = document.getElementById('citas-lista');
  cont.innerHTML = `<div style="padding:24px;text-align:center"><span class="girador oscuro"></span></div>`;
  try {
    const citas = await api('GET', `/citas/paciente/${pacienteActual.id}`);
    if (!citas.length) {
      cont.innerHTML = `<div class="vacio"><div class="vacio-icono">📅</div><h3>Sin citas registradas</h3><p>Agenda tu primera cita médica</p></div>`;
      return;
    }
    cont.innerHTML = `<table>
      <thead><tr><th>Médico</th><th>Especialidad</th><th>Fecha</th><th>Motivo</th><th>Estado</th><th>Acciones</th></tr></thead>
      <tbody>${citas.map(c => `<tr>
        <td><strong>${c.doctorName}</strong></td>
        <td>${c.specialty}</td>
        <td>${formatearFecha(c.appointmentDate)}</td>
        <td style="max-width:180px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${c.reason}</td>
        <td>${insigniaEstado(c.status)}</td>
        <td><div class="celda-acciones">
          ${c.status !== 'CANCELADA' && c.status !== 'COMPLETADA'
            ? `<button class="btn btn-peligro btn-sm" onclick="cancelarCita('${c.id}')">Cancelar</button>` : ''}
          <button class="btn btn-secundario btn-sm" onclick="eliminarCita('${c.id}')">Eliminar</button>
        </div></td>
      </tr>`).join('')}</tbody>
    </table>`;
  } catch (e) { notificar(e.message, 'error'); }
}

async function crearCita() {
  const cuerpo = {
    patientId:       pacienteActual.id,
    doctorName:      document.getElementById('cita-doctor').value.trim(),
    specialty:       document.getElementById('cita-especialidad').value.trim(),
    appointmentDate: document.getElementById('cita-fecha').value,
    reason:          document.getElementById('cita-motivo').value.trim(),
    notes:           document.getElementById('cita-notas').value.trim() || null,
  };
  if (!cuerpo.doctorName || !cuerpo.specialty || !cuerpo.appointmentDate || !cuerpo.reason) {
    notificar('Completa todos los campos obligatorios', 'error'); return;
  }

  mostrarCargando('btn-crear-cita', true);
  try {
    await api('POST', '/citas', cuerpo);
    cerrarModal('modal-nueva-cita');
    notificar('Cita agendada exitosamente');
    ['cita-doctor','cita-especialidad','cita-fecha','cita-motivo','cita-notas'].forEach(id => document.getElementById(id).value = '');
    cargarCitas();
  } catch (e) {
    notificar(e.message, 'error');
  } finally {
    document.getElementById('btn-crear-cita').disabled = false;
    document.getElementById('btn-crear-cita').innerHTML = 'Agendar cita';
  }
}

async function cancelarCita(id) {
  if (!confirm('¿Deseas cancelar esta cita?')) return;
  try {
    await api('PATCH', `/citas/${id}/cancelar`);
    notificar('Cita cancelada', 'aviso');
    cargarCitas();
  } catch (e) { notificar(e.message, 'error'); }
}

async function eliminarCita(id) {
  if (!confirm('¿Eliminar esta cita permanentemente?')) return;
  try {
    await api('DELETE', `/citas/${id}`);
    notificar('Cita eliminada');
    cargarCitas();
  } catch (e) { notificar(e.message, 'error'); }
}

/* ════════════════════════════════════════════════════════════════
   HISTORIA CLÍNICA
════════════════════════════════════════════════════════════════ */
async function cargarHistoria() {
  const cont = document.getElementById('historia-lista');
  cont.innerHTML = `<div style="padding:24px;text-align:center"><span class="girador oscuro"></span></div>`;
  try {
    const registros = await api('GET', `/historia-clinica/paciente/${pacienteActual.id}`);
    if (!registros.length) {
      cont.innerHTML = `<div class="vacio"><div class="vacio-icono">📋</div><h3>Sin registros clínicos</h3><p>Tu historial médico aparecerá aquí</p></div>`;
      return;
    }
    cont.innerHTML = `<table>
      <thead><tr><th>Fecha</th><th>Médico</th><th>Diagnóstico</th><th>Síntomas</th><th>Acciones</th></tr></thead>
      <tbody>${registros.map(r => `<tr>
        <td style="white-space:nowrap">${formatearSoloFecha(r.visitDate)}</td>
        <td><strong>${r.doctorName}</strong></td>
        <td style="max-width:200px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${r.diagnosis}</td>
        <td style="max-width:160px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;color:var(--tinta-tenue)">${r.symptoms || '—'}</td>
        <td><div class="celda-acciones">
          <button class="btn btn-secundario btn-sm" onclick='verHistoria(${JSON.stringify(r)})'>Ver detalle</button>
          <button class="btn btn-peligro btn-sm" onclick="eliminarHistoria('${r.id}')">Eliminar</button>
        </div></td>
      </tr>`).join('')}</tbody>
    </table>`;
  } catch (e) { notificar(e.message, 'error'); }
}

function verHistoria(r) {
  document.getElementById('historia-detalle-contenido').innerHTML = `
    <div class="grilla-detalle">
      <div class="item-detalle"><label>Fecha de visita</label><span>${formatearFecha(r.visitDate)}</span></div>
      <div class="item-detalle"><label>Médico</label><span>${r.doctorName}</span></div>
      <div class="item-detalle" style="grid-column:1/-1"><label>Síntomas</label><span>${r.symptoms || '—'}</span></div>
      <div class="item-detalle" style="grid-column:1/-1"><label>Diagnóstico</label><span>${r.diagnosis}</span></div>
      <div class="item-detalle" style="grid-column:1/-1"><label>Tratamiento</label><span>${r.treatment}</span></div>
    </div>`;
  abrirModal('modal-detalle-historia');
}

async function crearHistoria() {
  const cuerpo = {
    patientId:  pacienteActual.id,
    doctorName: document.getElementById('hist-doctor').value.trim(),
    symptoms:   document.getElementById('hist-sintomas').value.trim(),
    diagnosis:  document.getElementById('hist-diagnostico').value.trim(),
    treatment:  document.getElementById('hist-tratamiento').value.trim(),
    visitDate:  document.getElementById('hist-fecha').value || null,
  };
  if (!cuerpo.doctorName || !cuerpo.diagnosis || !cuerpo.treatment) {
    notificar('Completa los campos obligatorios', 'error'); return;
  }

  mostrarCargando('btn-crear-historia', true);
  try {
    await api('POST', '/historia-clinica', cuerpo);
    cerrarModal('modal-nueva-historia');
    notificar('Registro clínico guardado');
    ['hist-doctor','hist-sintomas','hist-diagnostico','hist-tratamiento','hist-fecha'].forEach(id => document.getElementById(id).value = '');
    cargarHistoria();
  } catch (e) {
    notificar(e.message, 'error');
  } finally {
    document.getElementById('btn-crear-historia').disabled = false;
    document.getElementById('btn-crear-historia').innerHTML = 'Guardar registro';
  }
}

async function eliminarHistoria(id) {
  if (!confirm('¿Eliminar este registro clínico?')) return;
  try {
    await api('DELETE', `/historia-clinica/${id}`);
    notificar('Registro eliminado');
    cargarHistoria();
  } catch (e) { notificar(e.message, 'error'); }
}

/* ════════════════════════════════════════════════════════════════
   PRESCRIPCIONES
════════════════════════════════════════════════════════════════ */
async function cargarPrescripciones() {
  const cont = document.getElementById('prescripciones-lista');
  cont.innerHTML = `<div style="padding:24px;text-align:center"><span class="girador oscuro"></span></div>`;
  try {
    const lista = await api('GET', `/prescripciones/paciente/${pacienteActual.id}`);
    if (!lista.length) {
      cont.innerHTML = `<div class="tarjeta"><div class="vacio"><div class="vacio-icono">💊</div><h3>Sin prescripciones</h3><p>Tus recetas médicas aparecerán aquí</p></div></div>`;
      return;
    }
    cont.innerHTML = lista.map(p => `
      <div class="tarjeta">
        <div class="tarjeta-cabecera">
          <div>
            <div class="tarjeta-titulo">Receta — ${formatearSoloFecha(p.issuedAt)}</div>
            <div class="tarjeta-subtitulo">Emitida por ${p.doctorName} · Válida hasta: ${formatearSoloFecha(p.validUntil) || 'Sin fecha'}</div>
          </div>
          <button class="btn btn-peligro btn-sm" onclick="eliminarPrescripcion('${p.id}')">Eliminar</button>
        </div>
        <div class="tabla-envolvente">
          <table>
            <thead><tr><th>Medicamento</th><th>Dosis</th><th>Frecuencia</th><th>Duración</th></tr></thead>
            <tbody>${(p.medications || []).map(m => `<tr>
              <td><strong>${m.medicationName}</strong></td>
              <td>${m.dosage}</td>
              <td>${m.frequency}</td>
              <td>${m.duration}</td>
            </tr>`).join('')}</tbody>
          </table>
        </div>
        ${p.instructions ? `<p style="margin-top:16px;font-size:13px;color:var(--tinta-tenue);padding:12px;background:var(--crema);border-radius:8px">📝 ${p.instructions}</p>` : ''}
      </div>`).join('');
  } catch (e) { notificar(e.message, 'error'); }
}

async function eliminarPrescripcion(id) {
  if (!confirm('¿Eliminar esta prescripción?')) return;
  try {
    await api('DELETE', `/prescripciones/${id}`);
    notificar('Prescripción eliminada');
    cargarPrescripciones();
  } catch (e) { notificar(e.message, 'error'); }
}

/* ════════════════════════════════════════════════════════════════
   EXÁMENES
════════════════════════════════════════════════════════════════ */
async function cargarExamenes() {
  const cont = document.getElementById('examenes-lista');
  cont.innerHTML = `<div style="padding:24px;text-align:center"><span class="girador oscuro"></span></div>`;
  try {
    const examenes = await api('GET', `/examenes/paciente/${pacienteActual.id}`);
    if (!examenes.length) {
      cont.innerHTML = `<div class="vacio"><div class="vacio-icono">🔬</div><h3>Sin exámenes registrados</h3><p>Solicita tu primer examen médico</p></div>`;
      return;
    }
    cont.innerHTML = `<table>
      <thead><tr><th>Tipo de examen</th><th>Solicitado por</th><th>Motivo</th><th>Fecha programada</th><th>Estado</th><th>Resultados</th></tr></thead>
      <tbody>${examenes.map(e => `<tr>
        <td><strong>${e.examType}</strong></td>
        <td>${e.requestedBy}</td>
        <td style="max-width:160px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${e.reason}</td>
        <td>${e.scheduledDate ? formatearFecha(e.scheduledDate) : '—'}</td>
        <td>${insigniaEstado(e.status)}</td>
        <td style="max-width:160px;font-size:13px;color:var(--tinta-tenue)">${e.results || '—'}</td>
      </tr>`).join('')}</tbody>
    </table>`;
  } catch (e) { notificar(e.message, 'error'); }
}

async function crearExamen() {
  const cuerpo = {
    patientId:     pacienteActual.id,
    examType:      document.getElementById('exam-tipo').value.trim(),
    requestedBy:   document.getElementById('exam-doctor').value.trim(),
    reason:        document.getElementById('exam-motivo').value.trim(),
    scheduledDate: document.getElementById('exam-fecha').value || null,
  };
  if (!cuerpo.examType || !cuerpo.requestedBy || !cuerpo.reason) {
    notificar('Completa los campos obligatorios', 'error'); return;
  }

  mostrarCargando('btn-crear-examen', true);
  try {
    await api('POST', '/examenes', cuerpo);
    cerrarModal('modal-nuevo-examen');
    notificar('Examen solicitado exitosamente');
    ['exam-tipo','exam-doctor','exam-motivo','exam-fecha'].forEach(id => document.getElementById(id).value = '');
    cargarExamenes();
  } catch (e) {
    notificar(e.message, 'error');
  } finally {
    document.getElementById('btn-crear-examen').disabled = false;
    document.getElementById('btn-crear-examen').innerHTML = 'Solicitar examen';
  }
}

/* ════════════════════════════════════════════════════════════════
   CERRAR MODAL AL HACER CLIC EN EL FONDO
════════════════════════════════════════════════════════════════ */
document.querySelectorAll('.fondo-modal').forEach(fondo => {
  fondo.addEventListener('click', e => {
    if (e.target === fondo) fondo.classList.remove('abierto');
  });
});
