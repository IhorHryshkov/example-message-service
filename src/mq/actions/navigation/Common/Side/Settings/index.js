/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:05
 */
// --------index.js--------

import { constants } from "../../../../../../config/constants.json";

const { CHANGE_MODE, CHANGE_SIDE_MENU, GET_LOCAL_PREF, ADD_LOCAL_PREF } = constants.sideSettings.actions;
const { COUNTER_GETBYID } = constants.counter.actions;
const { INIT_SOCKET } = constants.global.actions;

export const allUserCounters = payload => {
	return {
		payload,
		type: COUNTER_GETBYID,
	};
};

export const changeMode = () => {
	return { type: CHANGE_MODE };
};

export const changeSideMenu = () => {
	return { type: CHANGE_SIDE_MENU };
};

export const getLocalPref = payload => {
	return {
		payload,
		type: GET_LOCAL_PREF,
	};
};

export const addLocalPref = payload => {
	return {
		payload,
		type: ADD_LOCAL_PREF,
	};
};

export const initSocket = payload => {
	return {
		payload,
		type: INIT_SOCKET,
	};
};
