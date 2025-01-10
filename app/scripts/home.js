import { Model, View, Presenter } from './mvp.js';
import { StateChangeEvent } from './events.js';
import { toggleDevice ,sendMessage} from './websocket.js';






const setupDeviceSwitch = (deviceId, deviceName) => {
  const deviceSwitch = document.getElementById(deviceId);
  if (deviceSwitch) {
    deviceSwitch.addEventListener('click', () => toggleDevice(deviceSwitch, deviceName));

  } else {
    console.log(`Switch for ${deviceName} is NULL`);
  }
};

const waitForDeviceSwitch = (deviceId, deviceName) => {
  const interval = setInterval(() => {
    const deviceSwitch = document.getElementById(deviceId);
    if (deviceSwitch) {
      setupDeviceSwitch(deviceId, deviceName); // Appelé après que l'élément est trouvé
      clearInterval(interval); // Stopper l'intervalle une fois l'élément trouvé
    }
  }, 100); // Vérification toutes les 100ms
};

// Assurez-vous que le DOM est chargé avant d'exécuter quoi que ce soit
document.addEventListener('DOMContentLoaded', () => {
  const devices = [
    { id: 'light', name: 'LivingroomLight' },
    { id: 'airConditioner', name: 'AirConditioner' },
    { id: 'ev', name: 'EV' },
    { id: 'garageDoor', name: 'GarageDoor' },
    {id: 'alarm' , name: 'alarm'},
  ];

  devices.forEach(device => {
    waitForDeviceSwitch(device.id, device.name);
  });
});




class HomeModel extends Model {
  constructor() {
    super('weatherModel');
    this.apiKey = '4b4d048301d1652b72c37664dc9147b3'; // API Key for OpenWeatherMap
    this.city = 'Tunis';
    this.country = 'TN';
    this.weatherData = null;
  }

  // Fetch the weather data from OpenWeatherMap API
  async fetchWeather() {
    const url = `https://api.openweathermap.org/data/2.5/weather?q=${this.city},${this.country}&units=metric&appid=${this.apiKey}`;
    try {
      const response = await fetch(url);
      const data = await response.json();
      this.weatherData = data;
      this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
    } catch (error) {
      console.error("Error fetching weather data:", error);
    }
  }

  loadModel() {
    this.fetchWeather();
  }


  async fetchMostRecentTemperature() {
    const url = `https://api.smarthomecot.lme:8443/rest-api/sensors/most-recent/temperature`;
    let accessToken = sessionStorage.getItem('accessToken');
    const token = accessToken ;
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
    } catch (error) {
      console.error("Error fetching temperature data:", error);
    }
  }
  
  
  loadTemperatureData() {
    this.fetchMostRecentTemperature();
  }



  async fetchOverallWaterConsumption() {
    const url = `https://api.smarthomecot.lme:8443/rest-api/sensors/most-recent/overallwaterconsumption`;
    let accessToken = sessionStorage.getItem('accessToken');
    const token = accessToken;
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      // Extract the water consumption value from the response
      const waterConsumptionValue = Math.round(data.value);

      // Update the DOM element with the water consumption value
      const waterConsumptionDisplay = document.getElementById('water');
      if (waterConsumptionDisplay) {
        waterConsumptionDisplay.innerHTML = `${waterConsumptionValue} L`;
      } else {
        console.error("Element with id 'water' not found in the DOM.");
      }
        this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
      } catch (error) {
        console.error("Error fetching water consumption data:", error);
      }
  }
  
  loadWaterConsumptionData() {
    this.fetchOverallWaterConsumption();
  }





  // air quality fetch value
  async fetchOverallairquality() {
    const url = `https://api.smarthomecot.lme:8443/rest-api/sensors/most-recent/airquality`;
    let accessToken = sessionStorage.getItem('accessToken');
    const token = accessToken;
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      // Extract the water consumption value from the response
      const airqualityvalue = Math.round(data.value);

      // Update the DOM element with the water consumption value
      const airqualityDisplay = document.getElementById('Air_Quality');
      if (airqualityDisplay) {
        airqualityDisplay.innerHTML = `${airqualityvalue}`;
      } else {
        console.error("Element with id 'water' not found in the DOM.");
      }
        this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
      } catch (error) {
        console.error("Error fetching water consumption data:", error);
      }
  }
  
  loadairquality() {
    this.fetchOverallairquality();
  }



  // humidity
  async fetchOverallhumidity() {
    const url = `https://api.smarthomecot.lme:8443/rest-api/sensors/most-recent/humidity`;
    let accessToken = sessionStorage.getItem('accessToken');
    const token = accessToken;
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      // Extract the water consumption value from the response
      const humidityvalue = Math.round(data.value);

      // Update the DOM element with the water consumption value
      const humidityDisplay = document.getElementById('humidity');
      if (humidityDisplay) {
        humidityDisplay.innerHTML = `${humidityvalue}`;
      } else {
        console.error("Element with id 'water' not found in the DOM.");
      }
        this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
      } catch (error) {
        console.error("Error fetching water consumption data:", error);
      }
  }
  
  loadhumidity() {
    this.fetchOverallhumidity();
  }
  

  


  // kitchen water
  async fetchkitchenwater() {
    const url = `https://api.smarthomecot.lme:8443/rest-api/sensors/most-recent/kitchenwaterconsumption`;
    let accessToken = sessionStorage.getItem('accessToken');
    const token = accessToken;
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      // Extract the water consumption value from the response
      const kitchenvalue = Math.round(data.value);

      // Update the DOM element with the water consumption value
      const kitchenDisplay = document.getElementById('kitchenwater');
      if (kitchenDisplay) {
        kitchenDisplay.innerHTML = `${kitchenvalue} L`;
      } else {
        console.error("Element with id 'kitchen' not found in the DOM.");
      }
        this.fireStateChangeEvent(data, StateChangeEvent.LOADED);
      } catch (error) {
        console.error("Error fetching kitchen consumption data:", error);
      }
  }
  
  loadkitchen() {
    this.fetchkitchenwater();
  }
  
  
}

