/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-22T05:19
 */
//--------WebSocketImpl.js--------

class WebSocketImpl {
	constructor({netConfig, defaultParams}) {
		this._net = netConfig;
		this._defaultParams = defaultParams;
		this.init = this.init.bind(this);
		this.receive = this.receive.bind(this);
		this.send = this.send.bind(this);
	}

	async init(client) {
		this._socket = await this._net.startSockConnect(client);
	}

	receive(nameQueue) {
		const {serviceQueue} = this._defaultParams.constants.global.network.webSocket;
		return new Promise(resolve => {
			this._socket.subscribe(
				`${serviceQueue}${nameQueue}`,
				message => {
					const body = JSON.parse(message.body);
					resolve(body);
				},
				{id: nameQueue}
			);
		});
	}

	send(nameQueue, body) {
		const {serviceQueue} = this._defaultParams.constants.global.network.webSocket;
		this._socket.send(
			`${serviceQueue}${nameQueue}`,
			body
		);
	}
}

export default WebSocketImpl;

