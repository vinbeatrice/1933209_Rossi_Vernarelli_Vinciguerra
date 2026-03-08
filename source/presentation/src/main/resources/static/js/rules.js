const PROCESSING_BASE_URL = "http://localhost:8082";

let rulesCache = [];

async function fetchRules() {
    const response = await fetch(`${PROCESSING_BASE_URL}/rules`);

    if (!response.ok) {
        throw new Error("Unable to load rules");
    }

    return await response.json();
}

async function loadRules() {
    try {
        rulesCache = await fetchRules();
        renderRules();
    } catch (error) {
        console.error("Error loading rules:", error);
        const container = document.getElementById("rulesContainer");
        container.innerHTML = `<div class="empty-message">Error loading automation rules.</div>`;
    }
}

function createRuleCard(rule) {
    const card = document.createElement("div");
    card.className = rule.enabled ? "rule-card" : "rule-card disabled";

    card.innerHTML = `
        <div class="rule-top">
            <div>
                <h2 class="rule-title">Rule #${rule.id}</h2>
                <p class="rule-description">${rule.description ?? ""}</p>
            </div>

            <div class="rule-status">
                <span class="status-label">${rule.enabled ? "Enabled" : "Disabled"}</span>
                <label class="switch">
                    <input type="checkbox" class="toggle-enabled" data-id="${rule.id}" ${rule.enabled ? "checked" : ""}>
                    <span class="slider"></span>
                </label>
            </div>
        </div>

        <div class="rule-body">
            <div class="rule-field">
                <span class="field-label">Sensor</span>
                <span class="field-value">${rule.sensorName}</span>
            </div>

            <div class="rule-field">
                <span class="field-label">Metric</span>
                <span class="field-value">${rule.metric}</span>
            </div>

            <div class="rule-field">
                <span class="field-label">Operator</span>
                <span class="field-value">${rule.operator}</span>
            </div>

            <div class="rule-field">
                <span class="field-label">Target State</span>
                <span class="field-value">${rule.targetState}</span>
            </div>

            <div class="rule-field full-width">
                <span class="field-label">Threshold</span>
                <div class="threshold-editor">
                    <input
                        class="threshold-input"
                        type="number"
                        step="0.01"
                        value="${rule.thresholdValue}"
                        data-id="${rule.id}">
                    <span class="field-value">${rule.unit ?? ""}</span>
                    <button class="save-button save-threshold" type="button" data-id="${rule.id}">
                        Save Threshold
                    </button>
                </div>
            </div>

            <div class="rule-field">
                <span class="field-label">Actuator</span>
                <span class="field-value">${rule.actuatorName}</span>
            </div>
        </div>

        <div class="rule-actions">
            <button class="delete-button delete-rule" type="button" data-id="${rule.id}">
                Delete
            </button>
        </div>
    `;

    return card;
}

function renderRules() {
    const container = document.getElementById("rulesContainer");
    container.innerHTML = "";

    if (rulesCache.length === 0) {
        container.innerHTML = `<div class="empty-message">No automation rules available.</div>`;
        return;
    }

    rulesCache.forEach(rule => {
        container.appendChild(createRuleCard(rule));
    });

    bindActions();
}

function bindActions() {
    document.querySelectorAll(".toggle-enabled").forEach(toggle => {
        toggle.addEventListener("change", handleToggleRule);
    });

    document.querySelectorAll(".save-threshold").forEach(button => {
        button.addEventListener("click", handleSaveThreshold);
    });

    document.querySelectorAll(".delete-rule").forEach(button => {
        button.addEventListener("click", handleDeleteRule);
    });
}

async function handleToggleRule(event) {
    const ruleId = event.target.dataset.id;
    const enabled = event.target.checked;

    const endpoint = enabled ? "enable" : "disable";

    try {
        const response = await fetch(`${PROCESSING_BASE_URL}/rules/${ruleId}/${endpoint}`, {
            method: "PATCH"
        });

        if (!response.ok) {
            throw new Error("Unable to update rule status");
        }

        await loadRules();
    } catch (error) {
        console.error("Error toggling rule:", error);
        alert("Error updating rule status.");
        await loadRules();
    }
}

async function handleSaveThreshold(event) {
    const ruleId = Number(event.target.dataset.id);
    const rule = rulesCache.find(r => r.id === ruleId);

    if (!rule) {
        alert("Rule not found.");
        return;
    }

    const input = document.querySelector(`.threshold-input[data-id="${ruleId}"]`);
    const newThreshold = parseFloat(input.value);

    if (isNaN(newThreshold)) {
        alert("Please insert a valid threshold value.");
        return;
    }

    const updatedRule = {
        ...rule,
        thresholdValue: newThreshold
    };

    try {
        const response = await fetch(`${PROCESSING_BASE_URL}/rules/${ruleId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedRule)
        });

        if (!response.ok) {
            throw new Error("Unable to update rule");
        }

        await loadRules();
    } catch (error) {
        console.error("Error updating threshold:", error);
        alert("Error updating threshold.");
    }
}

async function handleDeleteRule(event) {
    const ruleId = event.target.dataset.id;

    const confirmed = confirm(`Delete rule #${ruleId}?`);
    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${PROCESSING_BASE_URL}/rules/${ruleId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Unable to delete rule");
        }

        await loadRules();
    } catch (error) {
        console.error("Error deleting rule:", error);
        alert("Error deleting rule.");
    }
}

loadRules();