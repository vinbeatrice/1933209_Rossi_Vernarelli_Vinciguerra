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

        card.innerHTML = `
            <div class="sensor-name">${sensorName}</div>
            <div class="sensor-measurements">${measurementsHtml}</div>
            <div class="sensor-timestamp">${timestamp}</div>
        `;

        sensorGrid.appendChild(card);
    });
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

loadInitialLatest();
connectSse();