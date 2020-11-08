/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:08
 */
// --------index.js--------

import LocalizedStrings from "react-localization";
import { constants } from "../../../../../../config/constants.json";
import localization from "../../../../../../config/components/navigation/Side/Settings/localization.json";

const { CHANGE_MODE, CHANGE_SIDE_MENU, INIT_LOCAL_PREF } = constants.sideSettings.actions;

const { USER_ADD_SUCCESS } = constants.user.actions;
const { COUNTER_GETBYID_SUCCESS } = constants.counter.actions;

const { settings, chats } = constants.navigation.buttonId;

const initialState = {
	loadSettings: true,
	mode: "light",
	darkLightChecked: true,
	leftRightChecked: true,
	counterProgress: true,
	isInitSocket: false,
	strings: new LocalizedStrings(localization),
	counters: [],
	nav_side: {
		left: chats,
		right: settings,
	},
	user: {
		username: "",
		id: "",
	},
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_ADD_SUCCESS: {
			return {
				...state,
				isInitSocket: true,
				user: {
					...state.user,
					...action.payload,
				},
			};
		}
		case COUNTER_GETBYID_SUCCESS: {
			return {
				...state,
				counters: action.payload,
				counterProgress: false,
			};
		}
		case CHANGE_MODE: {
			return {
				...state,
				mode: state.mode === "light" ? "dark" : "light",
				darkLightChecked: !state.darkLightChecked,
			};
		}
		case CHANGE_SIDE_MENU: {
			return {
				...state,
				leftRightChecked: !state.leftRightChecked,
				nav_side: {
					left: !state.leftRightChecked ? chats : settings,
					right: state.leftRightChecked ? chats : settings,
				},
			};
		}
		case INIT_LOCAL_PREF: {
			return {
				...state,
				...action.payload,
				loadSettings: false,
			};
		}
		default:
			return state;
	}
};
