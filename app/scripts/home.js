import { Model, View, Presenter } from './mvp.js';
import { StateChangeEvent } from './events.js';

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

}

export class HomePresenter extends Presenter {
  constructor() {
    const view = new HomeView();
    const model = new HomeModel();
    super(view, model);

    // Register to listen for state change events
    this.model.register((weatherData) => {
      if (this.model.mvpEvent.isStateChange() && this.model.mvpEvent.event === StateChangeEvent.LOADED) {
        this.view.init(weatherData);
      }
    });

    // Check for the token on initialization
    this.checkToken();

    // Load weather data
    this.model.loadModel();
  }

  // Check for the token and hide the dashboard if it's not present
  checkToken() {
    const token = sessionStorage.getItem("accessToken"); // Replace with the actual key for your token
    if (!token) {
      this.view.hideLoggedOutUI();
    }
  }

  // Method to handle logout and token removal
  handleLogout() {
    sessionStorage.removeItem("accessToken"); // Replace with the actual token key
    this.view.hideLoggedOutUI();
  }

  // Add method to bind logout button
  bindLogoutButton() {
    this.view.bindLogoutButton(this);
  }
}
