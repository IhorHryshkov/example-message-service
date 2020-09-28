/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
import './front-end/assets/css/all.css';

import React                      from 'react';
import ReactDOM                   from 'react-dom';
import {Provider}                 from "react-redux";
import App                        from './front-end/components';
import {init as appInit}          from "./front-end/mq/store";
// import {init as sideSettingsInit} from "./front-end/mq/store/navigation/Common/Side/Settings";
import {init as sideUsersInit}    from "./front-end/mq/store/navigation/Common/Side/Users";
import {init as chatInit}         from "./front-end/mq/store/chat";
import {init as userInit}         from "./front-end/mq/store/user";
import navigationStore            from "./front-end/mq/store/navigation";
import messages                   from './config/front-end/messages';
import DatabaseConfigImpl         from './front-end/database';
import NetworkConfigImpl          from './front-end/network';
import {constants}                from './config/front-end/constants';
import Converter                  from './front-end/utils/Converter';

const defaultParams = {
	messages,
	constants,
	converter: new Converter()
};
const dbConfig      = new DatabaseConfigImpl({
	defaultParams,
	config: {
		local  : localStorage,
		session: sessionStorage
	}
});
const netConfig     = new NetworkConfigImpl({
	defaultParams,
	config: constants.global.network
});

const app          = appInit({
	netConfig,
	dbConfig,
	defaultParams
});
const sideUsers    = sideUsersInit({
	netConfig,
	dbConfig,
	defaultParams
});
const user         = userInit({
	netConfig,
	dbConfig,
	defaultParams
});
const chat         = chatInit({
	netConfig,
	dbConfig,
	defaultParams
});
ReactDOM.render(
	<Provider store={app}>
		<App
			config={{
				dbConfig,
				netConfig
			}}
			store={{
				chat,
				user,
				app,
				navigation: navigationStore,
				side      : {
					users   : sideUsers
				}
			}}
			defaultParams={defaultParams}/>
	</Provider>,
	document.getElementById('root')
);
