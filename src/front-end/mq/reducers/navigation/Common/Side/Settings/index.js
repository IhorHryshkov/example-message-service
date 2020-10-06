/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:08
 */
//--------index.js--------

import {constants}      from "../../../../../../../config/front-end/constants.json";
import LocalizedStrings from 'react-localization';
import localization     from "../../../../../../../config/front-end/components/navigation/Side/Settings/localization";

const {CHANGE_MODE, CHANGE_SIDE_MENU, INIT_LOCAL_PREF} = constants.sideSettings.actions;

const {USER_ADD_SUCCESS}        = constants.user.actions;
const {COUNTER_GETBYID_SUCCESS} = constants.counter.actions;

const {settings, users} = constants.navigation.buttonId;

const initialState = {
	loadSettings    : true,
	mode            : "light",
	darkLightChecked: true,
	leftRightChecked: true,
	counterProgress : true,
	strings         : new LocalizedStrings(localization),
	counters        : [],
	nav_side        : {
		left : users,
		right: settings
	},
	user            : {
		username: "",
		id      : ""
	}
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_ADD_SUCCESS: {
			const {id, username, timestamp} = action.payload;
			return {
				...state,
				user: {
					...state.user,
					id,
					username,
					timestamp
				}
			};
		}
		case COUNTER_GETBYID_SUCCESS: {
			return {
				...state,
				counters       : action.payload,
				counterProgress: false
			};
		}
		case CHANGE_MODE: {
			return {
				...state,
				mode            : state.mode === "light" ? "dark" : "light",
				darkLightChecked: !state.darkLightChecked
			}
		}
		case CHANGE_SIDE_MENU: {
			return {
				...state,
				leftRightChecked: !state.leftRightChecked,
				nav_side        : {
					left : !state.leftRightChecked ? users : settings,
					right: state.leftRightChecked ? users : settings
				}
			}
		}
		case INIT_LOCAL_PREF: {
			return {
				...state, ...action.payload,
				loadSettings: false
			};
		}
		default:
			return state;
	}
};
