/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T19:20
 */
// --------index.js--------
import LocalizedStrings from "react-localization";
import { constants } from "../../../../../../config/constants.json";
import localization from "../../../../../../config/components/navigation/Side/Chats/localization.json";

const { USER_ALL_SUCCESS } = constants.user.actions;

const initialState = {
	progress: true,
	users: [],
	strings: new LocalizedStrings(localization),
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_ALL_SUCCESS: {
			return {
				...state,
				progress: false,
				users: action.payload,
			};
		}
		default:
			return state;
	}
};
