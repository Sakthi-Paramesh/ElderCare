/**
 * ElderCare Connect - Senior-Friendly Web Application
 */

const API_BASE = '/api';

// Application State
const state = {
    token: localStorage.getItem('eldercare_token') || null,
    user: JSON.parse(localStorage.getItem('eldercare_user') || 'null'),
    departments: [],
    hospitals: [],
    doctors: [],
    selectedDoctor: null,
    selectedSlot: null,
    selectedDate: null,
    unreadNotifications: 0
};

// Document Ready
document.addEventListener('DOMContentLoaded', () => {
    initAccessibility();
    initNavigation();
    initAuthUI();
    loadDepartments();
    loadHospitals();
    loadDoctors();

    if (state.token && state.user) {
        handleAuthenticatedState();
    } else {
        showView('view-home');
    }
});

/* ==========================================================
   1. ACCESSIBILITY & DISPLAY CONTROLS
   ========================================================== */
function initAccessibility() {
    const savedFontSize = localStorage.getItem('eldercare_fontsize') || 'standard';
    const savedContrast = localStorage.getItem('eldercare_contrast') === 'true';

    setFontSize(savedFontSize);
    if (savedContrast) toggleContrast(true);

    document.querySelectorAll('.font-size-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const size = e.target.dataset.size;
            setFontSize(size);
        });
    });

    const contrastBtn = document.getElementById('contrast-toggle-btn');
    if (contrastBtn) {
        contrastBtn.addEventListener('click', () => toggleContrast());
    }
}

function setFontSize(size) {
    document.body.classList.remove('font-large', 'font-xlarge');
    document.querySelectorAll('.font-size-btn').forEach(b => b.classList.remove('active'));

    if (size === 'large') {
        document.body.classList.add('font-large');
    } else if (size === 'xlarge') {
        document.body.classList.add('font-xlarge');
    }
    
    const activeBtn = document.querySelector(`.font-size-btn[data-size="${size}"]`);
    if (activeBtn) activeBtn.classList.add('active');
    localStorage.setItem('eldercare_fontsize', size);
}

function toggleContrast(forceVal) {
    const isContrast = forceVal !== undefined ? forceVal : !document.body.classList.contains('high-contrast');
    if (isContrast) {
        document.body.classList.add('high-contrast');
    } else {
        document.body.classList.remove('high-contrast');
    }
    localStorage.setItem('eldercare_contrast', isContrast);
}

/* ==========================================================
   2. AUTHENTICATION & DEMO LOGIN SWITCHER
   ========================================================== */
function initAuthUI() {
    // Demo login buttons
    document.querySelectorAll('.btn-demo-login').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const role = e.target.dataset.role;
            await performDemoLogin(role);
        });
    });

    // Login Form Submit
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            await loginUser(email, password);
        });
    }

    // Patient Register Form Submit
    const regPatientForm = document.getElementById('form-register-patient');
    if (regPatientForm) {
        regPatientForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const payload = {
                fullName: document.getElementById('reg-p-name').value,
                email: document.getElementById('reg-p-email').value,
                password: document.getElementById('reg-p-password').value,
                mobileNumber: document.getElementById('reg-p-mobile').value,
                dateOfBirth: document.getElementById('reg-p-dob').value || null,
                gender: document.getElementById('reg-p-gender').value,
                city: document.getElementById('reg-p-city').value,
                address: document.getElementById('reg-p-address').value,
                emergencyContactName: document.getElementById('reg-p-em-name').value,
                emergencyContactNumber: document.getElementById('reg-p-em-phone').value
            };

            try {
                const res = await fetch(`${API_BASE}/auth/register/patient`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const data = await res.json();
                if (res.ok && data.success) {
                    showToast('Registration successful! Logging you in...', 'success');
                    closeModal('modal-register-patient');
                    await loginUser(payload.email, payload.password);
                } else {
                    showToast(data.message || 'Registration failed', 'error');
                }
            } catch (err) {
                showToast('Registration error: ' + err.message, 'error');
            }
        });
    }
}

async function loginUser(email, password) {
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok && data.token) {
            state.token = data.token;
            state.user = {
                id: data.id,
                email: data.email,
                name: data.name,
                role: data.role
            };
            localStorage.setItem('eldercare_token', data.token);
            localStorage.setItem('eldercare_user', JSON.stringify(state.user));
            showToast(`Welcome, ${data.name || 'User'}!`, 'success');
            closeModal('modal-login');
            handleAuthenticatedState();
        } else {
            showToast(data.message || 'Invalid email or password', 'error');
        }
    } catch (err) {
        showToast('Login error: ' + err.message, 'error');
    }
}

async function performDemoLogin(role) {
    let email = 'patient@eldercare.com';
    let pass = 'patient123';

    if (role === 'doctor') {
        email = 'doctor@eldercare.com';
        pass = 'doctor123';
    } else if (role === 'admin') {
        email = 'admin@eldercare.com';
        pass = 'admin123';
    }

    showToast(`Logging in as Demo ${role.toUpperCase()}...`, 'info');
    await loginUser(email, pass);
}

function logout() {
    state.token = null;
    state.user = null;
    localStorage.removeItem('eldercare_token');
    localStorage.removeItem('eldercare_user');
    showToast('Logged out successfully', 'info');
    updateNavForRole(null);
    showView('view-home');
}

