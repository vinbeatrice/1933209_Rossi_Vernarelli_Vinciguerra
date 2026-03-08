const INGESTION_HISTORY_BASE_URL = "http://localhost:8081";

document.getElementById("loadHistoryBtn").addEventListener("click", loadHistory);

async function loadHistory() {
    const sensorId = document.getElementById("sensorId").value;
    const output = document.getElementById("historyOutput");

    if (!sensorId) {
        output.textContent = "Please insert a sensor ID.";
        return;
    }

    const response = await fetch(`${INGESTION_HISTORY_BASE_URL}/api/sensors/history/${sensorId}`);

    if (!response.ok) {
        output.textContent = "Error loading history.";
        return;
    }

    const history = await response.json();
    output.textContent = JSON.stringify(history, null, 2);
}