/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-22T05:19
 */
// --------WebSocketImpl.js--------
/**
 * Web socket I/O processing
 */
export default class WebSocketImpl {
	/**
	 * Initialization of configuration data and network
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param defaultParams - Default params and constants
	 * @param serviceQueue - Root path to queue or topic
	 * @param clientName - Client name of STOMP client
	 */
	constructor({ netConfig, defaultParams, serviceQueue, clientName }) {
		this._net = netConfig;
		this._defaultParams = defaultParams;
		this._serviceQueue = serviceQueue;
		this.receiveQueue = this.receiveQueue.bind(this);
		this.receiveTopic = this.receiveTopic.bind(this);
		this.send = this.send.bind(this);
		this._client = this._net.getSockConnect(clientName);
	}

	/**
	 * Initialization subscribe queue for receive messages
	 * @param nameQueue - Name of queue
	 * @param subName - Name of subscription
	 * @param callbackSub - Callback function for listen subscription
	 * @param callbackReSub - Callback function for reconnect action
	 */
	receiveQueue(nameQueue, subName, callbackSub, callbackReSub) {
		this._client.configure({
			onConnect: event => callbackReSub(event),
		});
		const subscribe = this._client.subscribe(`${this._serviceQueue}${nameQueue}`, message => callbackSub(message, nameQueue), {
			durable: true,
			"auto-delete": false,
			ack: "client-individual",
		});
		this._net.addSockSubscribe(subscribe, subName);
	}

	/**
	 * Initialization subscribe topic queue for receive messages
	 * @param nameTopic - Name of topic
	 * @param nameQueue - Name of queue
	 * @param subName - Name of subscription
	 * @param callbackSub - Callback function for listen subscription
	 * @param callbackReSub - Callback function for reconnect action
	 */
	receiveTopic(nameTopic, nameQueue, subName, callbackSub, callbackReSub) {
		this._client.configure({
			onConnect: event => callbackReSub(event),
		});
		const subscribe = this._client.subscribe(`${this._serviceQueue}${nameTopic}`, message => callbackSub(message, nameQueue), {
			"x-queue-name": nameQueue,
			id: `${nameQueue}-${Date.now()}`,
			durable: true,
			"auto-delete": false,
			ack: "client",
		});
		this._net.addSockSubscribe(subscribe, subName);
	}

	/**
	 * Send message to queue or topic
	 * @param nameQueue - Name of queue or topic
	 * @param body - Data for send
	 */
	send(nameQueue, body) {
		this._client.send(`${this._serviceQueue}${nameQueue}`, {}, body);
	}
}
