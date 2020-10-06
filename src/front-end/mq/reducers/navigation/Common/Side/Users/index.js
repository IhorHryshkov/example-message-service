/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T19:20
 */
//--------index.js--------
import {constants}      from '../../../../../../../config/front-end/constants.json';
import LocalizedStrings from 'react-localization';
import localization     from "../../../../../../../config/front-end/components/navigation/Side/Users/localization";

const {USER_ALL_SUCCESS, USER_SELECT_SUCCESS} = constants.user.actions;

const initialState = {
	progress    : true,
	selectedUser: null,
	strings         : new LocalizedStrings(localization),
	users       : []
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_ALL_SUCCESS: {
			return {
				...state,
				progress: false,
				users   : action.payload
			}
		}
		case USER_SELECT_SUCCESS: {
			const indexOld = state.users.findIndex(user => user.id === state.selectedUser);
			if (indexOld >= 0) {
				state.users[indexOld].isChat = false;
			}
			const indexNew               = state.users.findIndex(user => user.id === action.payload);
			state.users[indexNew].isChat = true;
			return {
				...state,
				users       : state.users,
				selectedUser: action.payload
			}
		}
		default:
			return state;
	}
};
