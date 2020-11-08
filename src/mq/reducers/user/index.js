/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-20T18:56
 */
// --------index.js--------

import * as Yup from "yup";

import LocalizedStrings from "react-localization";
import { constants } from "../../../config/constants.json";
import localization from "../../../config/components/login/localization.json";

const { USER_ADD_PROGRESS, USER_ADD_SUCCESS, USERNAME_BUSY } = constants.user.actions;

const { ERROR, RESET } = constants.global.actions;
const { username } = constants.user.validation;
const strings = new LocalizedStrings(localization);

const regexUsername = new RegExp(username.regex, "g");

const initialState = {
	strings,
	user: {},
	exception: null,
	progress: false,
	schema: Yup.object({
		username: Yup.string()
			.min(username.min, strings.username.min)
			.max(username.max, strings.username.max)
			.matches(regexUsername, strings.username.notValid)
			.required(strings.username.required),
	}),
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USERNAME_BUSY: {
			return {
				...state,
				exception: strings.exceptions.userBusy,
				progress: false,
			};
		}
		case USER_ADD_PROGRESS: {
			return {
				...state,
				progress: true,
			};
		}
		case USER_ADD_SUCCESS: {
			return {
				...state,
				progress: false,
				exception: null,
				user: action.payload,
			};
		}
		case RESET: {
			return {
				...state,
				user: {},
				isUsernameBusy: false,
				progress: false,
				exception: null,
			};
		}
		case ERROR: {
			return {
				...state,
				exception: strings.exceptions.serverError,
				progress: false,
			};
		}
		default:
			return state;
	}
};
