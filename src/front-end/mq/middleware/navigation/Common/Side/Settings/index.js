/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:07
 */
//--------index.js--------

import {call, put, takeEvery} from "redux-saga/effects";
import {constants}            from "../../../../../../../config/front-end/constants.json";
import SideSettingsService    from '../../../../../../services/SideSettingsServiceImpl';

const {GET_LOCAL_PREF, ADD_LOCAL_PREF, INIT_LOCAL_PREF} = constants.sideSettings.actions;
const {ERROR}                                           = constants.global.actions;

let sideSettingsService;

export default function* sideSettingsWatcher(obj) {
	const {dbConfig, defaultParams} = obj;
	sideSettingsService = new SideSettingsService({
		dbConfig,
		defaultParams
	});

	yield takeEvery(GET_LOCAL_PREF, workerGetLocalPref);
	yield takeEvery(ADD_LOCAL_PREF, workerAddLocalPref);
}

function* workerGetLocalPref(action) {
	try {
		const result = yield call(sideSettingsService.all, action.payload);
		yield put({
			type   : INIT_LOCAL_PREF,
			payload: result
		});
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `Load settings from local storage have error: ${e.message}`
		});
	}
}

function* workerAddLocalPref(action) {
	try {
		yield call(sideSettingsService.add, action.payload);
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `Add settings to local storage have error: ${e.message}`
		});
	}
}

