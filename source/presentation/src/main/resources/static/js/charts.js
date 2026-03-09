let chart = null;
let selectedSensorId = null;
let selectedMetric = null;
let currentHistory = [];

function formatTimestampLabel(timestamp) {
    if (!timestamp) {
        return "";
    }

    try {
        const date = new Date(timestamp);
        if (isNaN(date.getTime())) {
            return timestamp;
        }
        return date.toLocaleTimeString();
    } catch (error) {
        return timestamp;
    }
}

async function loadSensorList() {
    const response = await fetch("/api/sensors/latest");
    if (!response.ok) {
        throw new Error("Unable to load sensor list");
    }

    return await response.json();
}

function populateSensorSelect(events) {
    const sensorSelect = document.getElementById("sensorSelect");
    sensorSelect.innerHTML = "";

    const sensorIds = events
        .map(event => event.sourceId)
        .filter(Boolean)
        .sort();

    sensorIds.forEach(sensorId => {
        const option = document.createElement("option");
        option.value = sensorId;
        option.textContent = sensorId;
        sensorSelect.appendChild(option);
    });

    if (sensorIds.length > 0) {
        selectedSensorId = sensorIds[0];
        sensorSelect.value = selectedSensorId;
    }
}

async function loadHistory(sensorId) {
    const response = await fetch(`/api/sensors/history/${encodeURIComponent(sensorId)}`);
    if (!response.ok) {
        throw new Error("Unable to load sensor history");
    }

    return await response.json();
}

function extractAvailableMetrics(history) {
    const metrics = new Set();

    history.forEach(event => {
        (event.measurements ?? []).forEach(measurement => {
            if (measurement.metric) {
                metrics.add(measurement.metric);
            }
        });
    });

    return Array.from(metrics).sort();
}

function populateMetricSelect(metrics) {
    const metricSelect = document.getElementById("metricSelect");
    metricSelect.innerHTML = "";

    metrics.forEach(metric => {
        const option = document.createElement("option");
        option.value = metric;
        option.textContent = metric;
        metricSelect.appendChild(option);
    });

    if (metrics.length > 0) {
        if (!metrics.includes(selectedMetric)) {
            selectedMetric = metrics[0];
        }
        metricSelect.value = selectedMetric;
    } else {
        selectedMetric = null;
    }
}

function buildChartData(history, metric) {
    const labels = [];
    const values = [];

    history.forEach(event => {
        const measurement = (event.measurements ?? []).find(m => m.metric === metric);

        if (measurement && measurement.value !== undefined && measurement.value !== null) {
            labels.push(formatTimestampLabel(event.timestamp));
            values.push(Number(measurement.value));
        }
    });

    return { labels, values };
}

function computeStats(values) {
    const numericValues = values
        .map(v => Number(v))
        .filter(v => !Number.isNaN(v));

    if (numericValues.length === 0) {
        return {
            min: "-",
            max: "-",
            avg: "-"
        };
    }

    const min = Math.min(...numericValues);
    const max = Math.max(...numericValues);
    const sum = numericValues.reduce((total, value) => total + value, 0);
    const avg = sum / numericValues.length;

    return {
        min: min.toFixed(2),
        max: max.toFixed(2),
        avg: avg.toFixed(2)
    };
}

function updateStats(values) {
    const stats = computeStats(values);

    const statMin = document.getElementById("statMin");
    const statMax = document.getElementById("statMax");
    const statAvg = document.getElementById("statAvg");

    if (statMin) {
        statMin.textContent = stats.min;
    }

    if (statMax) {
        statMax.textContent = stats.max;
    }

    if (statAvg) {
        statAvg.textContent = stats.avg;
    }
}

function renderChart(history, metric) {
    const canvas = document.getElementById("sensorChart");
    if (!canvas) {
        return;
    }

    const ctx = canvas.getContext("2d");
    const { labels, values } = buildChartData(history, metric);

    updateStats(values);

    if (chart) {
        chart.destroy();
    }

    chart = new Chart(ctx, {
        type: "line",
        data: {
            labels,
            datasets: [
                {
                    label: metric,
                    data: values,
                    fill: false,
                    tension: 0.2
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: false
                }
            }
        }
    });
}

async function refreshChartForSelectedSensor() {
    if (!selectedSensorId) {
        return;
    }

    currentHistory = await loadHistory(selectedSensorId);

    const metrics = extractAvailableMetrics(currentHistory);
    populateMetricSelect(metrics);

    if (selectedMetric) {
        renderChart(currentHistory, selectedMetric);
    }
}

function connectSse() {
    const eventSource = new EventSource("/api/sensors/stream");

    eventSource.addEventListener("sensor-event", function(event) {
        const data = JSON.parse(event.data);

        if (data.sourceId === selectedSensorId) {
            currentHistory.push(data);

            if (currentHistory.length > 30) {
                currentHistory.shift();
            }

            const metrics = extractAvailableMetrics(currentHistory);
            populateMetricSelect(metrics);

            if (selectedMetric) {
                renderChart(currentHistory, selectedMetric);
            }
        }
    });

    eventSource.onerror = function(error) {
        console.error("SSE connection error:", error);
    };
}

async function initChartsPage() {
    const latestEvents = await loadSensorList();
    populateSensorSelect(latestEvents);
    await refreshChartForSelectedSensor();

    document.getElementById("sensorSelect").addEventListener("change", async function(event) {
        selectedSensorId = event.target.value;
        await refreshChartForSelectedSensor();
    });

    document.getElementById("metricSelect").addEventListener("change", function(event) {
        selectedMetric = event.target.value;
        renderChart(currentHistory, selectedMetric);
    });

    connectSse();
}

initChartsPage().catch(error => {
    console.error("Error initializing charts page:", error);
});