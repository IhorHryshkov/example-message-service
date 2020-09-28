/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T20:07
 */
//--------index.js--------

import {constants} from "../../../../../../../config/front-end/constants.json";

const {USER_GET, USER_SELECT} = constants.user.actions;

export const loadUser = payload => {
	return {
		payload,
		type: USER_GET
	}
};

export const selectUser = payload => {
	return {
		payload,
		type: USER_SELECT
	}
};
