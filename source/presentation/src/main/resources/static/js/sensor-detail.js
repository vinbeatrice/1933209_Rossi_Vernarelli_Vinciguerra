let sensorId = null;
let sensorHistory = [];
const chartsByMetric = {};

function getSensorIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("sensorId");
}

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

async function loadSensorHistory(sensorId) {
    const response = await fetch(`/api/sensors/history?sensorId=${encodeURIComponent(sensorId)}`);
    if (!response.ok) {
        throw new Error("Unable to load sensor history");
    }

    return await response.json();
}

function extractMetrics(history) {
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

function buildMetricSeries(history, metric) {
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

function renderChartsGrid(history) {
    const chartsGrid = document.getElementById("chartsGrid");
    chartsGrid.innerHTML = "";

    const metrics = extractMetrics(history);

    if (metrics.length === 0) {
        chartsGrid.innerHTML = `<div class="empty-message">No measurements available for this sensor.</div>`;
        return;
    }

    metrics.forEach(metric => {
        const chartCard = document.createElement("div");
        chartCard.className = "chart-card";

        const safeMetricId = metric.replace(/[^a-zA-Z0-9_-]/g, "_");
        const canvasId = `chart-${safeMetricId}`;
        const statMinId = `stat-min-${safeMetricId}`;
        const statMaxId = `stat-max-${safeMetricId}`;
        const statAvgId = `stat-avg-${safeMetricId}`;

        chartCard.innerHTML = `
            <h2>${metric}</h2>

            <div class="chart-stats">
                <div class="stat-box">
                    <span class="stat-label">Min</span>
                    <span id="${statMinId}" class="stat-value">-</span>
                </div>

                <div class="stat-box">
                    <span class="stat-label">Max</span>
                    <span id="${statMaxId}" class="stat-value">-</span>
                </div>

                <div class="stat-box">
                    <span class="stat-label">Avg</span>
                    <span id="${statAvgId}" class="stat-value">-</span>
                </div>
            </div>

            <canvas id="${canvasId}"></canvas>
        `;

        chartsGrid.appendChild(chartCard);

        const ctx = document.getElementById(canvasId).getContext("2d");
        const { labels, values } = buildMetricSeries(history, metric);
        const stats = computeStats(values);

        document.getElementById(statMinId).textContent = stats.min;
        document.getElementById(statMaxId).textContent = stats.max;
        document.getElementById(statAvgId).textContent = stats.avg;

        chartsByMetric[metric] = new Chart(ctx, {
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
    });
}

function updateCharts(history) {
    const metrics = extractMetrics(history);

    metrics.forEach(metric => {
        const chart = chartsByMetric[metric];
        if (!chart) {
            return;
        }

        const { labels, values } = buildMetricSeries(history, metric);
        const stats = computeStats(values);

        chart.data.labels = labels;
        chart.data.datasets[0].data = values;
        chart.update();

        const safeMetricId = metric.replace(/[^a-zA-Z0-9_-]/g, "_");

        const statMin = document.getElementById(`stat-min-${safeMetricId}`);
        const statMax = document.getElementById(`stat-max-${safeMetricId}`);
        const statAvg = document.getElementById(`stat-avg-${safeMetricId}`);

        if (statMin) {
            statMin.textContent = stats.min;
        }
        if (statMax) {
            statMax.textContent = stats.max;
        }
        if (statAvg) {
            statAvg.textContent = stats.avg;
        }
    });
}

function connectSse() {
    const eventSource = new EventSource("/api/sensors/stream");

    eventSource.addEventListener("sensor-event", function(event) {
        const data = JSON.parse(event.data);

        if (data.sourceId !== sensorId) {
            return;
        }

        sensorHistory.push(data);

        if (sensorHistory.length > 30) {
            sensorHistory.shift();
        }

        updateCharts(sensorHistory);
    });

    eventSource.onerror = function(error) {
        console.error("SSE connection error:", error);
    };
}

async function initPage() {
    sensorId = getSensorIdFromUrl();

    if (!sensorId) {
        document.getElementById("sensorTitle").textContent = "Sensor not specified";
        document.getElementById("chartsGrid").innerHTML =
            `<div class="empty-message">No sensor selected.</div>`;
        return;
    }

    document.getElementById("sensorTitle").textContent = sensorId;

    sensorHistory = await loadSensorHistory(sensorId);
    renderChartsGrid(sensorHistory);
    connectSse();
}

initPage().catch(error => {
    console.error("Error initializing sensor detail page:", error);
    document.getElementById("chartsGrid").innerHTML =
        `<div class="empty-message">Error loading sensor detail.</div>`;
});