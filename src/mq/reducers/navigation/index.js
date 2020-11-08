/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:08
 */
// --------index.js--------

import LocalizedStrings from "react-localization";
import { constants } from "../../../config/constants.json";
import localization from "../../../config/components/navigation/localization.json";

const {
	HIDE_NAVIGATION_UPDATE,
	CLOSE_ALL,
	OPEN_MENU,
	CLOSE_MENU,
	TOUCH_START_MENU,
	TOUCH_MOVE_MENU,
	TOUCH_CLEAN_MENU,
	UPDATE_ITEM_MENU,
} = constants.navigation.actions;

const { settings, chats } = constants.navigation.buttonId;

const initialState = {
	navigationShow: true,
	nav_side: {
		[chats]: {
			headerName: "site",
			show: "show",
			buttonId: chats,
		},
		[settings]: {
			headerName: "user",
			show: "",
			buttonId: settings,
		},
	},
	excludeClose: ["side-menu", "side-menu-body", "side-menu-dark-light-check", "side-menu-left-right", "side-users-chat", settings, chats],
	minSwipeDistance: constants.navigation.minSwipeDistance,
	swipe: {},
	strings: new LocalizedStrings(localization),
};

export default (state = initialState, action) => {
	switch (action.type) {
		case HIDE_NAVIGATION_UPDATE: {
			return {
				...state,
				navigationShow: action.payload,
			};
		}
		case CLOSE_ALL: {
			if (state.nav_side[chats].show !== "" || state.nav_side[settings].show !== "") {
				return {
					...state,
					nav_side: {
						...state.nav_side,
						[chats]: {
							...state.nav_side[chats],
							show: "",
						},
						[settings]: {
							...state.nav_side[settings],
							show: "",
						},
					},
				};
			}
			return state;
		}
		case OPEN_MENU: {
			return {
				...state,
				nav_side: {
					...state.nav_side,
					[action.payload]: {
						...state.nav_side[action.payload],
						show: "show",
					},
				},
			};
		}
		case CLOSE_MENU: {
			return {
				...state,
				nav_side: {
					...state.nav_side,
					[action.payload]: {
						...state.nav_side[action.payload],
						show: "",
					},
				},
			};
		}
		case TOUCH_START_MENU: {
			return {
				...state,
				swipe: { startX: action.payload },
			};
		}
		case TOUCH_MOVE_MENU: {
			return {
				...state,
				swipe: {
					...state.swipe,
					swiping: true,
					endX: action.payload,
				},
			};
		}
		case TOUCH_CLEAN_MENU: {
			return {
				...state,
				swipe: {},
			};
		}
		case UPDATE_ITEM_MENU: {
			return {
				...state,
				nav_active: action.payload,
			};
		}
		default:
			return state;
	}
};
