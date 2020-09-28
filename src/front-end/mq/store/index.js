/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:09
 */
//--------index.js--------

import {applyMiddleware, compose, createStore} from "redux";

import createSagaMiddleware from "redux-saga";
import reducerSettings      from "../reducers/navigation/Common/Side/Settings";
import sagaSettings         from "../middleware/navigation/Common/Side/Settings";

const storeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
const initMiddleware = createSagaMiddleware();
const store          = createStore(reducerSettings,
	storeEnhancers(applyMiddleware(initMiddleware))
);
const init           = ({dbConfig, defaultParams}) => {
	initMiddleware.run(
		sagaSettings,
		{
			dbConfig,
			defaultParams
		}
	);
	return store;
};


export {init};