function handleAuthenticatedState() {
    updateNavForRole(state.user.role);
    startNotificationPolling();

    if (state.user.role === 'ROLE_PATIENT') {
        showView('view-patient-dashboard');
        loadPatientAppointments();
        loadPatientProfile();
    } else if (state.user.role === 'ROLE_DOCTOR') {
        showView('view-doctor-dashboard');
        loadDoctorAppointments();
    } else if (state.user.role === 'ROLE_ADMIN') {
        showView('view-admin-dashboard');
        loadAdminStats();
        loadPendingDoctors();
    } else {
        showView('view-home');
    }
}

/* ==========================================================
   3. NAVIGATION & VIEW ROUTING
   ========================================================== */
function initNavigation() {
    document.querySelectorAll('[data-view-target]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = e.currentTarget.dataset.viewTarget;
            showView(target);
        });
    });

    const logoutBtn = document.getElementById('nav-logout-btn');
    if (logoutBtn) logoutBtn.addEventListener('click', logout);

    // Emergency Hotline Call Modal
    const emergencyBtn = document.getElementById('emergency-call-btn');
    if (emergencyBtn) {
        emergencyBtn.addEventListener('click', (e) => {
            e.preventDefault();
            openModal('modal-emergency');
        });
    }
}

function showView(viewId) {
    document.querySelectorAll('.view-section').forEach(sec => sec.style.display = 'none');
    const target = document.getElementById(viewId);
    if (target) {
        target.style.display = 'block';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Refresh active navigation link
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.toggle('active', item.dataset.viewTarget === viewId);
    });
}

function updateNavForRole(role) {
    const guestActions = document.getElementById('nav-guest-actions');
    const userActions = document.getElementById('nav-user-actions');
    const navPatient = document.getElementById('nav-link-patient');
    const navDoctor = document.getElementById('nav-link-doctor');
    const navAdmin = document.getElementById('nav-link-admin');

    if (navPatient) navPatient.style.display = 'none';
    if (navDoctor) navDoctor.style.display = 'none';
    if (navAdmin) navAdmin.style.display = 'none';

    if (!role) {
        if (guestActions) guestActions.style.display = 'flex';
        if (userActions) userActions.style.display = 'none';
    } else {
        if (guestActions) guestActions.style.display = 'none';
        if (userActions) userActions.style.display = 'flex';

        const userNameEl = document.getElementById('nav-user-name');
        const userRoleTag = document.getElementById('nav-user-role-tag');

        if (userNameEl) userNameEl.textContent = state.user.name || state.user.email;
        if (userRoleTag) {
            userRoleTag.className = 'user-role-tag';
            if (role === 'ROLE_PATIENT') {
                userRoleTag.textContent = 'Senior / Patient';
                userRoleTag.classList.add('role-patient');
                if (navPatient) navPatient.style.display = 'inline-block';
            } else if (role === 'ROLE_DOCTOR') {
                userRoleTag.textContent = 'Doctor';
                userRoleTag.classList.add('role-doctor');
                if (navDoctor) navDoctor.style.display = 'inline-block';
            } else if (role === 'ROLE_ADMIN') {
                userRoleTag.textContent = 'Admin';
                userRoleTag.classList.add('role-admin');
                if (navAdmin) navAdmin.style.display = 'inline-block';
            }
        }
    }
}

/* ==========================================================
   4. DOCTOR DIRECTORY & SEARCH
   ========================================================== */
async function loadDepartments() {
    try {
        const res = await fetch(`${API_BASE}/departments`);
        state.departments = await res.json();
        renderDepartmentPills();
        populateDepartmentDropdowns();
    } catch (err) {
        console.error('Failed to load departments', err);
    }
}

async function loadHospitals() {
    try {
        const res = await fetch(`${API_BASE}/hospitals`);
        state.hospitals = await res.json();
        populateHospitalDropdowns();
    } catch (err) {
        console.error('Failed to load hospitals', err);
    }
}

async function loadDoctors(filters = {}) {
    const grid = document.getElementById('doctors-grid');
    if (!grid) return;
    grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px;"><p style="font-size: 1.2rem; color: var(--text-muted);">Finding specialized geriatric doctors...</p></div>';

    let url = `${API_BASE}/doctors`;
    const params = new URLSearchParams();
    if (filters.specializationId) params.append('specializationId', filters.specializationId);
    if (filters.hospitalId) params.append('hospitalId', filters.hospitalId);
    if (filters.city) params.append('city', filters.city);
    if (filters.name) params.append('name', filters.name);

    if ([...params].length > 0) {
        url += '?' + params.toString();
    }

    try {
        const res = await fetch(url);
        state.doctors = await res.json();
        renderDoctorCards(state.doctors);
    } catch (err) {
        grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--danger);"><p>Error loading doctors: ${err.message}</p></div>`;
    }
}

function getDoctorPhoto(doc) {
    const name = (doc.fullName || '').toLowerCase();
    if (name.includes('ananya')) {
        return 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=400&q=80';
    } else if (name.includes('rajesh')) {
        return 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=400&q=80';
    } else if (name.includes('suresh')) {
        return 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=400&q=80';
    } else if (name.includes('priya')) {
        return 'https://images.unsplash.com/photo-1594824813571-638f02614d3f?auto=format&fit=crop&w=400&q=80';
    }
    const fallbacks = [
        'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=400&q=80',
        'https://images.unsplash.com/photo-1582750433449-648ed127bb54?auto=format&fit=crop&w=400&q=80',
        'https://images.unsplash.com/photo-1622902046580-2b47f47f5471?auto=format&fit=crop&w=400&q=80'
    ];
    return fallbacks[(doc.id || 0) % fallbacks.length];
}

