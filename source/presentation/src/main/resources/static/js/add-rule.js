const PROCESSING_BASE_URL = "http://localhost:8082";

document.getElementById("addRuleForm").addEventListener("submit", async function(event) {
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

    try {
        const response = await fetch(`${PROCESSING_BASE_URL}/rules`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(rule)
        });

        if (!response.ok) {
            throw new Error("Unable to create rule");
        }

        window.location.href = "/rules.html";
    } catch (error) {
        console.error("Error creating rule:", error);
        alert("Error creating rule.");
    }
});