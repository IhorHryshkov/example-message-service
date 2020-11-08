/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T01:01
 */
// --------index.js--------

import { call, put, takeEvery } from "redux-saga/effects";
import { constants } from "../../../config/constants.json";
import ChatServiceImpl from "../../../services/ChatServiceImpl";
import CounterServiceImpl from "../../../services/CounterServiceImpl";
import WebSocketImpl from "../../../network/websocket/WebSocketImpl";
import Sleep from "../../../utils/Sleep";
import SideSettingsService from "../../../services/PreferencesServiceImpl";

const { SEND_MESSAGE, LOAD_CHAT, LOAD_CHAT_SUCCESS, UPDATE_CHAT } = constants.chat.actions;
const { ERROR, LISTEN_SOCKET, INIT_SOCKET } = constants.global.actions;

const { proxyQueue, sockListenerErrorRetry } = constants.global.network.webSocket;

let chatService;
let counterService;
let webSocket;
let keyPref;
let sideSettingsService;

/**
 * Send message processing, add message to DB, send message to proxy server, add new message counts to server counter
 * @param action - Payload object with chat user ID, logged user object and message for send {type: string, payload: {user_id: string, user:{id: string, username: string, timestamp: number}, data:{message: string}}}
 */
function* workerSendMessage(action) {
	try {
		const { user_id, user, data } = action.payload;
		const user_own_id = user.id;
		const { username } = user;
		const sendData = {
			user_own_id,
			user_id,
			body: data.message ? data.message.trim() : undefined,
			timestamp: Date.now(),
		};

		const result = yield call(chatService.add, sendData);
		const { id, body, timestamp } = result;
		yield call(
			webSocket.send,
			user_id,
			JSON.stringify({
				id,
				body,
				timestamp,
				user_id: user_own_id,
			})
		);
		yield put({
			type: LOAD_CHAT,
			payload: {
				user_own_id,
				user_id,
			},
		});
		yield call(counterService.addMessageCounter, {
			username,
			user_id: user_own_id,
		});
	} catch (e) {
		// console.error(e);
		yield call(Sleep, sockListenerErrorRetry);
		yield put({
			type: SEND_MESSAGE,
			payload: action.payload,
		});
	}
}

/**
 * Init web socket chat processing
 * @param action - Payload is object with topic name and store {type: string, payload: {store: object, user_own_id: string}}
 */
function* workerInitSocket(action) {
	const { store, user_own_id } = action.payload;
	yield call(
		webSocket.receiveTopic,
		user_own_id,
		user_own_id,
		"proxy",
		async message => {
			try {
				if (!message.body || message.body.length <= 0) {
					message.ack();
				}
				const body = JSON.parse(message.body);
				let useStore;
				await chatService.add(body);
				const result = await sideSettingsService.all({ key: keyPref });
				if (!result.lastChat || result.lastChat !== body.user_id) {
					if (store.side.chats[body.user_id]) {
						useStore = store.side.chats[body.user_id];
					} else {
						// await webSocket.send(user_own_id, message.body);
						message.ack();
					}
				} else {
					useStore = store.chat;
					useStore.dispatch({
						type: LOAD_CHAT,
						payload: {
							user_own_id,
							user_id: body.user_id,
						},
					});
				}
				if (useStore) {
					useStore.dispatch({
						type: LISTEN_SOCKET,
						payload: {
							message,
							body,
						},
					});
				}
			} catch (e) {
				// console.error(e);
				message.nack();
			}
		},
		() => {
			store.chat.dispatch({
				type: INIT_SOCKET,
				payload: action.payload,
			});
		}
	);
}

/**
 * Load all messages from DB for specific chat
 * @param action - Payload is object with chat ID {type: string, payload:{chat_id: string}}
 */
function* workerLoadMessages(action) {
	try {
		const payload = yield call(chatService.getById, action.payload);
		yield put({
			payload,
			type: LOAD_CHAT_SUCCESS,
		});
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `Load chat have error: ${e.message}`,
		});
	}
}

/**
 * Update chat messages
 * @param action - Payload is object with array of messages and STOMP message object {type: string, payload: {body: object, message: object}}
 */
function* workerReceiveMessage(action) {
	const { message, body } = action.payload;
	try {
		yield put({
			payload: body,
			type: UPDATE_CHAT,
		});
		message.ack();
	} catch (e) {
		// console.error(e);
		if (message) {
			message.nack();
		}
	}
}

export default function* chatWatcher(obj) {
	const { netConfig, dbConfig, defaultParams } = obj;

	const { prefixStorageKey, postfixStorageKey } = defaultParams.constants.sideSettings;
	keyPref = `${prefixStorageKey}User${postfixStorageKey}`;

	sideSettingsService = new SideSettingsService({
		dbConfig,
		defaultParams,
	});

	chatService = new ChatServiceImpl({
		dbConfig,
		netConfig,
		defaultParams,
	});
	counterService = new CounterServiceImpl({
		dbConfig,
		netConfig,
		defaultParams,
	});

	webSocket = new WebSocketImpl({
		netConfig,
		defaultParams,
		serviceQueue: proxyQueue,
		clientName: "proxy",
	});

	yield takeEvery(SEND_MESSAGE, workerSendMessage);
	yield takeEvery(LOAD_CHAT, workerLoadMessages);
	yield takeEvery(LISTEN_SOCKET, workerReceiveMessage);
	yield takeEvery(INIT_SOCKET, workerInitSocket);
}
