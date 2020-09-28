/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T00:57
 */
//--------index.js--------

import {constants} from "../../../../config/front-end/constants";

const {SEND_MESSAGE} = constants.chat.actions;

export const sendMessage = (payload) => {
	return {
		type: SEND_MESSAGE,
		payload
	}
};
