const INGESTION_BASE_URL = "http://localhost:8081";

async function loadLatestSensors() {
    const response = await fetch(`${INGESTION_BASE_URL}/api/sensors/latest`);
    const sensors = await response.json();

    const tableBody = document.getElementById("sensorTableBody");
    tableBody.innerHTML = "";

    sensors.forEach(sensor => {
        if (!sensor.measurements) {
            return;
        }

        sensor.measurements.forEach(measurement => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${sensor.sourceId ?? ""}</td>
                <td>${measurement.metric ?? ""}</td>
                <td>${measurement.value ?? ""}</td>
                <td>${measurement.unit ?? ""}</td>
                <td>${sensor.timestamp ?? ""}</td>
            `;

            tableBody.appendChild(row);
        });
    });
}

loadLatestSensors();
setInterval(loadLatestSensors, 5000);