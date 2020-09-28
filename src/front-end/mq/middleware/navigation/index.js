/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:07
 */
//--------index.js--------

import {put, takeEvery}      from "redux-saga/effects";
import {constants}           from "../../../../config/front-end/constants";
import {searchFirstIdInNode} from '../../../utils/Search';

const {ALL_CLICK_MENU, CLOSE_ALL, OPEN_MENU, CLOSE_MENU, ALL_KEY_MENU, TOUCH_END_MENU, TOUCH_CLEAN_MENU, SELECT_ITEM_MENU, UPDATE_ITEM_MENU} = constants.navigation.actions;

export default function* watcherNavigation() {
	yield takeEvery(
		ALL_CLICK_MENU,
		workerAllClick
	);
	yield takeEvery(
		ALL_KEY_MENU,
		workerHandleAllKey
	);
	yield takeEvery(
		TOUCH_END_MENU,
		workerTouchEnd
	);
	yield takeEvery(
		SELECT_ITEM_MENU,
		workerSelectItem
	);
}

function* workerAllClick(action) {
	const {path, excludeClose, buttonIds, nav_side} = action.payload;
	const id                                        = searchFirstIdInNode(path);
	if (excludeClose.indexOf(id) < 0) {
		yield put({type: CLOSE_ALL});
	} else if (buttonIds.indexOf(id) >= 0) {
		let type;
		if (nav_side[id].show !== "show") {
			type = OPEN_MENU;
		} else {
			type = CLOSE_MENU;
		}
		yield put({
			type,
			payload: id
		});
	}
}

function* workerHandleAllKey(action) {
	const key = action.payload;
	if (key === 'Escape') {
		yield put({type: CLOSE_ALL});
	}
}

function* workerSelectItem(action) {
	const {key} = action.payload;
	//Add request data to server for load specified data from server side
	yield put({
		type   : UPDATE_ITEM_MENU,
		payload: key
	});
}

function* workerTouchEnd(action) {
	const {startX, endX, swiping, minSwipeDistance, left, right} = action.payload;

	const fullSwipeSize = endX - startX;
	const absX          = Math.abs(fullSwipeSize);
	if (swiping && absX > minSwipeDistance) {
		let close,
			open;
		if (fullSwipeSize > 0) {
			close = right;
			open  = left;
		} else {
			close = left;
			open  = right;
		}
		yield put({
			type   : OPEN_MENU,
			payload: open
		});
		yield put({
			type   : CLOSE_MENU,
			payload: close
		});
	}
	yield put({type: TOUCH_CLEAN_MENU});
}
