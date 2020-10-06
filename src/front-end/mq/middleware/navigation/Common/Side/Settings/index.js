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
import CounterService         from '../../../../../../services/CounterServiceImpl';
import StatusService          from '../../../../../../services/StatusServiceImpl';

const {GET_LOCAL_PREF, ADD_LOCAL_PREF, INIT_LOCAL_PREF} = constants.sideSettings.actions;
const {COUNTER_GETBYID, COUNTER_GETBYID_SUCCESS}        = constants.counter.actions;
const {ERROR}                                           = constants.global.actions;

let sideSettingsService;
let counterService;
let statusService;

export default function* sideSettingsWatcher(obj) {
	const {netConfig, dbConfig, defaultParams} = obj;

	sideSettingsService = new SideSettingsService({
		dbConfig,
		defaultParams
	});
	counterService      = new CounterService({
		netConfig,
		dbConfig,
		defaultParams
	});
	statusService       = new StatusService({
		netConfig,
		dbConfig,
		defaultParams
	});

	yield takeEvery(
		COUNTER_GETBYID,
		workerAllCounter
	);
	yield takeEvery(
		GET_LOCAL_PREF,
		workerGetLocalPref
	);
	yield takeEvery(
		ADD_LOCAL_PREF,
		workerAddLocalPref
	);
}

function* workerAllCounter(action) {
	try {
		const result = yield call(
			counterService.getById,
			action.payload
		);
		yield put({
			type   : COUNTER_GETBYID_SUCCESS,
			payload: result
		});
	} catch (e) {
		yield put({
			type   : ERROR,
			payload: `Load counters by user have error: ${e.message}`
		});
	}
}

function* workerGetLocalPref(action) {
	try {
		yield call(statusService.all);
		const result = yield call(
			sideSettingsService.all,
			action.payload
		);
		yield put({
			type   : INIT_LOCAL_PREF,
			payload: result
		});
	} catch (e) {
		console.log(e);
		yield put({
			type   : ERROR,
			payload: `Load settings from local storage have error: ${e.message}`
		});
	}
}

function* workerAddLocalPref(action) {
	try {
		yield call(
			sideSettingsService.add,
			action.payload
		);
	} catch (e) {
		console.log(e);
		yield put({
			type   : ERROR,
			payload: `Add settings to local storage have error: ${e.message}`
		});
	}
}

