const INGESTION_BASE_URL = "http://localhost:8081";
const PROCESSING_BASE_URL = "http://localhost:8082";
const latestMap = {};
let activeRules = [];
let latestTriggeredRule = null;

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

function isRuleSatisfied(rule, measurement) {
    if (!rule || !measurement) {
        return false;
    }

    if ((rule.metric ?? "").toLowerCase() !== (measurement.metric ?? "").toLowerCase()) {
        return false;
    }

    const sensorValue = measurement.value;
    const threshold = rule.thresholdValue;
    const operator = rule.operator;

    switch (operator) {
        case ">":
            return sensorValue > threshold;
        case ">=":
            return sensorValue >= threshold;
        case "<":
            return sensorValue < threshold;
        case "<=":
            return sensorValue <= threshold;
        case "=":
            return Math.abs(sensorValue - threshold) < 0.0001;
        default:
            return false;
    }
}

function getMatchingRulesForMeasurement(event, measurement) {
    return activeRules.filter(rule =>
        rule.enabled === true &&
        rule.sensorName === event.sourceId &&
        isRuleSatisfied(rule, measurement)
    );
}

function createMeasurementHtml(event, measurement) {
    const metric = measurement.metric ?? "";
    const value = measurement.value ?? "";
    const unit = measurement.unit ?? "";

    const matchingRules = getMatchingRulesForMeasurement(event, measurement);
    const thresholdClass = matchingRules.length > 0 ? "threshold-reached" : "";

    return `
        <div class="measurement-row ${thresholdClass}">
            <div class="measurement-metric">${metric}</div>
            <div>
                <span class="measurement-value">${value}</span>
                <span class="measurement-unit">${unit}</span>
            </div>
        </div>
    `;
}

function renderLatestTriggeredRule() {
    const content = document.getElementById("latestRuleContent");

    if (!latestTriggeredRule) {
        content.innerHTML = `No rule triggered yet.`;
        return;
    }

    content.innerHTML = `
        <div class="rule-triggered-box">
            <div><strong>Rule #${latestTriggeredRule.ruleId}</strong></div>
            <div>Sensor: ${latestTriggeredRule.sensorName}</div>
            <div>Condition: ${latestTriggeredRule.metric} ${latestTriggeredRule.operator} ${latestTriggeredRule.thresholdValue} ${latestTriggeredRule.unit ?? ""}</div>
            <div>Actuator: ${latestTriggeredRule.actuatorName} → ${latestTriggeredRule.targetState}</div>
            <div>Timestamp: ${formatTimestamp(latestTriggeredRule.timestamp)}</div>
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

        const measurements = event.measurements ?? [];
        const hasWarning = measurements.some(m => getMatchingRulesForMeasurement(event, m).length > 0);
        card.className = hasWarning ? "sensor-card rule-warning" : "sensor-card";

        const sensorName = event.sourceId ?? "Unknown sensor";
        const timestamp = formatTimestamp(event.timestamp);

        const measurementsHtml = measurements.length > 0
            ? measurements.map(m => createMeasurementHtml(event, m)).join("")
            : `<div class="empty-message">No measurements available</div>`;

        const refreshButtonHtml = isRestSensor(event)
            ? `<button class="refresh-button" data-sensor-id="${sensorName}" title="Refresh sensor">⟳</button>`
            : "";

        card.innerHTML = `
            <div class="sensor-name">
                <a class="sensor-link" href="/sensor-detail.html?sensorId=${encodeURIComponent(sensorName)}">
                    ${sensorName}
                </a>
            </div>
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

async function loadRules(shouldRenderGrid = false) {
    const response = await fetch(`${PROCESSING_BASE_URL}/rules`);
    if (!response.ok) {
        throw new Error("Unable to load rules");
    }

    activeRules = await response.json();

    if (shouldRenderGrid) {
        renderGrid();
    }
}

async function loadLatestTriggeredRule() {
    const response = await fetch("/api/rules/latest-triggered");
    if (!response.ok) {
        return;
    }

    latestTriggeredRule = await response.json();
    renderLatestTriggeredRule();
}

function connectSse() {
    const eventSource = new EventSource("/api/sensors/stream");

    eventSource.addEventListener("sensor-event", function(event) {
        const data = JSON.parse(event.data);
        latestMap[data.sourceId] = data;
        renderGrid();
    });

    eventSource.addEventListener("rule-triggered", function(event) {
        const data = JSON.parse(event.data);
        latestTriggeredRule = data;
        renderLatestTriggeredRule();
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

    if (isNaN(pollingDelayMs) || pollingDelayMs < 0) {
        alert("Polling interval must be greater than 0 ms.");
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

async function initPage() {
    await loadInitialLatest();
    await loadRules(true);
    await loadLatestTriggeredRule();
    connectSse();
    loadPollingInterval();
}

initPage();
setInterval(() => loadRules(false), 10000);