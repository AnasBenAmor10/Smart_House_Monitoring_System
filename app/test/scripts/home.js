import {Model,View,Presenter,Observable,ComputedObservable} from './mvp.js';
import {StateChangeEvent,IntentEvent} from './events.js';
import {switchToWssSubdomain} from './util.js'

class HomeModel extends Model{
	constructor(){
		super('homeModel');
		this.apis = {
			currentWeather: {
				//api:"https://weatherapi-com.p.rapidapi.com/current.json?q=",
				api:"https://api.phoenix.xyz:8443/rest-api/echoes?q=",
				headers: {
					'X-RapidAPI-Key': 'dab2eedb20msh55ebe41c5818abcp194421jsn1cb4dd493cb2',
					'X-RapidAPI-Host': 'weatherapi-com.p.rapidapi.com'
				},
				url: (lat, lon) => {
					return this.apis.currentWeather.api + lat + "," + lon;
				}
			}
		};
	}
	
	geolocate() { 
		return new Promise((resolve, reject) =>  navigator.geolocation.getCurrentPosition(resolve, reject))
	}
	
	getCurrentWeather(location) {
		const lat = location.coords.latitude;
		const lon = location.coords.longitude;
		let actualHeaders = {...this.apis.headers,'Accept':'application/xml'};
		let options = { method: 'GET', headers:actualHeaders};
		let _this = this;
		return fetch(this.apis.currentWeather.url(lat, lon),options)
        .then(response => response.text())
        .then(str => new window.DOMParser().parseFromString(str, "text/xml"))
        .then(data => _this.fireStateChangeEvent(data,StateChangeEvent.LOADED));
	}
	
	loadModel(){
		this.geolocate()
		.then(coords => this.getCurrentWeather(coords))
		.catch(error => this.getCurrentWeather({coords:{latitude:34.15,longitude:10.15}}));
	}
}

class HomeView extends View{
	constructor(){
		super('homeView');
	}
	
	defineBindings(weather){
		console.log('here');
		console.log(weather.documentElement.getAttribute('altitude'));
		console.log(weather.documentElement.getAttribute('longitude'));
		document.getElementById('connect').addEventListener('click',(e)=>{
			e.preventDefault();
			this.connect();
			
		});
		document.getElementById('disconnect').addEventListener('click',(e)=>{
			e.preventDefault();
			this.disconnect();
		});
		document.getElementById('sayHello').addEventListener('click',(e)=>{
			e.preventDefault();
			this.sendMessage();
		});
	}

	connect() {
		this.websocket = new WebSocket(switchToWssSubdomain('api','pushes'));
		let _this = this;
		this.websocket.onopen = function() {
			_this.displayStatus('Open');
			document.getElementById('sayHello').disabled = false;
			_this.displayMessage('Connection is now open. Type a name and click Say Hello to send a message.');
		};
		
		this.websocket.onmessage = function(event) {
			// log the event
			_this.displayMessage('The response was received! ' + event.data, 'success');
		};
		
		this.websocket.onerror = function(event) {
			// log the event
			_this.displayMessage('Error! ' + event.data, 'error');
		};
		
		this.websocket.onclose = function() {
			_this.displayStatus('Closed');
			_this.displayMessage('The connection was closed or timed out. Please click the Open Connection button to reconnect.');
			document.getElementById('sayHello').disabled = true;
		};
	}

    disconnect() {
		if (this.websocket !== null) {
			this.websocket.close();
			this.websocket = null;
		}
		message.setAttribute("class", "message");
		message.value = 'WebSocket closed.';
		// log the event
    }

    sendMessage() {
		if (this.websocket !== null) {
			var content = document.getElementById('name').value;
			this.websocket.send(content);
		} else {
			this.displayMessage('WebSocket connection is not established. Please click the Open Connection button.', 'error');
		}
    }

	displayMessage(data, style) {
		var message = document.getElementById('hellomessage');
		message.setAttribute("class", style);
		message.value = data;
	}

	displayStatus(status) {
		var currentStatus = document.getElementById('currentstatus');
		currentStatus.value = status;
	}	
	
}

export class HomePresenter extends Presenter {
	constructor(){
		super(new HomeView(),new HomeModel());
		this.model.register((weather)=>{
			if(this.model.mvpEvent.isStateChange() && this.model.mvpEvent.event === StateChangeEvent.LOADED){
				this.view.init(weather);
			}			
		});
		this.model.loadModel();
	}
}