function renderDepartmentPills() {
    const container = document.getElementById('departments-strip');
    if (!container) return;

    const iconMap = {
        'Geriatric Medicine': 'bi-person-heart',
        'Cardiology': 'bi-heart-pulse-fill',
        'Orthopedics & Joint Care': 'bi-bandaid-fill',
        'Neurology & Memory Care': 'bi-lightbulb-fill',
        'Ophthalmology': 'bi-eye-fill',
        'General Medicine': 'bi-hospital-fill',
        'Physiotherapy & Rehab': 'bi-activity'
    };

    let html = `<div class="specialty-pill active" onclick="filterByDepartment(null, this)"><i class="bi bi-grid-fill"></i> All Specializations</div>`;
    state.departments.forEach(dep => {
        const icon = iconMap[dep.name] || 'bi-stethoscope';
        html += `<div class="specialty-pill" onclick="filterByDepartment(${dep.id}, this)"><i class="bi ${icon}"></i> ${dep.name}</div>`;
    });
    container.innerHTML = html;
}

function populateDepartmentDropdowns() {
    const selects = ['search-department-select', 'reg-d-department'];
    selects.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.innerHTML = '<option value="">All Specializations</option>' + 
                state.departments.map(d => `<option value="${d.id}">${d.name}</option>`).join('');
        }
    });
}

function populateHospitalDropdowns() {
    const selects = ['search-hospital-select', 'reg-d-hospital'];
    selects.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.innerHTML = '<option value="">All Hospitals</option>' + 
                state.hospitals.map(h => `<option value="${h.id}">${h.name} (${h.city})</option>`).join('');
        }
    });
}

function filterByDepartment(departmentId, pillElement) {
    document.querySelectorAll('.specialty-pill').forEach(p => p.classList.remove('active'));
    if (pillElement) {
        pillElement.classList.add('active');
    } else {
        const defaultPill = document.querySelector(`.specialty-pill`);
        if (defaultPill) defaultPill.classList.add('active');
    }

    const select = document.getElementById('search-department-select');
    if (select) select.value = departmentId || '';
    
    executeDoctorSearch();
}

function executeDoctorSearch() {
    const depId = document.getElementById('search-department-select')?.value;
    const hospId = document.getElementById('search-hospital-select')?.value;
    const city = document.getElementById('search-city-input')?.value;
    const name = document.getElementById('search-name-input')?.value;

    loadDoctors({
        specializationId: depId || null,
        hospitalId: hospId || null,
        city: city || null,
        name: name || null
    });
}

