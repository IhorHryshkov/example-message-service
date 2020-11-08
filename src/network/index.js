/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
// --------index.js--------

import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import Axios from "axios";
import Validator from "../utils/Validator";

/**
 * Initialization instances the networks
 */
export default class NetworkConfigImpl {
	/**
	 * Initialization of network request and web socket objects
	 * @param config - Network config data
	 * @param defaultParams - Default params and constants
	 */
	constructor({ config, defaultParams }) {
		const baseURL = process.env.REACT_APP_SERVER_BASE_URL || "localhost";
		const proxyURL = process.env.REACT_APP_SOCKET_PROXY_URL || "localhost";
		this._defaultParams = defaultParams;
		this._socketConnections = {};
		this._socketSubscribe = {};
		this._request = Axios.create({
			baseURL,
			timeout: config.timeout,
		});
		this._stompClientService = Stomp.over(() => {
			return new SockJS(`${baseURL}${config.webSocket.servicePath}`);
		});
		this._stompClientService.configure({
			reconnectDelay: config.webSocket.serviceReconnect,
		});
		this._stompClientProxy = Stomp.client(`${proxyURL}${config.webSocket.proxyPath}`);
		this._stompClientProxy.configure({
			reconnectDelay: config.webSocket.proxyReconnect,
			// heartbeat     : {
			// 	outgoing: 0,
			// 	incoming: 0
			// }
		});
	}

	/**
	 * Initialization validator for I/O data
	 * @returns {Validator}
	 */
	getValidator() {
		return new Validator({ defaultParams: this._defaultParams });
	}

	/**
	 * Return axios object
	 * @returns {AxiosInstance}
	 */
	getAxios() {
		return this._request;
	}

	/**
	 * Return STOMP client for work with "server" web socket
	 * @returns {CompatClient}
	 */
	getSockService() {
		return this._stompClientService;
	}

	/**
	 * Return STOMP client for work with "proxy"(send/receive messages between clients) web socket
	 * @returns {CompatClient}
	 */
	getSockProxy() {
		return this._stompClientProxy;
	}

	/**
	 * Init web socket connection use specific client instance, headers and name
	 * @param client - Instance of the STOMP client {@link CompatClient}
	 * @param headers - Connect headers of the STOMP client
	 * @param name - Connection name of the STOMP client
	 * @returns {Promise<boolean>}
	 */
	startSockConnect(client, headers, name) {
		return new Promise(resolve => {
			if (!this._socketConnections[name]) {
				client.debug = f => f;
				client.connect(headers, () => {
					this._socketConnections[name] = client;
					resolve(true);
				});
			}
		});
	}

	/**
	 * Add web socket subscribes
	 * @param subscribe - Subscribe of the STOMP client {@link StompSubscription}
	 * @param name - Subscribe name of the STOMP client
	 */
	addSockSubscribe(subscribe, name) {
		this._socketSubscribe[name] = subscribe;
	}

	/**
	 * Get web socket client by name
	 * @param name - Subscribe name of the STOMP client
	 * @returns {StompSubscription}
	 */
	getSockSubscribe(name) {
		return this._socketSubscribe[name];
	}

	/**
	 * Get web socket instance with connection ready
	 * @param name - Connection name of the STOMP client
	 * @returns {CompatClient}
	 */
	getSockConnect(name) {
		return this._socketConnections[name];
	}

	/**
	 * Web socket disconnect by name
	 * @param name - Connection name of the STOMP client
	 */
	stopSockConnect(name) {
		this._socketConnections[name].disconnect();
	}
}
