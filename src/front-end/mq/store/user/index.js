/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:43
 */
//--------index.js--------
import {applyMiddleware, compose, createStore} from "redux";

import createSagaMiddleware from "redux-saga";
import reducer              from "../../reducers/user";
import saga                 from "../../middleware/user";

const storeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
const initMiddleware = createSagaMiddleware();
const store          = createStore(
	reducer,
	storeEnhancers(applyMiddleware(initMiddleware))
);
const init           = ({netConfig, dbConfig, defaultParams}) => {
	initMiddleware.run(
		saga,
		{
			netConfig,
			dbConfig,
			defaultParams
		}
	);
	return store;
};


export {init};
