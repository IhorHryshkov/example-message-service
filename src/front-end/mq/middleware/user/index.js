/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-20T18:58
 */
//--------index.js--------

import UserServiceImpl        from '../../../services/UserServiceImpl';
import {call, put, takeEvery} from 'redux-saga/effects';
import {constants}            from '../../../../config/front-end/constants.json';
import WebSocketImpl          from '../../../network/websocket/WebSocketImpl';
import CallbackServiceImpl    from '../../../services/CallbackServiceImpl';

const {USER_ADD, USER_ADD_PROGRESS, USERNAME_BUSY, USER_ADD_SUCCESS} = constants.user.actions;

const {ERROR, LISTEN_SOCKET, INIT_SOCKET} = constants.global.actions;

let userService;
let webSocket;
let callbackService;
let enums;

export default function* loginWatcher(obj) {
	const {netConfig, dbConfig, defaultParams} = obj;

	enums           = defaultParams.constants.callback.enums;
	callbackService = new CallbackServiceImpl({
		dbConfig,
		netConfig,
		defaultParams
	});

	userService = new UserServiceImpl({
		netConfig,
		dbConfig,
		defaultParams
	});
	webSocket   = new WebSocketImpl({
		netConfig,
		defaultParams
	});

	yield takeEvery(
		USER_ADD,
		workerLogin
	);
	yield takeEvery(
		INIT_SOCKET,
		initSocket
	);
	yield takeEvery(
		LISTEN_SOCKET,
		workerListener
	);

	yield put({
		payload: netConfig.getSockService(),
		type   : INIT_SOCKET
	});
}

function* initSocket(action) {
	try {
		yield call(
			webSocket.init,
			action.payload
		);
	} catch (e) {
	}
}

function* workerLogin(action) {
	try {
		const {payload} = action;
		yield put({
			type: USER_ADD_PROGRESS
		});

		const result = payload.oldUsername &&
		payload.oldUsername === payload.username
			? yield call(
				userService.update,
				payload
			) : yield call(
				userService.add,
				payload
			);
		if (result.status === 202) {
			yield put({
				payload,
				type: USERNAME_BUSY
			});
		} else if (result.status === 200 || result.status === 201) {
			yield put({
				payload,
				type: LISTEN_SOCKET
			});
		} else {
			throw new Error(`Result status is invalid: ${result.status}`);
		}
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `User login have error: ${e.message}`
		});
	}
}

function* workerListener(action) {
	try {
		const {username}      = action.payload;
		const timestamp       = new Date().getTime();
		const result          = yield call(
			webSocket.receive,
			username
		);
		const {resId, userId} = result;

		const callbackRes = yield call(
			callbackService.getById,
			{
				username,
				resId
			}
		);
		switch (callbackRes.callback) {
			case enums.ADD_USER: {
				yield put({
					type   : USER_ADD_SUCCESS,
					payload: {
						username,
						id: userId,
						timestamp
					}
				});
				yield call(
					callbackService.add,
					{
						username,
						resId
					}
				);
				break;
			}
			case enums.UPDATE_USER: {
				yield put({
					type   : USER_ADD_SUCCESS,
					payload: {
						username,
						id: userId,
						timestamp
					}
				});
				yield call(
					callbackService.add,
					{
						username,
						resId
					}
				);
				break;
			}
			default:
				console.log(`Callback not found: ${callbackRes.callback}`);
		}
		yield put({
			payload: action.payload,
			type   : LISTEN_SOCKET
		});
	} catch (e) {
		console.log(e);
	}
}