function renderDoctorCards(doctors) {
    const grid = document.getElementById('doctors-grid');
    if (!grid) return;

    if (!doctors || doctors.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="card card-clean p-5 mx-auto text-center" style="max-width: 500px;">
                    <div class="mx-auto mb-3 p-3 rounded-circle bg-light text-muted fs-2 d-flex align-items-center justify-content-center" style="width: 64px; height: 64px;">
                        <i class="bi bi-person-x"></i>
                    </div>
                    <h5 class="fw-bold mb-1 text-dark">No Specialists Found</h5>
                    <p class="text-muted small mb-4">No doctors matched your selected filters or city. Please adjust your search criteria or explore other departments.</p>
                    <div>
                        <button class="btn btn-outline-primary btn-sm px-4" onclick="resetSearch()">
                            <i class="bi bi-arrow-counterclockwise me-1"></i> Reset Filters
                        </button>
                    </div>
                </div>
            </div>
        `;
        return;
    }

    grid.innerHTML = doctors.map(doc => {
        const photoUrl = getDoctorPhoto(doc);
        return `
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card card-clean card-hover h-100 p-4 d-flex flex-column justify-content-between">
                    <div>
                        <div class="d-flex align-items-start gap-3 mb-3">
                            <div class="doctor-avatar-box">
                                <img src="${photoUrl}" alt="${doc.fullName}" class="doctor-avatar-img" onerror="this.src='https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=400&q=80'">
                                <span class="doctor-online-dot" title="Verified & Accepting Appointments"></span>
                            </div>
                            <div class="flex-grow-1">
                                <div class="d-flex align-items-center gap-1 text-warning small mb-1">
                                    <i class="bi bi-star-fill text-warning"></i>
                                    <span class="fw-semibold text-dark">4.9</span>
                                    <span class="text-muted" style="font-size: 0.75rem;">(Top Rated)</span>
                                </div>
                                <h6 class="fw-bold mb-1 text-dark">${doc.fullName}</h6>
                                <span class="badge bg-primary-subtle text-primary fw-medium rounded-pill px-2.5 py-1 small">${doc.departmentName || 'Specialist'}</span>
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-1 mb-3">
                            <span class="badge bg-light text-secondary border fw-normal py-1 px-2">
                                <i class="bi bi-mortarboard text-primary me-1"></i>${doc.qualification || 'Senior Physician'}
                            </span>
                            <span class="badge bg-light text-secondary border fw-normal py-1 px-2">
                                <i class="bi bi-clock-history text-success me-1"></i>${doc.experienceYears}+ Yrs Exp
                            </span>
                            <span class="badge bg-light text-secondary border fw-normal py-1 px-2">
                                <i class="bi bi-hospital text-secondary me-1"></i>${doc.hospitalName || 'City Wing'}
                            </span>
                        </div>

                        <p class="text-muted small mb-3" style="line-height: 1.5; min-height: 40px;">
                            ${doc.about || 'Dedicated to geriatric medical evaluation, chronic medication safety, and elderly comfort.'}
                        </p>
                    </div>

                    <div class="pt-3 border-top d-flex align-items-center justify-content-between">
                        <div>
                            <span class="small text-muted d-block" style="font-size: 0.75rem;">Consultation Fee</span>
                            <div class="fw-bold text-dark fs-5">₹${doc.consultationFee} <span class="text-muted fw-normal fs-6">/ visit</span></div>
                        </div>
                        <button class="btn btn-primary btn-sm px-3 py-2 rounded-2" onclick="initiateBooking(${doc.id})">
                            <i class="bi bi-calendar-check me-1"></i> Book Slot
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function resetSearch() {
    if (document.getElementById('search-department-select')) document.getElementById('search-department-select').value = '';
    if (document.getElementById('search-hospital-select')) document.getElementById('search-hospital-select').value = '';
    if (document.getElementById('search-city-input')) document.getElementById('search-city-input').value = '';
    if (document.getElementById('search-name-input')) document.getElementById('search-name-input').value = '';
    loadDoctors();
}

/* ==========================================================
   5. APPOINTMENT BOOKING WORKFLOW
   ========================================================== */
async function initiateBooking(doctorId) {
    if (!state.token || !state.user) {
        showToast('Please sign in as a Patient to book an appointment', 'info');
        openModal('modal-login');
        return;
    }

    if (state.user.role !== 'ROLE_PATIENT') {
        showToast('Only registered Patients can book appointments. (Switching to Patient Demo account)', 'info');
        await performDemoLogin('patient');
    }

    state.selectedDoctor = state.doctors.find(d => d.id === doctorId);
    if (!state.selectedDoctor) {
        try {
            const res = await fetch(`${API_BASE}/doctors/${doctorId}`);
            state.selectedDoctor = await res.json();
        } catch (e) {
            showToast('Unable to load doctor information', 'error');
            return;
        }
    }

    // Populate Doctor Booking Modal Info
    document.getElementById('book-doc-name').textContent = state.selectedDoctor.fullName;
    document.getElementById('book-doc-spec').textContent = state.selectedDoctor.departmentName;
    document.getElementById('book-doc-hospital').textContent = `${state.selectedDoctor.hospitalName}, ${state.selectedDoctor.hospitalCity}`;
    document.getElementById('book-doc-fee').textContent = `₹${state.selectedDoctor.consultationFee}`;

    // Set Date Picker to Tomorrow by default
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateStr = tomorrow.toISOString().split('T')[0];
    
    const datePicker = document.getElementById('book-date-input');
    datePicker.min = new Date().toISOString().split('T')[0];
    datePicker.value = dateStr;
    state.selectedDate = dateStr;
    state.selectedSlot = null;

    datePicker.onchange = (e) => {
        state.selectedDate = e.target.value;
        loadAvailableSlots(doctorId, state.selectedDate);
    };

    openModal('modal-book-appointment');
    await loadAvailableSlots(doctorId, dateStr);
}

async function loadAvailableSlots(doctorId, dateStr) {
    const container = document.getElementById('book-slots-grid');
    const confirmBtn = document.getElementById('btn-confirm-booking');
    confirmBtn.disabled = true;
    container.innerHTML = '<p style="color: var(--text-muted); grid-column: 1/-1;">Loading available time slots...</p>';

    try {
        const res = await fetch(`${API_BASE}/doctors/${doctorId}/slots?date=${dateStr}`);
        const data = await res.json();

        if (!data.availableSlots || data.availableSlots.length === 0) {
            container.innerHTML = '<p style="color: var(--danger); grid-column: 1/-1; padding: 12px; font-weight: 600;">No available slots for this date. The doctor may not be scheduled or all slots are booked. Please choose another date.</p>';
            return;
        }

        container.innerHTML = data.availableSlots.map(slot => {
            const formatted = slot.substring(0, 5); // "09:00:00" -> "09:00"
            return `<div class="slot-pill" onclick="selectSlot('${slot}', this)">${formatted}</div>`;
        }).join('');
    } catch (err) {
        container.innerHTML = `<p style="color: var(--danger); grid-column: 1/-1;">Error loading slots: ${err.message}</p>`;
    }
}

function selectSlot(timeString, element) {
    document.querySelectorAll('.slot-pill').forEach(s => s.classList.remove('selected'));
    element.classList.add('selected');
    state.selectedSlot = timeString;

    const confirmBtn = document.getElementById('btn-confirm-booking');
    confirmBtn.disabled = false;
    confirmBtn.innerHTML = `Confirm Booking for ${timeString.substring(0, 5)}`;
}

async function confirmBooking() {
    if (!state.selectedDoctor || !state.selectedDate || !state.selectedSlot) {
        showToast('Please select an appointment date and slot', 'error');
        return;
    }

    const payload = {
        doctorId: state.selectedDoctor.id,
        hospitalId: state.selectedDoctor.hospitalId,
        appointmentDate: state.selectedDate,
        appointmentTime: state.selectedSlot
    };

    try {
        const res = await fetch(`${API_BASE}/appointments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok && data.id) {
            closeModal('modal-book-appointment');
            showToast('Appointment successfully requested!', 'success');
            showView('view-patient-dashboard');
            loadPatientAppointments();
        } else {
            showToast(data.message || 'Failed to book appointment', 'error');
        }
    } catch (err) {
        showToast('Booking error: ' + err.message, 'error');
    }
}

/* ==========================================================
   6. PATIENT DASHBOARD & MEDICAL RECORDS
   ========================================================== */
async function loadPatientAppointments() {
    const container = document.getElementById('patient-appointments-list');
    if (!container) return;

    container.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary" role="status"></div><p class="text-muted small mt-2">Loading your consultations...</p></div>';

    try {
        const res = await fetch(`${API_BASE}/appointments/patient/my`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const appts = await res.json();

        // Update dashboard counter
        const upcomingCount = appts ? appts.filter(a => a.status === 'PENDING' || a.status === 'CONFIRMED').length : 0;
        const upcomingEl = document.getElementById('pat-stat-upcoming');
        if (upcomingEl) upcomingEl.textContent = upcomingCount;

        if (!appts || appts.length === 0) {
            container.innerHTML = `
                <div class="card card-clean p-5 text-center my-3">
                    <div class="mx-auto mb-3 p-3 rounded-circle bg-light text-muted fs-2 d-flex align-items-center justify-content-center" style="width: 64px; height: 64px;">
                        <i class="bi bi-calendar-x"></i>
                    </div>
                    <h6 class="fw-bold mb-1 text-dark">No Appointments Scheduled Yet</h6>
                    <p class="text-muted small mb-3">You have not booked any consultations yet. Browse verified geriatric doctors and book senior care.</p>
                    <div>
                        <button class="btn btn-primary btn-sm px-3" onclick="showView('view-home')">
                            <i class="bi bi-search me-1"></i> Find a Specialist
                        </button>
                    </div>
                </div>
            `;
            return;
        }

        container.innerHTML = appts.map(a => {
            const dateObj = new Date(a.appointmentDate);
            const day = dateObj.getDate();
            const month = dateObj.toLocaleString('default', { month: 'short' });

            return `
                <div class="card card-clean p-3 mb-3">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <div class="date-badge">
                                <div class="date-badge-day">${day}</div>
                                <div class="date-badge-month">${month}</div>
                                <div class="small text-muted mt-1"><i class="bi bi-clock me-1"></i>${a.appointmentTime ? a.appointmentTime.substring(0, 5) : ''}</div>
                            </div>
                            <div>
                                <h6 class="fw-bold mb-1 text-dark">${a.doctorName}</h6>
                                <div class="small text-muted mb-1">
                                    <span class="badge bg-primary-subtle text-primary me-2">${a.doctorSpecialization}</span>
                                    <span>Fee: <strong class="text-dark">₹${a.consultationFee}</strong></span>
                                </div>
                                <div class="small text-secondary"><i class="bi bi-geo-alt text-muted me-1"></i>${a.hospitalName}, ${a.hospitalCity}</div>
                            </div>
                        </div>

                        <div class="d-flex flex-column align-items-end gap-2">
                            <span class="badge ${a.status === 'CONFIRMED' ? 'bg-success-subtle text-success border border-success-subtle' : a.status === 'PENDING' ? 'bg-warning-subtle text-warning-emphasis border border-warning-subtle' : a.status === 'COMPLETED' ? 'bg-primary-subtle text-primary border border-primary-subtle' : 'bg-danger-subtle text-danger border border-danger-subtle'} px-3 py-1.5 rounded-pill">
                                <i class="bi ${a.status === 'CONFIRMED' ? 'bi-check-circle-fill' : a.status === 'PENDING' ? 'bi-hourglass-split' : a.status === 'COMPLETED' ? 'bi-patch-check-fill' : 'bi-x-circle-fill'} me-1"></i>
                                ${a.status}
                            </span>
                            <div class="d-flex gap-2">
                                ${a.status === 'PENDING' || a.status === 'CONFIRMED' ? `
                                    <button class="btn btn-sm btn-outline-danger" onclick="cancelAppointment(${a.id})">
                                        <i class="bi bi-x-lg me-1"></i> Cancel
                                    </button>
                                ` : ''}
                                ${a.hasMedicalRecord || a.status === 'COMPLETED' ? `
                                    <button class="btn btn-sm btn-outline-primary" onclick="viewPrescription(${a.id})">
                                        <i class="bi bi-prescription2 me-1"></i> Prescription
                                    </button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger"><i class="bi bi-exclamation-triangle-fill me-2"></i>Error loading appointments: ${err.message}</div>`;
    }
}

async function cancelAppointment(appointmentId) {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;

    try {
        const res = await fetch(`${API_BASE}/appointments/${appointmentId}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify({ status: 'CANCELLED' })
        });

        if (res.ok) {
            showToast('Appointment cancelled', 'info');
            loadPatientAppointments();
        } else {
            const data = await res.json();
            showToast(data.message || 'Unable to cancel', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function viewPrescription(appointmentId) {
    try {
        const res = await fetch(`${API_BASE}/medical-records/appointment/${appointmentId}`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const rec = await res.json();

        if (!res.ok) {
            showToast(rec.message || 'Prescription not found', 'info');
            return;
        }

        document.getElementById('rx-modal-patient-name').textContent = rec.patientName;
        document.getElementById('rx-modal-doc-name').textContent = rec.doctorName;
        document.getElementById('rx-modal-doc-spec').textContent = rec.doctorSpecialization;
        document.getElementById('rx-modal-date').textContent = rec.appointmentDate;
        document.getElementById('rx-modal-diagnosis').textContent = rec.diagnosis || 'Clinical Assessment';
        document.getElementById('rx-modal-notes').textContent = rec.consultationNotes || 'None';

        const tbody = document.getElementById('rx-table-body');
        if (rec.prescriptions && rec.prescriptions.length > 0) {
            tbody.innerHTML = rec.prescriptions.map(p => `
                <tr>
                    <td><strong>${p.medicineName}</strong></td>
                    <td>${p.dosage || 'As directed'}</td>
                    <td>${p.duration || 'N/A'}</td>
                    <td>${p.instructions || '-'}</td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: var(--text-muted);">No medication prescribed</td></tr>`;
        }

        openModal('modal-view-prescription');
    } catch (err) {
        showToast('Error loading prescription: ' + err.message, 'error');
    }
}

async function loadPatientProfile() {
    try {
        const res = await fetch(`${API_BASE}/patients/me`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const data = await res.json();
        if (res.ok) {
            if (document.getElementById('prof-name')) document.getElementById('prof-name').value = data.fullName || '';
            if (document.getElementById('prof-mobile')) document.getElementById('prof-mobile').value = data.mobileNumber || '';
            if (document.getElementById('prof-city')) document.getElementById('prof-city').value = data.city || '';
            if (document.getElementById('prof-address')) document.getElementById('prof-address').value = data.address || '';
            if (document.getElementById('prof-em-name')) document.getElementById('prof-em-name').value = data.emergencyContactName || '';
            if (document.getElementById('prof-em-phone')) document.getElementById('prof-em-phone').value = data.emergencyContactNumber || '';
            if (document.getElementById('profile-card-name') && data.fullName) {
                document.getElementById('profile-card-name').textContent = data.fullName;
            }
            const savedAvatar = localStorage.getItem('eldercare_patient_avatar');
            if (savedAvatar && document.getElementById('patient-profile-avatar')) {
                document.getElementById('patient-profile-avatar').src = savedAvatar;
            }
        }
    } catch (e) {
        console.error('Failed to load profile', e);
    }
}

/* ==========================================================
   7. DOCTOR DASHBOARD & CONSULTATION WRITER
   ========================================================== */
async function loadDoctorAppointments() {
    const container = document.getElementById('doctor-appointments-list');
    if (!container) return;

    container.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-teal" role="status" style="color: var(--medical-teal);"></div><p class="text-muted small mt-2">Loading schedule...</p></div>';

    try {
        const res = await fetch(`${API_BASE}/appointments/doctor/my`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const appts = await res.json();

        if (!appts || appts.length === 0) {
            container.innerHTML = `
                <div class="card card-clean p-5 text-center my-3">
                    <div class="mx-auto mb-3 p-3 rounded-circle bg-light text-muted fs-2 d-flex align-items-center justify-content-center" style="width: 64px; height: 64px;">
                        <i class="bi bi-calendar-check"></i>
                    </div>
                    <h6 class="fw-bold mb-1 text-dark">No Consultations Scheduled</h6>
                    <p class="text-muted small mb-0">You currently have no patient appointments in your queue.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = appts.map(a => {
            const dateObj = new Date(a.appointmentDate);
            const day = dateObj.getDate();
            const month = dateObj.toLocaleString('default', { month: 'short' });

            return `
                <div class="card card-clean p-3 mb-3">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <div class="date-badge">
                                <div class="date-badge-day">${day}</div>
                                <div class="date-badge-month">${month}</div>
                                <div class="small text-muted mt-1"><i class="bi bi-clock me-1"></i>${a.appointmentTime ? a.appointmentTime.substring(0, 5) : ''}</div>
                            </div>
                            <div>
                                <h6 class="fw-bold mb-1 text-dark">${a.patientName}</h6>
                                <div class="small text-muted mb-1">
                                    <span><i class="bi bi-telephone text-muted me-1"></i>${a.patientMobile || 'N/A'}</span>
                                    <span class="mx-2">•</span>
                                    <span><i class="bi bi-heart text-danger me-1"></i>Caregiver: ${a.patientEmergencyName} (${a.patientEmergencyContact})</span>
                                </div>
                                <div class="small text-secondary"><i class="bi bi-hospital text-muted me-1"></i>${a.hospitalName}</div>
                            </div>
                        </div>

                        <div class="d-flex flex-column align-items-end gap-2">
                            <span class="badge ${a.status === 'CONFIRMED' ? 'bg-success-subtle text-success border border-success-subtle' : a.status === 'PENDING' ? 'bg-warning-subtle text-warning-emphasis border border-warning-subtle' : a.status === 'COMPLETED' ? 'bg-primary-subtle text-primary border border-primary-subtle' : 'bg-danger-subtle text-danger border border-danger-subtle'} px-3 py-1.5 rounded-pill">
                                ${a.status}
                            </span>
                            <div class="d-flex gap-2">
                                ${a.status === 'PENDING' ? `
                                    <button class="btn btn-sm btn-success" onclick="updateDoctorApptStatus(${a.id}, 'CONFIRMED')">
                                        <i class="bi bi-check-lg me-1"></i> Accept
                                    </button>
                                    <button class="btn btn-sm btn-outline-danger" onclick="updateDoctorApptStatus(${a.id}, 'REJECTED')">
                                        <i class="bi bi-x-lg me-1"></i> Reject
                                    </button>
                                ` : ''}
                                ${a.status === 'CONFIRMED' ? `
                                    <button class="btn btn-sm btn-primary" onclick="openConsultationModal(${a.id}, '${a.patientName}')">
                                        <i class="bi bi-prescription2 me-1"></i> Write Rx & Complete
                                    </button>
                                ` : ''}
                                ${a.status === 'COMPLETED' ? `
                                    <button class="btn btn-sm btn-outline-primary" onclick="viewPrescription(${a.id})">
                                        <i class="bi bi-eye me-1"></i> View Rx
                                    </button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger"><i class="bi bi-exclamation-triangle-fill me-2"></i>Error loading schedule: ${err.message}</div>`;
    }
}

async function updateDoctorApptStatus(appointmentId, status) {
    try {
        const res = await fetch(`${API_BASE}/appointments/${appointmentId}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify({ status })
        });

        if (res.ok) {
            showToast(`Appointment marked as ${status}`, 'success');
            loadDoctorAppointments();
        } else {
            const err = await res.json();
            showToast(err.message || 'Status update failed', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

let activeConsultationApptId = null;

function openConsultationModal(appointmentId, patientName) {
    activeConsultationApptId = appointmentId;
    document.getElementById('rx-input-patient-name').textContent = patientName;
    document.getElementById('rx-input-diagnosis').value = '';
    document.getElementById('rx-input-notes').value = '';

    // Reset prescriptions list to 1 blank row
    const container = document.getElementById('rx-medicines-container');
    container.innerHTML = '';
    addPrescriptionRow();

    openModal('modal-write-consultation');
}

function addPrescriptionRow() {
    const container = document.getElementById('rx-medicines-container');
    const row = document.createElement('div');
    row.className = 'row g-2 rx-med-row align-items-center mb-2';
    row.innerHTML = `
        <div class="col-md-4">
            <input type="text" class="form-control rounded-3 med-name" placeholder="Medicine (e.g. Telmisartan 40mg)" required>
        </div>
        <div class="col-md-3">
            <input type="text" class="form-control rounded-3 med-dosage" placeholder="Dosage (e.g. 1-0-0 After Meal)">
        </div>
        <div class="col-md-2">
            <input type="text" class="form-control rounded-3 med-duration" placeholder="Duration (e.g. 30 Days)">
        </div>
        <div class="col-md-2">
            <input type="text" class="form-control rounded-3 med-instructions" placeholder="Instructions">
        </div>
        <div class="col-md-1 text-center">
            <button type="button" class="btn btn-outline-danger btn-sm rounded-circle p-2" onclick="this.closest('.rx-med-row').remove()" title="Remove Medicine">
                <i class="bi bi-trash"></i>
            </button>
        </div>
    `;
    container.appendChild(row);
}

async function submitConsultationRecord() {
    if (!activeConsultationApptId) return;

    const diagnosis = document.getElementById('rx-input-diagnosis').value;
    const notes = document.getElementById('rx-input-notes').value;

    if (!diagnosis) {
        showToast('Please enter a clinical diagnosis', 'error');
        return;
    }

    const prescriptions = [];
    document.querySelectorAll('.rx-med-row').forEach(row => {
        const name = row.querySelector('.med-name')?.value;
        if (name && name.trim()) {
            prescriptions.push({
                medicineName: name.trim(),
                dosage: row.querySelector('.med-dosage')?.value || '',
                duration: row.querySelector('.med-duration')?.value || '',
                instructions: row.querySelector('.med-instructions')?.value || ''
            });
        }
    });

    const payload = {
        appointmentId: activeConsultationApptId,
        diagnosis,
        consultationNotes: notes,
        prescriptions
    };

    try {
        const res = await fetch(`${API_BASE}/medical-records`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (res.ok && data.id) {
            closeModal('modal-write-consultation');
            showToast('Consultation record and prescription issued successfully!', 'success');
            loadDoctorAppointments();
        } else {
            showToast(data.message || 'Failed to save medical record', 'error');
        }
    } catch (e) {
        showToast('Error saving consultation: ' + e.message, 'error');
    }
}

/* ==========================================================
   8. ADMIN DASHBOARD & APPROVALS
   ========================================================== */
async function loadAdminStats() {
    try {
        const res = await fetch(`${API_BASE}/admin/stats`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const stats = await res.json();
        if (res.ok) {
            document.getElementById('stat-patients').textContent = stats.totalPatients;
            document.getElementById('stat-doctors').textContent = stats.totalDoctors;
            document.getElementById('stat-pending').textContent = stats.pendingDoctors;
            document.getElementById('stat-appts').textContent = stats.totalAppointments;
        }
    } catch (e) {
        console.error('Failed to load admin stats', e);
    }
}

async function loadPendingDoctors() {
    const container = document.getElementById('admin-pending-doctors');
    if (!container) return;

    container.innerHTML = '<div class="p-3 text-muted"><div class="spinner-border spinner-border-sm text-warning me-2"></div>Checking pending approvals...</div>';

    try {
        const res = await fetch(`${API_BASE}/admin/doctors/pending`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const docs = await res.json();

        if (!docs || docs.length === 0) {
            container.innerHTML = '<div class="alert alert-success d-flex align-items-center mb-0"><i class="bi bi-check-circle-fill fs-5 me-2"></i>All doctors are currently verified and approved. No pending items in queue.</div>';
            return;
        }

        container.innerHTML = docs.map(d => {
            const photoUrl = getDoctorPhoto(d);
            return `
                <div class="card card-clean p-3 mb-3">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <img src="${photoUrl}" alt="${d.fullName}" class="rounded-circle" style="width: 52px; height: 52px; object-fit: cover; border: 1px solid var(--medical-border);">
                            <div>
                                <h6 class="fw-bold mb-1 text-dark">${d.fullName}</h6>
                                <div class="small text-muted mb-1">
                                    <span class="badge bg-primary-subtle text-primary me-1">${d.departmentName}</span>
                                    <span>${d.qualification}</span> • <span>${d.experienceYears} Yrs Experience</span>
                                </div>
                                <div class="small text-secondary"><i class="bi bi-hospital me-1"></i>${d.hospitalName} | <i class="bi bi-envelope me-1"></i>${d.email} | Fee: <strong class="text-dark">₹${d.consultationFee}</strong></div>
                            </div>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-sm btn-success" onclick="reviewDoctorApproval(${d.id}, true)">
                                <i class="bi bi-check-lg me-1"></i> Approve
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="reviewDoctorApproval(${d.id}, false)">
                                <i class="bi bi-x-lg me-1"></i> Reject
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (e) {
        container.innerHTML = `<div class="alert alert-danger"><i class="bi bi-exclamation-triangle-fill me-2"></i>Error loading pending approvals: ${e.message}</div>`;
    }
}

async function reviewDoctorApproval(doctorId, approve) {
    const action = approve ? 'approve' : 'reject';
    try {
        const res = await fetch(`${API_BASE}/admin/doctors/${doctorId}/${action}`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${state.token}` }
        });

        if (res.ok) {
            showToast(`Doctor registration ${approve ? 'approved' : 'rejected'}`, 'success');
            loadAdminStats();
            loadPendingDoctors();
            loadDoctors();
        } else {
            showToast('Action failed', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

/* ==========================================================
   9. NOTIFICATIONS DRAWER
   ========================================================== */
let pollInterval = null;

function startNotificationPolling() {
    if (pollInterval) clearInterval(pollInterval);
    checkNotifications();
    pollInterval = setInterval(checkNotifications, 15000); // every 15s
}

async function checkNotifications() {
    if (!state.token) return;
    try {
        const res = await fetch(`${API_BASE}/notifications/unread-count`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        if (!res.ok) {
            if (res.status === 401 || res.status === 403 || res.status === 500) {
                if (pollInterval) clearInterval(pollInterval);
            }
            return;
        }
        const data = await res.json();
        const badge = document.getElementById('notif-count-badge');
        if (badge) {
            badge.textContent = data.unreadCount || 0;
            badge.style.display = data.unreadCount > 0 ? 'flex' : 'none';
        }
    } catch (e) {
        // silent fail
    }
}

async function toggleNotificationsDrawer() {
    const drawer = document.getElementById('notif-drawer');
    if (!drawer) return;

    if (drawer.classList.contains('active')) {
        drawer.classList.remove('active');
    } else {
        drawer.classList.add('active');
        await loadNotificationList();
    }
}

async function loadNotificationList() {
    const list = document.getElementById('notif-items-list');
    list.innerHTML = '<p style="padding: 16px; color: var(--text-muted);">Loading alerts...</p>';

    try {
        const res = await fetch(`${API_BASE}/notifications`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        const notifs = await res.json();

        if (!notifs || notifs.length === 0) {
            list.innerHTML = '<p style="padding: 16px; color: var(--text-muted); text-align: center;">No notifications</p>';
            return;
        }

        list.innerHTML = notifs.map(n => `
            <div class="notif-item ${!n.read ? 'unread' : ''}" onclick="markNotificationRead(${n.id})">
                <div>${n.message}</div>
                <div class="notif-time">${new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} • ${new Date(n.createdAt).toLocaleDateString()}</div>
            </div>
        `).join('');
    } catch (e) {
        list.innerHTML = `<p style="padding: 16px; color: var(--danger);">Error: ${e.message}</p>`;
    }
}

async function markNotificationRead(id) {
    try {
        await fetch(`${API_BASE}/notifications/${id}/read`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        checkNotifications();
        loadNotificationList();
    } catch (e) {}
}

async function markAllNotificationsRead() {
    try {
        await fetch(`${API_BASE}/notifications/read-all`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        checkNotifications();
        loadNotificationList();
    } catch (e) {}
}

/* ==========================================================
   10. MODAL & TOAST UTILITIES
   ========================================================== */
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast-medical ${type}`;
    const icon = type === 'success' ? 'bi-check-circle-fill text-success' : type === 'error' ? 'bi-exclamation-triangle-fill text-danger' : 'bi-info-circle-fill text-primary';
    toast.innerHTML = `<i class="bi ${icon} fs-5"></i><span>${message}</span>`;
    
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
