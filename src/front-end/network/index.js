/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
//--------index.js--------

import SockJS    from 'sockjs-client';
import {Stomp}   from '@stomp/stompjs';
import Axios     from 'axios';
import Validator from '../utils/Validator';

class NetworkConfigImpl {
	constructor({config, defaultParams}) {
		const baseURL = process.env.REACT_APP_BASE_URL || "localhost";
		this._defaultParams      = defaultParams;
		this._network            = Axios.create({
			baseURL,
			timeout: config.timeout
		});
		this._stompClientService = Stomp.over(() => {
			return new SockJS(`${baseURL}${config.webSocket.servicePath}`);
		});
		this._stompClientService.configure({
			reconnectDelay: config.webSocket.serviceReconnect
		});
		this._stompClientProxy = Stomp.over(() => {
			return new SockJS(`${config.proxyURL}${config.webSocket.proxyPath}`);
		});
		this._stompClientProxy.configure({
			reconnectDelay: config.webSocket.proxyReconnect
		});

	}

	getValidator() {
		return new Validator({defaultParams: this._defaultParams});
	}

	getAxios() {
		return this._network;
	}

	getSockService() {
		return this._stompClientService;
	}

	getSockProxy() {
		return this._stompClientProxy;
	}

	startSockConnect(client) {
		client.debug = f => f;
		return new Promise(resolve => {
			client.connect({}, (frame) => {
				resolve(client);
			});
		});
	}

	stopSockConnect(client) {
		client.disconnect();
	}
}

export default NetworkConfigImpl;
