/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T00:57
 */
// --------index.js--------
import { constants } from "../../../config/constants.json";

const { SEND_MESSAGE, INIT_CHAT, LOAD_CHAT } = constants.chat.actions;
const { INIT_SOCKET } = constants.global.actions;

export const sendMessage = payload => {
	return {
		type: SEND_MESSAGE,
		payload,
	};
};

export const initChat = payload => {
	return {
		type: INIT_CHAT,
		payload,
	};
};

export const loadChat = payload => {
	return {
		type: LOAD_CHAT,
		payload,
	};
};

export const initMessageListener = payload => {
	return {
		type: INIT_SOCKET,
		payload,
	};
};
