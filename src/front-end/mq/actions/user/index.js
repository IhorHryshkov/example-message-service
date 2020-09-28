/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-21T04:00
 */
//--------index.js--------

import {constants} from "../../../../config/front-end/constants";

const {USER_ADD, USER_ADD_SUCCESS} = constants.user.actions;

export const addUser = payload => {
	return {
		payload,
		type: USER_ADD
	}
};

export const addUserSuccess = payload => {
	return {
		payload,
		type: USER_ADD_SUCCESS
	}
};
