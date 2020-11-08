/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-12T13:29
 */
// --------index.js--------

import LocalizedStrings from "react-localization";
import localization from "../../../../../../../config/components/navigation/Side/Chats/UserChat/localization.json";
import changeNumberToTextCounter from "../../../../../../../utils/Converter";
import { constants } from "../../../../../../../config/constants.json";

const { USER_SELECT_SUCCESS, USER_MESSAGE_COUNT_UPDATE } = constants.user.actions;

const initialState = {
	selectedUser: null,
	strings: new LocalizedStrings(localization),
	newMessageCount: 0,
	newMessageCountString: "",
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_MESSAGE_COUNT_UPDATE: {
			const newMessageCount = state.newMessageCount + 1;
			return {
				...state,
				newMessageCount,
				newMessageCountString: changeNumberToTextCounter(newMessageCount),
			};
		}
		case USER_SELECT_SUCCESS: {
			return {
				...state,
				selectedUser: action.payload,
				newMessageCount: 0,
				newMessageCountString: "",
			};
		}
		default:
			return state;
	}
};
