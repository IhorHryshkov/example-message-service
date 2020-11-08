/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:06
 */
// --------index.js--------
import { constants } from "../../../config/constants.json";

const {
	HIDE_NAVIGATION,
	SELECT_ITEM_MENU,
	ALL_CLICK_MENU,
	ALL_KEY_MENU,
	TOUCH_START_MENU,
	TOUCH_MOVE_MENU,
	TOUCH_END_MENU,
} = constants.navigation.actions;

export const allClickMenu = payload => {
	return {
		type: ALL_CLICK_MENU,
		payload,
	};
};

export const allKeyMenu = payload => {
	return {
		type: ALL_KEY_MENU,
		payload,
	};
};

export const touchStartMenu = payload => {
	return {
		type: TOUCH_START_MENU,
		payload,
	};
};
export const touchMoveMenu = payload => {
	return {
		type: TOUCH_MOVE_MENU,
		payload,
	};
};
export const touchEndMenu = payload => {
	return {
		type: TOUCH_END_MENU,
		payload,
	};
};

export const selectItemMenu = payload => {
	return {
		type: SELECT_ITEM_MENU,
		payload,
	};
};

export const hideNavigation = payload => {
	return {
		payload,
		type: HIDE_NAVIGATION,
	};
};
