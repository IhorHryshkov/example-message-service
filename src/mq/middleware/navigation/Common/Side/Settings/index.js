/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:07
 */
// --------index.js--------

import { call, put, takeEvery } from "redux-saga/effects";
import { constants } from "../../../../../../config/constants.json";
import SideSettingsService from "../../../../../../services/PreferencesServiceImpl";
import CounterService from "../../../../../../services/CounterServiceImpl";
import StatusService from "../../../../../../services/StatusServiceImpl";
import TypeService from "../../../../../../services/TypeServiceImpl";
import CallbackServiceImpl from "../../../../../../services/CallbackServiceImpl";
import WebSocketImpl from "../../../../../../network/websocket/WebSocketImpl";

const { GET_LOCAL_PREF, ADD_LOCAL_PREF, INIT_LOCAL_PREF } = constants.sideSettings.actions;
const { COUNTER_GETBYID, COUNTER_GETBYID_SUCCESS } = constants.counter.actions;
const { ERROR, INIT_SOCKET, LISTEN_SOCKET } = constants.global.actions;

const { serviceQueue } = constants.global.network.webSocket;

let sideSettingsService;
let counterService;
let statusService;
let typeService;
let keyPref;
let callbackService;
let webSocket;
let enums;

/**
 * Load counters from DB
 * @param action - Payload is user ID of the logged user {type: string, payload: string}
 */
function* workerAllCounter(action) {
	try {
		const result = yield call(counterService.getById, action.payload);
		yield put({
			type: COUNTER_GETBYID_SUCCESS,
			payload: result,
		});
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `Load counters by user have error: ${e.message}`,
		});
	}
}

/**
 * Load configuration data from DB
 * @param action - Payload is object with DB key {type: string, payload:{key: string}}
 */
function* workerGetLocalPref(action) {
	try {
		yield call(statusService.all);
		yield call(typeService.all);
		const result = yield call(sideSettingsService.all, action.payload);
		yield put({
			type: INIT_LOCAL_PREF,
			payload: result,
		});
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `Load settings from local storage have error: ${e.message}`,
		});
	}
}

/**
 * Add or update in DB configuration data
 * @param action - Payload is object with key and data {type: string, payload:{key: string, data:{user:{id: string, username: string, timestamp: number}, mode: string, darkLightChecked: boolean, leftRightChecked: boolean, nav_side: object}}}
 */
function* workerAddLocalPref(action) {
	try {
		const result = yield call(sideSettingsService.all, { key: keyPref });
		yield call(sideSettingsService.add, {
			key: action.payload.key,
			data: { ...result, ...action.payload.data },
		});
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `Add settings to local storage have error: ${e.message}`,
		});
	}
}

/**
 * Init web socket callback processing
 * @param action - Payload is object with queue name and store {type: string, payload: {store: object, username: string}}
 */
function* workerInitSocket(action) {
	const { store, username } = action.payload;
	yield call(
		webSocket.receiveQueue,
		username,
		"server",
		async message => {
			try {
				const body = JSON.parse(message.body);
				const { resId } = body;
				let callbackRes;
				try {
					callbackRes = await callbackService.getById({
						username,
						resId,
					});
				} catch (ex) {
					await callbackService.add({
						username,
						resId: body.resId,
					});
					return;
				}
				switch (callbackRes.callback) {
					case enums.ADD_USER: {
						store.user.dispatch({
							type: LISTEN_SOCKET,
							payload: {
								message,
								body,
								username,
							},
						});
						break;
					}
					case enums.UPDATE_USER: {
						store.user.dispatch({
							type: LISTEN_SOCKET,
							payload: {
								message,
								body,
								username,
							},
						});
						break;
					}
					case enums.UPDATE_COUNTER: {
						store.app.dispatch({
							type: LISTEN_SOCKET,
							payload: {
								message,
								body,
								username,
							},
						});
						message.ack();
						break;
					}
					default: {
						message.ack();
						await callbackService.add({
							username,
							resId: body.resId,
						});
					}
				}
			} catch (e) {
				// console.error(e);
			}
		},
		() => {
			store.app.dispatch({
				type: INIT_SOCKET,
				payload: action.payload,
			});
		}
	);
}

/**
 * Callback processing messages for counter add
 * @param action - Action data for counter callback processing {message: object, body: {userId: string, resId: string}, username: string}
 */
function* workerListener(action) {
	const { message, body, username } = action.payload;
	try {
		const { resId } = body;
		const result = yield call(counterService.updateMessageCounter);
		yield put({
			type: COUNTER_GETBYID_SUCCESS,
			payload: result,
		});

		yield call(callbackService.add, {
			username,
			resId,
		});
		message.ack();
	} catch (e) {
		// console.error(e);
		if (message) {
			message.nack();
		}
	}
}

export default function* sideSettingsWatcher(obj) {
	const { netConfig, dbConfig, defaultParams } = obj;

	sideSettingsService = new SideSettingsService({
		dbConfig,
		defaultParams,
	});
	counterService = new CounterService({
		netConfig,
		dbConfig,
		defaultParams,
	});
	statusService = new StatusService({
		netConfig,
		dbConfig,
		defaultParams,
	});
	typeService = new TypeService({
		netConfig,
		dbConfig,
		defaultParams,
	});
	enums = defaultParams.constants.callback.enums;
	callbackService = new CallbackServiceImpl({
		dbConfig,
		netConfig,
		defaultParams,
	});
	webSocket = new WebSocketImpl({
		netConfig,
		defaultParams,
		serviceQueue,
		clientName: "server",
	});
	const { prefixStorageKey, postfixStorageKey } = defaultParams.constants.sideSettings;
	keyPref = `${prefixStorageKey}User${postfixStorageKey}`;

	yield takeEvery(COUNTER_GETBYID, workerAllCounter);
	yield takeEvery(GET_LOCAL_PREF, workerGetLocalPref);
	yield takeEvery(ADD_LOCAL_PREF, workerAddLocalPref);

	yield takeEvery(INIT_SOCKET, workerInitSocket);
	yield takeEvery(LISTEN_SOCKET, workerListener);
}
