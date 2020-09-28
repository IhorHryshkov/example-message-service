/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T19:21
 */
//--------index.js--------

import {call, put, takeEvery} from "redux-saga/effects";
import {constants}            from "../../../../../../../config/front-end/constants.json";
import UserServiceImpl        from '../../../../../../services/UserServiceImpl';

const {USER_GET, USER_GET_SUCCESS, USER_SELECT, USER_SELECT_SUCCESS} = constants.user.actions;

const {ERROR} = constants.global.actions;

let userService;

export default function* sideUsersWatcher(obj) {
	const {netConfig, dbConfig, defaultParams} = obj;

	userService = new UserServiceImpl({
		netConfig,
		dbConfig,
		defaultParams
	});

	yield takeEvery(
		USER_GET,
		workerGetUser
	);
	yield takeEvery(
		USER_SELECT,
		workerUserSelect
	);
}

function* workerGetUser(action) {
	try {
		const payload = yield call(
			userService.all,
			action.payload
		);
		yield put({
			payload,
			type: USER_GET_SUCCESS
		});
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `Load users have error: ${e.message}`
		});
	}
}

function* workerUserSelect(action) {
	yield put({
		payload: action.payload,
		type   : USER_SELECT_SUCCESS
	});
}
