/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-12T14:14
 */
// --------index.js--------
import { call, put, takeEvery } from "redux-saga/effects";
import { constants } from "../../../../../../../config/constants.json";
import SideSettingsService from "../../../../../../../services/PreferencesServiceImpl";
import UserServiceImpl from "../../../../../../../services/UserServiceImpl";

const { USER_SELECT, USER_SELECT_SUCCESS, USER_MESSAGE_COUNT_UPDATE } = constants.user.actions;
const { LISTEN_SOCKET } = constants.global.actions;

let sideSettingsService;
let keyPref;
let userService;

/**
 * Select chat processing, update user of last chat, update user timestamp in DB for show user at the top of the list
 * @param action - Payload object with user ID and store {type: string, payload:{user_id: string, store: object}}
 */
function* workerUserSelect(action) {
	const { store, user_id } = action.payload;
	const result = yield call(sideSettingsService.all, { key: keyPref });
	if (result && result.lastChat && result.lastChat !== user_id && store.side.chats[result.lastChat]) {
		yield call(store.side.chats[result.lastChat].dispatch, {
			payload: null,
			type: USER_SELECT_SUCCESS,
		});
	}
	result.lastChat = user_id;
	yield call(sideSettingsService.add, {
		key: keyPref,
		data: result,
	});
	yield call(userService.select, user_id);
	yield put({
		payload: user_id,
		type: USER_SELECT_SUCCESS,
	});
}

/**
 * Update message counter
 * @param action - Payload object with body from server and STOMP message {type: string, payload: {message: object, body:{user_id: string}}}
 */
function* workerUpdateUserChat(action) {
	const { message, body } = action.payload;
	try {
		yield put({
			payload: body.user_id,
			type: USER_MESSAGE_COUNT_UPDATE,
		});
		message.ack();
	} catch (e) {
		// console.error(e);
		if (message) {
			message.nack();
		}
	}
}

export default function* sideUserChatWatcher(obj) {
	const { netConfig, dbConfig, defaultParams } = obj;

	const { prefixStorageKey, postfixStorageKey } = defaultParams.constants.sideSettings;
	keyPref = `${prefixStorageKey}User${postfixStorageKey}`;

	sideSettingsService = new SideSettingsService({
		dbConfig,
		defaultParams,
	});
	userService = new UserServiceImpl({
		netConfig,
		dbConfig,
		defaultParams,
	});

	yield takeEvery(USER_SELECT, workerUserSelect);
	yield takeEvery(LISTEN_SOCKET, workerUpdateUserChat);
}
