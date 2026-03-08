const INGESTION_BASE_URL = "http://localhost:8081";
const latestMap = {};

function formatTimestamp(timestamp) {
    if (!timestamp) {
        return "";
    }

    try {
        const date = new Date(timestamp);
        if (isNaN(date.getTime())) {
            return timestamp;
        }
        return date.toLocaleString();
    } catch (error) {
        return timestamp;
    }
}

function createMeasurementHtml(measurement) {
    const metric = measurement.metric ?? "";
    const value = measurement.value ?? "";
    const unit = measurement.unit ?? "";

    return `
        <div class="measurement-row">
            <div class="measurement-metric">${metric}</div>
            <div>
                <span class="measurement-value">${value}</span>
                <span class="measurement-unit">${unit}</span>
            </div>
        </div>
    `;
}

function isRestSensor(event) {
    return event.sourceType && event.sourceType.toUpperCase() === "REST";
}

function renderGrid() {
    const sensorGrid = document.getElementById("sensorGrid");
    sensorGrid.innerHTML = "";

    const events = Object.values(latestMap)
        .sort((a, b) => (a.sourceId ?? "").localeCompare(b.sourceId ?? ""));

    if (events.length === 0) {
        sensorGrid.innerHTML = `<div class="empty-message">No sensor data received yet.</div>`;
        return;
    }

    events.forEach(event => {
        const card = document.createElement("div");
        card.className = "sensor-card";

        const sensorName = event.sourceId ?? "Unknown sensor";
        const measurements = event.measurements ?? [];
        const timestamp = formatTimestamp(event.timestamp);

        const measurementsHtml = measurements.length > 0
            ? measurements.map(createMeasurementHtml).join("")
            : `<div class="empty-message">No measurements available</div>`;

        const refreshButtonHtml = isRestSensor(event)
            ? `<button class="refresh-button" data-sensor-id="${sensorName}" title="Refresh sensor">⟳</button>`
            : "";

        card.innerHTML = `
            <div class="sensor-name">${sensorName}</div>
            <div class="sensor-measurements">${measurementsHtml}</div>
            <div class="sensor-timestamp">${timestamp}</div>
            ${refreshButtonHtml}
        `;

        sensorGrid.appendChild(card);
    });

    bindRefreshButtons();
}

function bindRefreshButtons() {
    document.querySelectorAll(".refresh-button").forEach(button => {
        button.addEventListener("click", handleManualRefresh);
    });
}

async function handleManualRefresh(event) {
    const sensorId = event.target.dataset.sensorId;

    try {
        const response = await fetch(`${INGESTION_BASE_URL}/api/rest-sensors/${sensorId}/refresh`, {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("Unable to refresh sensor");
        }

        const updatedEvent = await response.json();
        latestMap[updatedEvent.sourceId] = updatedEvent;
        renderGrid();
    } catch (error) {
        console.error("Manual refresh failed:", error);
        alert("Error refreshing sensor.");
    }
}

async function loadInitialLatest() {
    try {
        const response = await fetch("/api/sensors/latest");
        const events = await response.json();

        events.forEach(event => {
            latestMap[event.sourceId] = event;
        });

        renderGrid();
    } catch (error) {
        console.error("Error loading initial sensor data:", error);
    }
}

function connectSse() {
    const eventSource = new EventSource("/api/sensors/stream");

    eventSource.addEventListener("sensor-event", function(event) {
        const data = JSON.parse(event.data);
        latestMap[data.sourceId] = data;
        renderGrid();
    });

    eventSource.onerror = function(error) {
        console.error("SSE connection error:", error);
    };
}

async function loadPollingInterval() {
    try {
        const response = await fetch(`${INGESTION_BASE_URL}/api/rest-sensors/polling-interval`);
        if (!response.ok) {
            throw new Error("Unable to get polling interval");
        }

        const data = await response.json();
        document.getElementById("pollingIntervalInput").value = data.pollingDelayMs;
    } catch (error) {
        console.error("Error loading polling interval:", error);
    }
}

async function updatePollingInterval() {
    const input = document.getElementById("pollingIntervalInput");
    const pollingDelayMs = parseInt(input.value, 10);

    if (isNaN(pollingDelayMs) || pollingDelayMs < 1000) {
        alert("Polling interval must be at least 1000 ms.");
        return;
    }

    try {
        const response = await fetch(`${INGESTION_BASE_URL}/api/rest-sensors/polling-interval`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ pollingDelayMs })
        });

        if (!response.ok) {
            throw new Error("Unable to update polling interval");
        }

        const data = await response.json();
        input.value = data.pollingDelayMs;
    } catch (error) {
        console.error("Error updating polling interval:", error);
        alert("Error updating polling interval.");
    }
}

document.getElementById("savePollingIntervalBtn").addEventListener("click", updatePollingInterval);

loadInitialLatest();
connectSse();
loadPollingInterval();