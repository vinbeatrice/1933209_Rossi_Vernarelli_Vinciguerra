const PROCESSING_BASE_URL = "http://localhost:8082";

let actuatorsCache = {};

async function fetchActuators() {
    const response = await fetch(`${PROCESSING_BASE_URL}/actuators`);

    if (!response.ok) {
        throw new Error("Unable to load actuators");
    }

    return await response.json();
}

function renderActuators() {
    const grid = document.getElementById("actuatorsGrid");
    grid.innerHTML = "";

    const actuatorNames = Object.keys(actuatorsCache).sort();

    if (actuatorNames.length === 0) {
        grid.innerHTML = `<div class="empty-message">No actuators available.</div>`;
        return;
    }

    actuatorNames.forEach(name => {
        const actuator = actuatorsCache[name];
        const state = actuator?.state ?? "OFF";
        const isOn = state.toUpperCase() === "ON";

        const card = document.createElement("div");
        card.className = "actuator-card";

        card.innerHTML = `
            <div class="actuator-name">${name}</div>

            <div class="actuator-state">
                Current state:
                <span class="actuator-state-value ${isOn ? "state-on" : "state-off"}">
                    ${state}
                </span>
            </div>

            <div class="actuator-control">
                <span class="status-label">${isOn ? "ON" : "OFF"}</span>
                <label class="switch">
                    <input type="checkbox" class="actuator-toggle" data-name="${name}" ${isOn ? "checked" : ""}>
                    <span class="slider"></span>
                </label>
            </div>
        `;

        grid.appendChild(card);
    });

    bindToggleActions();
}

function bindToggleActions() {
    document.querySelectorAll(".actuator-toggle").forEach(toggle => {
        toggle.addEventListener("change", handleToggleActuator);
    });
}

async function handleToggleActuator(event) {
    const actuatorName = event.target.dataset.name;
    const targetState = event.target.checked ? "ON" : "OFF";

    try {
        const response = await fetch(`${PROCESSING_BASE_URL}/actuators/${actuatorName}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ state: targetState })
        });

        if (!response.ok) {
            throw new Error("Unable to update actuator");
        }

        actuatorsCache[actuatorName] = { state: targetState };
        renderActuators();
    } catch (error) {
        console.error("Error updating actuator:", error);
        alert("Error updating actuator state.");
        await loadActuators();
    }
}

async function loadActuators() {
    try {
        actuatorsCache = await fetchActuators();
        renderActuators();
    } catch (error) {
        console.error("Error loading actuators:", error);
        const grid = document.getElementById("actuatorsGrid");
        grid.innerHTML = `<div class="empty-message">Error loading actuators.</div>`;
    }
}

loadActuators();