class HomeView extends View {
  constructor() {
    super('View');
  }

  defineBindings(weatherData) {
    const weatherCondition = weatherData.weather[0].main.toLowerCase();
    const description = weatherData.weather[0].description;
    const temperature = Math.round(weatherData.main.temp);
    const city = weatherData.name;
    const country = weatherData.sys.country;

    // Binding weather data to the UI
    document.getElementById('temp').textContent = temperature;
    document.getElementById('location').textContent = `${city}, ${country}`;
    document.getElementById('weather-description').textContent = description;

    // Set background and icon based on weather condition
    const weatherBg = document.getElementById('weather-bg');
    const weatherIcon = document.getElementById('weather-icon');
    switch (weatherCondition) {
      case 'clear':
        weatherBg.style.backgroundImage = "url('images/weather/weather-animations/clear.gif')";
        weatherIcon.src = "sun.png";
        break;
      case 'rain':
        weatherBg.style.backgroundImage = "url('images/weather/weather-animations/rain.gif')";
        weatherIcon.src = "rain.png";
        break;
      case 'snow':
        weatherBg.style.backgroundImage = "url('images/weather/weather-animations/snow.gif')";
        weatherIcon.src = "snow.png";
        break;
      case 'clouds':
      default:
        weatherBg.style.backgroundImage = "url('images/weather/weather-animations/clouds.gif')";
        weatherIcon.src = "cloud.png";
        break;
    }

    // Update the current date
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').textContent = new Date().toLocaleDateString('en-US', options);

    // Set sunrise and sunset times
    const sunriseTimestamp = weatherData.sys.sunrise;
    const sunsetTimestamp = weatherData.sys.sunset;
    const sunriseLocal = new Date(sunriseTimestamp * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const sunsetLocal = new Date(sunsetTimestamp * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    document.getElementById('sunrise-time').textContent = sunriseLocal;
    document.getElementById('sunset-time').textContent = sunsetLocal;
  }

  // This method handles actions like updating the weather data when it changes
  applyBindings() {
    try {
      const weatherData = this.value;
      if (weatherData) {
        this.defineBindings(weatherData);
      }
    } catch (e) {
      console.error('Error applying bindings:', e);
    }
  }
  updateTemperatureDisplay(sensorData) {
    const temperatureValue = Math.round(sensorData.value);
    const temperatureDisplay = document.getElementById('temperature');
    const temperatureStatus = document.getElementById('temperature_status');
    // Update the temperature value
    temperatureDisplay.innerHTML = `${temperatureValue}°C`;
  
    // Determine the status based on temperature value
    let status = '';
    if (temperatureValue <= 10) {
      status = 'Cold';
    } else if (temperatureValue <= 25) {
      status = 'Mild';
    } else {
      status = 'Hot';
    }
  
    // Update the status display
    temperatureStatus.textContent = status;
    }




    updateWaterConsumptionDisplay(sensorData) {
      const waterConsumptionValue = Math.round(sensorData.value);
      const waterConsumptionDisplay = document.getElementById('water');
    
      // Update the water consumption value
      waterConsumptionDisplay.innerHTML = `${waterConsumptionValue} L`;
    
      // Determine the status based on consumption value
      let status = '';
      if (waterConsumptionValue <= 50) {
        status = 'Low';
      } else if (waterConsumptionValue <= 200) {
        status = 'Moderate';
      } else {
        status = 'High';
      }
    }
    
  

}

export class HomePresenter extends Presenter {
  constructor() {
    const view = new HomeView();
    const model = new HomeModel();
    super(view, model);

    // Register to listen for state change events
    this.model.register((data) => {
      if (this.model.mvpEvent.isStateChange() && this.model.mvpEvent.event === StateChangeEvent.LOADED) {
        if (data.type === 'temperature') {
          this.view.updateTemperatureDisplay(data);
        } 
        if (data.type === 'overallWaterConsumption') {
          this.view.updateWaterConsumptionDisplay(data);
        } else {
          this.view.init(data);
        }
      }
    });

    // Load weather, temperature, and water consumption data
    this.model.loadModel(); // Load weather data
    this.model.loadTemperatureData(); // Load temperature data
    this.model.loadWaterConsumptionData(); // Load water consumption data
    this.model.loadairquality();
    this.model.loadhumidity();
    this.model.loadkitchen();
  }
  
}
