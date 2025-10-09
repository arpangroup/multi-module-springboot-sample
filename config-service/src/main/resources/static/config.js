
const configState = {}; // current values
const defaultConfigState = {}; // original values from server
const configBaseUrl = "http://localhost:8888";

function renderConfigs(data) {
  const container = document.getElementById('configContainer');
  container.innerHTML = '';

  data.forEach(({ key, value, valueType, enumValues }) => {
    const div = document.createElement('div');
    div.className = 'config-item';

    const label = document.createElement('label');
    label.textContent = key;

    let inputElement;

    // Handle boolean as toggle
    if (valueType === 'BOOLEAN' || value === 'true' || value === 'false') {
      // Boolean toggle
      inputElement = document.createElement('input');
      inputElement.type = 'checkbox';
      inputElement.checked = (value === 'true');
      inputElement.onchange = () => configState[key] = inputElement.checked.toString();
    }

    // Handle enum as dropdown
    else if (enumValues && enumValues.trim().length > 0) {
      inputElement = document.createElement('select');

      enumValues.split(',').map(e => e.trim()).forEach(optionValue => {
        const option = document.createElement('option');
        option.value = optionValue;
        option.textContent = optionValue;
        if (optionValue === value) option.selected = true;
        inputElement.appendChild(option);
      });

      inputElement.onchange = () => configState[key] = inputElement.value;
    }

    // Number → Numeric input
    else if (['INT', 'FLOAT', 'DOUBLE', 'BIG_DECIMAL'].includes(valueType)) {
      inputElement = document.createElement('input');
      inputElement.type = 'number';
      inputElement.value = value;
      inputElement.step = valueType === 'INT' ? '1' : 'any';
      inputElement.oninput = () => configState[key] = inputElement.value;
    }

    // Handle everything else as text input
    else {
      // Input for numbers and strings
      inputElement = document.createElement('input');
      inputElement.type = 'text';
      inputElement.value = value;
      inputElement.oninput = () => configState[key] = inputElement.value;
    }

    // Initial state
    configState[key] = value;
    defaultConfigState[key] = value; // Store original value for diffing

    div.appendChild(label);
    div.appendChild(inputElement);
    container.appendChild(div);
  });
}

async function loadConfigs() {
    console.log("loadConfigs......");
  //const response = await fetch(`${configBaseUrl}/api/v1/configs`);
  const response = await fetch(`/api/v1/configs`);
  const data = await response.json();
  renderConfigs(data);
  generateConfigUrl(); // Call function to generate the dynamic URL
}

function generateConfigUrl() {
  const appName = configState['application'];
  const profile = configState['profile'];
  console.log("appName: ", appName);
  console.log("profile: ", profile);

  if (!appName || !profile) return;

  const fullUrl = `http://localhost:8888/${appName}/${profile}`;
  const link = document.getElementById('config_url');

  if (link) {
    link.href = fullUrl;
    link.textContent = fullUrl;
  }
}

async function updateConfigs() {
  console.log("UPDATE.....");
  const changedConfigs = Object.entries(configState)
    .filter(([key, value]) => value !== defaultConfigState[key])
    .map(([key, value]) => ({ key, value }));

  if (changedConfigs.length === 0) {
    alert('No changes to update.');
    return;
  }

  console.log("changedConfigs: ", changedConfigs);


  const response = await fetch(`${configBaseUrl}/api/v1/configs/update`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(changedConfigs)
  });

  if (response.ok) {
    alert('Configs updated successfully');
    changedConfigs.forEach(({ key, value }) => defaultConfigState[key] = value);
  } else {
    alert('Failed to update configs');
  }
}


document.getElementById('reloadConfig').addEventListener('click', async () => {
  try {
    const response = await fetch('/api/v1/configs/reload', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    });
    if (response.ok) {
      alert('Configs loaded successfully');
    } else {
      alert('Failed to update configs');
    }
  } catch (error) {
    alert('Request failed: ' + error.message);
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('addConfigForm');
  const valueTypeSelect = document.getElementById('valueTypeSelect');
  const valueFieldContainer = document.getElementById('valueFieldContainer');


  // Form submit handler
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(form);

    const config = {
      key: formData.get('key'),
      value: formData.get('value'),
      application: formData.get('application'),
      profile: formData.get('profile')
    };

    try {
      const response = await fetch(`${configBaseUrl}/api/v1/configs/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
      });

      if (response.ok) {
        alert('Configuration added successfully');
        form.reset();
        await loadConfigs(); // Reload config list
      } else {
        const errorText = await response.text();
        alert('Failed to add configuration: ' + errorText);
      }
    } catch (error) {
      alert('Request failed: ' + error.message);
    }
  });
});

function exportToProperty() {
  // TODO: implement backend call or file generation logic
  const outputTextarea = document.getElementById('propertiesOutput');
  if (!outputTextarea) return;

  // Convert configState object to properties-style string
    const propertiesString = Object.entries(configState)
      .map(([key, value]) => `${key}=${value}`)
      .join('\n');

  const configs = configState;

    // Set it in the textarea
     outputTextarea.value = propertiesString;
}

function parseYamlValue(value) {
  if (value === 'true' || value === 'false') return value;
  if (!isNaN(value) && value.trim() !== '') return value;
  return `"${value.replace(/"/g, '\\"')}"`; // wrap strings in quotes
}

function exportToYaml() {
  const outputTextarea = document.getElementById('propertiesOutput');
    if (!outputTextarea) return;

    const nested = buildNestedYamlObject(configState);
    const yamlString = convertToYaml(nested);
    outputTextarea.value = yamlString;
}

function buildNestedYamlObject(configs) {
  const result = {};

  for (const [fullKey, value] of Object.entries(configs)) {
    const keys = fullKey.split('.');
    let current = result;

    keys.forEach((k, index) => {
      if (index === keys.length - 1) {
        current[k] = parseYamlValue(value); // final value
      } else {
        current[k] = current[k] || {};
        current = current[k];
      }
    });
  }

  return result;
}
function convertToYaml(obj, indent = 0) {
  const spaces = '  '.repeat(indent);
  return Object.entries(obj).map(([key, value]) => {
    if (typeof value === 'object' && value !== null) {
      return `${spaces}${key}:\n${convertToYaml(value, indent + 1)}`;
    } else {
      return `${spaces}${key}: ${value}`;
    }
  }).join('\n');
}

loadConfigs();
