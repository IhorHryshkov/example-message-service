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
import Sleep                  from '../../../../../../utils/Sleep';

const {USER_ALL, USER_ALL_SUCCESS, USER_SELECT, USER_SELECT_SUCCESS} = constants.user.actions;

const {ERROR}     = constants.global.actions;
const {retryTime} = constants.global.preferences;

let userService;

export default function* sideUsersWatcher(obj) {
	const {netConfig, dbConfig, defaultParams} = obj;

	userService = new UserServiceImpl({
		netConfig,
		dbConfig,
		defaultParams
	});

	yield takeEvery(
		USER_ALL,
		workerAll
	);
	yield takeEvery(
		USER_SELECT,
		workerUserSelect
	);
}

function* workerAll(action) {
	try {
		const payload = yield call(
			userService.all,
			action.payload
		);
		yield put({
			payload,
			type: USER_ALL_SUCCESS
		});
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `Load users have error: ${e.message}`
		});
		yield call(
			Sleep.sleep,
			retryTime
		);
		yield put({
			payload: action.payload,
			type   : USER_ALL
		});
	}

}

function* workerUserSelect(action) {
	yield put({
		payload: action.payload,
		type   : USER_SELECT_SUCCESS
	});
}
