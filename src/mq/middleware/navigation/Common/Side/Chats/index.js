/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T19:21
 */
// --------index.js--------
import { call, put, takeEvery } from "redux-saga/effects";
import { constants } from "../../../../../../config/constants.json";
import UserServiceImpl from "../../../../../../services/UserServiceImpl";
import Sleep from "../../../../../../utils/Sleep";
import userChatInit from "../../../../../store/navigation/Common/Side/Chats/UserChat";

const { USER_ALL, USER_ALL_SUCCESS } = constants.user.actions;
const { ERROR, INIT_SOCKET } = constants.global.actions;

const { retryTime } = constants.global.preferences;

let userService;
let configs;

/**
 * Load all chats, init for all chats specific stores and send init web socket connection for chat
 * @param action - Payload is object with user ID and store {type: string, payload: {user_id: string, store: object}}
 */
function* workerAll(action) {
	try {
		const { user_id, store } = action.payload;
		const payload = yield call(userService.all, { user_id });
		if (payload && payload.length > 0) {
			for (let i = 0, { length } = payload; i < length; i += 1) {
				if (!store.side.chats[payload[i].id]) {
					store.side.chats[payload[i].id] = userChatInit(configs);
				}
			}
		}
		yield put({
			payload,
			type: USER_ALL_SUCCESS,
		});
		yield call(store.chat.dispatch, {
			type: INIT_SOCKET,
			payload: {
				store,
				user_own_id: user_id,
			},
		});
	} catch (e) {
		// console.error(e);
		yield put({
			type: ERROR,
			payload: `Load users have error: ${e.message}`,
		});
		yield call(Sleep, retryTime);
		yield put({
			payload: action.payload,
			type: USER_ALL,
		});
	}
}

export default function* sideUsersWatcher(obj) {
	configs = obj;

	userService = new UserServiceImpl(configs);

	yield takeEvery(USER_ALL, workerAll);
}
