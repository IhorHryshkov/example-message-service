/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-25T08:09
 */
// --------index.js--------

import * as Yup from "yup";

import LocalizedStrings from "react-localization";
import { constants } from "../../../config/constants.json";
import localization from "../../../config/components/chat/localization.json";

const { ERROR } = constants.global.actions;
const { INIT_CHAT, LOAD_CHAT_SUCCESS, UPDATE_CHAT } = constants.chat.actions;
const { message } = constants.chat.validation;
const strings = new LocalizedStrings(localization);

const initialState = {
	strings,
	selectedChat: null,
	exceptionMessage: null,
	progress: true,
	schema: Yup.object({
		message: Yup.string().min(message.min, strings.message.min).required(strings.message.required),
	}),
	messages: [],
};

export default (state = initialState, action) => {
	switch (action.type) {
		case UPDATE_CHAT: {
			return {
				...state,
				// messages: state.messages.push(action.payload)
			};
		}
		case LOAD_CHAT_SUCCESS: {
			return {
				...state,
				messages: action.payload,
				progress: false,
			};
		}
		case INIT_CHAT: {
			return {
				...state,
				selectedChat: action.payload,
			};
		}
		case ERROR: {
			return {
				...state,
				progress: true,
				selectedChat: null,
			};
		}
		default:
			return state;
	}
};
