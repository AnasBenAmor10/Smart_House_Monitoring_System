document.addEventListener("DOMContentLoaded", () => {
    const API_KEY = "4b4d048301d1652b72c37664dc9147b3"; // Replace with your API key
    const CITY = "Tunis";
    const COUNTRY = "TN";
  
    // Fetch weather data
    async function fetchWeather() {
      const url = `https://api.openweathermap.org/data/2.5/weather?q=${CITY},${COUNTRY}&units=metric&appid=${API_KEY}`;
      try {
        const response = await fetch(url);
        const data = await response.json();
        updateWeatherCard(data);
      } catch (error) {
        console.error("Error fetching weather data:", error);
      }
    }
  
    // Update the weather card with fetched data
    function updateWeatherCard(data) {
      const temp = document.getElementById("temp");
      const location = document.getElementById("location");
      const weatherDescription = document.getElementById("weather-description");
      const weatherIcon = document.getElementById("weather-icon");
      const weatherBg = document.getElementById("weather-bg");
      const currentDate = document.getElementById("current-date");
      const currentTime = document.getElementById("current-time");
      const sunriseTime = document.getElementById("sunrise-time");
      const sunsetTime = document.getElementById("sunset-time");
  
      const weatherCondition = data.weather[0].main.toLowerCase();
      const description = data.weather[0].description;
      const temperature = Math.round(data.main.temp);
      const city = data.name;
      const country = data.sys.country;
  
      // Update weather information
      temp.textContent = temperature;
      location.textContent = `${city}, ${country}`;
      weatherDescription.textContent = description;
  
      // Set background and icon based on condition
      switch (weatherCondition) {
        case "clear":
          weatherBg.style.backgroundImage = "url('image/weather-animations/clear.gif')";
          weatherIcon.src = "sun.png";
          break;
        case "rain":
          weatherBg.style.backgroundImage = "url('image/weather-animations/rain.gif')";
          weatherIcon.src = "rain.png";
          break;
        case "snow":
          weatherBg.style.backgroundImage = "url('image/weather-animations/snow.gif')";
          weatherIcon.src = "snow.png";
          break;
        case "clouds":
        default:
          weatherBg.style.backgroundImage = "url('image/weather-animations/clouds.gif')";
          weatherIcon.src = "cloud.png";
          break;
      }
  
      // Set current date
      const options = { weekday: "long", year: "numeric", month: "long", day: "numeric" };
      currentDate.textContent = new Date().toLocaleDateString("en-US", options);
  
      // Set sunrise and sunset times
      const sunriseTimestamp = data.sys.sunrise;
      const sunsetTimestamp = data.sys.sunset;
  
      const sunriseLocal = new Date(sunriseTimestamp * 1000).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
      const sunsetLocal = new Date(sunsetTimestamp * 1000).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  
      sunriseTime.textContent = sunriseLocal;
      sunsetTime.textContent = sunsetLocal;
    }
  
    // Fetch weather on page load
    fetchWeather();
  });
  