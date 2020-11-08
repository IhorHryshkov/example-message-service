/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-20T18:58
 */
// --------index.js--------

import { call, put, takeEvery } from "redux-saga/effects";
import UserServiceImpl from "../../../services/UserServiceImpl";
import { constants } from "../../../config/constants.json";
import CallbackServiceImpl from "../../../services/CallbackServiceImpl";

const { USER_ADD, USER_ADD_PROGRESS, USERNAME_BUSY, USER_ADD_SUCCESS } = constants.user.actions;

const { ERROR, LISTEN_SOCKET, INIT_SOCKET } = constants.global.actions;

let userService;
let callbackService;

/**
 * User login processing
 * @param action - Action data for login processing {type: string, payload: {username: string, oldUsername: string, id: string, store: object}}
 */
function* workerLogin(action) {
	try {
		const { username, oldUsername, id, store } = action.payload;
		yield put({
			type: USER_ADD_PROGRESS,
		});

		const result =
			oldUsername && oldUsername === username
				? yield call(userService.update, {
						username,
						id,
				  })
				: yield call(userService.add, { username });
		if (result.status === 202) {
			yield put({
				type: USERNAME_BUSY,
			});
		} else if (result.status === 200 || result.status === 201) {
			yield call(store.app.dispatch, {
				type: INIT_SOCKET,
				payload: {
					store,
					username,
				},
			});
		} else {
			throw new Error(`Result status is invalid: ${result.status}`);
		}
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `User login have error: ${e.message}`,
		});
	}
}

/**
 * Callback processing messages for user add and update
 * @param action - Action data for user callback processing {message: object, body: {userId: string, resId: string}, username: string}
 */
function* workerListener(action) {
	const { message, body, username } = action.payload;
	try {
		const { resId, userId } = body;
		const timestamp = Date.now();
		yield put({
			type: USER_ADD_SUCCESS,
			payload: {
				username,
				id: userId,
				timestamp,
			},
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

export default function* loginWatcher(obj) {
	const { netConfig, dbConfig, defaultParams } = obj;

	callbackService = new CallbackServiceImpl({
		dbConfig,
		netConfig,
		defaultParams,
	});

	userService = new UserServiceImpl({
		netConfig,
		dbConfig,
		defaultParams,
	});

	yield takeEvery(USER_ADD, workerLogin);
	yield takeEvery(LISTEN_SOCKET, workerListener);
}
