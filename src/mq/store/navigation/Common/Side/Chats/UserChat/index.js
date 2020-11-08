/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-12T13:27
 */
// --------index.js--------

import { applyMiddleware, compose, createStore } from "redux";

import createSagaMiddleware from "redux-saga";
import reducer from "../../../../../../reducers/navigation/Common/Side/Chats/UserChat";
import saga from "../../../../../../middleware/navigation/Common/Side/Chats/UserChat";

const storeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
export default ({ netConfig, dbConfig, defaultParams }) => {
	const initMiddleware = createSagaMiddleware();
	const store = createStore(reducer, storeEnhancers(applyMiddleware(initMiddleware)));

	initMiddleware.run(saga, {
		netConfig,
		dbConfig,
		defaultParams,
	});
	return store;
};
