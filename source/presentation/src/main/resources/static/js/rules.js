const PROCESSING_BASE_URL = "http://localhost:8082";

async function loadRules() {
    const response = await fetch(`${PROCESSING_BASE_URL}/rules`);
    const rules = await response.json();

    const tableBody = document.getElementById("rulesTableBody");
    tableBody.innerHTML = "";

    rules.forEach(rule => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${rule.id ?? ""}</td>
            <td>${rule.sensorName ?? ""}</td>
            <td>${rule.metric ?? ""}</td>
            <td>${rule.operator ?? ""}</td>
            <td>${rule.thresholdValue ?? ""}</td>
            <td>${rule.unit ?? ""}</td>
            <td>${rule.actuatorName ?? ""}</td>
            <td>${rule.targetState ?? ""}</td>
            <td>${rule.enabled}</td>
            <td>${rule.description ?? ""}</td>
        `;

        tableBody.appendChild(row);
    });
}

document.getElementById("ruleForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const rule = {
        sensorName: document.getElementById("sensorName").value,
        metric: document.getElementById("metric").value,
        operator: document.getElementById("operator").value,
        thresholdValue: parseFloat(document.getElementById("thresholdValue").value),
        unit: document.getElementById("unit").value,
        actuatorName: document.getElementById("actuatorName").value,
        targetState: document.getElementById("targetState").value,
        enabled: document.getElementById("enabled").value === "true",
        description: document.getElementById("description").value
    };

    const response = await fetch(`${PROCESSING_BASE_URL}/rules`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(rule)
    });

    if (response.ok) {
        alert("Rule created successfully");
        document.getElementById("ruleForm").reset();
        loadRules();
    } else {
        alert("Error creating rule");
    }
});

loadRules();