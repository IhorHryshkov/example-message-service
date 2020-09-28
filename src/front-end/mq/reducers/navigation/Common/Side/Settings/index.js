/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:08
 */
//--------index.js--------

import {constants} from "../../../../../../../config/front-end/constants.json";

const {CHANGE_MODE, CHANGE_SIDE_MENU, INIT_LOCAL_PREF} = constants.sideSettings.actions;

const {USER_ADD_SUCCESS} = constants.user.actions;

const {settings, users} = constants.navigation.buttonId;

const initialState = {
	mode            : "light",
	darkLightChecked: true,
	leftRightChecked: true,
	counters        : [
		{
			"name"  : "Message",
			"counts": 20
		},
		{
			"name"  : "Online",
			"counts": 2
		},
		{
			"name"  : "Offline",
			"counts": 1
		}
	],
	nav_side        : {
		left : users,
		right: settings
	},
	user            : {
		username: "",
		id      : "",
		guest   : "Guest"
	}
};

export default (state = initialState, action) => {
	switch (action.type) {
		case USER_ADD_SUCCESS: {
			const {id, username} = action.payload;
			return {
				...state,
				user: {
					...state.user,
					id,
					username
				}
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
			return {...state, ...action.payload};
		}
		default:
			return state;
	}
};
